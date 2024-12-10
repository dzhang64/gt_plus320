/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.web.op;

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
import com.gt_plus.modules.mgmt.entity.op.OnlinePetition;
import com.gt_plus.modules.mgmt.service.op.OnlinePetitionService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 网上信访Controller
 * @author GT0155
 * @version 2017-11-28
 */
@Controller
@RequestMapping(value = "${adminPath}/mgmt/op/onlinePetition")
public class OnlinePetitionController extends BaseController {

	@Autowired
	private OnlinePetitionService onlinePetitionService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public OnlinePetition get(@RequestParam(required=false) String id) {
		OnlinePetition entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = onlinePetitionService.get(id);
		}
		if (entity == null){
			entity = new OnlinePetition();
		}
		return entity;
	}
	
	/**
	 * 网上信访列表页面
	 */
	@RequiresPermissions("mgmt:op:onlinePetition:list")
	@RequestMapping(value = {"list", ""})
	public String list(OnlinePetition onlinePetition, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/mgmt/op/onlinePetitionList";
	}
	
	/**
	 * 网上信访列表数据
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:op:onlinePetition:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(OnlinePetition onlinePetition, HttpServletRequest request, HttpServletResponse response, Model model) {
		onlinePetition.getSqlMap().put("dsf", this.getDataScope());
		Page<OnlinePetition> page = onlinePetitionService.findPage(new Page<OnlinePetition>(request, response), onlinePetition); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑网上信访表单页面
	 */
	@RequiresPermissions(value={"mgmt:op:onlinePetition:view","mgmt:op:onlinePetition:add","mgmt:op:onlinePetition:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(OnlinePetition onlinePetition, Model model) {
		model.addAttribute("onlinePetition", onlinePetition);
		return "modules/mgmt/op/onlinePetitionForm";
	}

	/**
	 * 保存网上信访
	 */
	@ResponseBody
	@RequiresPermissions(value={"mgmt:op:onlinePetition:add","mgmt:op:onlinePetition:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(OnlinePetition onlinePetition, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, onlinePetition)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!onlinePetition.getIsNewRecord()){
			//修改保存
			OnlinePetition t = onlinePetitionService.get(onlinePetition.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) onlinePetitionService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(onlinePetition, t);
				onlinePetitionService.save(t);
			}
		}else{
			//新建保存
			onlinePetition.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			onlinePetitionService.save(onlinePetition);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存网上信访信息成功","网上信访"));
		//保存成功后处理逻辑
		this.afterSave("网上信访", onlinePetition);
		return j;
	}
	
	/**
	 * 删除网上信访
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:op:onlinePetition:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(OnlinePetition onlinePetition, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			onlinePetition.setDelFlag(Global.YES);
			onlinePetitionService.saveV(onlinePetition); 
		}
		onlinePetitionService.delete(onlinePetition);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除网上信访信息成功","网上信访"));
		return j;
	}
	
	/**
	 * 批量删除网上信访
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:op:onlinePetition:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				OnlinePetition obj = onlinePetitionService.get(id);
				obj.setDelFlag(Global.YES);
				onlinePetitionService.saveV(obj); 
			}
			onlinePetitionService.delete(onlinePetitionService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除网上信访信息成功","网上信访"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:op:onlinePetition:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(OnlinePetition onlinePetition, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "网上信访"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OnlinePetition> page = onlinePetitionService.findPage(new Page<OnlinePetition>(request, response, -1), onlinePetition);
    		new ExportExcel("网上信访", OnlinePetition.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出网上信访信息记录失败！", "网上信访") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("mgmt:op:onlinePetition:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OnlinePetition> list = ei.getDataList(OnlinePetition.class);
			for (OnlinePetition onlinePetition : list){
				try{
					onlinePetitionService.save(onlinePetition);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条网上信访记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条网上信访记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入网上信访失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/mgmt/op/onlinePetition/?repage";
    }
	
	/**
	 * 下载导入网上信访数据模板
	 */
	@RequiresPermissions("mgmt:op:onlinePetition:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "网上信访数据导入模板.xlsx";
    		List<OnlinePetition> list = Lists.newArrayList(); 
    		new ExportExcel("网上信访数据", OnlinePetition.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/mgmt/op/onlinePetition/?repage";
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
	private void afterSave(String title, OnlinePetition onlinePetition) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/mgmt/op/onlinePetition");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, onlinePetition.getOwnerCode(), roleMap);
	}
	
	/**
	 * 网上信访新增接口
	 */
	@RequestMapping(value ="saveObj",method=RequestMethod.GET)
    @ResponseBody
    public String saveObj(OnlinePetition onlinePetition){
		if(onlinePetition.getComp()!=null){
			try{
				onlinePetitionService.save(onlinePetition);
				return "callback({'id':'" + onlinePetition.getId() + "'});";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "callback({'id':''});";
    }
	
	/**
	 * 网上信访更新接口
	 */
	@RequestMapping(value ="updateObj",method=RequestMethod.GET)
    @ResponseBody
    public String updateObj(OnlinePetition onlinePetition){
		if(onlinePetition.getId()!=null){
			try{
				onlinePetitionService.save(onlinePetition);
				return "callback({'msg':'提交成功'});";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "callback({'msg':'提交失败'});";
    }
}