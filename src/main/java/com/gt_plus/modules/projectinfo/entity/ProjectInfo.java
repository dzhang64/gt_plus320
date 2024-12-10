/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectinfo.entity;

import com.gt_plus.modules.projectapprove.entity.ProjectApprove;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.sys.entity.Office;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 项目信息Entity
 * @author zdy
 * @version 2018-02-24
 */
public class ProjectInfo extends DataEntity<ProjectInfo> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
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
	private Integer isClosed;		// 关闭标识
	
	
	
	public ProjectInfo(ProjectApprove projectApprove) {
		super();
		this.ownerCode = projectApprove.getOwnerCode();
		this.projectName = projectApprove.getProjectName();
		this.projectNum = projectApprove.getProjectNum();
		this.projectAttribute = projectApprove.getProjectAttribute();
		this.projectType = projectApprove.getProjectType();
		this.productType = projectApprove.getProductType();
		this.area = projectApprove.getArea();
		this.office = projectApprove.getOffice();
		this.deviceCompany = projectApprove.getDeviceCompany();
		this.cooperative = projectApprove.getCooperative();
		this.operator = projectApprove.getOperator();
		this.contractAmount = projectApprove.getContractAmount();
		this.startTime = projectApprove.getStartTime();
		this.endTime = projectApprove.getEndTime();
		this.projectmanager = projectApprove.getProjectmanager();
		this.attachfile = projectApprove.getAttachfile();
		this.attachmessage = projectApprove.getAttachmessage();
	}

	public ProjectInfo() {
		super();
	}

	public ProjectInfo(String id){
		super(id);
	}

	
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="项目名称", align=2, sort=8)
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	
	@ExcelField(title="项目编号", align=2, sort=9)
	public String getProjectNum() {
		return projectNum;
	}

	public void setProjectNum(String projectNum) {
		this.projectNum = projectNum;
	}
	
	
	@ExcelField(title="项目性质", dictType="project_attribute", align=2, sort=10)
	public String getProjectAttribute() {
		return projectAttribute;
	}

	public void setProjectAttribute(String projectAttribute) {
		this.projectAttribute = projectAttribute;
	}
	
	
	@ExcelField(title="项目类型", dictType="project_type", align=2, sort=11)
	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	
	
	@ExcelField(title="产品类型", dictType="product", align=2, sort=12)
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
	@ExcelField(title="区域", fieldType=Area.class, value="area.name", align=2, sort=13)
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	
	@ExcelField(title="执行部门", fieldType=Office.class, value="office.name", align=2, sort=14)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	
	@ExcelField(title="设备商", dictType="device_company_type", align=2, sort=15)
	public String getDeviceCompany() {
		return deviceCompany;
	}

	public void setDeviceCompany(String deviceCompany) {
		this.deviceCompany = deviceCompany;
	}
	
	
	@ExcelField(title="合作对象", align=2, sort=16)
	public String getCooperative() {
		return cooperative;
	}

	public void setCooperative(String cooperative) {
		this.cooperative = cooperative;
	}
	
	
	@ExcelField(title="运营商", dictType="operator", align=2, sort=17)
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	
	@ExcelField(title="合同额", align=2, sort=18)
	public Double getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(Double contractAmount) {
		this.contractAmount = contractAmount;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="开工日期", align=2, sort=19)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="完工日期", align=2, sort=20)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
	@ExcelField(title="项目经理", fieldType=UserInfo.class, value="projectmanager.loginName", align=2, sort=21)
	public UserInfo getProjectmanager() {
		return projectmanager;
	}

	public void setProjectmanager(UserInfo projectmanager) {
		this.projectmanager = projectmanager;
	}
	
	
	@ExcelField(title="附件", align=2, sort=22)
	public String getAttachfile() {
		return attachfile;
	}

	public void setAttachfile(String attachfile) {
		this.attachfile = attachfile;
	}
	
	
	@ExcelField(title="简介", align=2, sort=23)
	public String getAttachmessage() {
		return attachmessage;
	}

	public void setAttachmessage(String attachmessage) {
		this.attachmessage = attachmessage;
	}
	
	
	@ExcelField(title="关闭标识", align=2, sort=24)
	public Integer getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(Integer isClosed) {
		this.isClosed = isClosed;
	}
	
	
}