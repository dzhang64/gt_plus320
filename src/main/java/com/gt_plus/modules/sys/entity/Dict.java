/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import org.hibernate.validator.constraints.Length;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 字典Entity
 * @author GT0291
 * @version 2017-07-25
 */
public class Dict extends DataEntity<Dict> {
	
	private static final long serialVersionUID = 1L;
	private String value;		// 键值
	private String label;		// 标签
	private String types;		// 类型
	private Dict parent;		// 上级
	private String description;		// 描述
	private String view;		// 查询可见
	private String edit;		// 编辑可见
	private Integer sort;		// 排序
	
	public Dict() {
		super();
	}

	public Dict(String id){
		super(id);
	}

	@XmlAttribute
	@Length(min=1, max=100)
	@ExcelField(title="键值", align=2, sort=1)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlAttribute
	@Length(min=1, max=100)
	@ExcelField(title="标签", align=2, sort=2)
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	
	@ExcelField(title="类型", dictType="yes_no", align=2, sort=3)
	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}
	
	
	@JsonBackReference 
	@ExcelField(title="上级", align=2, sort=4)
	public Dict getParent() {
		return parent;
	}

	public void setParent(Dict parent) {
		this.parent = parent;
	}
	
	@XmlAttribute
	@Length(min=1, max=100)
	@ExcelField(title="描述", align=2, sort=5)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	@ExcelField(title="查询可见", dictType="yes_no", align=2, sort=12)
	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
	
	@ExcelField(title="编辑可见", dictType="yes_no", align=2, sort=13)
	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}
	
	
	@NotNull(message="排序不能为空") 
	@ExcelField(title="排序", align=2, sort=14)
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	
}