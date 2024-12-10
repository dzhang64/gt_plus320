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
import com.gt_plus.modules.act.entity.Act;
import com.gt_plus.modules.oa.entity.setting.TaskSettingVersion;
import com.gt_plus.modules.oa.dao.setting.TaskSettingVersionDao;

/**
 * 节点权限版本Service
 * @author GT0155
 * @version 2017-11-15
 */
@Service
@Transactional(readOnly = true)
public class TaskSettingVersionService extends CrudService<TaskSettingVersionDao, TaskSettingVersion> {
	
	@Autowired
	private TaskSettingVersionDao taskSettingVersionDao;

	public TaskSettingVersion get(String id) {
		return super.get(id);
	}
	
	public List<TaskSettingVersion> findList(TaskSettingVersion taskSettingVersion) {
		return super.findList(taskSettingVersion);
	}
	
	public Page<TaskSettingVersion> findPage(Page<TaskSettingVersion> page, TaskSettingVersion taskSettingVersion) {
		return super.findPage(page, taskSettingVersion);
	}
	
	@Transactional(readOnly = false)
	public void save(TaskSettingVersion taskSettingVersion) {
		super.save(taskSettingVersion);
	}
	
	@Transactional(readOnly = false)
	public void saveV(TaskSettingVersion taskSettingVersion) {
		super.saveV(taskSettingVersion);
	}
	
	@Transactional(readOnly = false)
	public void delete(TaskSettingVersion taskSettingVersion) {
		super.delete(taskSettingVersion);
	}

	@Transactional(readOnly = false)
	public void batchSave(List<TaskSettingVersion> taskSettingVersionList) {
		taskSettingVersionDao.batchSave(taskSettingVersionList);
	}

	@Transactional(readOnly = false)
	public TaskSettingVersion getTaskSettingVersionByAct(Act act) {
		return taskSettingVersionDao.getTaskSettingVersionByAct(act);
	}
	
	
	
	
}