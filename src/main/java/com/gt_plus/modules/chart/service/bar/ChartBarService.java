/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.service.bar;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.chart.entity.bar.ChartBar;
import com.gt_plus.modules.chart.dao.bar.ChartBarDao;

/**
 * 柱状图Service
 * @author GT0155
 * @version 2017-09-07
 */
@Service
@Transactional(readOnly = true)
public class ChartBarService extends CrudService<ChartBarDao, ChartBar> {

	public ChartBar get(String id) {
		return super.get(id);
	}
	
	public List<ChartBar> findList(ChartBar chartBar) {
		return super.findList(chartBar);
	}
	
	public Page<ChartBar> findPage(Page<ChartBar> page, ChartBar chartBar) {
		return super.findPage(page, chartBar);
	}
	
	@Transactional(readOnly = false)
	public void save(ChartBar chartBar) {
		super.save(chartBar);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ChartBar chartBar) {
		super.saveV(chartBar);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChartBar chartBar) {
		super.delete(chartBar);
	}
	
	
	
	
}