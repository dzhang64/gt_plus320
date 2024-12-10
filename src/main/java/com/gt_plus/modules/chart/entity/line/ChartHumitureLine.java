/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.entity.line;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 温湿度曲线图Entity
 * @author GT0155
 * @version 2017-09-06
 */
public class ChartHumitureLine extends DataEntity<ChartHumitureLine> {
	
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	
	public ChartHumitureLine() {
		super();
	}

	public ChartHumitureLine(String id){
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