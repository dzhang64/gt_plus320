package com.gt_plus.modules.gen.dao;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.gen.entity.GenScheme;

/**
 * 生成方案DAO接口
 * @author ThinkGem
 * @version 2013-10-15
 */
@MyBatisDao
public interface GenSchemeDao extends CrudDao<GenScheme> {
	
}