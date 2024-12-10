/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.UserSsoSubsystem;
import com.gt_plus.modules.sys.dao.UserSsoSubsystemDao;

/**
 * 用户SSO子系统关系Service
 * @author GT0155
 * @version 2017-12-05
 */
@Service
@Transactional(readOnly = true)
public class UserSsoSubsystemService extends CrudService<UserSsoSubsystemDao, UserSsoSubsystem> {
	
	@Autowired
	private UserSsoSubsystemDao userSsoSubsystemDao;
	
	public UserSsoSubsystem get(String id) {
		return super.get(id);
	}
	
	public List<UserSsoSubsystem> findList(UserSsoSubsystem userSsoSubsystem) {
		return super.findList(userSsoSubsystem);
	}
	
	public Page<UserSsoSubsystem> findPage(Page<UserSsoSubsystem> page, UserSsoSubsystem userSsoSubsystem) {
		return super.findPage(page, userSsoSubsystem);
	}
	
	@Transactional(readOnly = false)
	public void save(UserSsoSubsystem userSsoSubsystem) {
		super.save(userSsoSubsystem);
	}
	
	@Transactional(readOnly = false)
	public void saveV(UserSsoSubsystem userSsoSubsystem) {
		super.saveV(userSsoSubsystem);
	}
	
	@Transactional(readOnly = false)
	public void delete(UserSsoSubsystem userSsoSubsystem) {
		super.delete(userSsoSubsystem);
	}

	@Transactional(readOnly = false)
	public UserSsoSubsystem getLoginNameAndPassword(String no, String key) {
		return userSsoSubsystemDao.getLoginNameAndPassword(no, key);
	}
	
	@Transactional(readOnly = false)
	public List<UserSsoSubsystem> getSsoList(String no) {
		return userSsoSubsystemDao.getSsoList(no);
	}
	
	
}