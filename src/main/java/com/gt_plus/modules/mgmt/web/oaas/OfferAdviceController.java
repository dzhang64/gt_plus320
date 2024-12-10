/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.web.oaas;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.google.common.collect.Maps;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.mgmt.entity.oaas.OfferAdvice;
import com.gt_plus.modules.mgmt.service.oaas.OfferAdviceService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 建言献策Controller
 * @author GT0155
 * @version 2017-11-28
 */
@Controller
@RequestMapping(value = "${adminPath}/mgmt/oaas/offerAdvice")
public class OfferAdviceController extends BaseController {

	@Autowired
	private OfferAdviceService offerAdviceService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public OfferAdvice get(@RequestParam(required=false) String id) {
		OfferAdvice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = offerAdviceService.get(id);
		}
		if (entity == null){
			entity = new OfferAdvice();
		}
		return entity;
	}
	
	/**
	 * 建言献策列表页面
	 */
	@RequiresPermissions("mgmt:oaas:offerAdvice:list")
	@RequestMapping(value = {"list", ""})
	public String list(OfferAdvice offerAdvice, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/mgmt/oaas/offerAdviceList";
	}
	
	/**
	 * 建言献策列表数据
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:oaas:offerAdvice:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(OfferAdvice offerAdvice, HttpServletRequest request, HttpServletResponse response, Model model) {
		offerAdvice.getSqlMap().put("dsf", this.getDataScope());
		Page<OfferAdvice> page = offerAdviceService.findPage(new Page<OfferAdvice>(request, response), offerAdvice); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑建言献策表单页面
	 */
	@RequiresPermissions(value={"mgmt:oaas:offerAdvice:view","mgmt:oaas:offerAdvice:add","mgmt:oaas:offerAdvice:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(OfferAdvice offerAdvice, Model model) {
		model.addAttribute("offerAdvice", offerAdvice);
		return "modules/mgmt/oaas/offerAdviceForm";
	}

	/**
	 * 保存建言献策
	 */
	@ResponseBody
	@RequiresPermissions(value={"mgmt:oaas:offerAdvice:add","mgmt:oaas:offerAdvice:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(OfferAdvice offerAdvice, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, offerAdvice)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!offerAdvice.getIsNewRecord()){
			//修改保存
			OfferAdvice t = offerAdviceService.get(offerAdvice.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) offerAdviceService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(offerAdvice, t);
				offerAdviceService.save(t);
			}
		}else{
			//新建保存
			offerAdvice.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			offerAdviceService.save(offerAdvice);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存建言献策信息成功","建言献策"));
		//保存成功后处理逻辑
		this.afterSave("建言献策", offerAdvice);
		return j;
	}
	
	/**
	 * 删除建言献策
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:oaas:offerAdvice:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(OfferAdvice offerAdvice, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			offerAdvice.setDelFlag(Global.YES);
			offerAdviceService.saveV(offerAdvice); 
		}
		offerAdviceService.delete(offerAdvice);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除建言献策信息成功","建言献策"));
		return j;
	}
	
	/**
	 * 批量删除建言献策
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:oaas:offerAdvice:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				OfferAdvice obj = offerAdviceService.get(id);
				obj.setDelFlag(Global.YES);
				offerAdviceService.saveV(obj); 
			}
			offerAdviceService.delete(offerAdviceService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除建言献策信息成功","建言献策"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:oaas:offerAdvice:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(OfferAdvice offerAdvice, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "建言献策"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OfferAdvice> page = offerAdviceService.findPage(new Page<OfferAdvice>(request, response, -1), offerAdvice);
    		new ExportExcel("建言献策", OfferAdvice.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出建言献策信息记录失败！", "建言献策") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("mgmt:oaas:offerAdvice:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OfferAdvice> list = ei.getDataList(OfferAdvice.class);
			for (OfferAdvice offerAdvice : list){
				try{
					offerAdviceService.save(offerAdvice);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条建言献策记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条建言献策记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入建言献策失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/mgmt/oaas/offerAdvice/?repage";
    }
	
	/**
	 * 下载导入建言献策数据模板
	 */
	@RequiresPermissions("mgmt:oaas:offerAdvice:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "建言献策数据导入模板.xlsx";
    		List<OfferAdvice> list = Lists.newArrayList(); 
    		new ExportExcel("建言献策数据", OfferAdvice.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/mgmt/oaas/offerAdvice/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" AND a.owner_code = '" + officeService.get(UserUtils.getUser().getCompany().getId()).getCode() + "'");
		return sqlString.toString();
	}
	
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, OfferAdvice offerAdvice) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/mgmt/oaas/offerAdvice");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, offerAdvice.getOwnerCode(), roleMap);
	}
	
	/**
	 * 建言献策新增接口
	 */
	@RequestMapping(value ="saveObj",method=RequestMethod.GET)
    @ResponseBody
    public String saveObj(OfferAdvice offerAdvice){
		if(offerAdvice.getComp()!=null){
			try{
				offerAdviceService.save(offerAdvice);
				return "callback({'id':'" + offerAdvice.getId() + "'});";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "callback({'id':''});";
    }
	
	/**
	 * 建言献策更新接口
	 */
	@RequestMapping(value ="updateObj",method=RequestMethod.GET)
    @ResponseBody
    public String updateObj(OfferAdvice offerAdvice){
		if(offerAdvice.getId()!=null){
			try{
				offerAdviceService.save(offerAdvice);
				return "callback({'msg':'提交成功'});";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "callback({'msg':'提交失败'});";
    }
	
}