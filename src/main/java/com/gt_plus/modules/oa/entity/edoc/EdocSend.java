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
 * 发文管理Entity
 * @author GT0155
 * @version 2017-12-08
 */
public class EdocSend extends ActEntity<EdocSend> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	/*private String procInsId;		// 流程实例
	private String procTaskName;		// 任务名称
	private String procTaskPermission;		// 任务权限*/
	private String paper;		// 国家物资储备发文稿纸
	private String dense;		// 密级
	private String denseLimit;		// 密级期限
	private String urgency1;		// 缓急1
	private String urgency2;		// 缓急2
	private String urgency3;		// 缓急3
	private String year;		// 年
	private String number;		// 号
	private String sign;		// 签发
	private String countersign;		// 会签
	private String sendTo;		// 主送单位
	private String copyTo;		// 抄送单位
	private String files;		// 电子附件
	private String paperFiles;		// 纸质附件
	private String title;		// 标题
	private String active;		// 主动公开
	private String byApply;		// 依申请公开
	private String inactive;		// 不公开
	private String reportInfo;		// 上报信息
	private String officeNetwork;		// 加载机关内网
	private String systemNetwork;		// 加载系统专网
	private String normativeDoc;		// 规范性文件
	private String innovationItem;		// 创新事项
	private String handleOrg;		// 主办单位
	private String handler;		// 负责人
	private Date handleDate;		// 主办日期
	private String drafter;		// 拟稿人
	private String phone;		// 电话
	private Date draftDate;		// 拟稿日期
	private String office;		// 办公室
	private String reviewer;		// 核稿人
	private Date reviewDate;		// 核稿日期
	private String printCount;		// 打印份数
	private String proofreader;		// 校对人
	private String sendType;		// 发送类型
	private String contentUrl;		// 正文URL
	
	public EdocSend() {
		super();
	}

	public EdocSend(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	/*@ExcelField(title="流程实例", align=2, sort=8)
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}
	
	
	@ExcelField(title="任务名称", align=2, sort=9)
	public String getProcTaskName() {
		return procTaskName;
	}

	public void setProcTaskName(String procTaskName) {
		this.procTaskName = procTaskName;
	}
	
	
	@ExcelField(title="任务权限", align=2, sort=10)
	public String getProcTaskPermission() {
		return procTaskPermission;
	}

	public void setProcTaskPermission(String procTaskPermission) {
		this.procTaskPermission = procTaskPermission;
	}*/
	
	
	@ExcelField(title="国家物资储备发文稿纸", dictType="oa_edoc_paper", align=2, sort=11)
	public String getPaper() {
		return paper;
	}

	public void setPaper(String paper) {
		this.paper = paper;
	}
	
	
	@ExcelField(title="密级", dictType="oa_edoc_dense2", align=2, sort=12)
	public String getDense() {
		return dense;
	}

	public void setDense(String dense) {
		this.dense = dense;
	}
	
	
	@ExcelField(title="密级期限", dictType="", align=2, sort=13)
	public String getDenseLimit() {
		return denseLimit;
	}

	public void setDenseLimit(String denseLimit) {
		this.denseLimit = denseLimit;
	}
	
	
	@ExcelField(title="缓急1", dictType="oa_edoc_urgency", align=2, sort=14)
	public String getUrgency1() {
		return urgency1;
	}

	public void setUrgency1(String urgency1) {
		this.urgency1 = urgency1;
	}
	
	
	@ExcelField(title="缓急2", dictType="oa_edoc_urgency2", align=2, sort=15)
	public String getUrgency2() {
		return urgency2;
	}

	public void setUrgency2(String urgency2) {
		this.urgency2 = urgency2;
	}
	
	
	@ExcelField(title="缓急3", align=2, sort=16)
	public String getUrgency3() {
		return urgency3;
	}

	public void setUrgency3(String urgency3) {
		this.urgency3 = urgency3;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@ExcelField(title="签发", align=2, sort=19)
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	@ExcelField(title="会签", align=2, sort=20)
	public String getCountersign() {
		return countersign;
	}

	public void setCountersign(String countersign) {
		this.countersign = countersign;
	}
	
	
	@ExcelField(title="主送单位", align=2, sort=21)
	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}
	
	
	@ExcelField(title="抄送单位", align=2, sort=22)
	public String getCopyTo() {
		return copyTo;
	}

	public void setCopyTo(String copyTo) {
		this.copyTo = copyTo;
	}
	
	
	@ExcelField(title="电子附件", align=2, sort=23)
	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
	
	
	@ExcelField(title="纸质附件", align=2, sort=24)
	public String getPaperFiles() {
		return paperFiles;
	}

	public void setPaperFiles(String paperFiles) {
		this.paperFiles = paperFiles;
	}
	
	
	@ExcelField(title="标题", align=2, sort=25)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	@ExcelField(title="主动公开", dictType="oa_edoc_common_option", align=2, sort=26)
	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
	
	
	@ExcelField(title="依申请公开", dictType="oa_edoc_common_option", align=2, sort=27)
	public String getByApply() {
		return byApply;
	}

	public void setByApply(String byApply) {
		this.byApply = byApply;
	}
	
	
	@ExcelField(title="不公开", dictType="oa_edoc_common_option", align=2, sort=28)
	public String getInactive() {
		return inactive;
	}

	public void setInactive(String inactive) {
		this.inactive = inactive;
	}
	
	
	@ExcelField(title="上报信息", dictType="oa_edoc_common_option", align=2, sort=29)
	public String getReportInfo() {
		return reportInfo;
	}

	public void setReportInfo(String reportInfo) {
		this.reportInfo = reportInfo;
	}
	
	
	@ExcelField(title="加载机关内网", dictType="oa_edoc_common_option", align=2, sort=30)
	public String getOfficeNetwork() {
		return officeNetwork;
	}

	public void setOfficeNetwork(String officeNetwork) {
		this.officeNetwork = officeNetwork;
	}
	
	
	@ExcelField(title="加载系统专网", dictType="oa_edoc_common_option", align=2, sort=31)
	public String getSystemNetwork() {
		return systemNetwork;
	}

	public void setSystemNetwork(String systemNetwork) {
		this.systemNetwork = systemNetwork;
	}
	
	
	@ExcelField(title="规范性文件", dictType="oa_edoc_common_option", align=2, sort=32)
	public String getNormativeDoc() {
		return normativeDoc;
	}

	public void setNormativeDoc(String normativeDoc) {
		this.normativeDoc = normativeDoc;
	}
	
	
	@ExcelField(title="创新事项", dictType="oa_edoc_common_option", align=2, sort=33)
	public String getInnovationItem() {
		return innovationItem;
	}

	public void setInnovationItem(String innovationItem) {
		this.innovationItem = innovationItem;
	}
	
	
	@ExcelField(title="主办单位", align=2, sort=34)
	public String getHandleOrg() {
		return handleOrg;
	}

	public void setHandleOrg(String handleOrg) {
		this.handleOrg = handleOrg;
	}
	
	
	@ExcelField(title="负责人", align=2, sort=35)
	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="主办日期", align=2, sort=36)
	public Date getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Date handleDate) {
		this.handleDate = handleDate;
	}
	
	
	@ExcelField(title="拟稿人", align=2, sort=37)
	public String getDrafter() {
		return drafter;
	}

	public void setDrafter(String drafter) {
		this.drafter = drafter;
	}
	
	
	@ExcelField(title="电话", align=2, sort=38)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="拟稿日期", align=2, sort=39)
	public Date getDraftDate() {
		return draftDate;
	}

	public void setDraftDate(Date draftDate) {
		this.draftDate = draftDate;
	}
	
	
	@ExcelField(title="办公室", align=2, sort=40)
	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}
	
	
	@ExcelField(title="核稿人", align=2, sort=41)
	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="核稿日期", align=2, sort=42)
	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	
	
	@ExcelField(title="打印份数", align=2, sort=43)
	public String getPrintCount() {
		return printCount;
	}

	public void setPrintCount(String printCount) {
		this.printCount = printCount;
	}
	
	
	@ExcelField(title="校对人", align=2, sort=44)
	public String getProofreader() {
		return proofreader;
	}

	public void setProofreader(String proofreader) {
		this.proofreader = proofreader;
	}
	
	
	@ExcelField(title="发送类型", dictType="oa_edoc_sendType", align=2, sort=45)
	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

}