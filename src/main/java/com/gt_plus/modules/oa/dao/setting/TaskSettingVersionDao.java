/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.setting;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.act.entity.Act;
import com.gt_plus.modules.oa.entity.setting.TaskSettingVersion;

/**
 * 节点权限版本DAO接口
 * @author GT0155
 * @version 2017-11-15
 */
@MyBatisDao
public interface TaskSettingVersionDao extends CrudDao<TaskSettingVersion> {

	void batchSave(@Param("taskSettingVersionList")List<TaskSettingVersion> taskSettingVersionList);

	TaskSettingVersion getTaskSettingVersionByAct(@Param("act")Act act);

	
}