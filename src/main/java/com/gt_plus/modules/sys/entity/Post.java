/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import javax.validation.constraints.NotNull;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 岗位Entity
 * @author David
 * @version 2017-11-01
 */
public class Post extends DataEntity<Post> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 岗位名称
	private String code;		// 岗位代码
	private String typeCode;		// 岗位类别
	private Integer sort;		// 排序号
	private String useable;		// 状态
	
	private String officeId;    //机构编码，用于过滤数据
	
	public Post() {
		super();
	}

	public Post(String id){
		super(id);
	}

	@ExcelField(title="岗位名称", align=2, sort=6)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ExcelField(title="岗位代码", align=2, sort=7)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	@ExcelField(title="岗位类别", dictType="sys_post_type", align=2, sort=8)
	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	
	
	@NotNull(message="排序号不能为空") 
	@ExcelField(title="排序号", align=2, sort=9)
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	
	@ExcelField(title="状态", dictType="sys_useable", align=2, sort=10)
	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
}