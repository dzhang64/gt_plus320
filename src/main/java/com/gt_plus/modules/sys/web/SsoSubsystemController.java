/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.google.common.collect.Maps;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
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
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sys.entity.SsoSubsystem;
import com.gt_plus.modules.sys.service.SsoSubsystemService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * SSO子系统Controller
 * @author GT0155
 * @version 2017-12-12
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/ssoSubsystem")
public class SsoSubsystemController extends BaseController {

	@Autowired
	private SsoSubsystemService ssoSubsystemService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public SsoSubsystem get(@RequestParam(required=false) String id) {
		SsoSubsystem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = ssoSubsystemService.get(id);
		}
		if (entity == null){
			entity = new SsoSubsystem();
		}
		return entity;
	}
	
	/**
	 * SSO子系统列表页面
	 */
	@RequiresPermissions("sys:ssoSubsystem:list")
	@RequestMapping(value = {"list", ""})
	public String list(SsoSubsystem ssoSubsystem, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/sys/ssoSubsystemList";
	}
	
	/**
	 * SSO子系统列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:ssoSubsystem:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(SsoSubsystem ssoSubsystem, HttpServletRequest request, HttpServletResponse response, Model model) {
		ssoSubsystem.getSqlMap().put("dsf", this.getDataScope());
		Page<SsoSubsystem> page = ssoSubsystemService.findPage(new Page<SsoSubsystem>(request, response), ssoSubsystem); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑SSO子系统表单页面
	 */
	@RequiresPermissions(value={"sys:ssoSubsystem:view","sys:ssoSubsystem:add","sys:ssoSubsystem:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SsoSubsystem ssoSubsystem, Model model) {
		model.addAttribute("ssoSubsystem", ssoSubsystem);
		return "modules/sys/ssoSubsystemForm";
	}

	/**
	 * 保存SSO子系统
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:ssoSubsystem:add","sys:ssoSubsystem:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SsoSubsystem ssoSubsystem, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, ssoSubsystem)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!ssoSubsystem.getIsNewRecord()){
			//修改保存
			SsoSubsystem t = ssoSubsystemService.get(ssoSubsystem.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) ssoSubsystemService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(ssoSubsystem, t);
				ssoSubsystemService.save(t);
			}
		}else{
			//新建保存
			ssoSubsystem.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			ssoSubsystemService.save(ssoSubsystem);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存SSO子系统信息成功","SSO子系统"));
		//保存成功后处理逻辑
		this.afterSave("SSO子系统", ssoSubsystem);
		return j;
	}
	
	/**
	 * 删除SSO子系统
	 */
	@ResponseBody
	@RequiresPermissions("sys:ssoSubsystem:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SsoSubsystem ssoSubsystem, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			ssoSubsystem.setDelFlag(Global.YES);
			ssoSubsystemService.saveV(ssoSubsystem); 
		}
		ssoSubsystemService.delete(ssoSubsystem);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除SSO子系统信息成功","SSO子系统"));
		return j;
	}
	
	/**
	 * 批量删除SSO子系统
	 */
	@ResponseBody
	@RequiresPermissions("sys:ssoSubsystem:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SsoSubsystem obj = ssoSubsystemService.get(id);
				obj.setDelFlag(Global.YES);
				ssoSubsystemService.saveV(obj); 
			}
			ssoSubsystemService.delete(ssoSubsystemService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除SSO子系统信息成功","SSO子系统"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:ssoSubsystem:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SsoSubsystem ssoSubsystem, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "SSO子系统"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SsoSubsystem> page = ssoSubsystemService.findPage(new Page<SsoSubsystem>(request, response, -1), ssoSubsystem);
    		new ExportExcel("SSO子系统", SsoSubsystem.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出SSO子系统信息记录失败！", "SSO子系统") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:ssoSubsystem:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SsoSubsystem> list = ei.getDataList(SsoSubsystem.class);
			for (SsoSubsystem ssoSubsystem : list){
				try{
					ssoSubsystemService.save(ssoSubsystem);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条SSO子系统记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条SSO子系统记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入SSO子系统失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/ssoSubsystem/?repage";
    }
	
	/**
	 * 下载导入SSO子系统数据模板
	 */
	@RequiresPermissions("sys:ssoSubsystem:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "SSO子系统数据导入模板.xlsx";
    		List<SsoSubsystem> list = Lists.newArrayList(); 
    		new ExportExcel("SSO子系统数据", SsoSubsystem.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/ssoSubsystem/?repage";
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
	private void afterSave(String title, SsoSubsystem ssoSubsystem) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sys/ssoSubsystem");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, ssoSubsystem.getOwnerCode(), roleMap);
	}
	
}