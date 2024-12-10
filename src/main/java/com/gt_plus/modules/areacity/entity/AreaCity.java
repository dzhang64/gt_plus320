/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.areacity.entity;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 省地市Entity
 * @author wl
 * @version 2018-01-15
 */
public class AreaCity extends DataEntity<AreaCity> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String area;		// 省
	private String city;		// 地市
	//com.gt_plus.modules.areacity.entity.AreaCity
	public AreaCity() {
		super();
	}

	public AreaCity(String id){
		super(id);
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="省", align=2, sort=8)
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
	
	@ExcelField(title="地市", align=2, sort=9)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	
}