/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.arc;

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
import com.gt_plus.modules.oa.entity.arc.ArcCategory;
import com.gt_plus.modules.oa.service.arc.ArcCategoryService;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.service.OfficeService;

/**
 * 档案目录Controller
 * @author LS0077
 * @version 2017-11-07
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/arc/arcCategory")
public class ArcCategoryController extends BaseController {

	@Autowired
	private ArcCategoryService arcCategoryService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false;
	
	@ModelAttribute
	public ArcCategory get(@RequestParam(required=false) String id) {
		ArcCategory entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = arcCategoryService.get(id);
		}
		if (entity == null){
			entity = new ArcCategory();
		}
		return entity;
	}
	
	/**
	 * 档案目录列表页面
	 */
	@RequiresPermissions("oa:arc:arcCategory:list")
	@RequestMapping(value = {"list", ""})
	public String list(ArcCategory arcCategory,  HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/oa/arc/arcCategoryList";
	}

	/**
	 * 查看，增加，编辑档案目录表单页面
	 */
	@RequiresPermissions(value={"oa:arc:arcCategory:view","oa:arc:arcCategory:add","oa:arc:arcCategory:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ArcCategory arcCategory, Model model) {
		if (arcCategory.getParent()!=null && StringUtils.isNotBlank(arcCategory.getParent().getId())){
			arcCategory.setParent(arcCategoryService.get(arcCategory.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(arcCategory.getId())){
				ArcCategory arcCategoryChild = new ArcCategory();
				arcCategoryChild.setParent(new ArcCategory(arcCategory.getParent().getId()));
				List<ArcCategory> list = arcCategoryService.findList(arcCategory); 
				if (list.size() > 0){
					arcCategory.setSort(list.get(list.size()-1).getSort());
					if (arcCategory.getSort() != null){
						arcCategory.setSort(arcCategory.getSort() + 30);
					}
				}
			}
		}
		if (arcCategory.getSort() == null){
			arcCategory.setSort(30);
		}
		model.addAttribute("arcCategory", arcCategory);
		return "modules/oa/arc/arcCategoryForm";
	}

	/**
	 * 保存档案目录
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:arc:arcCategory:add","oa:arc:arcCategory:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ArcCategory arcCategory, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, arcCategory)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!arcCategory.getIsNewRecord()){
			//修改保存
			ArcCategory t = arcCategoryService.get(arcCategory.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) arcCategoryService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(arcCategory, t);
				arcCategoryService.save(t);
			}
		}else{
			//新建保存
			arcCategory.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			arcCategoryService.save(arcCategory);
		}
		j.setSuccess(true);
		j.put("arcCategory", arcCategory);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存档案目录信息成功","档案目录"));
		//保存成功后处理逻辑
		this.afterSave("档案目录", arcCategory);
		return j;
	}
	
	@ResponseBody
	@RequiresPermissions("oa:arc:arcCategory:list")
	@RequestMapping(value = "getChildren")
	public List<ArcCategory> getChildren(String parentId){
		if("-1".equals(parentId)){//如果是-1，没指定任何父节点，就从根节点开始查找
			parentId = "0";
		}
		return arcCategoryService.getChildren(parentId);
	}
	
	/**
	 * 删除档案目录
	 */
	@ResponseBody
	@RequiresPermissions("oa:arc:arcCategory:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ArcCategory arcCategory, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			arcCategory.setDelFlag(Global.YES);
			arcCategoryService.saveV(arcCategory); 
		}
		arcCategoryService.delete(arcCategory);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除档案目录信息成功","档案目录"));
		return j;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<ArcCategory> list = arcCategoryService.findList(new ArcCategory());
		for (int i=0; i<list.size(); i++){
			ArcCategory e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
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
		List<ArcCategory> list = arcCategoryService.findList(new ArcCategory());
		for (int i=0; i<list.size(); i++){
			ArcCategory e = list.get(i);
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
	private void afterSave(String title, ArcCategory arcCategory) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/arc/arcCategory");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, arcCategory.getOwnerCode(), roleMap);
	}
	
}