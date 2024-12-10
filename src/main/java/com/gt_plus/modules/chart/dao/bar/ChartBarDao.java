/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.dao.bar;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.chart.entity.bar.ChartBar;

/**
 * 柱状图DAO接口
 * @author GT0155
 * @version 2017-09-07
 */
@MyBatisDao
public interface ChartBarDao extends CrudDao<ChartBar> {

	
}