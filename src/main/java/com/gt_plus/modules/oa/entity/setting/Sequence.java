/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.setting;

import javax.validation.constraints.NotNull;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 文单序列Entity
 * @author GT0291
 * @version 2017-12-25
 */
public class Sequence extends DataEntity<Sequence> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String key;				// 编码
	private String name;			// 名称
	private Integer startValue;		// 起始值
	private Integer currentValue;	// 当前值
	private Integer nextValue;      //下一个值
	private Integer year;			// 当前年度
	private String cycleByYear;		// 按年循环
	private Integer lastYearValue;	// 上年度值
	private Integer confirmNextValue;      //确认值，用于判断并发冲突
	
	public Sequence() {
		super();
	}

	public Sequence(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=6)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="编码", align=2, sort=7)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
	@ExcelField(title="名称", align=2, sort=8)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@NotNull(message="起始值不能为空") 
	@ExcelField(title="起始值", align=2, sort=9)
	public Integer getStartValue() {
		return startValue;
	}

	public void setStartValue(Integer startValue) {
		this.startValue = startValue;
	}
	
	
	@NotNull(message="当前值不能为空") 
	@ExcelField(title="当前值", align=2, sort=10)
	public Integer getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Integer currentValue) {
		this.currentValue = currentValue;
	}
	
	@ExcelField(title="下一个值", align=2, sort=10)
	public Integer getNextValue() {
		return nextValue;
	}

	public void setNextValue(Integer nextValue) {
		this.nextValue = nextValue;
	}
	
	
	@NotNull(message="当前年度不能为空") 
	@ExcelField(title="当前年度", align=2, sort=11)
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	
	@ExcelField(title="按年循环", dictType="yes_no", align=2, sort=12)
	public String getCycleByYear() {
		return cycleByYear;
	}

	public void setCycleByYear(String cycleByYear) {
		this.cycleByYear = cycleByYear;
	}
	
	
	@NotNull(message="上年度值不能为空") 
	@ExcelField(title="上年度值", align=2, sort=13)
	public Integer getLastYearValue() {
		return lastYearValue;
	}

	public void setLastYearValue(Integer lastYearValue) {
		this.lastYearValue = lastYearValue;
	}

	public Integer getConfirmNextValue() {
		return confirmNextValue;
	}

	public void setConfirmNextValue(Integer confirmNextValue) {
		this.confirmNextValue = confirmNextValue;
	}
}