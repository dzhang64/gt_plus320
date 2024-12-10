/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.areacity.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.areacity.entity.AreaCity;

/**
 * 省地市DAO接口
 * @author wl
 * @version 2018-01-15
 */
@MyBatisDao
public interface AreaCityDao extends CrudDao<AreaCity> {

	List<AreaCity> getAreaCityByCity(AreaCity city);
}