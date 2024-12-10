/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.salarylevel.service;

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
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.gt_plus.modules.salarylevel.dao.SalaryLevelDao;

/**
 * 薪资级别Service
 * @author zdy
 * @version 2018-01-30
 */
@Service
@Transactional(readOnly = true)
public class SalaryLevelService extends CrudService<SalaryLevelDao, SalaryLevel> {
		
	public SalaryLevel get(String id) {
		return super.get(id);
	}
	
	public List<SalaryLevel> findList(SalaryLevel salaryLevel) {
		return super.findList(salaryLevel);
	}
	
	public Page<SalaryLevel> findPage(Page<SalaryLevel> page, SalaryLevel salaryLevel) {
		return super.findPage(page, salaryLevel);
	}
	
	@Transactional(readOnly = false)
	public void save(SalaryLevel salaryLevel) {
		super.save(salaryLevel);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SalaryLevel salaryLevel) {
		super.saveV(salaryLevel);
	}
	
	@Transactional(readOnly = false)
	public void delete(SalaryLevel salaryLevel) {
		super.delete(salaryLevel);
	}
	
	public Page<SalaryLevel> findPageBysalary(Page<SalaryLevel> page, SalaryLevel salary) {
		salary.setPage(page);
		page.setList(dao.findListBysalary(salary));
		return page;
	}
	
	
	
}