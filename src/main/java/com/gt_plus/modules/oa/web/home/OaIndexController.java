/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.home;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gt_plus.common.web.BaseController;

/**
 * 收文管理Controller
 * @author GT0155
 * @version 2017-12-08
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/home/oaIndex")
public class OaIndexController extends BaseController {
	
	/**
	 * OA首页
	 */
	@RequiresPermissions("oa:home:oaIndex:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/oa/home/oaIndex";
	}
	
}