/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.common.persistence;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.modules.act.entity.Act;
import com.gt_plus.modules.oa.entity.setting.TaskPermission;

/**
 * Activiti Entity类
 * @author gt_plus
 * @version 2013-05-28
 */
public abstract class ActEntity<T> extends DataEntity<T> {

	private static final long serialVersionUID = 1L;
	
	protected String procInsId;			//流程实例
	protected String procTaskName;		//任务名称
	protected TaskPermission procTaskPermission;//任务权限
	protected String[] tempLoginName; 	//临时存储用户userLoginName
	protected Map<String, Map<String, String>> ruleArgs = Maps.newHashMap();

	protected Act act; 		// 流程任务对象
	protected int isDo = 0;  //流程完成标识


	public int getIsDo() {
		return isDo;
	}

	public void setIsDo(int isDo) {
		this.isDo = isDo;
	}

	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}

	public String getProcTaskName() {
		return procTaskName;
	}

	public void setProcTaskName(String procTaskName) {
		this.procTaskName = procTaskName;
	}
	
	public TaskPermission getProcTaskPermission() {
		return procTaskPermission;
	}

	public void setProcTaskPermission(TaskPermission procTaskPermission) {
		this.procTaskPermission = procTaskPermission;
	}

	public String[] getTempLoginName() {
		return tempLoginName;
	}

	public void setTempLoginName(String[] tempLoginName) {
		this.tempLoginName = tempLoginName;
	}
	
	public Map<String, Map<String, String>> getRuleArgs() {
		return ruleArgs;
	}

	public void setRuleArgs(Map<String, Map<String, String>> ruleArgs) {
		this.ruleArgs = ruleArgs;
	}
	
	public String getRuleArgsJson() {
		return new Gson().toJson(ruleArgs);
	}

	public ActEntity() {
		super();
	}
	
	public ActEntity(String id) {
		super(id);
	}
	
	@JsonIgnore
	public Act getAct() {
		if (act == null){
			act = new Act();
		}
		return act;
	}

	public void setAct(Act act) {
		this.act = act;
	}

	/**
	 * 获取流程实例ID
	 * @return
	 */
	//public String getProcInsId() {
	//	return this.getAct().getProcInsId();
	//}

	/**
	 * 设置流程实例ID
	 * @param procInsId
	 */
	//public void setProcInsId(String procInsId) {
	//	this.getAct().setProcInsId(procInsId);
	//}
}
