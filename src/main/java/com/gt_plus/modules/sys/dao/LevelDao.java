/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.Level;

/**
 * 职务DAO接口
 * @author David
 * @version 2017-11-01
 */
@MyBatisDao
public interface LevelDao extends CrudDao<Level> {

	
}