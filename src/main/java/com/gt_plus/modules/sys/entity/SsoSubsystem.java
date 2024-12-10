/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * SSO子系统Entity
 * @author GT0155
 * @version 2017-12-12
 */
public class SsoSubsystem extends DataEntity<SsoSubsystem> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String name;		// 系统名称
	private Office org;		// 归属单位
	private String url;		// 请求地址
	private String caUrl;		// CA地址
	private String isInuse;		// 是否可用
	private String isSync;		// 是否允许同步密码
	private String key;		// KEY
	
	public SsoSubsystem() {
		super();
	}

	public SsoSubsystem(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="系统名称", align=2, sort=8)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ExcelField(title="归属单位", fieldType=Office.class, value="org.name", align=2, sort=9)
	public Office getOrg() {
		return org;
	}

	public void setOrg(Office org) {
		this.org = org;
	}
	
	
	@ExcelField(title="请求地址", align=2, sort=10)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	@ExcelField(title="CA地址", align=2, sort=11)
	public String getCaUrl() {
		return caUrl;
	}

	public void setCaUrl(String caUrl) {
		this.caUrl = caUrl;
	}
	
	
	@ExcelField(title="是否可用", dictType="yes_no", align=2, sort=12)
	public String getIsInuse() {
		return isInuse;
	}

	public void setIsInuse(String isInuse) {
		this.isInuse = isInuse;
	}
	
	
	@ExcelField(title="是否允许同步密码", dictType="yes_no", align=2, sort=13)
	public String getIsSync() {
		return isSync;
	}

	public void setIsSync(String isSync) {
		this.isSync = isSync;
	}
	
	
	@ExcelField(title="KEY", align=2, sort=14)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
}