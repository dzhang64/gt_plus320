/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.Organization;
import com.gt_plus.modules.sys.entity.User;

/**
 * 组织管理DAO接口
 * @author LS0195
 * @version 2017-12-07
 */
@MyBatisDao
public interface OrganizationDao extends CrudDao<Organization> {
	
	List<User> findUserToOrg(@Param("id") String id);

	//void insertUserToOrg(String organization,String user);
	
	void insertUserToOrg(@Param("organization") Organization organization,@Param("user")  User user);

	void deleteUserToOrg(@Param("userId") String userId,@Param("orgId") String orgId);

	int findOrgNumberBy(@Param("org") String org);

	List<Organization> findListByUser(@Param("org")Organization org);
	
}