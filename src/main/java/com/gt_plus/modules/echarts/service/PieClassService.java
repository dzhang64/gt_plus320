/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.echarts.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.echarts.dao.PieClassDao;
import com.gt_plus.modules.echarts.entity.PieClass;

/**
 * 班级Service
 * @author lgf
 * @version 2016-05-26
 */
@Service
@Transactional(readOnly = true)
public class PieClassService extends CrudService<PieClassDao, PieClass> {

	public PieClass get(String id) {
		return super.get(id);
	}
	
	public List<PieClass> findList(PieClass pieClass) {
		return super.findList(pieClass);
	}
	
	public Page<PieClass> findPage(Page<PieClass> page, PieClass pieClass) {
		return super.findPage(page, pieClass);
	}
	
	@Transactional(readOnly = false)
	public void save(PieClass pieClass) {
		super.save(pieClass);
	}
	
	@Transactional(readOnly = false)
	public void delete(PieClass pieClass) {
		super.delete(pieClass);
	}
	
	
	
	
}