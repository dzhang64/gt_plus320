/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.web.bgcolumn;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
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
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.projectbg.entity.bgcolumn.ProjectBg;
import com.gt_plus.modules.projectbg.entity.bgcolumn.ProjectBgColumn;
import com.gt_plus.modules.projectbg.service.bgcolumn.ProjectBgColumnService;
import com.gt_plus.modules.projectbg.service.bgentry.ProjectBgEntryService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 项目预算行Controller
 * @author zdy
 * @version 2018-03-21
 */
@Controller
@RequestMapping(value = "${adminPath}/projectbg/bgcolumn/projectBgColumn")
public class ProjectBgColumnController extends BaseController {

	@Autowired
	private ProjectBgColumnService projectBgColumnService;
	
	@Autowired
	private ProjectBgEntryService projectBgEntryService;
	
	@Autowired
	private EdocTplService edocTplService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public ProjectBgColumn get(@RequestParam(required=false) String id) {
		ProjectBgColumn entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectBgColumnService.get(id);
		}
		if (entity == null){
			entity = new ProjectBgColumn();
		}
		return entity;
	}
	
	/**
	 * 项目预算行列表页面
	 */
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:list")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectBgColumn projectBgColumn, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/projectbg/bgcolumn/projectBgColumnList";
	}
	
	/**
	 * 项目预算行列表数据
	 */
	@ResponseBody
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ProjectBgColumn projectBgColumn, HttpServletRequest request, HttpServletResponse response, Model model) {
		projectBgColumn.getSqlMap().put("dsf", this.getDataScope());
		Page<ProjectBgColumn> page = projectBgColumnService.findPage(new Page<ProjectBgColumn>(request, response), projectBgColumn); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑项目预算行表单页面
	 */
	@RequiresPermissions(value={"projectbg:bgcolumn:projectBgColumn:view","projectbg:bgcolumn:projectBgColumn:add","projectbg:bgcolumn:projectBgColumn:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ProjectBgColumn projectBgColumn, Model model,HttpServletRequest request) {
		model.addAttribute("projectBgDate",projectBgEntryService.treeData());
		model.addAttribute("projectBgColumn", projectBgColumn);
		return "modules/projectbg/bgcolumn/projectBgColumnForm";
	}
	
	
	/**
	 * 获取预算数据
	 */
	@ResponseBody
	@RequestMapping(value = "getBgTreeDate")
	public List<Map<String, String>> getBgTreeDate() {
		return projectBgEntryService.treeData();
	}
	

	/**
	 * 保存项目预算行
	 */
	@ResponseBody
	@RequiresPermissions(value={"projectbg:bgcolumn:projectBgColumn:add","projectbg:bgcolumn:projectBgColumn:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ProjectBg projectBg, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, projectBg)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		System.out.println(projectBg.getColumnList().get(0).getBgSort());
		
		System.out.println("helleo");
		/*
		if(!columnlist.getIsNewRecord()){
			//修改保存
			ProjectBgColumn t = projectBgColumnService.get(columnlist.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) projectBgColumnService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(columnlist, t);
				projectBgColumnService.save(t);
			}
		}else{
			//新建保存
			columnlist.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			projectBgColumnService.save(columnlist);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存项目预算行信息成功","项目预算行"));
		//保存成功后处理逻辑
		this.afterSave("项目预算行", columnlist);*/
		return j;
	}
	
	/**
	 * 删除项目预算行
	 */
	@ResponseBody
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ProjectBgColumn projectBgColumn, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			projectBgColumn.setDelFlag(Global.YES);
			projectBgColumnService.saveV(projectBgColumn); 
		}
		projectBgColumnService.delete(projectBgColumn);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除项目预算行信息成功","项目预算行"));
		return j;
	}
	
	/**
	 * 批量删除项目预算行
	 */
	@ResponseBody
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ProjectBgColumn obj = projectBgColumnService.get(id);
				obj.setDelFlag(Global.YES);
				projectBgColumnService.saveV(obj); 
			}
			projectBgColumnService.delete(projectBgColumnService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除项目预算行信息成功","项目预算行"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ProjectBgColumn projectBgColumn, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "项目预算行"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ProjectBgColumn> page = projectBgColumnService.findPage(new Page<ProjectBgColumn>(request, response, -1), projectBgColumn);
    		new ExportExcel("项目预算行", ProjectBgColumn.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出项目预算行信息记录失败！", "项目预算行") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ProjectBgColumn> list = ei.getDataList(ProjectBgColumn.class);
			for (ProjectBgColumn projectBgColumn : list){
				try{
					projectBgColumnService.save(projectBgColumn);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条项目预算行记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条项目预算行记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入项目预算行失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/projectbg/bgcolumn/projectBgColumn/?repage";
    }
	
	/**
	 * 下载导入项目预算行数据模板
	 */
	@RequiresPermissions("projectbg:bgcolumn:projectBgColumn:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "项目预算行数据导入模板.xlsx";
    		List<ProjectBgColumn> list = Lists.newArrayList(); 
    		new ExportExcel("项目预算行数据", ProjectBgColumn.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/projectbg/bgcolumn/projectBgColumn/?repage";
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
	private void afterSave(String title, ProjectBgColumn projectBgColumn) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/projectbg/bgcolumn/projectBgColumn");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, projectBgColumn.getOwnerCode(), roleMap);
	}
	
}