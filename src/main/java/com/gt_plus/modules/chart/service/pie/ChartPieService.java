/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.service.pie;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.chart.entity.pie.ChartPie;
import com.gt_plus.modules.chart.dao.pie.ChartPieDao;

/**
 * 饼图Service
 * @author GT0155
 * @version 2017-09-06
 */
@Service
@Transactional(readOnly = true)
public class ChartPieService extends CrudService<ChartPieDao, ChartPie> {

	public ChartPie get(String id) {
		return super.get(id);
	}
	
	public List<ChartPie> findList(ChartPie chartPie) {
		return super.findList(chartPie);
	}
	
	public Page<ChartPie> findPage(Page<ChartPie> page, ChartPie chartPie) {
		return super.findPage(page, chartPie);
	}
	
	@Transactional(readOnly = false)
	public void save(ChartPie chartPie) {
		super.save(chartPie);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ChartPie chartPie) {
		super.saveV(chartPie);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChartPie chartPie) {
		super.delete(chartPie);
	}
	
	
	
	
}