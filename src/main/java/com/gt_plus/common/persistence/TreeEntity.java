/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.common.persistence;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gt_plus.common.utils.Reflections;
import com.gt_plus.common.utils.StringUtils;

/**
 * 数据Entity类
 * @author gt_plus
 * @version 2014-05-16
 */
public abstract class TreeEntity<T> extends DataEntity<T> {

	private static final long serialVersionUID = 1L;

	protected T parent;	            // 父级编号
	protected String parentIds;     // 所有父级编号
	protected String name; 	        // 机构名称
	protected String fullPathName; 	// 全路径名称
	protected Integer sort;		    // 排序
	private boolean hasChildren;    //是否有子节点
	
	public TreeEntity() {
		super();
		this.sort = 30;
	}
	
	public TreeEntity(String id) {
		super(id);
	}
	
	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	@JsonBackReference
	@NotNull
	public abstract T getParent();

	/**
	 * 父对象，只能通过子类实现，父类实现mybatis无法读取
	 * @return
	 */
	public abstract void setParent(T parent);

	@Length(min=1, max=2000)
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getFullPathName() {
		if (false == StringUtils.isEmpty(fullPathName) && fullPathName.indexOf(",") == 0) {
			return fullPathName.substring(1);
		} else {
			return fullPathName;
		}
	}

	public void setFullPathName(String fullPathName) {
		this.fullPathName = fullPathName;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	public String getParentId() {
		String id = null;
		if (parent != null){
			id = (String)Reflections.getFieldValue(parent, "id");
		}
		return StringUtils.isNotBlank(id) ? id : "0";
	}
	
	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
}
