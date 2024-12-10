/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import com.gt_plus.modules.sys.entity.Office;
import javax.validation.constraints.NotNull;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 组织管理Entity
 * @author LS0195
 * @version 2017-12-07
 */
public class Organization extends DataEntity<Organization> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String orgNumber;		// 组织编号
	private String orgName;		// 组织名称
	private User primaryPerson;		// 组织负责人
	private String orgEffect;		// 是否有效
	private Integer orgSequenceNumber;		// 序号
	
	public Organization() {
		super();
	}

	public Organization(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="组织编号", align=2, sort=8)
	public String getOrgNumber() {
		return orgNumber;
	}

	public void setOrgNumber(String orgNumber) {
		this.orgNumber = orgNumber;
	}
	
	
	@ExcelField(title="组织名称", align=2, sort=9)
	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	
	@NotNull(message="组织负责人不能为空") 
	@ExcelField(title="组织负责人", fieldType=Office.class, value="primaryPerson.name", align=2, sort=10)
	public User getPrimaryPerson() {
		return primaryPerson;
	}

	public void setPrimaryPerson(User primaryPerson) {
		this.primaryPerson = primaryPerson;
	}
	
	
	@ExcelField(title="是否有效", dictType="yes_no", align=2, sort=11)
	public String getOrgEffect() {
		return orgEffect;
	}

	public void setOrgEffect(String orgEffect) {
		this.orgEffect = orgEffect;
	}
	
	
	@ExcelField(title="序号", align=2, sort=12)
	public Integer getOrgSequenceNumber() {
		return orgSequenceNumber;
	}

	public void setOrgSequenceNumber(Integer orgSequenceNumber) {
		this.orgSequenceNumber = orgSequenceNumber;
	}
	
	
}