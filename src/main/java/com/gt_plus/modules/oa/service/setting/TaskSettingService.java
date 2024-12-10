/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.setting.TaskSetting;
import com.gt_plus.modules.oa.dao.setting.TaskSettingDao;

/**
 * 节点权限Service
 * @author GT0155
 * @version 2017-11-08
 */
@Service
@Transactional(readOnly = true)
public class TaskSettingService extends CrudService<TaskSettingDao, TaskSetting> {

	@Autowired
	private TaskSettingDao taskSettingDao;
	
	public TaskSetting get(String id) {
		return super.get(id);
	}
	
	public List<TaskSetting> findList(TaskSetting taskSetting) {
		return super.findList(taskSetting);
	}
	
	public Page<TaskSetting> findPage(Page<TaskSetting> page, TaskSetting taskSetting) {
		return super.findPage(page, taskSetting);
	}
	
	@Transactional(readOnly = false)
	public void save(TaskSetting taskSetting) {
		super.save(taskSetting);
	}
	
	@Transactional(readOnly = false)
	public void saveV(TaskSetting taskSetting) {
		super.saveV(taskSetting);
	}
	
	@Transactional(readOnly = false)
	public void delete(TaskSetting taskSetting) {
		super.delete(taskSetting);
	}

	@Transactional(readOnly = false)
	public TaskSetting getByProcAndTask(TaskSetting taskSetting) {
		return taskSettingDao.getByProcAndTask(taskSetting);
	}

	@Transactional(readOnly = false)
	public List<TaskSetting> findListByProcDefKey(String procDefKey) {
		return taskSettingDao.findListByProcDefKey(procDefKey);
	}
	
	
	
	
}