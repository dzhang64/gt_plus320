/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserinterentry.entity;

import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Area;
import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 实习生入职Entity
 * @author zdy
 * @version 2018-01-31
 */
public class SysInternUserInformation extends ActEntity<SysInternUserInformation> {
	private static final long serialVersionUID = 1L;
	private String procTaskName;		// 流程任务名称
	private String procInsId;		// 流程实例ID
	private String ownerCode;		// 归属机构编码
	private String name;		// 姓名
	private String idcardNum;		// 身份证编号
	private String photo;		// 照片
	private String gender;		// 性别
	private String nativePlace;		// 籍贯
	private String nameCode;		// 工号
	private String tel;		// 手机号码
	private Office office;		// 部门
	private Area area;		// 区域
	private String salaryCardNum;		// 工资卡号
	private String userStatus;		// 员工状态
	private String userType;		// 员工类型
	private SalaryLevel salary;		// 薪资级别
	private Double salaryLevel;		// 等级工资
	private String ownEmail;		// 个人邮箱
	private String emergencyUserName;		// 紧急联系人
	private String emergencyUserRelation;		// 紧急联系人关系
	private String emergencyUserPhone;		// 紧急联系人电话
	private String address;		// 通信地址
	private Date entryDate;		// 入职日期
	private Double serviceYear;		// 工作年限
	private String gradeSchool;		// 学生院校
	private String major;		// 学生专业
	private String ed;		// 学历
	private String bachelor;		// 学位
	private String foreignlanguageLevel;		// 外语等级
	private Date contractSartDate;		// 合同开始日期
	private Date contractEndDate;		// 合同结束日期
	private String contract;		// 合同编号
	private String contractType;		// 合同类型
	private String contractLimit;		// 合同期限类型
	private Area socailsecurityCity;		// 社保交纳地
	private Date internBeginDate;		// 实习开始日期
	private Date internEndDate;		// 实习结束日期
	private String idcardFile;		// 身份证
	private String gradeCertificate;		// 毕业证
	private String personResume;		// 人员简历
	private String comMail;		// 公司邮箱
	private String loginName;   //登陆名
	

	public SysInternUserInformation() {
		super();
	}

	public SysInternUserInformation(String id){
		super(id);
	}

	
	public String getProcTaskName() {
		return procTaskName;
	}

	public void setProcTaskName(String procTaskName) {
		this.procTaskName = procTaskName;
	}

	
	
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}
	
	
	
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="姓名", align=2, sort=11)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ExcelField(title="身份证编号", align=2, sort=12)
	public String getIdcardNum() {
		return idcardNum;
	}

	public void setIdcardNum(String idcardNum) {
		this.idcardNum = idcardNum;
	}
	
	
	@ExcelField(title="照片", align=2, sort=13)
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	
	@ExcelField(title="性别", dictType="sex", align=2, sort=14)
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	@ExcelField(title="籍贯", align=2, sort=15)
	public String getNativePlace() {
		return nativePlace;
	}

	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}
	
	
	
	public String getNameCode() {
		return nameCode;
	}

	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	
	
	@ExcelField(title="手机号码", align=2, sort=17)
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
	@ExcelField(title="部门", fieldType=Office.class, value="office.name", align=2, sort=18)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	
	@ExcelField(title="区域", fieldType=Area.class, value="area.name", align=2, sort=19)
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
	
	
	@ExcelField(title="工资卡号", align=2, sort=20)
	public String getSalaryCardNum() {
		return salaryCardNum;
	}

	public void setSalaryCardNum(String salaryCardNum) {
		this.salaryCardNum = salaryCardNum;
	}
	
	
	@ExcelField(title="员工状态", dictType="sys_user_status", align=2, sort=21)
	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	
	
	@ExcelField(title="员工类型", dictType="sys_user_type", align=2, sort=22)
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
	@ExcelField(title="薪资级别", fieldType=SalaryLevel.class, value="salary.level", align=2, sort=23)
	public SalaryLevel getSalary() {
		return salary;
	}

	public void setSalary(SalaryLevel salary) {
		this.salary = salary;
	}
	
	
	@ExcelField(title="等级工资", align=2, sort=24)
	public Double getSalaryLevel() {
		return salaryLevel;
	}

	public void setSalaryLevel(Double salaryLevel) {
		this.salaryLevel = salaryLevel;
	}
	
	
	@ExcelField(title="个人邮箱", align=2, sort=25)
	public String getOwnEmail() {
		return ownEmail;
	}

	public void setOwnEmail(String ownEmail) {
		this.ownEmail = ownEmail;
	}
	
	
	@ExcelField(title="紧急联系人", align=2, sort=26)
	public String getEmergencyUserName() {
		return emergencyUserName;
	}

	public void setEmergencyUserName(String emergencyUserName) {
		this.emergencyUserName = emergencyUserName;
	}
	
	
	@ExcelField(title="紧急联系人关系", align=2, sort=27)
	public String getEmergencyUserRelation() {
		return emergencyUserRelation;
	}

	public void setEmergencyUserRelation(String emergencyUserRelation) {
		this.emergencyUserRelation = emergencyUserRelation;
	}
	
	
	@ExcelField(title="紧急联系人电话", align=2, sort=28)
	public String getEmergencyUserPhone() {
		return emergencyUserPhone;
	}

	public void setEmergencyUserPhone(String emergencyUserPhone) {
		this.emergencyUserPhone = emergencyUserPhone;
	}
	
	
	@ExcelField(title="通信地址", align=2, sort=29)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="入职日期", align=2, sort=30)
	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	
	
	@ExcelField(title="工作年限", align=2, sort=31)
	public Double getServiceYear() {
		return serviceYear;
	}

	public void setServiceYear(Double serviceYear) {
		this.serviceYear = serviceYear;
	}
	
	
	@ExcelField(title="学生院校", align=2, sort=32)
	public String getGradeSchool() {
		return gradeSchool;
	}

	public void setGradeSchool(String gradeSchool) {
		this.gradeSchool = gradeSchool;
	}
	
	
	@ExcelField(title="学生专业", align=2, sort=33)
	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}
	
	
	@ExcelField(title="学历", align=2, sort=34)
	public String getEd() {
		return ed;
	}

	public void setEd(String ed) {
		this.ed = ed;
	}
	
	
	@ExcelField(title="学位", align=2, sort=35)
	public String getBachelor() {
		return bachelor;
	}

	public void setBachelor(String bachelor) {
		this.bachelor = bachelor;
	}
	
	
	@ExcelField(title="外语等级", dictType="sys_user_language", align=2, sort=36)
	public String getForeignlanguageLevel() {
		return foreignlanguageLevel;
	}

	public void setForeignlanguageLevel(String foreignlanguageLevel) {
		this.foreignlanguageLevel = foreignlanguageLevel;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="合同开始日期", align=2, sort=37)
	public Date getContractSartDate() {
		return contractSartDate;
	}

	public void setContractSartDate(Date contractSartDate) {
		this.contractSartDate = contractSartDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="合同结束日期", align=2, sort=38)
	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	
	
	@ExcelField(title="合同编号", align=2, sort=39)
	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}
	
	
	@ExcelField(title="合同类型", dictType="sys_user_contract", align=2, sort=40)
	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	
	
	@ExcelField(title="合同期限类型", dictType="sys_user_contract_year", align=2, sort=41)
	public String getContractLimit() {
		return contractLimit;
	}

	public void setContractLimit(String contractLimit) {
		this.contractLimit = contractLimit;
	}
	
	
	@ExcelField(title="社保交纳地", fieldType=Area.class, value="socailsecurityCity.name", align=2, sort=42)
	public Area getSocailsecurityCity() {
		return socailsecurityCity;
	}

	public void setSocailsecurityCity(Area socailsecurityCity) {
		this.socailsecurityCity = socailsecurityCity;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="实习开始日期", align=2, sort=43)
	public Date getInternBeginDate() {
		return internBeginDate;
	}

	public void setInternBeginDate(Date internBeginDate) {
		this.internBeginDate = internBeginDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="实习结束日期", align=2, sort=44)
	public Date getInternEndDate() {
		return internEndDate;
	}

	public void setInternEndDate(Date internEndDate) {
		this.internEndDate = internEndDate;
	}
	
	

	public String getIdcardFile() {
		return idcardFile;
	}

	public void setIdcardFile(String idcardFile) {
		this.idcardFile = idcardFile;
	}
	
	

	public String getGradeCertificate() {
		return gradeCertificate;
	}

	public void setGradeCertificate(String gradeCertificate) {
		this.gradeCertificate = gradeCertificate;
	}
	
	

	public String getPersonResume() {
		return personResume;
	}

	public void setPersonResume(String personResume) {
		this.personResume = personResume;
	}
	
	
	@ExcelField(title="公司邮箱", align=2, sort=48)
	public String getComMail() {
		return comMail;
	} 

	public void setComMail(String comMail) {
		this.comMail = comMail;
	}

	@ExcelField(title="登录名", align=2, sort=49)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
	
	
}