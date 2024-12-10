/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.setting;

import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 权限分类Entity
 * @author GT0155
 * @version 2017-12-25
 */
public class TaskPermission extends DataEntity<TaskPermission> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String name;		// 权限名称
	private String type;		// 权限类型
	private String category;		// 权限类别
	private String isInuse;		// 是否启用
	private String position;		// 权限位置
	private String describe;		// 描述
	private String operation;		// 高级操作
	
	public TaskPermission() {
		super();
	}

	public TaskPermission(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="权限名称", align=2, sort=8)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ExcelField(title="权限类型", dictType="oa_task_permission_type", align=2, sort=9)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	@ExcelField(title="权限类别", dictType="oa_task_permission_category", align=2, sort=10)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	@ExcelField(title="是否启用", dictType="yes_no", align=2, sort=11)
	public String getIsInuse() {
		return isInuse;
	}

	public void setIsInuse(String isInuse) {
		this.isInuse = isInuse;
	}
	
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@ExcelField(title="描述", align=2, sort=12)
	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	
	@ExcelField(title="高级操作", align=2, sort=13)
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = Encodes.unescapeHtml(operation);
	}
	
	
}