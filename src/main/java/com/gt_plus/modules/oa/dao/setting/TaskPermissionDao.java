/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.setting;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.setting.TaskPermission;

/**
 * 权限分类DAO接口
 * @author GT0155
 * @version 2017-12-25
 */
@MyBatisDao
public interface TaskPermissionDao extends CrudDao<TaskPermission> {

	
}