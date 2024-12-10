/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.entity.arc;

import com.gt_plus.modules.oa.entity.arc.ArcCategory;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 档案Entity
 * @author LS0077
 * @version 2017-11-07
 */
public class Archive extends DataEntity<Archive> {
	
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String title;		// 标题
	private String oaArcTypeCode;		// 内容类型
	private Integer readCount;		// 阅读量
	private String sourceId;		// 来源ID
	private String files;		// 附件
	private ArcCategory arc;		// 档案目录
	
	public Archive() {
		super();
	}

	public Archive(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=5)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="标题", align=2, sort=6)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	@ExcelField(title="内容类型", align=2, sort=8)
	public String getOaArcTypeCode() {
		return oaArcTypeCode;
	}

	public void setOaArcTypeCode(String oaArcTypeCode) {
		this.oaArcTypeCode = oaArcTypeCode;
	}
	
	
	@ExcelField(title="阅读量", align=2, sort=9)
	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}
	
	
	@ExcelField(title="来源ID", align=2, sort=10)
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	
	@ExcelField(title="附件", align=2, sort=12)
	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}
	
	
	@ExcelField(title="档案目录", align=2, sort=13)
	public ArcCategory getArc() {
		return arc;
	}

	public void setArc(ArcCategory arc) {
		this.arc = arc;
	}
	
	
}