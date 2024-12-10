/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.matter;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.matter.SynergyMatter;
import com.gt_plus.modules.oa.dao.matter.SynergyMatterDao;

/**
 * 协同事项Service
 * @author LS0077
 * @version 2017-11-08
 */
@Service
@Transactional(readOnly = true)
public class SynergyMatterService extends CrudService<SynergyMatterDao, SynergyMatter> {

	public SynergyMatter get(String id) {
		return super.get(id);
	}
	
	public List<SynergyMatter> findList(SynergyMatter synergyMatter) {
		return super.findList(synergyMatter);
	}
	
	public Page<SynergyMatter> findPage(Page<SynergyMatter> page, SynergyMatter synergyMatter) {
		return super.findPage(page, synergyMatter);
	}
	
	@Transactional(readOnly = false)
	public void save(SynergyMatter synergyMatter) {
		super.save(synergyMatter);
	}
	
	@Transactional(readOnly = false)
	public void saveV(SynergyMatter synergyMatter) {
		super.saveV(synergyMatter);
	}
	
	@Transactional(readOnly = false)
	public void delete(SynergyMatter synergyMatter) {
		super.delete(synergyMatter);
	}
	
	
	
	
}