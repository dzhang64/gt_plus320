/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.setting;

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
import com.gt_plus.modules.oa.entity.setting.TaskPermission;
import com.gt_plus.modules.oa.service.setting.TaskPermissionService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 权限分类Controller
 * @author GT0155
 * @version 2017-12-25
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/setting/taskPermission")
public class TaskPermissionController extends BaseController {

	@Autowired
	private TaskPermissionService taskPermissionService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public TaskPermission get(@RequestParam(required=false) String id) {
		TaskPermission entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = taskPermissionService.get(id);
		}
		if (entity == null){
			entity = new TaskPermission();
		}
		return entity;
	}
	
	/**
	 * 权限分类列表页面
	 */
	@RequiresPermissions("oa:setting:taskPermission:list")
	@RequestMapping(value = {"list", ""})
	public String list(TaskPermission taskPermission, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/oa/setting/taskPermissionList";
	}
	
	/**
	 * 权限分类列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskPermission:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(TaskPermission taskPermission, HttpServletRequest request, HttpServletResponse response, Model model) {
		taskPermission.getSqlMap().put("dsf", this.getDataScope());
		Page<TaskPermission> page = taskPermissionService.findPage(new Page<TaskPermission>(request, response), taskPermission); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑权限分类表单页面
	 */
	@RequiresPermissions(value={"oa:setting:taskPermission:view","oa:setting:taskPermission:add","oa:setting:taskPermission:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(TaskPermission taskPermission, Model model) {
		model.addAttribute("taskPermission", taskPermission);
		return "modules/oa/setting/taskPermissionForm";
	}
	

	/**
	 * 保存权限分类
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:setting:taskPermission:add","oa:setting:taskPermission:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(TaskPermission taskPermission, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, taskPermission)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!taskPermission.getIsNewRecord()){
			//修改保存
			TaskPermission t = taskPermissionService.get(taskPermission.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) taskPermissionService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(taskPermission, t);
				taskPermissionService.save(t);
			}
		}else{
			//新建保存
			taskPermission.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			taskPermissionService.save(taskPermission);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存权限分类信息成功","权限分类"));
		//保存成功后处理逻辑
		this.afterSave("权限分类", taskPermission);
		return j;
	}
	
	/**
	 * 删除权限分类
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskPermission:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(TaskPermission taskPermission, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			taskPermission.setDelFlag(Global.YES);
			taskPermissionService.saveV(taskPermission); 
		}
		taskPermissionService.delete(taskPermission);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除权限分类信息成功","权限分类"));
		return j;
	}
	
	/**
	 * 批量删除权限分类
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskPermission:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				TaskPermission obj = taskPermissionService.get(id);
				obj.setDelFlag(Global.YES);
				taskPermissionService.saveV(obj); 
			}
			taskPermissionService.delete(taskPermissionService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除权限分类信息成功","权限分类"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskPermission:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(TaskPermission taskPermission, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "权限分类"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TaskPermission> page = taskPermissionService.findPage(new Page<TaskPermission>(request, response, -1), taskPermission);
    		new ExportExcel("权限分类", TaskPermission.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出权限分类信息记录失败！", "权限分类") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:setting:taskPermission:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TaskPermission> list = ei.getDataList(TaskPermission.class);
			for (TaskPermission taskPermission : list){
				try{
					taskPermissionService.save(taskPermission);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条权限分类记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条权限分类记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入权限分类失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/taskPermission/?repage";
    }
	
	/**
	 * 下载导入权限分类数据模板
	 */
	@RequiresPermissions("oa:setting:taskPermission:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "权限分类数据导入模板.xlsx";
    		List<TaskPermission> list = Lists.newArrayList(); 
    		new ExportExcel("权限分类数据", TaskPermission.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/taskPermission/?repage";
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
	private void afterSave(String title, TaskPermission taskPermission) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/setting/taskPermission");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, taskPermission.getOwnerCode(), roleMap);
	}
	
}