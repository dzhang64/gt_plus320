/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import java.util.List;

import com.gt_plus.common.persistence.TreeDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.sys.entity.Office;

/**
 * 区域DAO接口
 * @author gt_plus
 * @version 2014-05-16
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {
	
	public List<Area> findListAreaByName(Area area);
	
}
