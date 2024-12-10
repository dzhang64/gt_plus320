/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.web.bgentry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.projectbg.entity.bgentry.ProjectBgEntry;
import com.gt_plus.modules.projectbg.service.bgentry.ProjectBgEntryService;
import com.gt_plus.modules.sys.service.OfficeService;

/**
 * 预算条目字典Controller
 * @author zdy
 * @version 2018-03-19
 */
@Controller
@RequestMapping(value = "${adminPath}/projectbg/bgentry/projectBgEntry")
public class ProjectBgEntryController extends BaseController {

	@Autowired
	private ProjectBgEntryService projectBgEntryService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false;
	
	@ModelAttribute
	public ProjectBgEntry get(@RequestParam(required=false) String id) {
		ProjectBgEntry entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectBgEntryService.get(id);
		}
		if (entity == null){
			entity = new ProjectBgEntry();
		}
		return entity;
	}
	
	/**
	 * 预算条目字典列表页面
	 */
	@RequiresPermissions("projectbg:bgentry:projectBgEntry:list")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectBgEntry projectBgEntry,  HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/projectbg/bgentry/projectBgEntryList";
	}

	/**
	 * 查看，增加，编辑预算条目字典表单页面
	 */
	@RequiresPermissions(value={"projectbg:bgentry:projectBgEntry:view","projectbg:bgentry:projectBgEntry:add","projectbg:bgentry:projectBgEntry:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ProjectBgEntry projectBgEntry, Model model) {
		if (projectBgEntry.getParent()!=null && StringUtils.isNotBlank(projectBgEntry.getParent().getId())){
			projectBgEntry.setParent(projectBgEntryService.get(projectBgEntry.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(projectBgEntry.getId())){
				ProjectBgEntry projectBgEntryChild = new ProjectBgEntry();
				projectBgEntryChild.setParent(new ProjectBgEntry(projectBgEntry.getParent().getId()));
				List<ProjectBgEntry> list = projectBgEntryService.findList(projectBgEntry); 
				if (list.size() > 0){
					projectBgEntry.setSort(list.get(list.size()-1).getSort());
					if (projectBgEntry.getSort() != null){
						projectBgEntry.setSort(projectBgEntry.getSort() + 30);
					}
				}
			}
		}
		if (projectBgEntry.getSort() == null){
			projectBgEntry.setSort(30);
		}
		model.addAttribute("projectBgEntry", projectBgEntry);
		return "modules/projectbg/bgentry/projectBgEntryForm";
	}

	/**
	 * 保存预算条目字典
	 */
	@ResponseBody
	@RequiresPermissions(value={"projectbg:bgentry:projectBgEntry:add","projectbg:bgentry:projectBgEntry:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ProjectBgEntry projectBgEntry, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, projectBgEntry)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!projectBgEntry.getIsNewRecord()){
			//修改保存
			ProjectBgEntry t = projectBgEntryService.get(projectBgEntry.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) projectBgEntryService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(projectBgEntry, t);
				projectBgEntryService.save(t);
			}
		}else{
			//新建保存
			projectBgEntry.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			projectBgEntryService.save(projectBgEntry);
		}
		j.setSuccess(true);
		j.put("projectBgEntry", projectBgEntry);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存预算条目字典信息成功","预算条目字典"));
		//保存成功后处理逻辑
		this.afterSave("预算条目字典", projectBgEntry);
		return j;
	}
	
	@ResponseBody
	@RequiresPermissions("projectbg:bgentry:projectBgEntry:list")
	@RequestMapping(value = "getChildren")
	public List<ProjectBgEntry> getChildren(String parentId){
		if("-1".equals(parentId)){//如果是-1，没指定任何父节点，就从根节点开始查找
			parentId = "0";
		}
		return projectBgEntryService.getChildren(parentId);
	}
	
	/**
	 * 删除预算条目字典
	 */
	@ResponseBody
	@RequiresPermissions("projectbg:bgentry:projectBgEntry:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ProjectBgEntry projectBgEntry, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			projectBgEntry.setDelFlag(Global.YES);
			projectBgEntryService.saveV(projectBgEntry); 
		}
		projectBgEntryService.delete(projectBgEntry);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除预算条目字典信息成功","预算条目字典"));
		return j;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, String>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, String>> mapList = Lists.newArrayList();
		List<ProjectBgEntry> list = projectBgEntryService.findList(new ProjectBgEntry());
		for (int i=0; i<list.size(); i++){
			ProjectBgEntry e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, String> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData2")
	public List<Map<String, Object>> treeData2(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ProjectBgEntry> list = projectBgEntryService.findList(new ProjectBgEntry());
		for (int i=0; i<list.size(); i++){
			ProjectBgEntry e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("text", e.getName());
				if(StringUtils.isBlank(e.getParentId()) || "0".equals(e.getParentId())){
					map.put("parent", "#");
					Map<String, Object> state = Maps.newHashMap();
					state.put("opened", true);
					map.put("state", state);
				}else{
					map.put("parent", e.getParentId());
				}
				mapList.add(map);
			}
		}
		return mapList;
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
	private void afterSave(String title, ProjectBgEntry projectBgEntry) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/projectbg/bgentry/projectBgEntry");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, projectBgEntry.getOwnerCode(), roleMap);
	}
}