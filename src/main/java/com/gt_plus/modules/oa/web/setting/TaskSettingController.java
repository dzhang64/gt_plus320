/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.setting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
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

import com.gt_plus.modules.act.service.ActProcessService;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.entity.Level;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Organization;
import com.gt_plus.modules.sys.entity.Post;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.LevelService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.OrganizationService;
import com.gt_plus.modules.sys.service.PostService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.oa.entity.setting.TaskPermission;
import com.gt_plus.modules.oa.entity.setting.TaskSetting;
import com.gt_plus.modules.oa.service.setting.TaskPermissionService;
import com.gt_plus.modules.oa.service.setting.TaskSettingService;
/**
 * 节点权限Controller
 * @author GT0155
 * @version 2017-11-08
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/setting/taskSetting")
public class TaskSettingController extends BaseController {

	@Autowired
	private TaskSettingService taskSettingService;
	
	
	@Autowired
	private BasMessageService basMessageService;
	
	@Autowired
	private LevelService levelService;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private TaskPermissionService taskPermissionService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public TaskSetting get(@RequestParam(required=false) String id) {
		TaskSetting entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = taskSettingService.get(id);
		}
		if (entity == null){
			entity = new TaskSetting();
		}
		return entity;
	}
	
	
	@Autowired
	private ActProcessService actProcessService;
	
	/**
	 * 节点权限列表页面
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = {"list", ""})
	public String list(TaskSetting taskSetting, HttpServletRequest request, HttpServletResponse response, Model model) {
		/*
		List<Map<String, Object>> list = Lists.newArrayList();
		try {
			InputStream resourceRead = actProcessService.resourceRead(taskSetting.getProcessId(), null, "xml");
			InputStreamReader isr = new InputStreamReader(resourceRead, "UTF-8");
			XMLInputFactory xif = XMLInputFactory.newInstance();
			XMLStreamReader xsr = xif.createXMLStreamReader(isr);
			BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xsr);
			Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
			for (FlowElement flowElement : flowElements) {
				if (flowElement.getClass().toString().indexOf("UserTask") != -1) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("flowElement", flowElement);
					map.put("processId", taskSetting.getProcessId());
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("list", list);*/
		return "modules/oa/setting/taskSettingList";
	}
	
	/**
	 * 节点权限列表数据
	 */
	/*@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "data")
	public Map<String, Object> data(TaskSetting taskSetting, HttpServletRequest request, HttpServletResponse response, Model model) {
		taskSetting.getSqlMap().put("dsf", this.getDataScope());
		Page<TaskSetting> page = taskSettingService.findPage(new Page<TaskSetting>(request, response), taskSetting); 
		return getBootstrapData(page);
	}*/
	
	/**
	 * 查看，增加，编辑节点权限表单页面
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "form")
	public String form(TaskSetting taskSetting, Model model) {
		
		TaskSetting taskSetting2 = taskSettingService.getByProcAndTask(taskSetting);
		if (taskSetting2 != null) {
			try {
				MyBeanUtils.copyBeanNotNull2Bean(taskSetting2, taskSetting);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*taskSetting = taskSetting2;*/
		}
		TaskPermission taskPermission = new TaskPermission();
		taskPermission.getSqlMap().put("dsf", " AND a.category = '" + taskSetting.getPermissionType() + "'");
		List<TaskPermission> taskPermissionList = taskPermissionService.findList(taskPermission);
		model.addAttribute("taskPermissionList", taskPermissionList);
		
		List<Office> officeList = officeService.findAll();
		List<Post> postList = postService.findList(new Post());
		List<Level> levelList = levelService.findList(new Level());
		List<Role> roleList = systemService.findAllRole();
		List<Organization> orgList = organizationService.findList(new Organization());
		model.addAttribute("officeList", officeList);
		model.addAttribute("postList", postList);
		model.addAttribute("levelList", levelList);
		model.addAttribute("roleList", roleList);
		model.addAttribute("orgList", orgList);
		
		model.addAttribute("taskSetting", taskSetting);
		return "modules/oa/setting/taskSettingForm";
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "getUsersByIdIndex")
	public String getUsersByIdIndex(String objId, Model model) {
		List<User> list = Lists.newArrayList();
		if (objId.split(":")[0].equalsIgnoreCase("role")) {
			String condition = " AND b."+ objId.split(":")[0] + "_id='" + objId.split(":")[1] + "' ";
			list = systemService.findUserForFlow(condition);
		} else if (objId.split(":")[0].equalsIgnoreCase("post") 
				|| objId.split(":")[0].equalsIgnoreCase("level")
				|| objId.split(":")[0].equalsIgnoreCase("office")) {
			String condition = " AND a."+ objId.split(":")[0] + "_id='" + objId.split(":")[1] + "' ";
			list = systemService.findUserForFlow(condition);
		} else if (objId.split(":")[0].equalsIgnoreCase("org")) {//2017-12-07新增条件，非原始查询方法
			list = organizationService.findUserToOrg(objId.split(":")[1]);
		}
		return new Gson().toJson(list);
	}
	
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "getTaskPermissionList/{category}")
	public AjaxJson getTaskPermissionList(@PathVariable String category){
		AjaxJson j = new AjaxJson();
		TaskPermission taskPermission = new TaskPermission();
		taskPermission.getSqlMap().put("dsf", " AND a.category = '" + category + "'");
		List<TaskPermission> taskPermissionList = taskPermissionService.findList(taskPermission);
		LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		map.put("taskPermissionList", taskPermissionList);
		j.setBody(map);
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 保存节点权限
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "save")
	public AjaxJson save(TaskSetting taskSetting, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, taskSetting)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!taskSetting.getIsNewRecord()){
			//修改保存
			TaskSetting t = taskSettingService.get(taskSetting.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) taskSettingService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(taskSetting, t);
				taskSettingService.save(t);
				LinkedHashMap<String, Object> body = Maps.newLinkedHashMap();
				body.put("id", taskSetting.getId());
				j.setBody(body);
			}
		}else{
			//新建保存
			taskSettingService.save(taskSetting);
			LinkedHashMap<String, Object> body = Maps.newLinkedHashMap();
			body.put("id", taskSetting.getId());
			j.setBody(body);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存节点权限信息成功","节点权限"));
		//保存成功后处理逻辑
		this.afterSave("节点权限", taskSetting);
		return j;
	}
	
	/**
	 * 删除节点权限
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "delete")
	public AjaxJson delete(TaskSetting taskSetting, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			taskSetting.setDelFlag(Global.YES);
			taskSettingService.saveV(taskSetting); 
		}
		taskSettingService.delete(taskSetting);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除节点权限信息成功","节点权限"));
		return j;
	}
	
	/**
	 * 批量删除节点权限
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				TaskSetting obj = taskSettingService.get(id);
				obj.setDelFlag(Global.YES);
				taskSettingService.saveV(obj); 
			}
			taskSettingService.delete(taskSettingService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除节点权限信息成功","节点权限"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("user")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(TaskSetting taskSetting, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "节点权限"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TaskSetting> page = taskSettingService.findPage(new Page<TaskSetting>(request, response, -1), taskSetting);
    		new ExportExcel("节点权限", TaskSetting.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出节点权限信息记录失败！", "节点权限") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("user")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TaskSetting> list = ei.getDataList(TaskSetting.class);
			for (TaskSetting taskSetting : list){
				try{
					taskSettingService.save(taskSetting);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条节点权限记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条节点权限记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入节点权限失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/taskSetting/?repage";
    }
	
	/**
	 * 下载导入节点权限数据模板
	 */
	@RequiresPermissions("user")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "节点权限数据导入模板.xlsx";
    		List<TaskSetting> list = Lists.newArrayList(); 
    		new ExportExcel("节点权限数据", TaskSetting.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/taskSetting/?repage";
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
	private void afterSave(String title, TaskSetting taskSetting) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/setting/taskSetting");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, taskSetting.getOwnerCode(), roleMap);
	}
}