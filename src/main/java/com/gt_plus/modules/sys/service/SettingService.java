/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.Setting;
import com.gt_plus.modules.sys.dao.SettingDao;

/**
 * 参数设置Service
 * @author David
 * @version 2017-11-07
 */
@Service
@Transactional(readOnly = true)
public class SettingService extends CrudService<SettingDao, Setting> {

	public Setting get(String id) {
		return super.get(id);
	}
	
	public List<Setting> findList(Setting setting) {
		return super.findList(setting);
	}
	
	public Page<Setting> findPage(Page<Setting> page, Setting setting) {
		return super.findPage(page, setting);
	}
	
	@Transactional(readOnly = false)
	public void save(Setting setting) {
		super.save(setting);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Setting setting) {
		super.saveV(setting);
	}
	
	@Transactional(readOnly = false)
	public void delete(Setting setting) {
		super.delete(setting);
	}
	
	
	
	
}