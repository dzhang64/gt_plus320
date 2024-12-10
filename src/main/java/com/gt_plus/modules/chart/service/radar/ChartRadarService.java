/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.service.radar;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.chart.entity.radar.ChartRadar;
import com.gt_plus.modules.chart.dao.radar.ChartRadarDao;

/**
 * 雷达图Service
 * @author GT0155
 * @version 2017-09-06
 */
@Service
@Transactional(readOnly = true)
public class ChartRadarService extends CrudService<ChartRadarDao, ChartRadar> {

	public ChartRadar get(String id) {
		return super.get(id);
	}
	
	public List<ChartRadar> findList(ChartRadar chartRadar) {
		return super.findList(chartRadar);
	}
	
	public Page<ChartRadar> findPage(Page<ChartRadar> page, ChartRadar chartRadar) {
		return super.findPage(page, chartRadar);
	}
	
	@Transactional(readOnly = false)
	public void save(ChartRadar chartRadar) {
		super.save(chartRadar);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ChartRadar chartRadar) {
		super.saveV(chartRadar);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChartRadar chartRadar) {
		super.delete(chartRadar);
	}
	
	
	
	
}