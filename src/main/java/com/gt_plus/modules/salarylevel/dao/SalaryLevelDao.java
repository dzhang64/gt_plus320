/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.salarylevel.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;

/**
 * 薪资级别DAO接口
 * @author zdy
 * @version 2018-01-30
 */
@MyBatisDao
public interface SalaryLevelDao extends CrudDao<SalaryLevel> {
	
	public List<SalaryLevel> findListBysalary(SalaryLevel salary);
	
}