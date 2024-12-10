/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserinterntranscription.entity;

import com.gt_plus.modules.userinfo.entity.UserInfo;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 实习生转录用Entity
 * @author wl
 * @version 2018-03-20
 */
public class SysUserInternTranscription extends ActEntity<SysUserInternTranscription> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private UserInfo name;		// 实习人员
	private Date startTime;		// 试用期开始日期
	private Date endTime;		// 试用期结束日期
	private Area ssc;		// 社保交纳地
	private String userStatus;		// 员工状态
	private String userType;		// 员工类型
	private Date beginDate;		// 入职时间
	private SalaryLevel level;		// 工资级别
	private Double salaryLevel;		// 等级工资
	private String transcriptionTable;		// 录用申请表
	private String gradeCertificate;		// 毕业证扫描件
	
	public SysUserInternTranscription() {
		super();
	}

	public SysUserInternTranscription(String id){
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
		
	@ExcelField(title="实习人员", align=2, sort=11)
	public UserInfo getName() {
		return name;
	}

	public void setName(UserInfo name) {
		this.name = name;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="试用期开始日期", align=2, sort=12)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="试用期结束日期", align=2, sort=13)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
	@ExcelField(title="社保交纳地", fieldType=Area.class, value="ssc.name", align=2, sort=14)
	public Area getSsc() {
		return ssc;
	}

	public void setSsc(Area ssc) {
		this.ssc = ssc;
	}
	
	
	@ExcelField(title="员工状态", dictType="sys_user_status", align=2, sort=15)
	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	
	
	@ExcelField(title="员工类型", dictType="sys_user_type", align=2, sort=16)
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8") 
	@ExcelField(title="入职时间", align=2, sort=17)
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	
	
	@ExcelField(title="工资级别", align=2, sort=18)
	public SalaryLevel getLevel() {
		return level;
	}

	public void setLevel(SalaryLevel level) {
		this.level = level;
	}
	
	
	@ExcelField(title="等级工资", align=2, sort=19)
	public Double getSalaryLevel() {
		return salaryLevel;
	}

	public void setSalaryLevel(Double salaryLevel) {
		this.salaryLevel = salaryLevel;
	}
	
	
	@ExcelField(title="录用申请表", align=2, sort=21)
	public String getTranscriptionTable() {
		return transcriptionTable;
	}

	public void setTranscriptionTable(String transcriptionTable) {
		this.transcriptionTable = transcriptionTable;
	}
	
	
	@ExcelField(title="毕业证扫描件", align=2, sort=22)
	public String getGradeCertificate() {
		return gradeCertificate;
	}

	public void setGradeCertificate(String gradeCertificate) {
		this.gradeCertificate = gradeCertificate;
	}
	
	
}