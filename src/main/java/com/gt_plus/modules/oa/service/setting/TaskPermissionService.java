/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.service.setting;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.oa.entity.setting.TaskPermission;
import com.gt_plus.modules.oa.dao.setting.TaskPermissionDao;

/**
 * 权限分类Service
 * @author GT0155
 * @version 2017-12-25
 */
@Service
@Transactional(readOnly = true)
public class TaskPermissionService extends CrudService<TaskPermissionDao, TaskPermission> {
		
	public TaskPermission get(String id) {
		return super.get(id);
	}
	
	public List<TaskPermission> findList(TaskPermission taskPermission) {
		return super.findList(taskPermission);
	}
	
	public Page<TaskPermission> findPage(Page<TaskPermission> page, TaskPermission taskPermission) {
		return super.findPage(page, taskPermission);
	}
	
	@Transactional(readOnly = false)
	public void save(TaskPermission taskPermission) {
		super.save(taskPermission);
	}
	
	@Transactional(readOnly = false)
	public void saveV(TaskPermission taskPermission) {
		super.saveV(taskPermission);
	}
	
	@Transactional(readOnly = false)
	public void delete(TaskPermission taskPermission) {
		super.delete(taskPermission);
	}
	
	
	
}