/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.entity.bgcolumn;

import java.util.List;

import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 项目预算行Entity
 * @author zdy
 * @version 2018-03-21
 */
public class ProjectBg extends DataEntity<ProjectBgColumn> {
	
	private List<ProjectBgColumn> columnList;
	
	
	public ProjectBg() {
		super();
	}

	public ProjectBg(String id){
		super(id);
	}

	
	public List<ProjectBgColumn> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<ProjectBgColumn> columnList) {
		this.columnList = columnList;
	}
	
	
}