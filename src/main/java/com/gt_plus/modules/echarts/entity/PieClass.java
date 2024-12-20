/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.echarts.entity;

import org.hibernate.validator.constraints.Length;

import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 班级Entity
 * @author lgf
 * @version 2016-05-26
 */
public class PieClass extends DataEntity<PieClass> {
	
	private static final long serialVersionUID = 1L;
	private String className;		// 班级
	private Integer num;		// 人数
	
	public PieClass() {
		super();
	}

	public PieClass(String id){
		super(id);
	}

	@Length(min=0, max=64, message="班级长度必须介于 0 和 64 之间")
	@ExcelField(title="班级", align=2, sort=7)
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	@ExcelField(title="人数", align=2, sort=8)
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
}