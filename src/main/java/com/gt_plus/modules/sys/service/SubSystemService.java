/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.SubSystem;
import com.gt_plus.modules.sys.dao.SubSystemDao;

/**
 * 子系统Service
 * @author GT0291
 * @version 2017-07-25
 */
@Service
@Transactional(readOnly = true)
public class SubSystemService extends CrudService<SubSystemDao, SubSystem> {

	public SubSystem get(String id) {
		return super.get(id);
	}
	
	public List<SubSystem> findList(SubSystem subSystem) {
		return super.findList(subSystem);
	}
	
	public Page<SubSystem> findPage(Page<SubSystem> page, SubSystem subSystem) {
		return super.findPage(page, subSystem);
	}
	
	@Transactional(readOnly = false)
	public void save(SubSystem subSystem) {
		super.save(subSystem);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SubSystem subSystem) {
		super.saveV(subSystem);
	}
	
	@Transactional(readOnly = false)
	public void delete(SubSystem subSystem) {
		super.delete(subSystem);
	}
	
	
	
	
}