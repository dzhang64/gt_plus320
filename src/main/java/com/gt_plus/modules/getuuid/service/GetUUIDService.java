/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.getuuid.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.modules.sys.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gt_plus.modules.sys.utils.UserUtils;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.getuuid.entity.GetUUID;
import com.gt_plus.modules.getuuid.dao.GetUUIDDao;

/**
 * 生成UUIDService
 * @author zdyt
 * @version 2018-01-30
 */
@Service
@Transactional(readOnly = true)
public class GetUUIDService extends CrudService<GetUUIDDao, GetUUID> {
		
	public GetUUID get(String id) {
		return super.get(id);
	}
	
	public List<GetUUID> findList(GetUUID getUUID) {
		return super.findList(getUUID);
	}
	
	public Page<GetUUID> findPage(Page<GetUUID> page, GetUUID getUUID) {
		return super.findPage(page, getUUID);
	}
	
	@Transactional(readOnly = false)
	public void save(GetUUID getUUID) {
		super.save(getUUID);
	}
	
	@Transactional(readOnly = false)
	public void saveV(GetUUID getUUID) {
		super.saveV(getUUID);
	}
	
	@Transactional(readOnly = false)
	public void delete(GetUUID getUUID) {
		super.delete(getUUID);
	}
	
	
	
}