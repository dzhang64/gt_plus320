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
import com.gt_plus.modules.oa.entity.setting.TaskSettingVersion;
import com.gt_plus.modules.oa.service.setting.TaskSettingVersionService;
/**
 * 节点权限版本Controller
 * @author GT0155
 * @version 2017-11-15
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/setting/taskSettingVersion")
public class TaskSettingVersionController extends BaseController {

	@Autowired
	private TaskSettingVersionService taskSettingVersionService;
	
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public TaskSettingVersion get(@RequestParam(required=false) String id) {
		TaskSettingVersion entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = taskSettingVersionService.get(id);
		}
		if (entity == null){
			entity = new TaskSettingVersion();
		}
		return entity;
	}
	
	/**
	 * 节点权限版本列表页面
	 */
	@RequiresPermissions("oa:setting:taskSettingVersion:list")
	@RequestMapping(value = {"list", ""})
	public String list(TaskSettingVersion taskSettingVersion, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/oa/setting/taskSettingVersionList";
	}
	
	/**
	 * 节点权限版本列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskSettingVersion:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(TaskSettingVersion taskSettingVersion, HttpServletRequest request, HttpServletResponse response, Model model) {
		taskSettingVersion.getSqlMap().put("dsf", this.getDataScope());
		Page<TaskSettingVersion> page = taskSettingVersionService.findPage(new Page<TaskSettingVersion>(request, response), taskSettingVersion); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑节点权限版本表单页面
	 */
	@RequiresPermissions(value={"oa:setting:taskSettingVersion:view","oa:setting:taskSettingVersion:add","oa:setting:taskSettingVersion:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(TaskSettingVersion taskSettingVersion, Model model) {
		model.addAttribute("taskSettingVersion", taskSettingVersion);
		return "modules/oa/setting/taskSettingVersionForm";
	}

	/**
	 * 保存节点权限版本
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:setting:taskSettingVersion:add","oa:setting:taskSettingVersion:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(TaskSettingVersion taskSettingVersion, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, taskSettingVersion)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!taskSettingVersion.getIsNewRecord()){
			//修改保存
			TaskSettingVersion t = taskSettingVersionService.get(taskSettingVersion.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) taskSettingVersionService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(taskSettingVersion, t);
				taskSettingVersionService.save(t);
			}
		}else{
			//新建保存
			taskSettingVersionService.save(taskSettingVersion);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存节点权限版本信息成功","节点权限版本"));
		//保存成功后处理逻辑
		this.afterSave("节点权限版本", taskSettingVersion);
		return j;
	}
	
	/**
	 * 删除节点权限版本
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskSettingVersion:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(TaskSettingVersion taskSettingVersion, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			taskSettingVersion.setDelFlag(Global.YES);
			taskSettingVersionService.saveV(taskSettingVersion); 
		}
		taskSettingVersionService.delete(taskSettingVersion);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除节点权限版本信息成功","节点权限版本"));
		return j;
	}
	
	/**
	 * 批量删除节点权限版本
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskSettingVersion:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				TaskSettingVersion obj = taskSettingVersionService.get(id);
				obj.setDelFlag(Global.YES);
				taskSettingVersionService.saveV(obj); 
			}
			taskSettingVersionService.delete(taskSettingVersionService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除节点权限版本信息成功","节点权限版本"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:taskSettingVersion:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(TaskSettingVersion taskSettingVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "节点权限版本"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TaskSettingVersion> page = taskSettingVersionService.findPage(new Page<TaskSettingVersion>(request, response, -1), taskSettingVersion);
    		new ExportExcel("节点权限版本", TaskSettingVersion.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出节点权限版本信息记录失败！", "节点权限版本") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:setting:taskSettingVersion:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TaskSettingVersion> list = ei.getDataList(TaskSettingVersion.class);
			for (TaskSettingVersion taskSettingVersion : list){
				try{
					taskSettingVersionService.save(taskSettingVersion);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条节点权限版本记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条节点权限版本记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入节点权限版本失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/taskSettingVersion/?repage";
    }
	
	/**
	 * 下载导入节点权限版本数据模板
	 */
	@RequiresPermissions("oa:setting:taskSettingVersion:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "节点权限版本数据导入模板.xlsx";
    		List<TaskSettingVersion> list = Lists.newArrayList(); 
    		new ExportExcel("节点权限版本数据", TaskSettingVersion.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/taskSettingVersion/?repage";
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
	private void afterSave(String title, TaskSettingVersion taskSettingVersion) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/setting/taskSettingVersion");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, taskSettingVersion.getOwnerCode(), roleMap);
	}
}