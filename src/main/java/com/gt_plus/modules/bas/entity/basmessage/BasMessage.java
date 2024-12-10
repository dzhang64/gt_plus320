/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.entity.basmessage;

import java.util.Date;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 消息Entity
 * @author GT0291
 * @version 2017-08-03
 */
public class BasMessage extends DataEntity<BasMessage> {
	
	private static final long serialVersionUID = 1L;
	private String types;		// 消息类型
	private String content;		// 消息内容
	private String status;		// 是否处理
	private Date beginCreateDate;		// 开始 创建时间
	private Date endCreateDate;		// 结束 创建时间
	
	public BasMessage() {
		super();
	}

	public BasMessage(String id){
		super(id);
	}

	@ExcelField(title="消息类型", align=2, sort=6)
	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}
	
	
	@ExcelField(title="消息内容", align=2, sort=7)
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	@ExcelField(title="是否处理", dictType="yes_no", align=2, sort=8)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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