package com.gt_plus.modules.ext.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.bas.entity.basmessage.BasMessage;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;
import com.gt_plus.modules.ext.service.UnionUserService;

@Controller
@RequestMapping(value = "${adminPath}/ext/unionUser")
public class UnionUserController extends BaseController {
	
	@Autowired
	private UnionUserService unionUserService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	@ResponseBody
	@RequestMapping("updateUnionUserPassword")
	public String updateUnionUserPassword(String loginName, String oldPassword, String newPassword) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		/*if (loginName != null) {
			return "FANG WEN CHENG GONG ! loginName : " + loginName + " , oldPassword : " + oldPassword + " , newPassword :" + newPassword;
		}*/
		String result = "";
		
		/*this.setDbType("bbs");
		boolean bbs = unionUserService.checkUnionUserPasswordForBbs(loginName, oldPassword);
		
		this.setDbType("portal");
		boolean portal = unionUserService.checkUnionUserPasswordForPortal(loginName, oldPassword);
		
		this.setDbType("fly");
		boolean fly = unionUserService.checkUnionUserPasswordForFly(loginName, oldPassword);
		
		this.setDbType("trs");
		boolean trs = unionUserService.checkUnionUserPasswordForTrs(loginName, oldPassword);
		
		this.setDbType("zhwz");
		if (!(bbs && portal && fly && trs)) {
			result = "01";
			return result;
		}*/
		
		List<String> list = Lists.newArrayList();
		try {
			this.setDbType("bbs");
			unionUserService.updateUnionUserPasswordForBbs(loginName, oldPassword, newPassword);
			list.add("bbs");
			
			this.setDbType("portal");
			unionUserService.updateUnionUserPasswordForPortal(loginName, oldPassword, newPassword);
			list.add("portal");
			
			this.setDbType("fly");
			unionUserService.updateUnionUserPasswordForFly(loginName, oldPassword, newPassword);
			list.add("fly");
			
			this.setDbType("trs");
			unionUserService.updateUnionUserPasswordForTrs(loginName, oldPassword, newPassword);
			list.add("trs");
			
			this.setDbType("zhwz");
			result = "11";
		} catch (Exception e) {
			Map<String, List<String>> map = Maps.newHashMap();
			map.put("UpdateSuccessSystemList", list);
			
			BasMessage basMessage = new BasMessage();
			basMessage.setTypes("UnionUserPassword");
			basMessage.setContent(new Gson().toJson(map));
			basMessageService.save(basMessage);
			
			List<String> list1 = Lists.newArrayList();
			try {
				for (String string : list) {
					if (string.equalsIgnoreCase("bbs")) {
						this.setDbType("bbs");
						unionUserService.updateUnionUserPasswordForBbs(loginName, newPassword, oldPassword);
						list1.add("bbs");
					}
					if (string.equalsIgnoreCase("portal")) {
						this.setDbType("portal");
						unionUserService.updateUnionUserPasswordForPortal(loginName, newPassword, oldPassword);
						list1.add("portal");
					}
					if (string.equalsIgnoreCase("fly")) {
						this.setDbType("fly");
						unionUserService.updateUnionUserPasswordForFly(loginName, newPassword, oldPassword);
						list1.add("fly");
					}
					if (string.equalsIgnoreCase("trs")) {
						this.setDbType("trs");
						unionUserService.updateUnionUserPasswordForTrs(loginName, newPassword, oldPassword);
						list1.add("trs");
					}
					
				}
				
				this.setDbType("zhwz");
				result = "00";
			} catch (Exception e1) {
				Map<String, List<String>> map1 = Maps.newHashMap();
				map.put("RestoreSuccessSystemList", list1);
				
				BasMessage basMessage1 = new BasMessage();
				basMessage1.setTypes("UnionUserPassword");
				basMessage1.setContent(new Gson().toJson(map1));
				basMessageService.save(basMessage1);
				
				result = "10";
			}
		}
		
		return result;
	}

}
