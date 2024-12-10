/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserresignation.entity;

import com.gt_plus.modules.userinfo.entity.UserInfo;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 离职申请Entity
 * @author wl
 * @version 2018-03-21
 */
public class SysUserResignation extends ActEntity<SysUserResignation> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private UserInfo name;		// 离职人员
	private String officeId;		// 所属部门
	private Date leaveDate;		// 离职日期
	private String leaveReason;		// 离职原因
	private String leaveType;		// 离职类型
	private String transferEquipment;		// 交接设备
	private String noTransfer;		// 未交接耗材
	private Double certificationDeduction;		// 认证扣款
	private Double noSalaryPay;		// 未付工资金额
	private Double noReimbursement;		// 未付报销金额
	private Double loanAmount;		// 借款金额
	private Double amountSum;		// 未付金额合计
	private Double leaveSum;		// 离职结算
	
	public SysUserResignation() {
		super();
	}

	public SysUserResignation(String id){
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
	
	@ExcelField(title="离职人员", align=2, sort=11)
	public UserInfo getName() {
		return name;
	}

	public void setName(UserInfo name) {
		this.name = name;
	}
	
	
	@ExcelField(title="所属部门", align=2, sort=12)
	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="离职日期", align=2, sort=13)
	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}
	
	
	@ExcelField(title="离职原因", align=2, sort=14)
	public String getLeaveReason() {
		return leaveReason;
	}

	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}
	
	
	@ExcelField(title="离职类型", dictType="leave_type", align=2, sort=15)
	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}
	
	
	@ExcelField(title="交接设备", align=2, sort=16)
	public String getTransferEquipment() {
		return transferEquipment;
	}

	public void setTransferEquipment(String transferEquipment) {
		this.transferEquipment = transferEquipment;
	}
	
	
	@ExcelField(title="未交接耗材", align=2, sort=17)
	public String getNoTransfer() {
		return noTransfer;
	}

	public void setNoTransfer(String noTransfer) {
		this.noTransfer = noTransfer;
	}
	
	
	@ExcelField(title="认证扣款", align=2, sort=18)
	public Double getCertificationDeduction() {
		return certificationDeduction;
	}

	public void setCertificationDeduction(Double certificationDeduction) {
		this.certificationDeduction = certificationDeduction;
	}
	
	
	@ExcelField(title="未付工资金额", align=2, sort=19)
	public Double getNoSalaryPay() {
		return noSalaryPay;
	}

	public void setNoSalaryPay(Double noSalaryPay) {
		this.noSalaryPay = noSalaryPay;
	}
	
	
	@ExcelField(title="未付报销金额", align=2, sort=20)
	public Double getNoReimbursement() {
		return noReimbursement;
	}

	public void setNoReimbursement(Double noReimbursement) {
		this.noReimbursement = noReimbursement;
	}
	
	
	@ExcelField(title="借款金额", align=2, sort=21)
	public Double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}
	
	
	@ExcelField(title="未付金额合计", align=2, sort=22)
	public Double getAmountSum() {
		return amountSum;
	}

	public void setAmountSum(Double amountSum) {
		this.amountSum = amountSum;
	}
	
	
	@ExcelField(title="离职结算", align=2, sort=23)
	public Double getLeaveSum() {
		return leaveSum;
	}

	public void setLeaveSum(Double leaveSum) {
		this.leaveSum = leaveSum;
	}
	
	
	
}