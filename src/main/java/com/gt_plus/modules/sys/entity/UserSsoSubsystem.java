/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.annotation.ExcelField;
import com.gt_plus.common.persistence.DataEntity;

/**
 * 用户SSO子系统关系Entity
 * @author GT0155
 * @version 2017-12-05
 */
public class UserSsoSubsystem extends DataEntity<UserSsoSubsystem> {
	private static final long serialVersionUID = 1L;
	private String ownerCode;		// 归属机构编码
	private String userId;		// 用户ID
	private SsoSubsystem ssoSubsystem;		// 子系统对象
	private String loginName;		// 用户名
	private String password;		// 密码
	private String isAllow;		// 是否允许登录
	
	public UserSsoSubsystem() {
		super();
	}

	public UserSsoSubsystem(String id){
		super(id);
	}

	@ExcelField(title="归属机构编码", align=2, sort=7)
	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	
	
	@ExcelField(title="用户ID", align=2, sort=8)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public SsoSubsystem getSsoSubsystem() {
		return ssoSubsystem;
	}

	public void setSsoSubsystem(SsoSubsystem ssoSubsystem) {
		this.ssoSubsystem = ssoSubsystem;
	}

	@ExcelField(title="用户名", align=2, sort=10)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
	@ExcelField(title="密码", align=2, sort=11)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	@ExcelField(title="是否允许登录", dictType="yes_no", align=2, sort=12)
	public String getIsAllow() {
		return isAllow;
	}

	public void setIsAllow(String isAllow) {
		this.isAllow = isAllow;
	}
	
	
}