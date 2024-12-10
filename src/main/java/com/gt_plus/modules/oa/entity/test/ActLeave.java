/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.test;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 请假表单Entity
 * @author GT0155
 * @version 2017-11-29
 */
public class ActLeave extends ActEntity<ActLeave> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
	private String person;		// 请假人
	private Date leaveDate;		// 请假时间
	private String leaveDays;		// 请假天数
	private String elses;		// 其他说明
	
	public ActLeave() {
		super();
	}

	public ActLeave(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
/*	@ExcelField(title="流程实例ID", align=2, sort=8)
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}
	
	
	@ExcelField(title="流程任务名称", align=2, sort=9)
	public String getProcTaskName() {
		return procTaskName;
	}

	public void setProcTaskName(String procTaskName) {
		this.procTaskName = procTaskName;
	}
	
	
	@ExcelField(title="流程任务权限", align=2, sort=10)
	public String getProcTaskPermission() {
		return procTaskPermission;
	}

	public void setProcTaskPermission(String procTaskPermission) {
		this.procTaskPermission = procTaskPermission;
	}*/
	
	
	@ExcelField(title="请假人", align=2, sort=11)
	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") 
	@ExcelField(title="请假时间", align=2, sort=12)
	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}
	
	
	@ExcelField(title="请假天数", align=2, sort=13)
	public String getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(String leaveDays) {
		this.leaveDays = leaveDays;
	}
	
	
	@ExcelField(title="其他说明", align=2, sort=14)
	public String getElses() {
		return elses;
	}

	public void setElses(String elses) {
		this.elses = elses;
	}
	
	
}