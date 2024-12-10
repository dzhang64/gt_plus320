/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.tools.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gt_plus.common.mail.MailSendUtils;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.sys.entity.SystemConfig;
import com.gt_plus.modules.sys.service.SystemConfigService;

/**
 * 发送外部邮件
 * @author lgf
 * @version 2016-01-07
 */
@Controller
@RequestMapping(value = "${adminPath}/tools/email")
public class EmailController extends BaseController {

	@Autowired
	private SystemConfigService systemConfigService;
	
	/**
	 * 打开邮件页面
	 */
	@RequestMapping(value = {"index", ""})
	public String index( HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/tools/sendEmail";
	}

	/**
	 * 发送邮件
	 */
	@RequestMapping("send")
	public String send(String emailAddress,  HttpServletResponse response, String title, String content, Model model) throws Exception {
		SystemConfig config = systemConfigService.get("1");
		String[]addresses = emailAddress.split(";");
		String result = "";
		for(String address: addresses){
			boolean isSuccess = MailSendUtils.sendEmail(config.getSmtp(), config.getPort(), config.getMailName(), config.getMailPassword(),address,  title, content, "0");
			if(isSuccess){
				result += address+":<font color='green'>发送成功!</font><br/>";
			}else{
				result += address+":<font color='red'>发送失败!</font><br/>";
			}
		}
			model.addAttribute("result", result);
			return "modules/tools/sendEmailResult";
	}
		
}