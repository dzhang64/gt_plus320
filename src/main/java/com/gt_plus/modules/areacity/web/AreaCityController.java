/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.areacity.web;

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
import com.gt_plus.modules.areacity.entity.AreaCity;
import com.gt_plus.modules.areacity.service.AreaCityService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 省地市Controller
 * @author wl
 * @version 2018-01-15
 */
@Controller
@RequestMapping(value = "${adminPath}/areacity/areaCity")
public class AreaCityController extends BaseController {

	@Autowired
	private AreaCityService areaCityService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public AreaCity get(@RequestParam(required=false) String id) {
		AreaCity entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = areaCityService.get(id);
		}
		if (entity == null){
			entity = new AreaCity();
		}
		return entity;
	}
	
	/**
	 * 省地市列表页面
	 */
	@RequiresPermissions("areacity:areaCity:list")
	@RequestMapping(value = {"list", ""})
	public String list(AreaCity areaCity, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/areacity/areaCityList";
	}
	
	/**
	 * 省地市列表数据
	 */
	@ResponseBody
	@RequiresPermissions("areacity:areaCity:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(AreaCity areaCity, HttpServletRequest request, HttpServletResponse response, Model model) {
		areaCity.getSqlMap().put("dsf", this.getDataScope());
		Page<AreaCity> page = areaCityService.findPage(new Page<AreaCity>(request, response), areaCity); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑省地市表单页面
	 */
	@RequiresPermissions(value={"areacity:areaCity:view","areacity:areaCity:add","areacity:areaCity:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(AreaCity areaCity, Model model) {
		model.addAttribute("areaCity", areaCity);
		return "modules/areacity/areaCityForm";
	}
	

	/**
	 * 保存省地市
	 */
	@ResponseBody
	@RequiresPermissions(value={"areacity:areaCity:add","areacity:areaCity:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(AreaCity areaCity, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, areaCity)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!areaCity.getIsNewRecord()){
			//修改保存
			AreaCity t = areaCityService.get(areaCity.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) areaCityService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(areaCity, t);
				areaCityService.save(t);
			}
		}else{
			//新建保存
			areaCity.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			areaCityService.save(areaCity);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存省地市信息成功","省地市"));
		//保存成功后处理逻辑
		this.afterSave("省地市", areaCity);
		return j;
	}
	
	/**
	 * 删除省地市
	 */
	@ResponseBody
	@RequiresPermissions("areacity:areaCity:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(AreaCity areaCity, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			areaCity.setDelFlag(Global.YES);
			areaCityService.saveV(areaCity); 
		}
		areaCityService.delete(areaCity);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除省地市信息成功","省地市"));
		return j;
	}
	
	/**
	 * 批量删除省地市
	 */
	@ResponseBody
	@RequiresPermissions("areacity:areaCity:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				AreaCity obj = areaCityService.get(id);
				obj.setDelFlag(Global.YES);
				areaCityService.saveV(obj); 
			}
			areaCityService.delete(areaCityService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除省地市信息成功","省地市"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("areacity:areaCity:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(AreaCity areaCity, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "省地市"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<AreaCity> page = areaCityService.findPage(new Page<AreaCity>(request, response, -1), areaCity);
    		new ExportExcel("省地市", AreaCity.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出省地市信息记录失败！", "省地市") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("areacity:areaCity:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<AreaCity> list = ei.getDataList(AreaCity.class);
			for (AreaCity areaCity : list){
				try{
					areaCityService.save(areaCity);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条省地市记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条省地市记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入省地市失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/areacity/areaCity/?repage";
    }
	
	/**
	 * 下载导入省地市数据模板
	 */
	@RequiresPermissions("areacity:areaCity:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "省地市数据导入模板.xlsx";
    		List<AreaCity> list = Lists.newArrayList(); 
    		new ExportExcel("省地市数据", AreaCity.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/areacity/areaCity/?repage";
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
	private void afterSave(String title, AreaCity areaCity) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/areacity/areaCity");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, areaCity.getOwnerCode(), roleMap);
	}
	
}