/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.Level;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.dao.LevelDao;

/**
 * 职务Service
 * @author David
 * @version 2017-11-01
 */
@Service
@Transactional(readOnly = true)
public class LevelService extends CrudService<LevelDao, Level> {

	public Level get(String id) {
		return super.get(id);
	}
	
	public List<Level> findList(Level level) {
		return super.findList(level);
	}
	
	public Page<Level> findPage(Page<Level> page, Level level) {
		return super.findPage(page, level);
	}
	
	@Transactional(readOnly = false)
	public void save(Level level) {
		super.save(level);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Level level) {
		super.saveV(level);
	}
	
	@Transactional(readOnly = false)
	public void delete(Level level) {
		super.delete(level);
	}
	
}