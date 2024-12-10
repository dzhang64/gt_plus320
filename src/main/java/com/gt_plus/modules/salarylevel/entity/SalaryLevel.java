/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.salarylevel.entity;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 薪资级别Entity
 * @author zdy
 * @version 2018-01-30
 */
public class SalaryLevel extends DataEntity<SalaryLevel> {
	private static final long serialVersionUID = 1L;
	private String level;		// 等级
	private String basicWage;		// 基本工资
	private String performancePay;		// 绩效工资
	private String subsidy;		// 补贴
	//com.gt_plus.modules.salarylevel.entity.SalaryLevel
	public SalaryLevel() {
		super();
	}

	public SalaryLevel(String id){
		super(id);
	}

	@ExcelField(title="等级", align=2, sort=6)
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	
	@ExcelField(title="基本工资", align=2, sort=7)
	public String getBasicWage() {
		return basicWage;
	}

	public void setBasicWage(String basicWage) {
		this.basicWage = basicWage;
	}
	
	
	@ExcelField(title="绩效工资", align=2, sort=8)
	public String getPerformancePay() {
		return performancePay;
	}

	public void setPerformancePay(String performancePay) {
		this.performancePay = performancePay;
	}
	
	
	@ExcelField(title="补贴", align=2, sort=9)
	public String getSubsidy() {
		return subsidy;
	}

	public void setSubsidy(String subsidy) {
		this.subsidy = subsidy;
	}

	@Override
	public String toString() {
		return level;
	}
	
	
	
	
}