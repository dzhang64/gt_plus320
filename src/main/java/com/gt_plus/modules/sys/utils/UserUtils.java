/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.google.common.collect.Lists;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.service.BaseService;
import com.gt_plus.common.sms.SMSUtils;
import com.gt_plus.common.utils.CacheUtils;
import com.gt_plus.common.utils.SpringContextHolder;
import com.gt_plus.modules.salarylevel.dao.SalaryLevelDao;
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.gt_plus.modules.sys.dao.AreaDao;
import com.gt_plus.modules.sys.dao.MenuDao;
import com.gt_plus.modules.sys.dao.OfficeDao;
import com.gt_plus.modules.sys.dao.RoleDao;
import com.gt_plus.modules.sys.dao.SettingDao;
import com.gt_plus.modules.sys.dao.UserDao;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.sys.entity.Menu;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.Setting;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;
import com.gt_plus.modules.userinfo.entity.UserInfo;

/**
 * 用户工具类
 * @author gt_plus
 * @version 2013-12-05
 */
public class UserUtils {

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static UserInfoDao userInfoDao = SpringContextHolder.getBean(UserInfoDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
	private static SalaryLevelDao salaryLevelDao = SpringContextHolder.getBean(SalaryLevelDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
	private static SettingDao settingDao = SpringContextHolder.getBean(SettingDao.class);


	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";

	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
	
	public static final String SUBSYSTEM_CODE = "subsystem_code";
	public static final String SUBSYSTEM_OA = "gtoa";
	
	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null){
			user = userDao.get(id);
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName){
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null){
			user = userDao.getByLoginName(new User(null, loginName));
			if (user == null){
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}
	
	
	/**
	 * 获取下一个工号
	 * @param 员工类型
	 * @return 员工编号
	 */
	@SuppressWarnings("null")
	public synchronized static String getUserCode(String type) {
		String userCode = userInfoDao.getLastUserCode();
		if (userCode == null || userCode.length() <= 0) {

			userCode = "0001";
		} else {
			userCode = Integer.toString(Integer.parseInt(userCode) + 1);

			for (int i = userCode.length(); i < 4; i++) {
				userCode = "0" + userCode;
			}

		}

		userCode = type + userCode;

		return userCode;
	}
	
	
	/**
	 * 获取登录名
	 * @param 用户中文名拼音，用户身份证编号
	 * @return 用户登录名
	 */
	public synchronized static String getloginName(String loginName,String idcardNum){
		int num = userDao.selectconutLoginName(loginName);  
		
		if(num>0){
			    loginName = loginName +  idcardNum.substring(idcardNum.length()-4, idcardNum.length());
				 int i =1;
				 while(userDao.selectconutLoginName(loginName)>0){
					 loginName = loginName+i;
					 i++;
				 }
			 }
		return loginName;
	}
	
	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache(){
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		UserUtils.clearCache(getUser());
	}
	
	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user){
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null){
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}
	
	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser(){
		Principal principal = getPrincipal();
		if (principal!=null){
			User user = get(principal.getId());
			if (user != null){
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}
	
	/**
	 * 设置单一用户的ROLE,返回1标识成功，返回0标识失败
	 * @return
	 */
	public static int setUserRole(String userId,String roleId){
		
		if(userId!= null&&userId.length()>0&&roleId!=null&&roleId.length()>0){
			userDao.insertUserRoleById(userId, roleId);
			return 1;
		}
		return 0;
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Role> getRoleList(){
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null){
			User user = getUser();
			if (user.isAdmin()){
				roleList = roleDao.findAllList(new Role());
			}else{
				Role role = new Role();
				role.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "u"));
				roleList = roleDao.findList(role);
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}
	
	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			Menu m = new Menu();
			m.setIsShow("");
			if (user.isAdmin()){
				menuList = menuDao.findAllList(m);
			}else{
				m.setUserId(user.getId());
				if(false == StringUtils.isEmpty(Global.getConfig(SUBSYSTEM_CODE))) {
					//配置文件中定义了子系统标识，只查询该子系统下的菜单
					m.setSubSystemCodeList(Global.getConfig(SUBSYSTEM_CODE));
				} else {
					//配置文件中未定义子系统表示，查询所有菜单
					m.setSubSystemCodeList("");
				}
				menuList = menuDao.findByUserId(m);
			}
			buildMyShortcut(menuList); //构造我的快捷菜单
			putCache(CACHE_MENU_LIST, menuList);
		}
		
		return menuList;
	}
	
	/**
	 * 获取当前用户授权菜单，用于权限分配
	 * @return
	 */
	public static List<Menu> getMenuListForPermission(){
		List<Menu> menuList = null;
		User user = getUser();
		Menu m = new Menu();
		m.setIsShow("");
		if (user.isAdmin()) {
			menuList = menuDao.findAllList(m);
		} else {
			m.setUserId(user.getId());
			if (false == StringUtils.isEmpty(Global.getConfig(SUBSYSTEM_CODE))) {
				// 配置文件中定义了子系统标识，只查询该子系统下的菜单
				m.setSubSystemCodeList(Global.getConfig(SUBSYSTEM_CODE));
			} else {
				// 配置文件中未定义子系统表示，查询所有菜单
				m.setSubSystemCodeList("");
			}
			menuList = menuDao.findByUserId(m);
		}
		return menuList;
	}
	
	/**
	 * 构建我的快捷菜单，仅对gtoa子系统有效
	 * @param menuList
	 */
	private static void buildMyShortcut(List<Menu> menuList) {
		if(Global.getConfig(SUBSYSTEM_CODE).equalsIgnoreCase(SUBSYSTEM_OA) 
				|| StringUtils.isEmpty(Global.getConfig(SUBSYSTEM_CODE))) {
			//查询用户我的快捷设置，查询不到，取系统默认设置
			String userShortcutString = getUserShortcutString();
			if (false == StringUtils.isEmpty(userShortcutString)) {
				Menu myShortcutMenu = null;
				List<Menu> myShortcutMenuList = Lists.newArrayList();
				for(Menu m : menuList) {
					if (m.getName().equals(Menu.MY_SHORTCUT_MENU_NAME)) {
						myShortcutMenu = m;
					}
				}
				if (myShortcutMenu != null) {
					for(Menu m : menuList) {
						if (userShortcutString.indexOf("," + m.getId() + ",") != -1) {
							myShortcutMenuList.add(m);
						}
					}
					for(Menu m : myShortcutMenuList) {
						if (m.getHref() != null && false == m.getHref().trim().equals("")) {
							Menu obj = new Menu();
							obj.setHref(m.getHref());
							obj.setId(m.getId());
							obj.setIcon(m.getIcon());
							obj.setParent(myShortcutMenu);
							obj.setName(m.getName());
							obj.setIsShow(m.getIsShow());
							menuList.add(obj);
						}
					}
				}
				//myShortcutMenu.setChildren(myShortcutMenuList);
			}
		}
	}
	
	/**
	 * 获取登录用户的我的快捷菜单设置
	 * @return
	 */
	public static String getUserShortcutString() {
		String shortcutString = "";
		Setting theSetting = new Setting();
		theSetting.setName(Setting.LINKS_USER_NAME);
		theSetting.setKey(getUser().getId());
		List<Setting> userSettingList = settingDao.findList(theSetting);
		if (userSettingList.size() == 0) {
			//用户我的快捷未设置，用系统默认设置
			theSetting = new Setting();
			theSetting.setName(Setting.LINKS_DEFAULT_NAME);
			theSetting.setKey(Setting.LINKS_DEFAULT_KEY);
			userSettingList = settingDao.findList(theSetting);
		}
		if (userSettingList.size() == 1) shortcutString = userSettingList.get(0).getValue();
		return shortcutString;
	}
	
	/**
	 * 查询带链接的菜单列表
	 * @return
	 */
	public static List<Menu> getHrefMenuList(String userShortcutString) {
		List<Menu> menuList = Lists.newArrayList();
		for (Menu menu : getMenuList()) {
			if (menu.getHref() != null && false == menu.getHref().trim().equals("")) {
				if (false == menu.getParent().getName().equals(Menu.MY_SHORTCUT_MENU_NAME)) {
					if (userShortcutString.indexOf("," + menu.getId() + ",") != -1) {
						menu.setIsShow(Global.YES);
					} else {
						menu.setIsShow(Global.NO);
					}
					menuList.add(menu);
				}
			}
		}
		return menuList;
	}
	
	/**
	 * 保存快捷设置
	 * @param userShortcutString
	 */
	public static boolean saveUserShortcutString(String userShortcutString) {
		boolean isOK = false;
		Setting theSetting = new Setting();
		theSetting.setName(Setting.LINKS_USER_NAME);
		theSetting.setKey(getUser().getId());
		List<Setting> userSettingList = settingDao.findList(theSetting);
		if (userSettingList.size() == 1) {
			theSetting = userSettingList.get(0);
			theSetting.setValue(userShortcutString);
			theSetting.preUpdate();
			settingDao.update(theSetting);
			isOK = true;
		} else if (userSettingList.size() == 0) {
			theSetting.setValue(userShortcutString);
			theSetting.preInsert();
			settingDao.insert(theSetting);
			isOK = true;
		}
		return isOK;
	}
	
	public static List<Menu> getMenuListForAdmin(){
		//@SuppressWarnings("unchecked")
		List<Menu> menuList = null; //(List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			Menu m = new Menu();
			User user = getUser();
			if (user.isAdmin()){
				m.setIsShow("");
				menuList = menuDao.findAllList(m);
				if (menuList.size() > 300) {
					m.setIsShow("1");
					menuList = menuDao.findAllList(m);
				}
			}else{
				m.setUserId(user.getId());
				if(false == StringUtils.isEmpty(Global.getConfig(SUBSYSTEM_CODE))) {
					//配置文件中定义了子系统标识，只查询该子系统下的菜单
					m.setSubSystemCodeList(Global.getConfig(SUBSYSTEM_CODE));
				} else {
					//配置文件中未定义子系统表示，查询所有菜单
					m.setSubSystemCodeList("");
				}
				menuList = menuDao.findByUserId(m);
			}
			//putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static Menu getTopMenu(){
		Menu topMenu =  getMenuList().get(0);
		return topMenu;
	}
	/**
	 * 获取当前用户授权的区域
	 * @return
	 */
	public static List<Area> getAreaList(){
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
			areaList = areaDao.findAllList(new Area());
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}
	
	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
			if (user.isAdmin()){
				officeList = officeDao.findAllList(new Office());
			}else{
				Office office = new Office();
				office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = officeDao.findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null){
			officeList = officeDao.findAllList(new Office());
		}
		return officeList;
	}
	
	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject(){
		return SecurityUtils.getSubject();
	}
	
	/**
	 * 获取当前登录者对象
	 */
	public static Principal getPrincipal(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null){
				return principal;
			}
//			subject.logout();
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	public static Session getSession(){
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null){
				session = subject.getSession();
			}
			if (session != null){
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e){
			
		}
		return null;
	}
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
		Object obj = getSession().getAttribute(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
//		getCacheMap().put(key, value);
		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
//		getCacheMap().remove(key);
		getSession().removeAttribute(key);
	}
	
	public static String getTime(Date date){
		StringBuffer time = new StringBuffer();
        Date date2 = new Date();
        long temp = date2.getTime() - date.getTime();    
        long days = temp / 1000 / 3600/24;                //相差小时数
        if(days>0){
        	time.append(days+"天");
        }
        long temp1 = temp % (1000 * 3600*24);
        long hours = temp1 / 1000 / 3600;                //相差小时数
        if(days>0 || hours>0){
        	time.append(hours+"小时");
        }
        long temp2 = temp1 % (1000 * 3600);
        long mins = temp2 / 1000 / 60;                    //相差分钟数
        time.append(mins + "分钟");
        return  time.toString();
	}


	//发送注册码
	public static String sendRandomCode(String uid, String pwd, String tel, String randomCode) throws IOException {
		//发送内容
		String content = "您的验证码是："+randomCode+"，有效期30分钟，请在有效期内使用。"; 
		
		return SMSUtils.send(uid, pwd, tel, content);

	}
	
	//注册用户重置密码
	public static String sendPass(String uid, String pwd, String tel, String password) throws IOException {
		//发送内容
		String content = "您的新密码是："+password+"，请登录系统，重新设置密码。"; 
		return SMSUtils.send(uid, pwd, tel, content);

	}
	
	/**
	 * 导出Excel调用,根据姓名转换为ID
	 */
	public static User getByUserName(String name){
		User u = new User();
		u.setName(name);
		List<User> list = userDao.findList(u);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new User();
		}
	}
	/**
	 * 导出Excel使用，根据名字转换为id
	 */
	public static Office getByOfficeName(String name){
		Office o = new Office();
		o.setName(name);
		List<Office> list = officeDao.findListOfficeByName(o);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new Office();
		}
	}
	/**
	 * 导出Excel使用，根据名字转换为id
	 */
	public static Area getByAreaName(String name){
		Area a = new Area();
		a.setName(name);
		List<Area> list = areaDao.findListAreaByName(a);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new Area();
		}
	}
	/**
	 * 导出Excel使用，根据名字转换为id
	 */
	public static SalaryLevel getBySalaryLevelName(String name){
		SalaryLevel a = new SalaryLevel();
		a.setLevel(name);
		List<SalaryLevel> list = salaryLevelDao.findList(a);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new SalaryLevel();
		}
	}
	
	
	/**
	 * 导出Excel使用，根据用户登录名获取用户
	 */
	public static UserInfo getUserInfoByLoginName(String loginName){
		UserInfo a = new UserInfo();
		a.setLoginName(loginName);
		List<UserInfo> list = userInfoDao.findListByUserInfo(a);
		if(list.size()>0){
			return list.get(0);
		}else{
			return new UserInfo();
		}
	}
	
	
	
	
}
