/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.service.op;

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
import com.gt_plus.modules.mgmt.entity.op.OnlinePetition;
import com.gt_plus.modules.mgmt.dao.op.OnlinePetitionDao;

/**
 * 网上信访Service
 * @author GT0155
 * @version 2017-11-28
 */
@Service
@Transactional(readOnly = true)
public class OnlinePetitionService extends CrudService<OnlinePetitionDao, OnlinePetition> {
		
	public OnlinePetition get(String id) {
		return super.get(id);
	}
	
	public List<OnlinePetition> findList(OnlinePetition onlinePetition) {
		return super.findList(onlinePetition);
	}
	
	public Page<OnlinePetition> findPage(Page<OnlinePetition> page, OnlinePetition onlinePetition) {
		return super.findPage(page, onlinePetition);
	}
	
	@Transactional(readOnly = false)
	public void save(OnlinePetition onlinePetition) {
		super.save(onlinePetition);
	}
	
	@Transactional(readOnly = false)
	public void saveV(OnlinePetition onlinePetition) {
		super.saveV(onlinePetition);
	}
	
	@Transactional(readOnly = false)
	public void delete(OnlinePetition onlinePetition) {
		super.delete(onlinePetition);
	}
	
	
	
}