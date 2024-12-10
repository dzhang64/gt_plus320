/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.entity.UserSsoSubsystem;

/**
 * 用户SSO子系统关系DAO接口
 * @author GT0155
 * @version 2017-12-05
 */
@MyBatisDao
public interface UserSsoSubsystemDao extends CrudDao<UserSsoSubsystem> {

	UserSsoSubsystem getLoginNameAndPassword(@Param("no")String no, @Param("key")String key);

	List<UserSsoSubsystem> getSsoList(@Param("no")String no);

}