/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;

/**
 * 参数设置Entity
 * @author David
 * @version 2017-11-07
 */
public class Setting extends DataEntity<Setting> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 参数名称
	private String key;		// 参数Key
	private String value;		// 参数Value
	
	public static final String LINKS_DEFAULT_NAME = "links_default"; //系统默认的，我的快捷 name
	public static final String LINKS_DEFAULT_KEY = "links_default";  //系统默认的，我的快捷 key
	public static final String LINKS_USER_NAME = "links_user";       //用户，我的快捷 name
	
	public Setting() {
		super();
	}

	public Setting(String id){
		super(id);
	}

	@ExcelField(title="参数名称", align=2, sort=6)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ExcelField(title="参数Key", align=2, sort=7)
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
	@ExcelField(title="参数Value", align=2, sort=8)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}