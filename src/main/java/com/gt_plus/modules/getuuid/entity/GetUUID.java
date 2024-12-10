/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.getuuid.entity;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 生成UUIDEntity
 * @author zdyt
 * @version 2018-01-30
 */
public class GetUUID extends DataEntity<GetUUID> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private int num;		// UUID数量
	
	public GetUUID() {
		super();
	}

	public GetUUID(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="UUID数量", align=2, sort=8)
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	
}