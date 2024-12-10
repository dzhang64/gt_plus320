/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.edoc;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 签报管理Entity
 * @author GT0155
 * @version 2017-12-08
 */
public class EdocSign extends ActEntity<EdocSign> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	/*private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
*/	private String dense;		// 密级
	private String urgency;		// 缓急
	private String orgDepartment;		// 国家物资储备局处 
	private String docNumber;		// 公文文号
	private String leaderInstructions;		// 局领导批示
	private String sendTo;		// 呈送
	private String sendToMainPerson;		//  呈报单位负责人
	private String signMainPerson;		// 会签单位负责人
	private String title;		// 标题
	private String files;		// 附件
	private String drafter;		// 拟稿人
	private String officeAgent;		// 办公室经办人
	private String phone;		// 电话
	private Date agentDate;		// 经办日期
	private String reportInfo;		// 上报信息
	private String officeNetwork;		// 加载机关内网
	private String systemNetwork;		// 加载系统专网
	private String innovationItem;		// 创新事项
	private String result;		// 批示页处理情况
	private String contentUrl;		// 正文URL
	
	public EdocSign() {
		super();
	}

	public EdocSign(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
/*	@ExcelField(title="流程实例ID", align=2, sort=8)
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
	
	
	@ExcelField(title="流程任务权限", align=2, sort=10)
	public String getProcTaskPermission() {
		return procTaskPermission;
	}

	public void setProcTaskPermission(String procTaskPermission) {
		this.procTaskPermission = procTaskPermission;
	}*/
	
	
	@ExcelField(title="密级", dictType="oa_edoc_dense2", align=2, sort=11)
	public String getDense() {
		return dense;
	}

	public void setDense(String dense) {
		this.dense = dense;
	}
	
	
	@ExcelField(title="缓急", dictType="oa_edoc_urgency", align=2, sort=12)
	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}
	
	
	@ExcelField(title="国家物资储备局处 ", align=2, sort=13)
	public String getOrgDepartment() {
		return orgDepartment;
	}

	public void setOrgDepartment(String orgDepartment) {
		this.orgDepartment = orgDepartment;
	}
	
	
	@ExcelField(title="公文文号", dictType="oa_edoc_doc_number", align=2, sort=14)
	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}
	
	
	@ExcelField(title="局领导批示", align=2, sort=15)
	public String getLeaderInstructions() {
		return leaderInstructions;
	}

	public void setLeaderInstructions(String leaderInstructions) {
		this.leaderInstructions = leaderInstructions;
	}
	
	
	@ExcelField(title="呈送", align=2, sort=16)
	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}
	
	
	@ExcelField(title=" 呈报单位负责人", align=2, sort=17)
	public String getSendToMainPerson() {
		return sendToMainPerson;
	}

	public void setSendToMainPerson(String sendToMainPerson) {
		this.sendToMainPerson = sendToMainPerson;
	}
	
	
	@ExcelField(title="会签单位负责人", align=2, sort=18)
	public String getSignMainPerson() {
		return signMainPerson;
	}

	public void setSignMainPerson(String signMainPerson) {
		this.signMainPerson = signMainPerson;
	}
	
	
	@ExcelField(title="标题", align=2, sort=19)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	@ExcelField(title="附件", align=2, sort=20)
	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
	
	
	@ExcelField(title="拟稿人", align=2, sort=21)
	public String getDrafter() {
		return drafter;
	}

	public void setDrafter(String drafter) {
		this.drafter = drafter;
	}
	
	
	@ExcelField(title="办公室经办人", align=2, sort=22)
	public String getOfficeAgent() {
		return officeAgent;
	}

	public void setOfficeAgent(String officeAgent) {
		this.officeAgent = officeAgent;
	}
	
	
	@ExcelField(title="电话", align=2, sort=23)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="经办日期", align=2, sort=24)
	public Date getAgentDate() {
		return agentDate;
	}

	public void setAgentDate(Date agentDate) {
		this.agentDate = agentDate;
	}
	
	
	@ExcelField(title="上报信息", dictType="oa_edoc_common_option", align=2, sort=25)
	public String getReportInfo() {
		return reportInfo;
	}

	public void setReportInfo(String reportInfo) {
		this.reportInfo = reportInfo;
	}
	
	
	@ExcelField(title="加载机关内网", dictType="oa_edoc_common_option", align=2, sort=26)
	public String getOfficeNetwork() {
		return officeNetwork;
	}

	public void setOfficeNetwork(String officeNetwork) {
		this.officeNetwork = officeNetwork;
	}
	
	
	@ExcelField(title="加载系统专网", dictType="oa_edoc_common_option", align=2, sort=27)
	public String getSystemNetwork() {
		return systemNetwork;
	}

	public void setSystemNetwork(String systemNetwork) {
		this.systemNetwork = systemNetwork;
	}
	
	
	@ExcelField(title="创新事项", dictType="oa_edoc_common_option", align=2, sort=28)
	public String getInnovationItem() {
		return innovationItem;
	}

	public void setInnovationItem(String innovationItem) {
		this.innovationItem = innovationItem;
	}
	
	
	@ExcelField(title="批示页处理情况", align=2, sort=29)
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
	@ExcelField(title="正文URL", align=2, sort=30)
	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	
	
}