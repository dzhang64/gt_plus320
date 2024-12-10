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
 * 收文管理Entity
 * @author GT0155
 * @version 2017-12-08
 */
public class EdocReceive extends ActEntity<EdocReceive> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
/*	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
*/	private String dense;		// 密级
	private String year;		// 年度
	private String secrecyLimit;		// 保密期限
	private String urgency;		// 缓急
	private String leaderResult;		// 局领导批示
	private String officeResult;		// 办公室批分
	private String partResult;		// 处室办理结果
	private String type;		// 类别
	private String innerNumber;		// 内部编号
	private Date receiveDate;		// 收文日期
	private String receiveFrom;		// 来文单位
	private String docNumber;		// 文号
	private String timeLimit;		// 限时
	private String title;		// 标题
	private String files;		// 附件
	private String contentUrl;		// 正文URL
	
	public EdocReceive() {
		super();
	}

	public EdocReceive(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	/*@ExcelField(title="流程实例ID", align=2, sort=8)
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
	
	
	@ExcelField(title="密级", dictType="oa_edoc_dense", align=2, sort=11)
	public String getDense() {
		return dense;
	}

	public void setDense(String dense) {
		this.dense = dense;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@ExcelField(title="保密期限", align=2, sort=13)
	public String getSecrecyLimit() {
		return secrecyLimit;
	}

	public void setSecrecyLimit(String secrecyLimit) {
		this.secrecyLimit = secrecyLimit;
	}
	
	
	@ExcelField(title="缓急", dictType="oa_edoc_urgency", align=2, sort=14)
	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}
	
	
	@ExcelField(title="局领导批示", align=2, sort=15)
	public String getLeaderResult() {
		return leaderResult;
	}

	public void setLeaderResult(String leaderResult) {
		this.leaderResult = leaderResult;
	}
	
	
	@ExcelField(title="办公室批分", align=2, sort=16)
	public String getOfficeResult() {
		return officeResult;
	}

	public void setOfficeResult(String officeResult) {
		this.officeResult = officeResult;
	}
	
	
	@ExcelField(title="处室办理结果", align=2, sort=17)
	public String getPartResult() {
		return partResult;
	}

	public void setPartResult(String partResult) {
		this.partResult = partResult;
	}
	
	
	@ExcelField(title="类别", dictType="oa_edoc_type", align=2, sort=18)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	@ExcelField(title="内部编号", align=2, sort=19)
	public String getInnerNumber() {
		return innerNumber;
	}

	public void setInnerNumber(String innerNumber) {
		this.innerNumber = innerNumber;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8") 
	@ExcelField(title="收文日期", align=2, sort=20)
	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	
	@ExcelField(title="来文单位", align=2, sort=21)
	public String getReceiveFrom() {
		return receiveFrom;
	}

	public void setReceiveFrom(String receiveFrom) {
		this.receiveFrom = receiveFrom;
	}
	
	
	@ExcelField(title="文号", align=2, sort=22)
	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}
	
	
	@ExcelField(title="限时", align=2, sort=23)
	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	
	@ExcelField(title="标题", align=2, sort=24)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	@ExcelField(title="附件", align=2, sort=25)
	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
	
	
	@ExcelField(title="正文URL", align=2, sort=26)
	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	
	
}