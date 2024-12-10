/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.common.web;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.gt_plus.common.beanvalidator.BeanValidators;
import com.gt_plus.common.ds.DataSourceContextHolder;
import com.gt_plus.common.mapper.JsonMapper;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.modules.sys.utils.FileUtils;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 控制器支持类
 * @author gt_plus
 * @version 2013-3-23
 */
public abstract class BaseController {

	protected BaseController() {
		DataSourceContextHolder.setDbType(DataSourceContextHolder.DEFAULT_DBTYPE);
	}
	
	/**
	 * 设置数据源
	 * @param dbType
	 */
	protected void setDbType(String dbType) {
		if(false == StringUtils.isEmpty(dbType)) DataSourceContextHolder.setDbType(dbType);
	}
	
	/**
	 * 重置数据源
	 * @param dbType
	 */
	protected void resetDbType() {
		DataSourceContextHolder.setDbType(DataSourceContextHolder.DEFAULT_DBTYPE);
	}
	
	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 管理基础路径
	 */
	@Value("${adminPath}")
	protected String adminPath;
	
	/**
	 * 前端基础路径
	 */
	@Value("${frontPath}")
	protected String frontPath;
	
	/**
	 * 前端URL后缀
	 */
	@Value("${urlSuffix}")
	protected String urlSuffix;
	
	/**
	 * 验证Bean实例对象
	 */
	@Autowired
	protected Validator validator;

	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
	 */
	protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			addMessage(model, list.toArray(new String[]{}));
			return false;
		}
		return true;
	}
	
	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组
	 * @return 验证成功：返回true；严重失败：将错误信息添加到 flash message 中
	 */
	protected boolean beanValidator(RedirectAttributes redirectAttributes, Object object, Class<?>... groups) {
		try{
			BeanValidators.validateWithException(validator, object, groups);
		}catch(ConstraintViolationException ex){
			List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
			list.add(0, "数据验证失败：");
			addMessage(redirectAttributes, list.toArray(new String[]{}));
			return false;
		}
		return true;
	}
	
	/**
	 * 服务端参数有效性验证
	 * @param object 验证的实体对象
	 * @param groups 验证组，不传入此参数时，同@Valid注解验证
	 * @return 验证成功：继续执行；验证失败：抛出异常跳转400页面。
	 */
	protected void beanValidator(Object object, Class<?>... groups) {
		BeanValidators.validateWithException(validator, object, groups);
	}
	
	/**
	 * 添加Model消息
	 * @param message
	 */
	protected void addMessage(Model model, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		model.addAttribute("message", sb.toString());
	}
	
	/**
	 * 添加Flash消息
	 * @param message
	 */
	protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		redirectAttributes.addFlashAttribute("message", sb.toString());
	}
	
	/**
	 * 客户端返回JSON字符串
	 * @param response
	 * @param object
	 * @return
	 */
	protected String renderString(HttpServletResponse response, Object object) {
		return renderString(response, JsonMapper.toJsonString(object));
	}
	
	/**
	 * 客户端返回字符串
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string) {
		try {
			response.reset();
	        response.setContentType("application/json");
	        response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 参数绑定异常
	 */
	@ExceptionHandler({BindException.class, ConstraintViolationException.class, ValidationException.class})
    public String bindException() {  
        return "error/400";
    }
	
	/**
	 * 授权登录异常
	 */
	@ExceptionHandler({AuthenticationException.class})
    public String authenticationException() {  
        return "error/403";
    }
	
	/**
	 * 初始化数据绑定
	 * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
	 * 2. 将字段中Date类型转换为String类型
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
			}
			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
		// Date 类型转换
		binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(DateUtils.parseDate(text));
			}
//			@Override
//			public String getAsText() {
//				Object value = getValue();
//				return value != null ? DateUtils.formatDateTime((Date)value) : "";
//			}
		});
	}
	
	/**
	 * 获取bootstrap data分页数据
	 * @param page
	 * @return map对象
	 */
	public <T> Map<String, Object> getBootstrapData(Page page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rows", page.getList());
		map.put("total", page.getCount());
		return map;
	}
	
	/**
	 * 构造审核意见
	 * @param preAudits
	 * @param audits
	 * @return
	 */
	public String buildAudits(String preAudits ,String audits, String preStatus, String status, String roles) {
		String auditsResult = "";
		String auditAction = "";
		String auditColor = "blue";
		Boolean isAction = true;
		if (status.endsWith("0")) {
			auditAction = "【创建】";
		} else if (false == preStatus.equals(status) && status.endsWith("9")) {
			auditAction = "【批准】";
			auditColor = "green";
		} else if (false == preStatus.equals(status) && status.endsWith("8")) {
			auditAction = "【通过】";
			auditColor = "green";
		} else if (false == preStatus.equals(status) && status.endsWith("5")) {
			auditAction = "【驳回】";
			auditColor = "orange";
		} else if (false == preStatus.equals(status) && status.endsWith("4")) {
			auditAction = "【退回】";
			auditColor = "orange";
		} else if (false == preStatus.equals(status) && status.endsWith("1")) {
			auditAction = "【上报】";
		} else if (false == status.endsWith("0") && StringUtils.isEmpty(preAudits)) {
			//直接上报
			auditAction = "【创建＆上报】";
		} else {
			isAction = false;
		}
		if (isAction) {
			String userInfo = UserUtils.getUser().getName() + "（" + UserUtils.getUser().getCompany().getName() + "，" + UserUtils.getUser().getOffice().getName() + "）";
			if (StringUtils.isEmpty(preAudits) || preAudits.equals(audits)) {
				//第一次创建
				//auditsResult =  "{\"auditUser\":\"" + UserUtils.getUser().getName() + "\", \"auditTime\":\"" + DateUtils.getDateTime() + "\", \"auditComments\":\"" + auditAction + audits + "\"}";
				auditsResult =  "{'auditUser':'" + userInfo + "', 'auditTime':'" + DateUtils.getDateTime() + "', 'auditComments':'" + auditAction + audits + "', 'auditColor':'" + auditColor + "','roles':'" + roles + "'}";
			} else if (false == status.endsWith("0")){
				//状态变化
				//auditsResult =  "{\"auditUser\":\"" + UserUtils.getUser().getName() + "\", \"auditTime\":\"" + DateUtils.getDateTime() + "\", \"auditComments\":\"" + auditAction + audits + "\"}" + "," + preAudits;
				auditsResult =  "{'auditUser':'" + userInfo + "', 'auditTime':'" + DateUtils.getDateTime() + "', 'auditComments':'" + auditAction + audits + "', 'auditColor':'" + auditColor + "','roles':'" + roles + "'}" + "," + preAudits;
			} else {
				//状态未变化
				auditsResult = preAudits;
			}
		} else {
			auditsResult = preAudits;
		}
		return auditsResult;
	}
	
	/**
	 * 文件上传
	 * @param request
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "fileUpload")
	public String fileUpload(HttpServletRequest request,@RequestParam(value = "file", required = false)MultipartFile file){
		//logger.debug("TOPDF:" + toPdf);
		return FileUtils.fileUpload(request, file, false);
	}
	
	/**
	 * 文件上传，转Pdf
	 * @param request
	 * @param file
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "fileUploadPdf")
	public String fileUploadPdf(HttpServletRequest request,@RequestParam(value = "file", required = false)MultipartFile file){
		//logger.debug("TOPDF:" + toPdf);
		return FileUtils.fileUpload(request, file, true);
	}
	
	/**
	 * 超出Spring限制文件大小时统一处理异常
	 * @param e
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(MaxUploadSizeExceededException.class) 
	public String handleException(Exception e){
		return new Gson().toJson("ExceedSize");
	}
	
}
