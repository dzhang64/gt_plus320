/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

import java.io.File;
import java.io.IOException;
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
import com.google.common.collect.Maps;
import com.gt_plus.common.beanvalidator.BeanValidators;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.FileUtils;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.sys.dao.UserDao;
import com.gt_plus.modules.sys.entity.Level;
import com.gt_plus.modules.sys.entity.Menu;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.SystemConfig;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.entity.UserSsoSubsystem;
import com.gt_plus.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.gt_plus.modules.sys.service.LevelService;
import com.gt_plus.modules.sys.service.PostService;
import com.gt_plus.modules.sys.service.SystemConfigService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.tools.utils.TwoDimensionCode;

/**
 * 用户Controller
 * @author gt_plus
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemConfigService systemConfigService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private LevelService levelService;
	
	@Autowired
	private PostService postService;
	
	@ModelAttribute
	public User get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return systemService.getUser(id);
		}else{
			return new User();
		}
	}

	@RequiresPermissions("sys:user:index")
	@RequestMapping(value = {"index"})
	public String index(User user, Model model) {
		return "modules/sys/userIndex";
	}
	
	@RequiresPermissions("sys:user:index")
	@RequestMapping(value = "userSelect")
	public String userSelect(boolean isMultiSelect, Model model) {
		model.addAttribute("isMultiSelect", isMultiSelect);
		return "modules/common/userSelect";
	}

	@RequiresPermissions("sys:user:index")
	@RequestMapping(value = {"list", ""})
	public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		//Page<User> page = systemService.findUser(new Page<User>(request, response), user);
        //model.addAttribute("page", page);
		return "modules/sys/userList";
	}

	/**
	 * 用户列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:index")
	@RequestMapping(value = "data")
	public Map<String, Object> data(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user); 
		Map<String, Object> map = getBootstrapData(page);
		return map;
	}
	
	@RequiresPermissions(value={"sys:user:view","sys:user:add","sys:user:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(User user, Model model) {
		if (user.getCompany()==null || user.getCompany().getId()==null){
			user.setCompany(UserUtils.getUser().getCompany());
		}
		if (user.getOffice()==null || user.getOffice().getId()==null){
			user.setOffice(UserUtils.getUser().getOffice());
		}
		model.addAttribute("user", user);
		model.addAttribute("allRoles", systemService.findAllRole());
		model.addAttribute("levelList", levelService.findList(new Level()));
		model.addAttribute("postList", postService.findListByUser(user));
		return "modules/sys/userForm";
	}
	
	@RequiresPermissions(value={"sys:user:view","sys:user:add","sys:user:edit"},logical=Logical.OR)
	@RequestMapping(value = "formSso")
	public String formSso(User user, Model model) {
		if (user.getCompany()==null || user.getCompany().getId()==null){
			user.setCompany(UserUtils.getUser().getCompany());
		}
		if (user.getOffice()==null || user.getOffice().getId()==null){
			user.setOffice(UserUtils.getUser().getOffice());
		}
		List<UserSsoSubsystem> ssolist = systemService.getUserSso(user);
		List<UserSsoSubsystem> userSsolist = systemService.getUserSsoList(user);
		for (UserSsoSubsystem userSsoSubsystem : userSsolist) {
			userSsoSubsystem.setPassword(SystemService.decryptDesPassword(userSsoSubsystem.getPassword()));
		}
		user.setUserSsoSubsystemList(userSsolist);
		model.addAttribute("user", user);
		model.addAttribute("ssolist", ssolist);
		/*model.addAttribute("allRoles", systemService.findAllRole());
		model.addAttribute("levelList", levelService.findList(new Level()));
		model.addAttribute("postList", postService.findListByUser(user));*/
		return "modules/sys/userFormSso";
	}

	@ResponseBody
	@RequiresPermissions(value={"sys:user:add","sys:user:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		
		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		user.setCompany(new Office(request.getParameter("company.id")));
		user.setOffice(new Office(request.getParameter("office.id")));
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
			user.setDesPassword(SystemService.entryptDesPassword(user.getNewPassword()));
		}
		
		// 如果SSO密码为空，则不更换SSO密码
		if (StringUtils.isNotBlank(user.getNewSsoDesPassword())) {
			user.setSsoDesPassword(SystemService.entryptDesPassword(user.getNewSsoDesPassword()));
		}
		
		
		if(Global.isDemoMode()){
			//addMessage(redirectAttributes, "演示模式，不允许操作！");
			//return "redirect:" + adminPath + "/sys/user/list?repage";
			j.setSuccess(false);
			j.setMsg("演示模式，不允许操作！");
		} else if (!beanValidator(model, user)){
			//return form(user, model);
			j.setSuccess(false);
			j.setMsg("保存失败，请检查输入项");
		} else if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))){
			//addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			//return form(user, model);
			j.setSuccess(false);
			j.setMsg("保存用户“" + user.getLoginName() + "”失败，登录名已存在");
		} else {
			// 角色数据有效性验证，过滤不在授权内的角色
			List<Role> roleList = Lists.newArrayList();
			List<String> roleIdList = user.getRoleIdList();
			for (Role r : systemService.findAllRole()){
				if (roleIdList.contains(r.getId())){
					roleList.add(r);
				}
			}
			user.setRoleList(roleList);
			//生成用户二维码，使用登录名
			String realPath = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL
			+ user.getId() + "/qrcode/";
			FileUtils.createDirectory(realPath);
			String name= user.getId()+".png"; //encoderImgId此处二维码的图片名
			String filePath = realPath + name;  //存放路径
			TwoDimensionCode.encoderQRCode(user.getLoginName(), filePath, "png");//执行生成二维码
			user.setQrCode(request.getContextPath()+Global.USERFILES_BASE_URL
				+  user.getId()  + "/qrcode/"+name);
			// 保存用户信息
			systemService.saveUser(user);
			// 清除当前用户缓存
			if (user.getLoginName().equals(UserUtils.getUser().getLoginName())){
				UserUtils.clearCache();
				//UserUtils.getCacheMap().clear();
			}
			//addMessage(redirectAttributes, "保存用户'" + user.getLoginName() + "'成功");
			//return "redirect:" + adminPath + "/sys/user/list?repage";
			j.setSuccess(true);
			j.setMsg("保存用户“" + user.getLoginName() + "”成功");
		}
		return j;
	}
	
	@ResponseBody
	@RequiresPermissions("sys:user:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(User user, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		if(Global.isDemoMode()){
			//addMessage(redirectAttributes, "演示模式，不允许操作！");
			//return "redirect:" + adminPath + "/sys/user/list?repage";
			j.setSuccess(false);
			j.setMsg("演示模式，不允许操作！");
		} else if (UserUtils.getUser().getId().equals(user.getId())){
			//addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
			j.setSuccess(false);
			j.setMsg("删除用户失败, 不允许删除当前用户");
		} else if (User.isAdmin(user.getId())){
			//addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
			j.setSuccess(false);
			j.setMsg("删除用户失败, 不允许删除超级管理员用户");
		} else {
			systemService.deleteUser(user);
			//addMessage(redirectAttributes, "删除用户成功");
			j.setSuccess(true);
			j.setMsg("删除用户成功");
		}
		//return "redirect:" + adminPath + "/sys/user/list?repage";
		return j;
	}
	
	/**
	 * 批量删除用户
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			User user = systemService.getUser(id);
			if(Global.isDemoMode()){
				//addMessage(redirectAttributes, "演示模式，不允许操作！");
				//return "redirect:" + adminPath + "/sys/user/list?repage";
				j.setSuccess(false);
				j.setMsg("演示模式，不允许操作！");
			} else if (UserUtils.getUser().getId().equals(user.getId())){
				//addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
				j.setSuccess(false);
				j.setMsg("删除用户失败, 不允许删除当前用户");
			}else if (User.isAdmin(user.getId())){
				//addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
				j.setSuccess(false);
				j.setMsg("删除用户失败, 不允许删除超级管理员用户");
			}else{
				systemService.deleteUser(user);
				//addMessage(redirectAttributes, "删除用户成功");
				j.setSuccess(true);
				j.setMsg("删除用户成功");
			}
		}
		//return "redirect:" + adminPath + "/sys/user/list?repage";
		return j;
	}
	
	/**
	 * 导出用户数据
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
    		new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }

	/**
	 * 导入用户数据
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		if(Global.isDemoMode()){
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list){
				try{
					if ("true".equals(checkLoginName("", user.getLoginName()))){
						user.setPassword(SystemService.entryptPassword("123456"));
						BeanValidators.validateWithException(validator, user);
						systemService.saveUser(user);
						successNum++;
					}else{
						failureMsg.append("<br/>登录名 "+user.getLoginName()+" 已存在; ");
						failureNum++;
					}
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>登录名 "+user.getLoginName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }
	
	/**
	 * 下载导入用户数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "用户数据导入模板.xlsx";
    		List<User> list = Lists.newArrayList(); list.add(UserUtils.getUser());
    		new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
    }

	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:user:add","sys:user:edit"},logical=Logical.OR)
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 用户信息显示
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfo";
	}
	
	/**
	 * 用户信息显示编辑保存
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "infoEdit")
	public String infoEdit(User user, boolean __ajax, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			if(user.getName() !=null )
				currentUser.setName(user.getName());
			if(user.getEmail() !=null )
				currentUser.setEmail(user.getEmail());
			if(user.getPhone() !=null )
				currentUser.setPhone(user.getPhone());
			if(user.getMobile() !=null )
				currentUser.setMobile(user.getMobile());
			if(user.getRemarks() !=null )
				currentUser.setRemarks(user.getRemarks());
//			if(user.getPhoto() !=null )
//				currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			if(__ajax){//手机访问
				AjaxJson j = new AjaxJson();
				j.setSuccess(true);
				j.setMsg("修改个人资料成功!");
				return renderString(response, j);
			}
			model.addAttribute("user", currentUser);
			model.addAttribute("Global", new Global());
			model.addAttribute("message", "保存用户信息成功");
			return "modules/sys/userInfo";
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfoEdit";
	}

	
	/**
	 * 用户头像显示编辑保存
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "imageEdit")
	public String imageEdit(User user, boolean __ajax, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			if(user.getPhoto() !=null )
				currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			if(__ajax){//手机访问
				AjaxJson j = new AjaxJson();
				j.setSuccess(true);
				j.setMsg("修改个人头像成功!");
				return renderString(response, j);
			}
			model.addAttribute("message", "保存用户信息成功");
			return "modules/sys/userInfo";
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userImageEdit";
	}
	/**
	 * 用户头像显示编辑保存
	 * @param user
	 * @param model
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "imageUpload")
	public String imageUpload( HttpServletRequest request, HttpServletResponse response,MultipartFile file) throws IllegalStateException, IOException {
		User currentUser = UserUtils.getUser();
		
		// 判断文件是否为空  
        if (!file.isEmpty()) {  
                // 文件保存路径  
            	String realPath = Global.USERFILES_BASE_URL
        		+ UserUtils.getPrincipal() + "/images/" ;
                // 转存文件  
            	FileUtils.createDirectory(Global.getUserfilesBaseDir()+realPath);
            	file.transferTo(new File( Global.getUserfilesBaseDir() +realPath +  file.getOriginalFilename()));  
            	currentUser.setPhoto(request.getContextPath()+realPath + file.getOriginalFilename());
    			systemService.updateUserInfo(currentUser);
        }  

		return "modules/sys/userImageEdit";
	}

	/**
	 * 返回用户信息
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public AjaxJson infoData() {
		AjaxJson j = new AjaxJson();
		j.setSuccess(true);
		j.setErrorCode("-1");
		j.setMsg("获取个人信息成功!");
		j.put("data", UserUtils.getUser());
		return j;
	}
	
	
	
	/**
	 * 修改个人用户密码
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			if(Global.isDemoMode()){
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			}else{
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
			return "modules/sys/userInfo";
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}
	
	/**
	 * 保存签名
	 */
	@ResponseBody
	@RequestMapping(value = "saveSign")
	public AjaxJson saveSign(User user, boolean __ajax, HttpServletResponse response, Model model) throws Exception{
		AjaxJson j = new AjaxJson();
		User currentUser = UserUtils.getUser();
		currentUser.setSign(user.getSign());
		systemService.updateUserInfo(currentUser);
		j.setMsg("设置签名成功");
		return j;
	}
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(officeId);
		for (int i=0; i<list.size(); i++){
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}
    
	/**
	 * web端ajax验证用户名是否可用
	 * @param loginName
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "validateLoginName")
	public boolean validateLoginName(String loginName, HttpServletResponse response) {
		
	    User user =  userDao.findUniqueByProperty("login_name", loginName);
	    if(user == null){
	    	return true;
	    }else{
		    return false;
	    }
	    
	}
	
	/**
	 * web端ajax验证手机号是否可以注册（数据库中不存在）
	 */
	@ResponseBody
	@RequestMapping(value = "validateMobile")
	public boolean validateMobile(String mobile, HttpServletResponse response, Model model) {
		  User user =  userDao.findUniqueByProperty("mobile", mobile);
		    if(user == null){
		    	return true;
		    }else{
			    return false;
		    }
	}
	
	/**
	 * web端ajax验证手机号是否已经注册（数据库中已存在）
	 */
	@ResponseBody
	@RequestMapping(value = "validateMobileExist")
	public boolean validateMobileExist(String mobile, HttpServletResponse response, Model model) {
		  User user =  userDao.findUniqueByProperty("mobile", mobile);
		    if(user != null){
		    	return true;
		    }else{
			    return false;
		    }
	}
	
	@ResponseBody
	@RequestMapping(value = "resetPassword")
	public AjaxJson resetPassword(String mobile, HttpServletResponse response, Model model) {
		SystemConfig config = systemConfigService.get("1");//获取短信配置的用户名和密码
		AjaxJson j = new AjaxJson();
		if(userDao.findUniqueByProperty("mobile", mobile) == null){
			j.setSuccess(false);
			j.setMsg("手机号不存在!");
			j.setErrorCode("1");
			return j;
		}
		User user =  userDao.findUniqueByProperty("mobile", mobile);
		String newPassword = String.valueOf((int) (Math.random() * 900000 + 100000));
		try {
			String result = UserUtils.sendPass(config.getSmsName(), config.getSmsPassword(), mobile, newPassword);
			if (!result.equals("100")) {
				j.setSuccess(false);
				j.setErrorCode("2");
				j.setMsg("短信发送失败，密码重置失败，错误代码："+result+"，请联系管理员。");
			}else{
				j.setSuccess(true);
				j.setErrorCode("-1");
				j.setMsg("短信发送成功，密码重置成功!");
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
			}
		} catch (IOException e) {
			j.setSuccess(false);
			j.setErrorCode("3");
			j.setMsg("因未知原因导致短信发送失败，请联系管理员。");
		}
		return j;
	}
	
	/**
	 * 根据用户编码返回用户登陆信息
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoNo")
	public AjaxJson infoNo(String no) {
		AjaxJson j = new AjaxJson();
		Principal principal = UserUtils.getPrincipal();
		User user = userDao.getUserByNo(no);
		if (principal == null) {
			j.setSuccess(false);
			j.setErrorCode("0");
			j.setMsg("没有登录!");
		} else if (user == null) {
			j.setSuccess(false);
			j.setErrorCode("3");
			j.setMsg("获取用户信息失败!");
		} else {
			j.setSuccess(true);
			j.setErrorCode("-1");
			j.setMsg("获取用户信息成功!");
			//j.put("data", UserUtils.getUser());
			j.put("login_name", user.getLoginName());
			j.put("des_password", user.getDesPassword());
			j.put("no", user.getNo());
			j.put("name", user.getName());
		}
		return j;
	}
	
	/**
	 * 根据用户编码返回用户登陆信息
	 * @return
	 */
	//@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoNoSimple")
	public String infoNoSimple(String no) {
		String userInfo = "";
		//Principal principal = UserUtils.getPrincipal();
		User user = userDao.getUserByNo(no);
		/*
		if (principal == null) {
			userInfo = "没有登录！";
		} else if (user == null) {
			userInfo = "获取用户信息失败！";
		} else {
			userInfo = user.getLoginName() + "," + user.getDesPassword();
		}*/
		userInfo = user.getLoginName() + "," + user.getDesPassword();
		return userInfo;
	}
	
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyShortcut")
	public String modifyShortcut(Model model) {
		
		return "modules/sys/modifyShortcut";
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "getShortcutData")
	public AjaxJson getShortcutData() {
		AjaxJson j = new AjaxJson();
		j.put("hrefMenuList", UserUtils.getHrefMenuList(UserUtils.getUserShortcutString()));
		//j.put("userShortcutString", UserUtils.getUserShortcutString());
		j.setSuccess(true);
		return j;
	}
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "saveShortcut")
	public AjaxJson saveShortcut(String userShortcutString, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		boolean isOK = UserUtils.saveUserShortcutString(userShortcutString);
		if (isOK) {
			j.setSuccess(true);
			j.setMsg("保存快捷设置成功");
		} else {
			j.setSuccess(false);
			j.setMsg("保存快捷设置失败");
		}
		return j;
	}
	
//	@InitBinder
//	public void initBinder(WebDataBinder b) {
//		b.registerCustomEditor(List.class, "roleList", new PropertyEditorSupport(){
//			@Autowired
//			private SystemService systemService;
//			@Override
//			public void setAsText(String text) throws IllegalArgumentException {
//				String[] ids = StringUtils.split(text, ",");
//				List<Role> roles = new ArrayList<Role>();
//				for (String id : ids) {
//					Role role = systemService.getRole(Long.valueOf(id));
//					roles.add(role);
//				}
//				setValue(roles);
//			}
//			@Override
//			public String getAsText() {
//				return Collections3.extractToString((List) getValue(), "id", ",");
//			}
//		});
//	}
}
