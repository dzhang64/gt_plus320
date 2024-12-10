/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.userinfo.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.utils.UserUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;

/**
 * 员工信息Service
 * @author zdy
 * @version 2018-01-30
 */
@Service
@Transactional(readOnly = true)
public class UserInfoService extends CrudService<UserInfoDao, UserInfo> {
		
	public UserInfo get(String id) {
		return super.get(id);
	}
	
	public List<UserInfo> findList(UserInfo userInfo) {
		return super.findList(userInfo);
	}
	
	public Page<UserInfo> findPage(Page<UserInfo> page, UserInfo userInfo) {
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			//使用用户名和登录名去过滤用户，因为可能存在同名员工
			userInfo.setName(user.getName());
			userInfo.setLoginName(user.getLoginName());
			
		}
		return super.findPage(page, userInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(UserInfo userInfo) {
		
		super.save(userInfo);
	}
	
	@Transactional(readOnly = false)
	public void saveV(UserInfo userInfo) {
		super.saveV(userInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserInfo userInfo) {
		super.delete(userInfo);
	}
	
	public Page<SalaryLevel> findPageBysalary(Page<SalaryLevel> page, SalaryLevel salary) {
		salary.setPage(page);
		page.setList(dao.findListBysalary(salary));
		return page;
	}
	
	public Page<UserInfo> findPageByUserInfo(Page<UserInfo> page, UserInfo userInfo) {
		userInfo.setPage(page);
		page.setList(dao.findListByUserInfo(userInfo));
		return page;
	}
	
	
}