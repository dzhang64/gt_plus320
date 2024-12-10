/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.entity.bgcolumn;

import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 项目预算行Entity
 * @author zdy
 * @version 2018-03-21
 */
public class ProjectBgColumn extends DataEntity<ProjectBgColumn> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private ProjectInfo projectInfo;		// 项目信息
	private Integer sort;		// 序号
	private String bgSort;		// 预算分类
	private String bgType;		// 预算类型
	private Double bgNum;		// 数量
	private Double bgCount;		// 月/天/周期
	private Double bgColumnPrice;		// 单价
	private Double bgColumnMonry;		// 总金额
	
	public ProjectBgColumn() {
		super();
	}

	public ProjectBgColumn(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="项目信息", align=2, sort=8)
	public ProjectInfo getProjectInfo() {
		return projectInfo;
	}

	public void setProjectInfo(ProjectInfo projectInfo) {
		this.projectInfo = projectInfo;
	}
	
	
	@ExcelField(title="序号", align=2, sort=9)
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	
	@ExcelField(title="预算分类", align=2, sort=10)
	public String getBgSort() {
		return bgSort;
	}

	public void setBgSort(String bgSort) {
		this.bgSort = bgSort;
	}
	
	
	@ExcelField(title="预算类型", align=2, sort=11)
	public String getBgType() {
		return bgType;
	}

	public void setBgType(String bgType) {
		this.bgType = bgType;
	}
	
	
	@ExcelField(title="数量", align=2, sort=12)
	public Double getBgNum() {
		return bgNum;
	}

	public void setBgNum(Double bgNum) {
		this.bgNum = bgNum;
	}
	
	
	@ExcelField(title="月/天/周期", align=2, sort=13)
	public Double getBgCount() {
		return bgCount;
	}

	public void setBgCount(Double bgCount) {
		this.bgCount = bgCount;
	}
	
	
	@ExcelField(title="单价", align=2, sort=14)
	public Double getBgColumnPrice() {
		return bgColumnPrice;
	}

	public void setBgColumnPrice(Double bgColumnPrice) {
		this.bgColumnPrice = bgColumnPrice;
	}
	
	
	@ExcelField(title="总金额", align=2, sort=15)
	public Double getBgColumnMonry() {
		return bgColumnMonry;
	}

	public void setBgColumnMonry(Double bgColumnMonry) {
		this.bgColumnMonry = bgColumnMonry;
	}
	
	
}