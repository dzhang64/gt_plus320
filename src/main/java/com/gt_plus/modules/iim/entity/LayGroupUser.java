/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.iim.entity;

import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.modules.sys.entity.User;

import org.hibernate.validator.constraints.Length;


/**
 * 群组Entity
 * @author lgf
 * @version 2016-08-07
 */
public class LayGroupUser extends DataEntity<LayGroupUser> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户
	private LayGroup group;		// 群组id 父类
	
	public LayGroupUser() {
		super();
	}

	public LayGroupUser(String id){
		super(id);
	}

	public LayGroupUser(LayGroup group){
		this.group = group;
	}

	@ExcelField(title="用户", fieldType=User.class, value="user.name", align=2, sort=7)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=64, message="群组id长度必须介于 0 和 64 之间")
	public LayGroup getGroup() {
		return group;
	}

	public void setGroup(LayGroup group) {
		this.group = group;
	}
	
}