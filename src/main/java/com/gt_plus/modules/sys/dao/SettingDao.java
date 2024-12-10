/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.Setting;

/**
 * 参数设置DAO接口
 * @author David
 * @version 2017-11-07
 */
@MyBatisDao
public interface SettingDao extends CrudDao<Setting> {

	
}