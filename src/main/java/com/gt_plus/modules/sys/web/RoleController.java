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
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.Collections3;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.SubSystem;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SubSystemService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 角色Controller
 * @author gt_plus
 * @version 2013-12-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleController extends BaseController {
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private SubSystemService subSystemService;
	
	@ModelAttribute("role")
	public Role get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getRole(id);
		}else{
			return new Role();
		}
	}
	
	@RequiresPermissions("sys:role:list")
	@RequestMapping(value = {"list", ""})
	public String list(Role role, Model model) {
		//List<Role> list = systemService.findRole(role);
		//改为ajax模式
		//List<Role> list = systemService.findRoleForAdmin(role);
		//model.addAttribute("list", list);
		model.addAttribute("subSystemList", subSystemService.findList(new SubSystem()));
		return "modules/sys/roleList";
	}
	
	/**
	 * 角色列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:role:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Role role, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Role> page = systemService.findRoleForAdminPage(new Page<Role>(request, response), role); 
		return getBootstrapData(page);
	}

	@RequiresPermissions(value={"sys:role:view","sys:role:add","sys:role:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Role role, Model model) {
		if (role.getOffice()==null){
			role.setOffice(UserUtils.getUser().getOffice());
		}
		model.addAttribute("role", role);
		model.addAttribute("menuList", systemService.findAllMenu());
		model.addAttribute("officeList", officeService.findAll());
		model.addAttribute("subSystemList", subSystemService.findList(new SubSystem()));
		return "modules/sys/roleForm";
	}
	
	@RequiresPermissions("sys:role:auth")
	@RequestMapping(value = "auth")
	public String auth(Role role, Model model) {
		if (role.getOffice()==null){
			role.setOffice(UserUtils.getUser().getOffice());
		}
		model.addAttribute("role", role);
		model.addAttribute("menuList", systemService.findAllMenuForPermission());
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/roleAuth";
	}
	
	@ResponseBody
	@RequiresPermissions(value={"sys:role:assign","sys:role:auth","sys:role:add","sys:role:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Role role, Model model, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		/*
		if(!UserUtils.getUser().isAdmin()&&role.getSysData().equals(Global.YES)){
			j.setSuccess(false);
			j.setMsg("越权操作，只有超级管理员才能修改此数据！");
		} else if (Global.isDemoMode()){
			j.setSuccess(false);
			j.setMsg("演示模式，不允许操作！");
		}*/
		
		//if (!beanValidator(model, role)){
		//	return list(role, model);
		//}
		if (Global.isDemoMode()){
			j.setSuccess(false);
			j.setMsg("演示模式，不允许操作！");
		}
		else if (!"true".equals(checkName(role.getOldName(), role.getName()))){
			j.setSuccess(false);
			j.setMsg("保存角色'" + role.getName() + "'失败, 角色名已存在！");
		} else if (!"true".equals(checkEnname(role.getOldEnname(), role.getEnname()))){
			j.setSuccess(false);
			j.setMsg("保存角色'" + role.getName() + "'失败, 英文名已存在！");
		} else {
			systemService.saveRole(role);
			j.setSuccess(true);
			j.setMsg("保存角色'" + role.getName() + "'成功！");
		}
		return j;
	}
	
	@ResponseBody
	@RequiresPermissions("sys:role:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Role role, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		/*
		if(!UserUtils.getUser().isAdmin() && role.getSysData().equals(Global.YES)){
			j.setSuccess(false);
			j.setMsg("越权操作，只有超级管理员才能修改此数据！");
		} else 
		*/
		if(Global.isDemoMode()){
			j.setSuccess(false);
			j.setMsg("演示模式，不允许操作！");
		} else {
			systemService.deleteRole(role);
			//addMessage(redirectAttributes, "删除角色成功");
			j.setSuccess(true);
			j.setMsg("删除角色成功！");
		}
		return j;
	}
	
	/**
	 * 批量删除角色
	 */
	@ResponseBody
	@RequiresPermissions("sys:role:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		if(Global.isDemoMode()){
			//addMessage(redirectAttributes, "演示模式，不允许操作！");
			//return "redirect:" + adminPath + "/sys/role/?repage";
			j.setSuccess(false);
			j.setMsg("演示模式，不允许操作！");
		} else {
			j.setSuccess(true);
			String idArray[] =ids.split(",");
			StringBuffer msg = new StringBuffer();
			for(String id : idArray){
				Role role = systemService.getRole(id);
				/*
				if(!UserUtils.getUser().isAdmin() && role.getSysData().equals(Global.YES)){
					msg.append( "越权操作，只有超级管理员才能修改["+role.getName()+"]数据！<br/>");
					j.setSuccess(false);
					break;
				}else{
					systemService.deleteRole(role);
					msg.append( "删除角色["+role.getName()+"]成功<br/>");
				}*/
				systemService.deleteRole(role);
				msg.append( "删除角色["+role.getName()+"]成功<br/>");
			}
			//addMessage(redirectAttributes, msg.toString());
			j.setMsg(msg.toString());
		}
		//return "redirect:" + adminPath + "/sys/role/?repage";
		return j;
	}
	
	/**
	 * 角色分配页面
	 * @param role
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@RequestMapping(value = "assign")
	public String assign(Role role, Model model) {
		List<User> userList = systemService.findUser(new User(new Role(role.getId())));
		model.addAttribute("userList", userList);
		return "modules/sys/roleAssign";
	}
	
	/**
	 * 角色分配 -- 打开角色分配对话框
	 * @param role
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@RequestMapping(value = "usertorole")
	public String selectUserToRole(Role role, Model model) {
		List<User> userList = systemService.findUser(new User(new Role(role.getId())));
		model.addAttribute("role", role);
		model.addAttribute("userList", userList);
		model.addAttribute("selectIds", Collections3.extractToString(userList, "name", ","));
		model.addAttribute("officeList", officeService.findAll());
		return "modules/sys/selectUserToRole";
	}
	
	/**
	 * 角色分配 -- 根据部门编号获取用户列表
	 * @param officeId
	 * @param response
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@ResponseBody
	@RequestMapping(value = "users")
	public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		User user = new User();
		user.setOffice(new Office(officeId));
		Page<User> page = systemService.findUser(new Page<User>(1, -1), user);
		for (User e : page.getList()) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", 0);
			map.put("name", e.getName());
			mapList.add(map);			
		}
		return mapList;
	}
	
	/**
	 * 角色分配 -- 从角色中移除用户
	 * @param userId
	 * @param roleId
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@RequestMapping(value = "outrole")
	public String outrole(String userId, String roleId, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id="+roleId;
		}
		Role role = systemService.getRole(roleId);
		User user = systemService.getUser(userId);
		if (UserUtils.getUser().getId().equals(userId)) {
			addMessage(redirectAttributes, "无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		}else {
			if (user.getRoleList().size() <= 1){
				addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
			}else{
				Boolean flag = systemService.outUserInRole(role, user);
				if (!flag) {
					addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
				}else {
					addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
				}
			}		
		}
		return "redirect:" + adminPath + "/sys/role/assign?id="+role.getId();
	}
	
	/**
	 * 角色分配
	 * @param role
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:role:assign")
	@RequestMapping(value = "assignrole")
	public String assignRole(Role role, String[] idsArr, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id="+role.getId();
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < idsArr.length; i++) {
			User user = systemService.assignUserToRole(role, systemService.getUser(idsArr[i]));
			if (null != user) {
				msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
				newNum++;
			}
		}
		addMessage(redirectAttributes, "已成功分配 "+newNum+" 个用户"+msg);
		return "redirect:" + adminPath + "/sys/role/assign?id="+role.getId();
	}

	/**
	 * 验证角色名是否有效
	 * @param oldName
	 * @param name
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "checkName")
	public String checkName(String oldName, String name) {
		if (name!=null && name.equals(oldName)) {
			return "true";
		} else if (name!=null && systemService.getRoleByName(name) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 验证角色英文名是否有效
	 * @param oldName
	 * @param name
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "checkEnname")
	public String checkEnname(String oldEnname, String enname) {
		if (enname!=null && enname.equals(oldEnname)) {
			return "true";
		} else if (enname!=null && systemService.getRoleByEnname(enname) == null) {
			return "true";
		}
		return "false";
	}

}
