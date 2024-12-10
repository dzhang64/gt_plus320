/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.dao.line;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.chart.entity.line.ChartHumitureLine;

/**
 * 温湿度曲线图DAO接口
 * @author GT0155
 * @version 2017-09-06
 */
@MyBatisDao
public interface ChartHumitureLineDao extends CrudDao<ChartHumitureLine> {

	
}