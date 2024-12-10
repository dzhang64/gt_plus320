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
import com.gt_plus.modules.sys.entity.UserSsoSubsystem;
import com.gt_plus.modules.sys.service.UserSsoSubsystemService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 用户SSO子系统关系Controller
 * @author GT0155
 * @version 2017-12-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/userSsoSubsystem")
public class UserSsoSubsystemController extends BaseController {

	@Autowired
	private UserSsoSubsystemService userSsoSubsystemService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public UserSsoSubsystem get(@RequestParam(required=false) String id) {
		UserSsoSubsystem entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = userSsoSubsystemService.get(id);
		}
		if (entity == null){
			entity = new UserSsoSubsystem();
		}
		return entity;
	}
	
	/**
	 * 用户SSO子系统关系列表页面
	 */
	@RequiresPermissions("sys:userSsoSubsystem:list")
	@RequestMapping(value = {"list", ""})
	public String list(UserSsoSubsystem userSsoSubsystem, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/sys/userSsoSubsystemList";
	}
	
	/**
	 * 用户SSO子系统关系列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:userSsoSubsystem:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(UserSsoSubsystem userSsoSubsystem, HttpServletRequest request, HttpServletResponse response, Model model) {
		userSsoSubsystem.getSqlMap().put("dsf", this.getDataScope());
		Page<UserSsoSubsystem> page = userSsoSubsystemService.findPage(new Page<UserSsoSubsystem>(request, response), userSsoSubsystem); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑用户SSO子系统关系表单页面
	 */
	@RequiresPermissions(value={"sys:userSsoSubsystem:view","sys:userSsoSubsystem:add","sys:userSsoSubsystem:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(UserSsoSubsystem userSsoSubsystem, Model model) {
		model.addAttribute("userSsoSubsystem", userSsoSubsystem);
		return "modules/sys/userSsoSubsystemForm";
	}

	/**
	 * 保存用户SSO子系统关系
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:userSsoSubsystem:add","sys:userSsoSubsystem:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(UserSsoSubsystem userSsoSubsystem, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, userSsoSubsystem)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!userSsoSubsystem.getIsNewRecord()){
			//修改保存
			UserSsoSubsystem t = userSsoSubsystemService.get(userSsoSubsystem.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) userSsoSubsystemService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(userSsoSubsystem, t);
				userSsoSubsystemService.save(t);
			}
		}else{
			//新建保存
			userSsoSubsystem.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			userSsoSubsystemService.save(userSsoSubsystem);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存用户SSO子系统关系信息成功","用户SSO子系统关系"));
		//保存成功后处理逻辑
		this.afterSave("用户SSO子系统关系", userSsoSubsystem);
		return j;
	}
	
	/**
	 * 删除用户SSO子系统关系
	 */
	@ResponseBody
	@RequiresPermissions("sys:userSsoSubsystem:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(UserSsoSubsystem userSsoSubsystem, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			userSsoSubsystem.setDelFlag(Global.YES);
			userSsoSubsystemService.saveV(userSsoSubsystem); 
		}
		userSsoSubsystemService.delete(userSsoSubsystem);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除用户SSO子系统关系信息成功","用户SSO子系统关系"));
		return j;
	}
	
	/**
	 * 批量删除用户SSO子系统关系
	 */
	@ResponseBody
	@RequiresPermissions("sys:userSsoSubsystem:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				UserSsoSubsystem obj = userSsoSubsystemService.get(id);
				obj.setDelFlag(Global.YES);
				userSsoSubsystemService.saveV(obj); 
			}
			userSsoSubsystemService.delete(userSsoSubsystemService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除用户SSO子系统关系信息成功","用户SSO子系统关系"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:userSsoSubsystem:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(UserSsoSubsystem userSsoSubsystem, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "用户SSO子系统关系"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<UserSsoSubsystem> page = userSsoSubsystemService.findPage(new Page<UserSsoSubsystem>(request, response, -1), userSsoSubsystem);
    		new ExportExcel("用户SSO子系统关系", UserSsoSubsystem.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出用户SSO子系统关系信息记录失败！", "用户SSO子系统关系") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:userSsoSubsystem:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<UserSsoSubsystem> list = ei.getDataList(UserSsoSubsystem.class);
			for (UserSsoSubsystem userSsoSubsystem : list){
				try{
					userSsoSubsystemService.save(userSsoSubsystem);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户SSO子系统关系记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户SSO子系统关系记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户SSO子系统关系失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/userSsoSubsystem/?repage";
    }
	
	/**
	 * 下载导入用户SSO子系统关系数据模板
	 */
	@RequiresPermissions("sys:userSsoSubsystem:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户SSO子系统关系数据导入模板.xlsx";
    		List<UserSsoSubsystem> list = Lists.newArrayList(); 
    		new ExportExcel("用户SSO子系统关系数据", UserSsoSubsystem.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/userSsoSubsystem/?repage";
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
	private void afterSave(String title, UserSsoSubsystem userSsoSubsystem) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sys/userSsoSubsystem");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, userSsoSubsystem.getOwnerCode(), roleMap);
	}
	
}