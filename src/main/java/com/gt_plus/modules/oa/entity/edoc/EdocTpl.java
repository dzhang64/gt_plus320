/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.edoc;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 模版类型Entity
 * @author GT0155
 * @version 2017-12-17
 */
public class EdocTpl extends DataEntity<EdocTpl> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String type;		// 模版类型
	private String files;		// 模版文件
	
	public EdocTpl() {
		super();
	}

	public EdocTpl(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="模版类型", dictType="oa_edoc_tpl_type", align=2, sort=8)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	@ExcelField(title="模版文件", align=2, sort=9)
	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
	
	
}