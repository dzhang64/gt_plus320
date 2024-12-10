/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.setting;

import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 节点权限Entity
 * @author GT0155
 * @version 2017-11-08
 */
public class TaskSetting extends DataEntity<TaskSetting> {
	
	private static final long serialVersionUID = 1L;
	private String userTaskId;		// 用户活动
	private String userTaskName;		// 活动名称
	private String procDefKey;		// 流程定义
	private String permissionType;		//权限类型
	private String permission;		//节点权限
	private String settingValue;		// 设置
	private String ruleArgs;		//规则变量
	
	public TaskSetting() {
		super();
	}

	public TaskSetting(String id){
		super(id);
	}

	@ExcelField(title="用户活动", align=2, sort=7)
	public String getUserTaskId() {
		return userTaskId;
	}

	public void setUserTaskId(String userTaskId) {
		this.userTaskId = userTaskId;
	}
	
	
	@ExcelField(title="活动名称", align=2, sort=8)
	public String getUserTaskName() {
		return userTaskName;
	}

	public void setUserTaskName(String userTaskName) {
		this.userTaskName = userTaskName;
	}
	
	
	@ExcelField(title="流程定义", align=2, sort=9)
	public String getProcDefKey() {
		return procDefKey;
	}

	public void setProcDefKey(String procDefKey) {
		this.procDefKey = procDefKey;
	}
	
	public String getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@ExcelField(title="设置", align=2, sort=10)
	public String getSettingValue() {
		return settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

	public String getRuleArgs() {
		return ruleArgs;
	}

	public void setRuleArgs(String ruleArgs) {
		this.ruleArgs = ruleArgs;
	}
	
	
}