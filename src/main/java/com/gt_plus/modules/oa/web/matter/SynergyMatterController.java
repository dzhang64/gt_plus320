/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.matter;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
import com.gt_plus.modules.oa.entity.matter.SynergyMatter;
import com.gt_plus.modules.oa.service.matter.SynergyMatterService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 协同事项Controller
 * @author LS0077
 * @version 2017-11-08
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/matter/synergyMatter")
public class SynergyMatterController extends BaseController {

	@Autowired
	private SynergyMatterService synergyMatterService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public SynergyMatter get(@RequestParam(required=false) String id) {
		SynergyMatter entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = synergyMatterService.get(id);
		}
		if (entity == null){
			entity = new SynergyMatter();
		}
		return entity;
	}
	
	/**
	 * 协同事项列表页面
	 */
	@RequiresPermissions("oa:matter:synergyMatter:list")
	@RequestMapping(value = {"list", ""})
	public String list(SynergyMatter synergyMatter, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/oa/matter/synergyMatterList";
	}
	
	/**
	 * 协同事项列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:matter:synergyMatter:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(SynergyMatter synergyMatter, HttpServletRequest request, HttpServletResponse response, Model model) {
		synergyMatter.getSqlMap().put("dsf", this.getDataScope());
		Page<SynergyMatter> page = synergyMatterService.findPage(new Page<SynergyMatter>(request, response), synergyMatter); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑协同事项表单页面
	 */
	@RequiresPermissions(value={"oa:matter:synergyMatter:view","oa:matter:synergyMatter:add","oa:matter:synergyMatter:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SynergyMatter synergyMatter, Model model) {
		model.addAttribute("synergyMatter", synergyMatter);
		return "modules/oa/matter/synergyMatterForm";
	}

	/**
	 * 保存协同事项
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:matter:synergyMatter:add","oa:matter:synergyMatter:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SynergyMatter synergyMatter, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, synergyMatter)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!synergyMatter.getIsNewRecord()){
			//修改保存
			SynergyMatter t = synergyMatterService.get(synergyMatter.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) synergyMatterService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(synergyMatter, t);
				synergyMatterService.save(t);
			}
		}else{
			//新建保存
			synergyMatter.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			synergyMatterService.save(synergyMatter);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存协同事项信息成功","协同事项"));
		//保存成功后处理逻辑
		this.afterSave("协同事项", synergyMatter);
		return j;
	}
	
	/**
	 * 删除协同事项
	 */
	@ResponseBody
	@RequiresPermissions("oa:matter:synergyMatter:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SynergyMatter synergyMatter, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			synergyMatter.setDelFlag(Global.YES);
			synergyMatterService.saveV(synergyMatter); 
		}
		synergyMatterService.delete(synergyMatter);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除协同事项信息成功","协同事项"));
		return j;
	}
	
	/**
	 * 批量删除协同事项
	 */
	@ResponseBody
	@RequiresPermissions("oa:matter:synergyMatter:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SynergyMatter obj = synergyMatterService.get(id);
				obj.setDelFlag(Global.YES);
				synergyMatterService.saveV(obj); 
			}
			synergyMatterService.delete(synergyMatterService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除协同事项信息成功","协同事项"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:matter:synergyMatter:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SynergyMatter synergyMatter, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "协同事项"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SynergyMatter> page = synergyMatterService.findPage(new Page<SynergyMatter>(request, response, -1), synergyMatter);
    		new ExportExcel("协同事项", SynergyMatter.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出协同事项信息记录失败！", "协同事项") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:matter:synergyMatter:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SynergyMatter> list = ei.getDataList(SynergyMatter.class);
			for (SynergyMatter synergyMatter : list){
				try{
					synergyMatterService.save(synergyMatter);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条协同事项记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条协同事项记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入协同事项失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/matter/synergyMatter/?repage";
    }
	
	/**
	 * 下载导入协同事项数据模板
	 */
	@RequiresPermissions("oa:matter:synergyMatter:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "协同事项数据导入模板.xlsx";
    		List<SynergyMatter> list = Lists.newArrayList(); 
    		new ExportExcel("协同事项数据", SynergyMatter.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/matter/synergyMatter/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, SynergyMatter synergyMatter) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/matter/synergyMatter");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, synergyMatter.getOwnerCode(), roleMap);
	}
}