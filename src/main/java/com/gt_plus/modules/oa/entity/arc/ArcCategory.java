/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.arc;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;

import com.gt_plus.common.persistence.TreeEntity;

/**
 * 档案目录Entity
 * @author LS0077
 * @version 2017-11-07
 */
public class ArcCategory extends TreeEntity<ArcCategory> {
	
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	
	public ArcCategory() {
		super();
	}

	public ArcCategory(String id){
		super(id);
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	public  ArcCategory getParent() {
			return parent;
	}
	
	@Override
	public void setParent(ArcCategory parent) {
		this.parent = parent;
	}
	
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}
}