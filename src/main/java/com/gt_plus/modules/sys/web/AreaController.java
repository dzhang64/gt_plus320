/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.sys.service.AreaService;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 区域Controller
 * @author gt_plus
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/area")
public class AreaController extends BaseController {

	@Autowired
	private AreaService areaService;
	
	@ModelAttribute("area")
	public Area get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return areaService.get(id);
		}else{
			return new Area();
		}
	}

	@RequiresPermissions("sys:area:list")
	@RequestMapping(value = {"list", ""})
	public String list(Area area, Model model) {
		//model.addAttribute("list", areaService.findAll());
		return "modules/sys/areaList";
	}
	
	/**
	 * 区域列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:area:list")
	@RequestMapping(value = "data")
	public List<Area> data(Area area, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Area> list = areaService.findAll(); 
		return list;
	}

	@RequiresPermissions(value={"sys:area:view","sys:area:add","sys:area:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Area area, Model model) {
		if (area.getParent()==null||area.getParent().getId()==null){
			area.setParent(UserUtils.getUser().getOffice().getArea());
		}else{
			area.setParent(areaService.get(area.getParent().getId()));
		}
		
//		// 自动获取排序号
//		if (StringUtils.isBlank(area.getId())){
//			int size = 0;
//			List<Area> list = areaService.findAll();
//			for (int i=0; i<list.size(); i++){
//				Area e = list.get(i);
//				if (e.getParent()!=null && e.getParent().getId()!=null
//						&& e.getParent().getId().equals(area.getParent().getId())){
//					size++;
//				}
//			}
//			area.setCode(area.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size : 1), 4, "0"));
//		}
		model.addAttribute("area", area);
		return "modules/sys/areaForm";
	}
	
	/**
	 * 保存区域
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:area:add","sys:area:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Area area, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, area)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		*/
		if(!area.getIsNewRecord()){
			//修改保存
			Area t = areaService.get(area.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg("记录已经被修改！");
				return j;
			} else {   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(area, t);
				areaService.save(t);
			}
		}else{
			//新建保存
			areaService.save(area);
		}
		j.setSuccess(true);
		j.setMsg("保存区域成功");
		return j;
	}
	
	/**
	 * 删除区域
	 */
	@ResponseBody
	@RequiresPermissions("sys:area:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Area area, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		areaService.delete(area);
		j.setMsg("删除区域成功");
		return j;
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Area> list = areaService.findAll();
		for (int i=0; i<list.size(); i++){
			Area e = list.get(i);
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
}
