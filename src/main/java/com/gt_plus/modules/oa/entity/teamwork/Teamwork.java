/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.teamwork;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.ActEntity;

/**
 * 协同管理Entity
 * @author LS0077
 * @version 2017-12-11
 */
public class Teamwork extends ActEntity<Teamwork> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
/*	private String procInsId;		// 流程实例ID
	private String procTaskName;		// 流程任务名称
	private String procTaskPermission;		// 流程任务权限
*/	private String subject;		// 标题
	private String urgentLevel;		// 缓急
	private String file;		// 附件
	private String docFile;		// 正文
	private String creatName;   //创建人
	
	
	
	public String getCreatName() {
		return creatName;
	}

	public void setCreatName(String creatName) {
		this.creatName = creatName;
	}

	public Teamwork() {
		super();
	}

	public Teamwork(String id){
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
	
	
	@ExcelField(title="标题", align=2, sort=11)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
	@ExcelField(title="缓急", dictType="oa_team_urgent_level", align=2, sort=12)
	public String getUrgentLevel() {
		return urgentLevel;
	}

	public void setUrgentLevel(String urgentLevel) {
		this.urgentLevel = urgentLevel;
	}
	
	
	@ExcelField(title="附件", align=2, sort=13)
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	
	@ExcelField(title="正文", align=2, sort=14)
	public String getDocFile() {
		return docFile;
	}

	public void setDocFile(String docFile) {
		this.docFile = docFile;
	}
	
	
}