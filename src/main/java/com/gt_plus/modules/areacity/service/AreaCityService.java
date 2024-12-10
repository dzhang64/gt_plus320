/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.areacity.service;

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
import com.gt_plus.modules.areacity.entity.AreaCity;
import com.gt_plus.modules.areacity.dao.AreaCityDao;

/**
 * 省地市Service
 * @author wl
 * @version 2018-01-15
 */
@Service
@Transactional(readOnly = true)
public class AreaCityService extends CrudService<AreaCityDao, AreaCity> {
		
	public AreaCity get(String id) {
		return super.get(id);
	}
	
	public List<AreaCity> findList(AreaCity areaCity) {
		return super.findList(areaCity);
	}
	
	public Page<AreaCity> findPage(Page<AreaCity> page, AreaCity areaCity) {
		return super.findPage(page, areaCity);
	}
	
	@Transactional(readOnly = false)
	public void save(AreaCity areaCity) {
		super.save(areaCity);
	}
	
	@Transactional(readOnly = false)
	public void saveV(AreaCity areaCity) {
		super.saveV(areaCity);
	}
	
	@Transactional(readOnly = false)
	public void delete(AreaCity areaCity) {
		super.delete(areaCity);
	}
	
	
	
}