/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserpersonnelturnpositive.entity;

import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 人员转正Entity
 * @author wl
 * @version 2018-03-19
 */
public class SysUserPersonnelTurnPositive extends ActEntity<SysUserPersonnelTurnPositive> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private UserInfo name;		// 姓名
	private String assessmentForm;		// 试用期考核表
	private SalaryLevel beforeSalary;		// 转正前薪资级别
	private Double gradePayBefore;		// 转正前等级工资
	private SalaryLevel afterSalary;		// 转正后薪资级别
	private Double afterGradePay;		// 转正后等级工资
	private Date entryTime;		// 入职时间
	private Date turnTime;		// 转正时间
	private String userType;		// 员工类型
	
	public SysUserPersonnelTurnPositive() {
		super();
	}

	public SysUserPersonnelTurnPositive(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="流程实例ID", align=2, sort=8)
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
	
	
	
	
	
	@ExcelField(title="姓名", align=2, sort=11)
	public UserInfo getName() {
		return name;
	}

	public void setName(UserInfo name) {
		this.name = name;
	}
	
	
	@ExcelField(title="试用期考核表", align=2, sort=12)
	public String getAssessmentForm() {
		return assessmentForm;
	}

	public void setAssessmentForm(String assessmentForm) {
		this.assessmentForm = assessmentForm;
	}
	
	
	@ExcelField(title="转正前薪资级别", align=2, sort=13)
	public SalaryLevel getBeforeSalary() {
		return beforeSalary;
	}

	public void setBeforeSalary(SalaryLevel beforeSalary) {
		this.beforeSalary = beforeSalary;
	}
	
	
	@ExcelField(title="转正前等级工资", align=2, sort=14)
	public Double getGradePayBefore() {
		return gradePayBefore;
	}

	public void setGradePayBefore(Double gradePayBefore) {
		this.gradePayBefore = gradePayBefore;
	}
	
	
	@ExcelField(title="转正后薪资级别", align=2, sort=15)
	public SalaryLevel getAfterSalary() {
		return afterSalary;
	}

	public void setAfterSalary(SalaryLevel afterSalary) {
		this.afterSalary = afterSalary;
	}
	
	
	@ExcelField(title="转正后等级工资", align=2, sort=16)
	public Double getAfterGradePay() {
		return afterGradePay;
	}

	public void setAfterGradePay(Double afterGradePay) {
		this.afterGradePay = afterGradePay;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") 
	@ExcelField(title="入职时间", align=2, sort=17)
	public Date getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") 
	@ExcelField(title="转正时间", align=2, sort=18)
	public Date getTurnTime() {
		return turnTime;
	}

	public void setTurnTime(Date turnTime) {
		this.turnTime = turnTime;
	}
	
	
	@ExcelField(title="员工类型", dictType="user_type", align=2, sort=19)
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	
	
	
}