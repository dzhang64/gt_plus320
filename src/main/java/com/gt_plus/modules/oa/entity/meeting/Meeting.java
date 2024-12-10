/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.meeting;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 会议管理Entity
 * @author LS0077
 * @version 2017-12-12
 */
public class Meeting extends ActEntity<Meeting> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	/*private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
*/	private String meetingName;		// 会议名称
	private String meetingSite;		// 会议地点
	private Date meetingStartDate;		// 开始时间
	private Date meetingEndDate;		// 结束时间
	private String compere;		// 主持人
	private String participationName;		// 与会人员
	private String file;		// 文件
	private String docFile;		// 正文
	
	public Meeting() {
		super();
	}

	public Meeting(String id){
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
	
	
	@ExcelField(title="会议名称", align=2, sort=11)
	public String getMeetingName() {
		return meetingName;
	}

	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}
	
	
	@ExcelField(title="会议地点", align=2, sort=12)
	public String getMeetingSite() {
		return meetingSite;
	}

	public void setMeetingSite(String meetingSite) {
		this.meetingSite = meetingSite;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="GMT+8") 
	@NotNull(message="开始时间不能为空") 
	@ExcelField(title="开始时间", align=2, sort=13)
	public Date getMeetingStartDate() {
		return meetingStartDate;
	}

	public void setMeetingStartDate(Date meetingStartDate) {
		this.meetingStartDate = meetingStartDate;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone="GMT+8") 
	@NotNull(message="结束时间不能为空") 
	@ExcelField(title="结束时间", align=2, sort=14)
	public Date getMeetingEndDate() {
		return meetingEndDate;
	}

	public void setMeetingEndDate(Date meetingEndDate) {
		this.meetingEndDate = meetingEndDate;
	}
	
	
	@ExcelField(title="主持人", align=2, sort=15)
	public String getCompere() {
		return compere;
	}

	public void setCompere(String compere) {
		this.compere = compere;
	}
	
	
	@ExcelField(title="与会人员", align=2, sort=16)
	public String getParticipationName() {
		return participationName;
	}

	public void setParticipationName(String participationName) {
		this.participationName = participationName;
	}
	
	
	@ExcelField(title="文件", align=2, sort=17)
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	
	@ExcelField(title="正文", align=2, sort=18)
	public String getDocFile() {
		return docFile;
	}

	public void setDocFile(String docFile) {
		this.docFile = docFile;
	}
	
	
}