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
import com.gt_plus.modules.sys.entity.Organization;
import com.gt_plus.modules.sys.dao.OrganizationDao;

/**
 * 组织管理Service
 * @author LS0195
 * @version 2017-12-07
 */
@Service
@Transactional(readOnly = true)
public class OrganizationService extends CrudService<OrganizationDao, Organization> {
		
	public Organization get(String id) {
		return super.get(id);
	}
	
	public List<Organization> findList(Organization organization) {
		return super.findList(organization);
	}
	
	public Page<Organization> findPage(Page<Organization> page, Organization organization) {
		return super.findPage(page, organization);
	}
	
	@Transactional(readOnly = false)
	public void save(Organization organization) {
		super.save(organization);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Organization organization) {
		super.saveV(organization);
	}
	
	@Transactional(readOnly = false)
	public void delete(Organization organization) {
		super.delete(organization);
	}
	
	@Transactional(readOnly = false)
	public List<User> findUserToOrg(String id) {
		
		return dao.findUserToOrg(id);
	}

	@Transactional(readOnly = false)
	public void insertUserToOrg(Organization organization, User user) {
		//dao.insertUserToOrg(organization.getId(),user.getId());
		dao.insertUserToOrg(organization,user);
	}

	@Transactional(readOnly = false)
	public void deleteUserToOrg(String userId, String orgId) {
		dao.deleteUserToOrg(userId,orgId);
	}

	@Transactional(readOnly = false)
	public int findOrgNumberBy(String org) {
		
		return dao.findOrgNumberBy(org);
	}

	@Transactional(readOnly = false)
	public List<Organization> findListByUser(Organization organization) {
		return dao.findListByUser(organization);
	}
	
}