/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusersscentry.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gt_plus.modules.sys.utils.UserUtils;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sysusersscentry.entity.SysUserSscEntry;
import com.gt_plus.modules.sysusersscentry.dao.SysUserSscEntryDao;
import com.gt_plus.modules.userinfo.dao.UserInfoDao;

/**
 * 社保统筹补录入Service
 * @author wl
 * @version 2018-03-30
 */
@Service
@Transactional(readOnly = true)
public class SysUserSscEntryService extends CrudService<SysUserSscEntryDao, SysUserSscEntry> {
	@Autowired
	private UserInfoDao userInfoDao;
	
	public SysUserSscEntry get(String id) {
		return super.get(id);
	}
	
	public List<SysUserSscEntry> findList(SysUserSscEntry sysUserSscEntry) {
		return super.findList(sysUserSscEntry);
	}
	
	public Page<SysUserSscEntry> findPage(Page<SysUserSscEntry> page, SysUserSscEntry sysUserSscEntry) {
		return super.findPage(page, sysUserSscEntry);
	}
	
	@Transactional(readOnly = false)
	public void save(SysUserSscEntry sysUserSscEntry) {
		super.save(sysUserSscEntry);
		String ssc =sysUserSscEntry.getSsc().getId();
		String coordinateNum=sysUserSscEntry.getCoordinateNum();
		String providentNum=sysUserSscEntry.getProvidentNum();
		String userId =sysUserSscEntry.getName().getId();
		userInfoDao.updateSscByuserId(ssc,coordinateNum,providentNum,userId);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SysUserSscEntry sysUserSscEntry) {
		super.saveV(sysUserSscEntry);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysUserSscEntry sysUserSscEntry) {
		super.delete(sysUserSscEntry);
	}
	
	
	
}