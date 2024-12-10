/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.getuuid.web;

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
import com.gt_plus.common.utils.IdGen;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.getuuid.entity.GetUUID;
import com.gt_plus.modules.getuuid.service.GetUUIDService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 生成UUIDController
 * @author zdyt
 * @version 2018-01-30
 */
@Controller
@RequestMapping(value = "${adminPath}/getuuid/getUUID")
public class GetUUIDController extends BaseController {

	@Autowired
	private GetUUIDService getUUIDService;
	
	@Autowired
	private EdocTplService edocTplService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public GetUUID get(@RequestParam(required=false) String id) {
		GetUUID entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = getUUIDService.get(id);
		}
		if (entity == null){
			entity = new GetUUID();
		}
		return entity;
	}
	
	/**
	 * 生成UUID列表页面
	 */
	@RequiresPermissions("getuuid:getUUID:list")
	@RequestMapping(value = {"list", ""})
	public String list(GetUUID getUUID, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/getuuid/getUUIDList";
	}
	
	/**
	 * 生成UUID列表数据
	 */
	@ResponseBody
	@RequiresPermissions("getuuid:getUUID:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(GetUUID getUUID, HttpServletRequest request, HttpServletResponse response, Model model) {
		getUUID.getSqlMap().put("dsf", this.getDataScope());
		Page<GetUUID> page = getUUIDService.findPage(new Page<GetUUID>(request, response), getUUID); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑生成UUID表单页面
	 */
	@RequiresPermissions(value={"getuuid:getUUID:view","getuuid:getUUID:add","getuuid:getUUID:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(GetUUID getUUID, Model model,HttpServletRequest request) {
		model.addAttribute("getUUID", getUUID);
		return "modules/getuuid/getUUIDForm";
	}
	

	/**
	 * 保存生成UUID
	 */
	@ResponseBody
	@RequiresPermissions(value={"getuuid:getUUID:add","getuuid:getUUID:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(GetUUID getUUID, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		for(int i =0; i<getUUID.getNum();i++){
			System.out.println(IdGen.uuid());
		}
		return j;
	}
	
	/**
	 * 删除生成UUID
	 */
	@ResponseBody
	@RequiresPermissions("getuuid:getUUID:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(GetUUID getUUID, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			getUUID.setDelFlag(Global.YES);
			getUUIDService.saveV(getUUID); 
		}
		getUUIDService.delete(getUUID);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除生成UUID信息成功","生成UUID"));
		return j;
	}
	
	/**
	 * 批量删除生成UUID
	 */
	@ResponseBody
	@RequiresPermissions("getuuid:getUUID:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				GetUUID obj = getUUIDService.get(id);
				obj.setDelFlag(Global.YES);
				getUUIDService.saveV(obj); 
			}
			getUUIDService.delete(getUUIDService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除生成UUID信息成功","生成UUID"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("getuuid:getUUID:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(GetUUID getUUID, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "生成UUID"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<GetUUID> page = getUUIDService.findPage(new Page<GetUUID>(request, response, -1), getUUID);
    		new ExportExcel("生成UUID", GetUUID.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出生成UUID信息记录失败！", "生成UUID") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("getuuid:getUUID:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<GetUUID> list = ei.getDataList(GetUUID.class);
			for (GetUUID getUUID : list){
				try{
					getUUIDService.save(getUUID);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条生成UUID记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条生成UUID记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入生成UUID失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/getuuid/getUUID/?repage";
    }
	
	/**
	 * 下载导入生成UUID数据模板
	 */
	@RequiresPermissions("getuuid:getUUID:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "生成UUID数据导入模板.xlsx";
    		List<GetUUID> list = Lists.newArrayList(); 
    		new ExportExcel("生成UUID数据", GetUUID.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/getuuid/getUUID/?repage";
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
	private void afterSave(String title, GetUUID getUUID) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/getuuid/getUUID");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, getUUID.getOwnerCode(), roleMap);
	}
	
}