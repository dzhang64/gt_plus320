/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercontractrenew.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.persistence.ActEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.sysusercommonentry.entity.SysCommonUserInformation;
import com.gt_plus.modules.sysuserinterentry.entity.SysInternUserInformation;
import com.gt_plus.modules.userinfo.entity.UserInfo;

/**
 * 合同续签Entity
 * @author wl
 * @version 2018-03-27
 */
public class SysUserContractRenew extends ActEntity<SysUserContractRenew> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private UserInfo name;		// 姓名
	private Date beginDate;		// 合同开始日期
	private Date endDate;		// 合同结束日期
	private String contractType;		// 合同签订类型
	private String contractLimit;		// 合同期限类型
	private Area area;		// 入职区域
	
	public SysUserContractRenew(SysInternUserInformation sysInternUserInformation) {
		super();
		this.isNewRecord = true;
		this.ownerCode = sysInternUserInformation.getOwnerCode();
		this.name.setId(sysInternUserInformation.getId());
		this.beginDate= sysInternUserInformation.getContractSartDate();		// 合同开始日期
		this.endDate=sysInternUserInformation.getContractEndDate();		// 合同结束日期
		this.contractType = sysInternUserInformation.getContractType();
		this.contractLimit = sysInternUserInformation.getContractLimit();
		this.area = sysInternUserInformation.getArea();
	}
	
	
	public SysUserContractRenew(SysCommonUserInformation sysCommonUserInformation) {
		super();
		this.isNewRecord = true;
		this.name.setId(sysCommonUserInformation.getId());
		this.beginDate= sysCommonUserInformation.getContractSartDate();		// 合同开始日期
		this.endDate=sysCommonUserInformation.getContractEndDate();		// 合同结束日期
		this.contractType = sysCommonUserInformation.getContractType();
		this.contractLimit = sysCommonUserInformation.getContractLimit();
		this.area = sysCommonUserInformation.getArea();
	}


	
	
	public SysUserContractRenew() {
		super();
	}

	public SysUserContractRenew(String id){
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
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="合同开始日期", align=2, sort=11)
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="合同结束日期", align=2, sort=12)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	@ExcelField(title="合同签订类型", dictType="sys_user_contract", align=2, sort=13)
	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	
	
	@ExcelField(title="合同期限类型", dictType="sys_user_contract_year", align=2, sort=14)
	public String getContractLimit() {
		return contractLimit;
	}

	public void setContractLimit(String contractLimit) {
		this.contractLimit = contractLimit;
	}
	
	
	@ExcelField(title="入职区域", fieldType=Area.class, value="area.name", align=2, sort=15)
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	

	
	
}