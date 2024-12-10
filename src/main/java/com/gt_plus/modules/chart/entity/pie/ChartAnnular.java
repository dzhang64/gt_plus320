/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.entity.pie;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 环形图Entity
 * @author GT0155
 * @version 2017-09-07
 */
public class ChartAnnular extends DataEntity<ChartAnnular> {
	
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	
	public ChartAnnular() {
		super();
	}

	public ChartAnnular(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
}