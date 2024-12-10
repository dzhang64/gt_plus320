/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.matter;

import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 协同事项Entity
 * @author LS0077
 * @version 2017-11-08
 */
public class SynergyMatter extends DataEntity<SynergyMatter> {
	
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String subject;		// 标题
	private String file;		// 附件
	private Office arcCategory;		// 预归档
	
	public SynergyMatter() {
		super();
	}

	public SynergyMatter(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="标题", align=2, sort=8)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
	@ExcelField(title="附件", align=2, sort=9)
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	
	@ExcelField(title="预归档", fieldType=Office.class, value="", align=2, sort=10)
	public Office getArcCategory() {
		return arcCategory;
	}

	public void setArcCategory(Office arcCategory) {
		this.arcCategory = arcCategory;
	}
	
	
}