/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercomputersubsidiesapply.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 电脑补贴申请Entity
 * @author wl
 * @version 2018-03-23
 */
public class SysUserComputerSubsidiesApply extends ActEntity<SysUserComputerSubsidiesApply> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
	private String computerConfiguration;		// 电脑配置
	private String computerSerialNumber;		// 电脑序列号
	private Date buyDate;		// 购买日期
	private Date beginDate;		// 起租日期
	private Date endDate;		// 终租日期
	private String subsidyStandard;		// 补贴标准
	private String isDo;		// 流程完成标识
	
	public SysUserComputerSubsidiesApply() {
		super();
	}

	public SysUserComputerSubsidiesApply(String id){
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
	
	
	@ExcelField(title="电脑配置", align=2, sort=11)
	public String getComputerConfiguration() {
		return computerConfiguration;
	}

	public void setComputerConfiguration(String computerConfiguration) {
		this.computerConfiguration = computerConfiguration;
	}
	
	
	@ExcelField(title="电脑序列号", align=2, sort=12)
	public String getComputerSerialNumber() {
		return computerSerialNumber;
	}

	public void setComputerSerialNumber(String computerSerialNumber) {
		this.computerSerialNumber = computerSerialNumber;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="购买日期", align=2, sort=13)
	public Date getBuyDate() {
		return buyDate;
	}

	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="起租日期", align=2, sort=14)
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="终租日期", align=2, sort=15)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	@ExcelField(title="补贴标准", align=2, sort=16)
	public String getSubsidyStandard() {
		return subsidyStandard;
	}

	public void setSubsidyStandard(String subsidyStandard) {
		this.subsidyStandard = subsidyStandard;
	}
	
	
	
}