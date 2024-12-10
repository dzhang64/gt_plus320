/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.security.shiro.session.SessionDAO;
import com.gt_plus.common.servlet.ValidateCodeServlet;
import com.gt_plus.common.utils.CacheUtils;
import com.gt_plus.common.utils.CookieUtils;
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.utils.IdGen;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.iim.entity.MailBox;
import com.gt_plus.modules.iim.entity.MailPage;
import com.gt_plus.modules.iim.service.MailBoxService;
import com.gt_plus.modules.oa.entity.OaNotify;
import com.gt_plus.modules.oa.service.OaNotifyService;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.entity.UserSsoSubsystem;
import com.gt_plus.modules.sys.security.FormAuthenticationFilter;
import com.gt_plus.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.service.UserSsoSubsystemService;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 登录Controller
 * @author gt_plus
 * @version 2013-5-31
 */
@Controller
public class LoginController extends BaseController{
	
	@Autowired
	private SessionDAO sessionDAO;
	
	@Autowired
	private OaNotifyService oaNotifyService;
	
	@Autowired
	private MailBoxService mailBoxService;
	
	@Autowired
	private SystemService systemService;
	
	private static final String PARAM_RETURN_URL = "sso_return_url";
	private static final String PARAM_SSO_COOKIE_NAME = "sso_user_ticket";
	private static final String PARAM_SSO_DEFAULT_URL = "sso.default_url";
	private static final String PARAM_CA_COOKIE_NAME = "KOAL_CERT_CN";
	
	private static final String NAME_DES_NO = "NkxIdaYT";
	private static final String NAME_LOGINNAME = "NkxIdaYTU";
	private static final String NAME_DESPASSWORD = "NkxIdaYTP";
	private static final String NAME_SSO_DESPASSWORD = "NkxIdaYTPOA";
	
	/**
	 * SSO登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "${adminPath}/sso_login")
	public String ssoLogin(HttpServletRequest request, HttpServletResponse response) {
		String returnUrl = request.getParameter(PARAM_RETURN_URL);
		if (false == StringUtils.isAnyEmpty(returnUrl)) {
			//request.getSession().setAttribute(PARAM_RETURN_URL, Encodes.urlDecode(returnUrl));
			CookieUtils.setCookie(response, PARAM_RETURN_URL, Encodes.urlDecode(returnUrl), -1);
		} else {
			//request.getSession().setAttribute(PARAM_RETURN_URL, Global.getConfig(PARAM_SSO_DEFAULT_URL));
			CookieUtils.setCookie(response, PARAM_RETURN_URL, Global.getConfig(PARAM_SSO_DEFAULT_URL), -1);
		}
		return "redirect:" + adminPath+"/login"; 
	}
	
	/**
	 * CA登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "${adminPath}/caLogin")
	public String caLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		//读取coocke，des加密，返回RETURN_URL
		//CookieUtils.setCookie(response, PARAM_CA_COOKIE_NAME, "");
		String caNo = CookieUtils.getCookie(request, PARAM_CA_COOKIE_NAME);
		//logger.debug("cano:" + caNo);
		if (false == StringUtils.isEmpty(caNo)) {
			User user = systemService.getUserByNo(caNo);
			if(user != null) {
				CookieUtils.setCookie(response, NAME_LOGINNAME, user.getLoginName(), -1);
				CookieUtils.setCookie(response, NAME_DESPASSWORD, user.getDesPassword(), -1);
				if (false == StringUtils.isEmpty(user.getSsoDesPassword())) {
					CookieUtils.setCookie(response, NAME_SSO_DESPASSWORD, user.getSsoDesPassword(), -1);
				}
				
				//if (request.getSession().getAttribute(PARAM_RETURN_URL) != null) {
				String returnUrl = CookieUtils.getCookie(request, PARAM_RETURN_URL);
				if (StringUtils.isEmpty(returnUrl)) {
					returnUrl = Global.getConfig(PARAM_SSO_DEFAULT_URL);
				}
				
				//String returnUrl = request.getSession().getAttribute(PARAM_RETURN_URL).toString();
				CookieUtils.setCookie(response, PARAM_SSO_COOKIE_NAME, user.getNo(), -1);
					
				if (returnUrl.indexOf("?") != -1) {
					returnUrl += "&" + NAME_DES_NO + "=" + Encodes.encodeBase64(SystemService.entryptDesPassword(user.getNo()));
				} else {
					returnUrl += "?" + NAME_DES_NO + "=" + Encodes.encodeBase64(SystemService.entryptDesPassword(user.getNo()));
				}
				return "redirect:" + returnUrl;
			}
		}
		//CA登陆失败
		String message = "CA登陆失败，请重试";
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);
		
		return "modules/sys/sysLogin";
	}
	
	/**
	 * SSO注销
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "${adminPath}/sso_logout", method = RequestMethod.GET)
	public String ssoLogout(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		String returnUrl = request.getParameter(PARAM_RETURN_URL);
		if (false == StringUtils.isAnyEmpty(returnUrl)) {
			CookieUtils.setCookie(response, PARAM_RETURN_URL, Encodes.urlDecode(returnUrl), -1);
		} else {
			CookieUtils.setCookie(response, PARAM_RETURN_URL, Global.getConfig(PARAM_SSO_DEFAULT_URL), -1);
		}
		
		Principal principal = UserUtils.getPrincipal();
		// 如果已经登录，则跳转到管理首页
		if(principal != null){
			UserUtils.getSubject().logout();
		}
	    // 如果是手机客户端退出跳转到login，则返回JSON字符串
		String ajax = request.getParameter("__ajax");
		if(	ajax!=null){
			model.addAttribute("success", "1");
			model.addAttribute("msg", "退出成功");
			return renderString(response, model);
		}
		CookieUtils.setCookie(response, NAME_LOGINNAME, "");
		CookieUtils.setCookie(response, NAME_DESPASSWORD, ""); 
		CookieUtils.setCookie(response, NAME_SSO_DESPASSWORD, "");
		CookieUtils.setCookie(response, PARAM_SSO_COOKIE_NAME, "");
		
		return "redirect:" + adminPath+"/login";
	}
	
	/**
	 * SSO登陆验证
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "${adminPath}/sso_check_cookie")
	public String ssoCheckCookie(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		
		String loginName = CookieUtils.getCookie(request, NAME_LOGINNAME);
		String password = CookieUtils.getCookie(request, NAME_DESPASSWORD);
		if (false == StringUtils.isEmpty(loginName)) {
			password = SystemService.decryptDesPassword(password);
		} else {
			loginName = "";
			password = "";
		}
		//loginName = "lid";
		//password = "123456";
		model.addAttribute("loginName", loginName);
		model.addAttribute("password", password);
		
		return "modules/sys/ssoCheckCookie";
	}
	
	/**
	 * SSO登陆验证
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "${adminPath}/sso_check_cookie_oa")
	public String ssoCheckCookieOa(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		
		String loginName = CookieUtils.getCookie(request, NAME_LOGINNAME);
		String password = CookieUtils.getCookie(request, NAME_SSO_DESPASSWORD);
		if (password == null) password = "";
		if (false == StringUtils.isEmpty(loginName) && false == StringUtils.isEmpty(password)) {
			password = SystemService.decryptDesPassword(password);
		} else {
			loginName = "";
			password = "";
		}
		//loginName = "lid";
		//password = "123456";
		model.addAttribute("loginName", loginName);
		model.addAttribute("password", password);
		
		return "modules/sys/ssoCheckCookie";
	}
	
	/**
	 * 从Cookie中读取SSO登录后的用户编码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "${adminPath}/sso_login_check")
	public String ssoLoginCheck(HttpServletRequest request, HttpServletResponse response) {
		String no = CookieUtils.getCookie(request, PARAM_SSO_COOKIE_NAME);
		AjaxJson j = new AjaxJson();
		if (false == StringUtils.isAnyEmpty(no)) {
			j.setSuccess(true);
			j.setErrorCode("0");
			j.setMsg("success");
			j.put("no", no);
		} else {
			//String returnUrl = request.getParameter(PARAM_RETURN_URL);
			//if (StringUtils.isAnyEmpty(returnUrl))  returnUrl = "";
			//return "redirect:" + adminPath+"/sso_login?" + PARAM_RETURN_URL + "=" + returnUrl;
			j.setSuccess(false);
			j.setErrorCode("0");
			j.setMsg("没有登录!");
			j.put("no", "");
		}
		return renderString(response, j);
	}
	
	/**
	 * 从Cookie中读取SSO登录后的用户编码
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "${adminPath}/sso_login_check_simple")
	public String ssoLoginCheckSimple(HttpServletRequest request, HttpServletResponse response) {
		String no = CookieUtils.getCookie(request, PARAM_SSO_COOKIE_NAME);
		if(StringUtils.isEmpty(no)) no = "";
		return no;
	}
	
	/**
	 * 管理登录
	 * @throws IOException 
	 */
	@RequestMapping(value = "${adminPath}/login")
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		Principal principal = UserUtils.getPrincipal();

//		// 默认页签模式
//		String tabmode = CookieUtils.getCookie(request, "tabmode");
//		if (tabmode == null){
//			CookieUtils.setCookie(response, "tabmode", "1");
//		}
		
		if (logger.isDebugEnabled()){
			logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}
		
		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))){
			CookieUtils.setCookie(response, "LOGINED", "false");
		}
		
		// 如果已经登录，则跳转到管理首页
		if(principal != null && !principal.isMobileLogin()){
			return "redirect:" + adminPath;
		}
		
		
		 SavedRequest savedRequest = WebUtils.getSavedRequest(request);//获取跳转到login之前的URL
		// 如果是手机没有登录跳转到到login，则返回JSON字符串
		 if(savedRequest != null){
			 String queryStr = savedRequest.getQueryString();
			if(	queryStr!=null &&( queryStr.contains("__ajax") || queryStr.contains("mobileLogin"))){
				AjaxJson j = new AjaxJson();
				j.setSuccess(false);
				j.setErrorCode("0");
				j.setMsg("没有登录!");
				return renderString(response, j);
			}
		 }
		 
		
		return "modules/sys/sysLogin";
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
	public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();
		
		// 如果已经登录，则跳转到管理首页
		if(principal != null){
			return "redirect:" + adminPath;
		}

		String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
		boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		String exception = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String)request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);
		
		if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")){
			message = "用户或密码错误, 请重试.";
		}

		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);
		
		if (logger.isDebugEnabled()){
			logger.debug("login fail, active session size: {}, message: {}, exception: {}", 
					sessionDAO.getActiveSessions(false).size(), message, exception);
		}
		
		// 非授权异常，登录失败，验证码加1。
		if (!UnauthorizedException.class.getName().equals(exception)){
			model.addAttribute("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
		}
		
		// 验证失败清空验证码
		request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());
		
		// 如果是手机登录，则返回JSON字符串
		if (mobile){
			AjaxJson j = new AjaxJson();
			j.setSuccess(false);
			j.setMsg(message);
			j.put("username", username);
			j.put("name","");
			j.put("mobileLogin", mobile);
			j.put("JSESSIONID", "");
	        return renderString(response, j.getJsonStr());
		}
		
		return "modules/sys/sysLogin";
	}

	/**
	 * 管理登录
	 * @throws IOException 
	 */
	@RequestMapping(value = "${adminPath}/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		Principal principal = UserUtils.getPrincipal();
		// 如果已经登录，则跳转到管理首页
		if(principal != null){
			UserUtils.getSubject().logout();
			
		}
	   // 如果是手机客户端退出跳转到login，则返回JSON字符串
			String ajax = request.getParameter("__ajax");
			if(	ajax!=null){
				model.addAttribute("success", "1");
				model.addAttribute("msg", "退出成功");
				return renderString(response, model);
			}
		 return "redirect:" + adminPath+"/login";
	}

	/**
	 * 登录成功，进入管理首页
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "${adminPath}")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		Principal principal = UserUtils.getPrincipal();
		//判断是否SSO登录，读取Session，写Cookie
		CookieUtils.setCookie(response, NAME_LOGINNAME, UserUtils.getUser().getLoginName(), -1);
		CookieUtils.setCookie(response, NAME_DESPASSWORD, UserUtils.getUser().getDesPassword(), -1);
		if (false == StringUtils.isEmpty(UserUtils.getUser().getSsoDesPassword())) {
			CookieUtils.setCookie(response, NAME_SSO_DESPASSWORD, UserUtils.getUser().getSsoDesPassword(), -1);
		}
		
		//if (request.getSession().getAttribute(PARAM_RETURN_URL) != null) {
		String returnUrl = CookieUtils.getCookie(request, PARAM_RETURN_URL);
		if (false == StringUtils.isEmpty(returnUrl)) {
			//String returnUrl = request.getSession().getAttribute(PARAM_RETURN_URL).toString();
			CookieUtils.setCookie(response, PARAM_SSO_COOKIE_NAME, principal.getNo(), -1);
			
			if (returnUrl.indexOf("?") != -1) {
				returnUrl += "&" + NAME_DES_NO + "=" + Encodes.encodeBase64(SystemService.entryptDesPassword(principal.getNo()));
			} else {
				returnUrl += "?" + NAME_DES_NO + "=" + Encodes.encodeBase64(SystemService.entryptDesPassword(principal.getNo()));
			}
			return "redirect:" + returnUrl;
		}
		
		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(principal.getLoginName(), false, true);
		
		if (logger.isDebugEnabled()){
			logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}
		
		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))){
			String logined = CookieUtils.getCookie(request, "LOGINED");
			if (StringUtils.isBlank(logined) || "false".equals(logined)){
				CookieUtils.setCookie(response, "LOGINED", "true");
			}else if (StringUtils.equals(logined, "true")){
				UserUtils.getSubject().logout();
				return "redirect:" + adminPath + "/login";
			}
		}
		
		// 如果是手机登录，则返回JSON字符串
		if (principal.isMobileLogin()){
			if (request.getParameter("login") != null){
				return renderString(response, principal);
			}
			if (request.getParameter("index") != null){
				return "modules/sys/sysIndex";
			}
			return "redirect:" + adminPath + "/login";
		}
		
//		// 登录成功后，获取上次登录的当前站点ID
//		UserUtils.putCache("siteId", StringUtils.toLong(CookieUtils.getCookie(request, "siteId")));

//		System.out.println("==========================a");
//		try {
//			byte[] bytes = com.gt_plus.common.utils.FileUtils.readFileToByteArray(
//					com.gt_plus.common.utils.FileUtils.getFile("c:\\sxt.dmp"));
//			UserUtils.getSession().setAttribute("kkk", bytes);
//			UserUtils.getSession().setAttribute("kkk2", bytes);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////		for (int i=0; i<1000000; i++){
////			//UserUtils.getSession().setAttribute("a", "a");
////			request.getSession().setAttribute("aaa", "aa");
////		}
//		System.out.println("==========================b");
		//
		OaNotify oaNotify = new OaNotify();
		oaNotify.setSelf(true);
		oaNotify.setReadFlag("0");
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify); 
		request.setAttribute("page", page);
		request.setAttribute("count", page.getList().size());//未读通知条数
		
		
		//
		MailBox mailBox = new MailBox();
		mailBox.setReceiver(UserUtils.getUser());
		mailBox.setReadstatus("0");//筛选未读
		Page<MailBox> mailPage = mailBoxService.findPage(new MailPage<MailBox>(request, response), mailBox); 
		request.setAttribute("noReadCount", mailBoxService.getCount(mailBox));
		request.setAttribute("mailPage", mailPage);
		// 默认风格
		String indexStyle = "default";
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie == null || StringUtils.isEmpty(cookie.getName())) {
				continue;
			}
			if (cookie.getName().equalsIgnoreCase("theme")) {
				indexStyle = cookie.getValue();
			}
		}
		// 要添加自己的风格，复制下面三行即可
		if (StringUtils.isNotEmpty(indexStyle)
				&& indexStyle.equalsIgnoreCase("ace")) {
			return "modules/sys/sysIndex-ace";
		}
		request.setAttribute("productName", Global.getConfig("productName"));
		return "modules/sys/sysIndex";
	}
	
	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response){
		if (StringUtils.isNotBlank(theme)){
			CookieUtils.setCookie(response, "theme", theme);
		}else{
			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:"+request.getParameter("url");
	}
	
	/**
	 * 是否是验证码登录
	 * @param useruame 用户名
	 * @param isFail 计数加1
	 * @param clean 计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean){
		Map<String, Integer> loginFailMap = (Map<String, Integer>)CacheUtils.get("loginFailMap");
		if (loginFailMap==null){
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum==null){
			loginFailNum = 0;
		}
		if (isFail){
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean){
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}
	
	
	/**
	 * 首页
	 * @throws IOException 
	 */
	@RequestMapping(value = "${adminPath}/home")
	public String home(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		
		return "modules/sys/sysHome";
		
	}
	
	@Autowired
	private UserSsoSubsystemService userSsoSubsystemService;
	
	/**
	 * 接收加密用户编码，加密子系统KEY，返回用户名密码
	 */
	@ResponseBody
	@RequestMapping(value = "${adminPath}/getSsoTicket")
	public String getSsoTicket(String no, String key) {
		no = SystemService.decryptDesPassword(Encodes.decodeBase64String(no));
		UserSsoSubsystem userSsoSubsystem = userSsoSubsystemService.getLoginNameAndPassword(no, key);
		return userSsoSubsystem.getLoginName() + "," + userSsoSubsystem.getPassword();
	}
	
	/**
	 * 接收加密用户编码，返回可用的SSO列表
	 */
	@ResponseBody
	@RequestMapping(value = "${adminPath}/getSsoList")
	public String getSsoList(String no) {
		no = SystemService.decryptDesPassword(Encodes.decodeBase64String(no));
		List<UserSsoSubsystem> list = userSsoSubsystemService.getSsoList(no);	
		List<Map<String, String>> ssoList = Lists.newArrayList();
		for (UserSsoSubsystem userSsoSubsystem : list) {
			Map<String, String> ssoMap  = Maps.newHashMap();
			ssoMap.put("loginName", userSsoSubsystem.getLoginName());
			ssoMap.put("name", userSsoSubsystem.getSsoSubsystem().getName());
			if ("1".equalsIgnoreCase(userSsoSubsystem.getIsAllow())) {
				ssoMap.put("url", userSsoSubsystem.getSsoSubsystem().getUrl());
			} else if ("0".equalsIgnoreCase(userSsoSubsystem.getIsAllow())) {
				ssoMap.put("url", userSsoSubsystem.getSsoSubsystem().getCaUrl());
			}
			ssoMap.put("key", userSsoSubsystem.getSsoSubsystem().getKey());
			ssoList.add(ssoMap);
		}
		return new Gson().toJson(ssoList);
	}
}
