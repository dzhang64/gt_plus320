/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.common.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.ckfinder.connector.ConnectorServlet;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.utils.FileUtils;
import com.gt_plus.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * CKFinderConnectorServlet
 * @author gt_plus
 * @version 2014-06-25
 */
public class CKFinderConnectorServlet extends ConnectorServlet {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		prepareGetResponse(request, response, false);
		super.doGet(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		prepareGetResponse(request, response, true);
		super.doPost(request, response);
	}
	
	private void prepareGetResponse(final HttpServletRequest request,
			final HttpServletResponse response, final boolean post) throws ServletException {
		Principal principal = (Principal) UserUtils.getPrincipal();
		if (principal == null){
			return;
		}
		String command = request.getParameter("command");
		String type = request.getParameter("type");
		// 初始化时，如果startupPath文件夹不存在，则自动创建startupPath文件夹
		if ("Init".equals(command)){
			String startupPath = request.getParameter("startupPath");// 当前文件夹可指定为模块名
			if (startupPath!=null){
				String[] ss = startupPath.split(":");
				if (ss.length==2){
					String realPath = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL
							+ principal + "/" + ss[0] + ss[1];
					FileUtils.createDirectory(realPath);
				}
			}
		}
		// 快捷上传，自动创建当前文件夹，并上传到该路径
		else if ("QuickUpload".equals(command) && type!=null){
			String currentFolder = request.getParameter("currentFolder");// 当前文件夹可指定为模块名
			String realPath = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL
					+ principal + "/" + type + (currentFolder != null ? currentFolder : "");
			FileUtils.createDirectory(realPath);
		}
//		System.out.println("------------------------");
//		for (Object key : request.getParameterMap().keySet()){
//			System.out.println(key + ": " + request.getParameter(key.toString()));
//		}
	}
	
}
