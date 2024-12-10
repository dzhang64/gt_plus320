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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.SsoSubsystem;
import com.gt_plus.modules.sys.dao.SsoSubsystemDao;

/**
 * SSO子系统Service
 * @author GT0155
 * @version 2017-12-12
 */
@Service
@Transactional(readOnly = true)
public class SsoSubsystemService extends CrudService<SsoSubsystemDao, SsoSubsystem> {
		
	public SsoSubsystem get(String id) {
		return super.get(id);
	}
	
	public List<SsoSubsystem> findList(SsoSubsystem ssoSubsystem) {
		return super.findList(ssoSubsystem);
	}
	
	public Page<SsoSubsystem> findPage(Page<SsoSubsystem> page, SsoSubsystem ssoSubsystem) {
		return super.findPage(page, ssoSubsystem);
	}
	
	@Transactional(readOnly = false)
	public void save(SsoSubsystem ssoSubsystem) {
		super.save(ssoSubsystem);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SsoSubsystem ssoSubsystem) {
		super.saveV(ssoSubsystem);
	}
	
	@Transactional(readOnly = false)
	public void delete(SsoSubsystem ssoSubsystem) {
		super.delete(ssoSubsystem);
	}
	
	
	
}