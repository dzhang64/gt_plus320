/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectapprove.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 项目立项Entity
 * @author zdy
 * @version 2018-03-17
 */
public class ProjectApprove extends ActEntity<ProjectApprove> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String projectName;		// 项目名称
	private String projectNum;		// 项目编号
	private String projectAttribute;		// 项目性质
	private String projectType;		// 项目类型
	private String productType;		// 产品类型
	private Area area;		// 区域
	private Office office;		// 执行部门
	private String deviceCompany;		// 设备商
	private String cooperative;		// 合作对象
	private String operator;		// 运营商
	private Double contractAmount;		// 合同额
	private Date startTime;		// 开工日期
	private Date endTime;		// 完工日期
	private UserInfo projectmanager;		// 项目经理
	private String attachfile;		// 附件
	private String attachmessage;		// 简介

	public ProjectApprove() {
		super();
	}

	public ProjectApprove(String id){
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
	
	
	@ExcelField(title="项目名称", align=2, sort=11)
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	
	@ExcelField(title="项目编号", align=2, sort=12)
	public String getProjectNum() {
		return projectNum;
	}

	public void setProjectNum(String projectNum) {
		this.projectNum = projectNum;
	}
	
	
	@ExcelField(title="项目性质", align=2, sort=13)
	public String getProjectAttribute() {
		return projectAttribute;
	}

	public void setProjectAttribute(String projectAttribute) {
		this.projectAttribute = projectAttribute;
	}
	
	
	@ExcelField(title="项目类型", align=2, sort=14)
	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	
	
	@ExcelField(title="产品类型", align=2, sort=15)
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
	@ExcelField(title="区域", fieldType=Area.class, value="area.name", align=2, sort=16)
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	
	@ExcelField(title="执行部门", fieldType=Office.class, value="office.name", align=2, sort=17)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	
	@ExcelField(title="设备商", align=2, sort=18)
	public String getDeviceCompany() {
		return deviceCompany;
	}

	public void setDeviceCompany(String deviceCompany) {
		this.deviceCompany = deviceCompany;
	}
	
	
	@ExcelField(title="合作对象", align=2, sort=19)
	public String getCooperative() {
		return cooperative;
	}

	public void setCooperative(String cooperative) {
		this.cooperative = cooperative;
	}
	
	
	@ExcelField(title="运营商", align=2, sort=20)
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
	@ExcelField(title="合同额", align=2, sort=21)
	public Double getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(Double contractAmount) {
		this.contractAmount = contractAmount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="开工日期", align=2, sort=22)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="完工日期", align=2, sort=23)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
	@ExcelField(title="项目经理", align=2, sort=24)
	public UserInfo getProjectmanager() {
		return projectmanager;
	}

	public void setProjectmanager(UserInfo projectmanager) {
		this.projectmanager = projectmanager;
	}
	
	
	@ExcelField(title="附件", align=2, sort=25)
	public String getAttachfile() {
		return attachfile;
	}

	public void setAttachfile(String attachfile) {
		this.attachfile = attachfile;
	}
	
	
	@ExcelField(title="简介", align=2, sort=26)
	public String getAttachmessage() {
		return attachmessage;
	}

	public void setAttachmessage(String attachmessage) {
		this.attachmessage = attachmessage;
	}

	
	
}