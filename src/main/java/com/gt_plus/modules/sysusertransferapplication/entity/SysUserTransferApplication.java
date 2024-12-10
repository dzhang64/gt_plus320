/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusertransferapplication.entity;

import com.gt_plus.modules.userinfo.entity.UserInfo;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 调薪申请Entity
 * @author wl
 * @version 2018-03-29
 */
public class SysUserTransferApplication extends ActEntity<SysUserTransferApplication> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
	private UserInfo name;		// 姓名
	private Date adjustTime;		// 调整时间
	private String nameCode;		// 工号
	private String officeName;		// 部门
	private String beforeSalaryId;		// 原薪资级别
	private String beforeSalaryLevel;		// 原等级工资
	private SalaryLevel afterSalary;		// 调整后薪资级别
	private String afterSalaryLevel;		// 调整后等级工资
	private String isDo;		// 流程结束标识
	
	public SysUserTransferApplication() {
		super();
	}

	public SysUserTransferApplication(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=6)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="流程实例ID", align=2, sort=7)
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}
	
	
	@ExcelField(title="流程任务名称", align=2, sort=8)
	public String getProcTaskName() {
		return procTaskName;
	}

	public void setProcTaskName(String procTaskName) {
		this.procTaskName = procTaskName;
	}
	
	
	@ExcelField(title="姓名", align=2, sort=10)
	public UserInfo getName() {
		return name;
	}

	public void setName(UserInfo name) {
		this.name = name;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") 
	@ExcelField(title="调整时间", align=2, sort=11)
	public Date getAdjustTime() {
		return adjustTime;
	}

	public void setAdjustTime(Date adjustTime) {
		this.adjustTime = adjustTime;
	}
	
	
	@ExcelField(title="工号", align=2, sort=12)
	public String getNameCode() {
		return nameCode;
	}

	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	
	
	@ExcelField(title="部门", align=2, sort=13)
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	
	@ExcelField(title="原薪资级别", align=2, sort=14)
	public String getBeforeSalaryId() {
		return beforeSalaryId;
	}

	public void setBeforeSalaryId(String beforeSalaryId) {
		this.beforeSalaryId = beforeSalaryId;
	}
	
	
	@ExcelField(title="原等级工资", align=2, sort=15)
	public String getBeforeSalaryLevel() {
		return beforeSalaryLevel;
	}

	public void setBeforeSalaryLevel(String beforeSalaryLevel) {
		this.beforeSalaryLevel = beforeSalaryLevel;
	}
	
	
	@ExcelField(title="调整后薪资级别", align=2, sort=16)
	public SalaryLevel getAfterSalary() {
		return afterSalary;
	}

	public void setAfterSalary(SalaryLevel afterSalary) {
		this.afterSalary = afterSalary;
	}
	
	
	@ExcelField(title="调整后等级工资", align=2, sort=17)
	public String getAfterSalaryLevel() {
		return afterSalaryLevel;
	}

	public void setAfterSalaryLevel(String afterSalaryLevel) {
		this.afterSalaryLevel = afterSalaryLevel;
	}
	
}