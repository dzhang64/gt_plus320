/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.service.line;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.chart.entity.line.ChartHumitureLine;
import com.gt_plus.modules.chart.dao.line.ChartHumitureLineDao;

/**
 * 温湿度曲线图Service
 * @author GT0155
 * @version 2017-09-06
 */
@Service
@Transactional(readOnly = true)
public class ChartHumitureLineService extends CrudService<ChartHumitureLineDao, ChartHumitureLine> {

	public ChartHumitureLine get(String id) {
		return super.get(id);
	}
	
	public List<ChartHumitureLine> findList(ChartHumitureLine chartHumitureLine) {
		return super.findList(chartHumitureLine);
	}
	
	public Page<ChartHumitureLine> findPage(Page<ChartHumitureLine> page, ChartHumitureLine chartHumitureLine) {
		return super.findPage(page, chartHumitureLine);
	}
	
	@Transactional(readOnly = false)
	public void save(ChartHumitureLine chartHumitureLine) {
		super.save(chartHumitureLine);
	}
	
	@Transactional(readOnly = false)
	public void saveV(ChartHumitureLine chartHumitureLine) {
		super.saveV(chartHumitureLine);
	}
	
	@Transactional(readOnly = false)
	public void delete(ChartHumitureLine chartHumitureLine) {
		super.delete(chartHumitureLine);
	}
	
	
	
	
}