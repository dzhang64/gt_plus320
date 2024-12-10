/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.entity.tasknotice;

import java.util.Date;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 任务通知Entity
 * @author GT0291
 * @version 2017-08-03
 */
public class BasTaskNotice extends DataEntity<BasTaskNotice> {
	
	private static final long serialVersionUID = 1L;
	private String messageId;		// 消息ID
	private String content;			// 通知内容
	private String userId;			// 接收用户ID
	private String userName;		// 接收人姓名
	private String createUserName;	// 提交人姓名
	private String officeName;		// 提交人归属机构
	private String status;			// 状态
	private String types;			// 类型
	private Date beginCreateDate;	// 开始 通知时间
	private Date endCreateDate;		// 结束 通知时间
		
	public BasTaskNotice() {
		super();
	}

	public BasTaskNotice(String id){
		super(id);
	}

	@ExcelField(title="消息ID", align=2, sort=1)
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	
	@ExcelField(title="通知内容", align=2, sort=7)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	@ExcelField(title="接收用户ID", align=2, sort=8)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	@ExcelField(title="接收人", align=2, sort=9)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	@ExcelField(title="接收人归属机构", align=2, sort=10)
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	
	@ExcelField(title="状态", dictType="oa_notify_read", align=2, sort=11)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public Date getBeginCreateDate() {
		return beginCreateDate;
	}

	public void setBeginCreateDate(Date beginCreateDate) {
		this.beginCreateDate = beginCreateDate;
	}
	
	public Date getEndCreateDate() {
		return endCreateDate;
	}

	public void setEndCreateDate(Date endCreateDate) {
		this.endCreateDate = endCreateDate;
	}
		
}