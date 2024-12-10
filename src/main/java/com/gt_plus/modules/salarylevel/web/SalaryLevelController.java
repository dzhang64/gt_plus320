/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.salarylevel.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

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
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.gt_plus.modules.salarylevel.service.SalaryLevelService;
/**
 * 薪资级别Controller
 * @author zdy
 * @version 2018-01-30
 */
@Controller
@RequestMapping(value = "${adminPath}/salarylevel/salaryLevel")
public class SalaryLevelController extends BaseController {

	@Autowired
	private SalaryLevelService salaryLevelService;
	
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public SalaryLevel get(@RequestParam(required=false) String id) {
		SalaryLevel entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = salaryLevelService.get(id);
		}
		if (entity == null){
			entity = new SalaryLevel();
		}
		return entity;
	}
	
	/**
	 * 薪资级别列表页面
	 */
	@RequiresPermissions("salarylevel:salaryLevel:list")
	@RequestMapping(value = {"list", ""})
	public String list(SalaryLevel salaryLevel, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/salarylevel/salaryLevelList";
	}
	
	/**
	 * 薪资级别列表数据
	 */
	@ResponseBody
	@RequiresPermissions("salarylevel:salaryLevel:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(SalaryLevel salaryLevel, HttpServletRequest request, HttpServletResponse response, Model model) {
		salaryLevel.getSqlMap().put("dsf", this.getDataScope());
		Page<SalaryLevel> page = salaryLevelService.findPage(new Page<SalaryLevel>(request, response), salaryLevel); 
		return getBootstrapData(page);
	}
	
	/**
	 * 选择薪资级别
	 */
	@RequestMapping(value = "selectsalary")
	public String selectsalary(SalaryLevel salary, String url, String fieldLabels, String fieldKeys, String fieldTypes, String filter,String searchLabel, String searchKey, HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			fieldLabels = URLDecoder.decode(fieldLabels, "UTF-8");
			fieldKeys = URLDecoder.decode(fieldKeys, "UTF-8");
			fieldTypes = URLDecoder.decode(fieldTypes, "UTF-8");
			filter = URLDecoder.decode(filter, "UTF-8");
			searchLabel = URLDecoder.decode(searchLabel, "UTF-8");
			searchKey = URLDecoder.decode(searchKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//过滤项，修改该位置的值,在XML文件需要设置过滤项的值
		/*if(filter!=null&&filter!=""&&filter.length()>0){
			
		}*/

		Page<SalaryLevel> page = salaryLevelService.findPageBysalary(new Page<SalaryLevel>(request, response),  salary);
		model.addAttribute("labelNames", fieldLabels.split("\\|"));
		model.addAttribute("labelValues", fieldKeys.split("\\|"));
		model.addAttribute("labelTypes", fieldTypes.split("\\|"));
		model.addAttribute("fieldLabels", fieldLabels);
		model.addAttribute("fieldKeys", fieldKeys);
		model.addAttribute("url", url);
		model.addAttribute("searchLabel", searchLabel);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("obj", salary);
		model.addAttribute("page", page);
		return "modules/sys/gridselect";
	}
	
	/**
	 * 查看，增加，编辑薪资级别表单页面
	 */
	@RequiresPermissions(value={"salarylevel:salaryLevel:view","salarylevel:salaryLevel:add","salarylevel:salaryLevel:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SalaryLevel salaryLevel, Model model) {
		model.addAttribute("salaryLevel", salaryLevel);
		return "modules/salarylevel/salaryLevelForm";
	}
	

	/**
	 * 保存薪资级别
	 */
	@ResponseBody
	@RequiresPermissions(value={"salarylevel:salaryLevel:add","salarylevel:salaryLevel:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SalaryLevel salaryLevel, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, salaryLevel)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!salaryLevel.getIsNewRecord()){
			//修改保存
			SalaryLevel t = salaryLevelService.get(salaryLevel.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) salaryLevelService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(salaryLevel, t);
				salaryLevelService.save(t);
			}
		}else{
			//新建保存
			salaryLevelService.save(salaryLevel);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存薪资级别信息成功","薪资级别"));
		//保存成功后处理逻辑
		this.afterSave("薪资级别", salaryLevel);
		return j;
	}
	
	/**
	 * 删除薪资级别
	 */
	@ResponseBody
	@RequiresPermissions("salarylevel:salaryLevel:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SalaryLevel salaryLevel, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			salaryLevel.setDelFlag(Global.YES);
			salaryLevelService.saveV(salaryLevel); 
		}
		salaryLevelService.delete(salaryLevel);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除薪资级别信息成功","薪资级别"));
		return j;
	}
	
	/**
	 * 批量删除薪资级别
	 */
	@ResponseBody
	@RequiresPermissions("salarylevel:salaryLevel:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SalaryLevel obj = salaryLevelService.get(id);
				obj.setDelFlag(Global.YES);
				salaryLevelService.saveV(obj); 
			}
			salaryLevelService.delete(salaryLevelService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除薪资级别信息成功","薪资级别"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("salarylevel:salaryLevel:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SalaryLevel salaryLevel, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "薪资级别"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SalaryLevel> page = salaryLevelService.findPage(new Page<SalaryLevel>(request, response, -1), salaryLevel);
    		new ExportExcel("薪资级别", SalaryLevel.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出薪资级别信息记录失败！", "薪资级别") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("salarylevel:salaryLevel:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SalaryLevel> list = ei.getDataList(SalaryLevel.class);
			for (SalaryLevel salaryLevel : list){
				try{
					salaryLevelService.save(salaryLevel);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条薪资级别记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条薪资级别记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入薪资级别失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/salarylevel/salaryLevel/?repage";
    }
	
	/**
	 * 下载导入薪资级别数据模板
	 */
	@RequiresPermissions("salarylevel:salaryLevel:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "薪资级别数据导入模板.xlsx";
    		List<SalaryLevel> list = Lists.newArrayList(); 
    		new ExportExcel("薪资级别数据", SalaryLevel.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/salarylevel/salaryLevel/?repage";
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
	private void afterSave(String title, SalaryLevel salaryLevel) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/salarylevel/salaryLevel");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, salaryLevel.getOwnerCode(), roleMap);
	}
	
}