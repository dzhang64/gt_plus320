/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusersscentry.entity;

import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 社保统筹补录入Entity
 * @author wl
 * @version 2018-03-30
 */
public class SysUserSscEntry extends DataEntity<SysUserSscEntry> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private UserInfo name;		// 员工姓名
	private String nameCode;		// 工号
	private String officeName;		// 部门
	private Area ssc;		// 社保交纳地
	private String coordinateNum;		// 统筹号
	private String providentNum;		// 公积金账号
	
	public SysUserSscEntry() {
		super();
	}

	public SysUserSscEntry(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="员工姓名", align=2, sort=8)
	public UserInfo getName() {
		return name;
	}

	public void setName(UserInfo name) {
		this.name = name;
	}
	
	
	@ExcelField(title="工号", align=2, sort=9)
	public String getNameCode() {
		return nameCode;
	}

	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	
	
	@ExcelField(title="部门", align=2, sort=10)
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	
	@ExcelField(title="社保交纳地", fieldType=Area.class, value="ssc.name", align=2, sort=11)
	public Area getSsc() {
		return ssc;
	}

	public void setSsc(Area ssc) {
		this.ssc = ssc;
	}
	
	
	@ExcelField(title="统筹号", align=2, sort=12)
	public String getCoordinateNum() {
		return coordinateNum;
	}

	public void setCoordinateNum(String coordinateNum) {
		this.coordinateNum = coordinateNum;
	}
	
	
	@ExcelField(title="公积金账号", align=2, sort=13)
	public String getProvidentNum() {
		return providentNum;
	}

	public void setProvidentNum(String providentNum) {
		this.providentNum = providentNum;
	}
	
	
}