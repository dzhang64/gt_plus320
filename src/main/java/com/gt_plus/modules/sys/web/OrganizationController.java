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
import com.gt_plus.common.utils.Collections3;
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
import com.gt_plus.modules.sys.entity.Organization;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OrganizationService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
/**
 * 组织管理Controller
 * @author LS0195
 * @version 2017-12-07
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/organization")
public class OrganizationController extends BaseController {

	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	

	@Autowired
	private SystemService systemService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public Organization get(@RequestParam(required=false) String id) {
		Organization entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = organizationService.get(id);
		}
		if (entity == null){
			entity = new Organization();
		}
		return entity;
	}
	
	/**
	 * 组织管理列表页面
	 */
	@RequiresPermissions("sys:organization:list")
	@RequestMapping(value = {"list", ""})
	public String list(Organization organization, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/sys/organizationList";
	}
	
	/**
	 * 组织管理列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:organization:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Organization organization, HttpServletRequest request, HttpServletResponse response, Model model) {
		organization.getSqlMap().put("dsf", this.getDataScope());
		Page<Organization> page = organizationService.findPage(new Page<Organization>(request, response), organization); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑组织管理表单页面
	 */
	@RequiresPermissions(value={"sys:organization:view","sys:organization:add","sys:organization:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Organization organization, Model model) {
		model.addAttribute("organization", organization);
		return "modules/sys/organizationForm";
	}

	/**
	 * 保存组织管理
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:organization:add","sys:organization:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Organization organization, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, organization)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!organization.getIsNewRecord()){
			//修改保存
			Organization t = organizationService.get(organization.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) organizationService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(organization, t);
				organizationService.save(t);
			}
		}else{
			//新建保存
			organization.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			organizationService.save(organization);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存组织管理信息成功","组织管理"));
		//保存成功后处理逻辑
		this.afterSave("组织管理", organization);
		return j;
	}
	
	/**
	 * 删除组织管理
	 */
	@ResponseBody
	@RequiresPermissions("sys:organization:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Organization organization, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			organization.setDelFlag(Global.YES);
			organizationService.saveV(organization); 
		}
		organizationService.delete(organization);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除组织管理信息成功","组织管理"));
		return j;
	}
	
	/**
	 * 批量删除组织管理
	 */
	@ResponseBody
	@RequiresPermissions("sys:organization:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Organization obj = organizationService.get(id);
				obj.setDelFlag(Global.YES);
				organizationService.saveV(obj); 
			}
			organizationService.delete(organizationService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除组织管理信息成功","组织管理"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:organization:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Organization organization, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "组织管理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Organization> page = organizationService.findPage(new Page<Organization>(request, response, -1), organization);
    		new ExportExcel("组织管理", Organization.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出组织管理信息记录失败！", "组织管理") + "失败信息：" + e.getMessage());
		}
		return j;
    }
	
	/**
	 * 成员分配页面
	 * @param role
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:organization:list")
	@RequestMapping(value = "assign")
	public String assign(Organization organization, Model model) {
		List<User> userList=organizationService.findUserToOrg(organization.getId());
		/*List<User> userList = systemService.findUser(new User(new Role("")));*/
		//model.addAttribute("organization",organization);
		model.addAttribute("userList", userList);
		return "modules/sys/organicationAssign";
	}
	
	
	/**
	 * 角色分配 -- 打开角色分配对话框
	 * @param role
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:organization:list")
	@RequestMapping(value = "usertoorg")
	public String selectUserToOrg(Organization organization,Model model) {
		List<User> usertoorgList=organizationService.findUserToOrg(organization.getId());
		model.addAttribute("usertoorgList", usertoorgList);
		List<User> userList = systemService.findUser(new User(new Role("")));
		model.addAttribute("userList", userList);
		model.addAttribute("selectIds", Collections3.extractToString(usertoorgList, "name", ","));
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/selectUserToOrg";
	}
	
	/**
	 * 成员分配
	 * @param organization
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:organization:list")
	@RequestMapping(value = "assignorg")
	public String assignUser(Organization organization, String[] idsArr,RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/organization/assign?id="+organization.getId();
		}
		for (int i = 0; i < idsArr.length; i++) {
			User user=systemService.getUser(idsArr[i]);
			organizationService.insertUserToOrg(organization,user);
		}
		return "redirect:" + adminPath + "/sys/organization/assign?id="+organization.getId();
	}
	
	/**
	 * 删除成员
	 * @param userId
	 * @param orgId
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:organization:list")
	@RequestMapping(value = "outuser")
	public String outUser(String userId, String orgId, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id="+orgId;
		}
		organizationService.deleteUserToOrg(userId,orgId);
		return "redirect:" + adminPath + "/sys/organization/assign?id="+orgId;
	}
	
	/**
	 * 验证组织编号
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:organization:add","sys:organization:edit"},logical=Logical.OR)
	@RequestMapping(value = "checkorgNumber")
	public String checkOrgNumber(String oldorgNumber,String orgNumber) {
		int orgList=organizationService.findOrgNumberBy(orgNumber);
		if(orgNumber != null && orgList == 0){
			return "true";
		}else if(orgNumber !=null && orgNumber.equals(oldorgNumber)){
			return "true";
		}
		return "false";
	}

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:organization:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Organization> list = ei.getDataList(Organization.class);
			for (Organization organization : list){
				try{
					organizationService.save(organization);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条组织管理记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条组织管理记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入组织管理失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/organization/?repage";
    }
	
	/**
	 * 下载导入组织管理数据模板
	 */
	@RequiresPermissions("sys:organization:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "组织管理数据导入模板.xlsx";
    		List<Organization> list = Lists.newArrayList(); 
    		new ExportExcel("组织管理数据", Organization.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/organization/?repage";
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
	private void afterSave(String title, Organization organization) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sys/organization");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, organization.getOwnerCode(), roleMap);
	}
	
}