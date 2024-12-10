/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.service.pie;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.chart.entity.pie.ChartAnnular;
import com.gt_plus.modules.chart.dao.pie.ChartAnnularDao;

/**
 * 环形图Service
 * @author GT0155
 * @version 2017-09-07
 */
@Service
@Transactional(readOnly = true)
public class ChartAnnularService extends CrudService<ChartAnnularDao, ChartAnnular> {

	public ChartAnnular get(String id) {
		return super.get(id);
	}
	
	public List<ChartAnnular> findList(ChartAnnular chartAnnular) {
		return super.findList(chartAnnular);
	}
	
	public Page<ChartAnnular> findPage(Page<ChartAnnular> page, ChartAnnular chartAnnular) {
		return super.findPage(page, chartAnnular);
	}
	
	@Transactional(readOnly = false)
	public void save(ChartAnnular chartAnnular) {
		super.save(chartAnnular);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ChartAnnular chartAnnular) {
		super.saveV(chartAnnular);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChartAnnular chartAnnular) {
		super.delete(chartAnnular);
	}
	
	
	
	
}