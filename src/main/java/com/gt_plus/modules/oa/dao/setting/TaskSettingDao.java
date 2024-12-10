/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.setting;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.setting.TaskSetting;

/**
 * 节点权限DAO接口
 * @author GT0155
 * @version 2017-11-08
 */
@MyBatisDao
public interface TaskSettingDao extends CrudDao<TaskSetting> {

	TaskSetting getByProcAndTask(@Param("taskSetting")TaskSetting taskSetting);

	List<TaskSetting> findListByProcDefKey(@Param("procDefKey")String procDefKey);

	
}