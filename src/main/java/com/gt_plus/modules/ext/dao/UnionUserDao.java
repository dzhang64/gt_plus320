package com.gt_plus.modules.ext.dao;

import org.apache.poi.ss.formula.functions.T;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface UnionUserDao extends CrudDao<T> {
	
}
