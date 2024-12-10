/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.entity.bgentry;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;

import com.gt_plus.common.persistence.TreeEntity;

/**
 * 预算条目字典Entity
 * @author zdy
 * @version 2018-03-19
 */
public class ProjectBgEntry extends TreeEntity<ProjectBgEntry> {
	
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	
	public ProjectBgEntry() {
		super();
	}

	public ProjectBgEntry(String id){
		super(id);
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	public  ProjectBgEntry getParent() {
			return parent;
	}
	
	@Override
	public void setParent(ProjectBgEntry parent) {
		this.parent = parent;
	}
	
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}
}