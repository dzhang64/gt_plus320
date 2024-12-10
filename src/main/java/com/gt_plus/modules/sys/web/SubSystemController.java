/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

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
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sys.entity.SubSystem;
import com.gt_plus.modules.sys.service.SubSystemService;
/**
 * 子系统Controller
 * @author GT0291
 * @version 2017-07-25
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/subSystem")
public class SubSystemController extends BaseController {

	@Autowired
	private SubSystemService subSystemService;
	

	private void setDbType() {
		//DataSourceContextHolder.setDbType("dataSourceApp");
	}
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public SubSystem get(@RequestParam(required=false) String id) {
		this.setDbType();
		SubSystem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = subSystemService.get(id);
		}
		if (entity == null){
			entity = new SubSystem();
		}
		return entity;
	}
	
	/**
	 * 子系统列表页面
	 */
	@RequiresPermissions("sys:subSystem:list")
	@RequestMapping(value = {"list", ""})
	public String list(SubSystem subSystem, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		return "modules/sys/subSystemList";
	}
	
	/**
	 * 子系统列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:subSystem:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(SubSystem subSystem, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		subSystem.getSqlMap().put("dsf", this.getDataScope());
		Page<SubSystem> page = subSystemService.findPage(new Page<SubSystem>(request, response), subSystem); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑子系统表单页面
	 */
	@RequiresPermissions(value={"sys:subSystem:view","sys:subSystem:add","sys:subSystem:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SubSystem subSystem, Model model) {
		this.setDbType();
		model.addAttribute("subSystem", subSystem);
		return "modules/sys/subSystemForm";
	}

	/**
	 * 保存子系统
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:subSystem:add","sys:subSystem:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SubSystem subSystem, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		this.setDbType();
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, subSystem)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		*/
		if(!subSystem.getIsNewRecord()){
			//修改保存
			SubSystem t = subSystemService.get(subSystem.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg("记录已经被修改！");
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) subSystemService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(subSystem, t);
				subSystemService.save(t);
			}
		}else{
			//新建保存
			subSystemService.save(subSystem);
		}
		j.setSuccess(true);
		j.setMsg("保存子系统成功");
		return j;
	}
	
	/**
	 * 删除子系统
	 */
	@ResponseBody
	@RequiresPermissions("sys:subSystem:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SubSystem subSystem, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			subSystem.setDelFlag(Global.YES);
			subSystemService.saveV(subSystem); 
		}
		subSystemService.delete(subSystem);
		j.setMsg("删除子系统成功");
		return j;
	}
	
	/**
	 * 批量删除子系统
	 */
	@ResponseBody
	@RequiresPermissions("sys:subSystem:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SubSystem obj = subSystemService.get(id);
				obj.setDelFlag(Global.YES);
				subSystemService.saveV(obj); 
			}
			subSystemService.delete(subSystemService.get(id));
		}
		j.setMsg("删除子系统成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:subSystem:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SubSystem subSystem, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "子系统"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SubSystem> page = subSystemService.findPage(new Page<SubSystem>(request, response, -1), subSystem);
    		new ExportExcel("子系统", SubSystem.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出子系统记录失败！失败信息："+e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:subSystem:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SubSystem> list = ei.getDataList(SubSystem.class);
			for (SubSystem subSystem : list){
				try{
					subSystemService.save(subSystem);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条子系统记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条子系统记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入子系统失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/subSystem/?repage";
    }
	
	/**
	 * 下载导入子系统数据模板
	 */
	@RequiresPermissions("sys:subSystem:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
            String fileName = "子系统数据导入模板.xlsx";
    		List<SubSystem> list = Lists.newArrayList(); 
    		new ExportExcel("子系统数据", SubSystem.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/subSystem/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
}