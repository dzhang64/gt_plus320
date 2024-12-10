/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.entity.UserSsoSubsystem;

/**
 * 用户DAO接口
 * @author gt_plus
 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {
	
	/**
	 * 根据登录名称查询用户
	 * @param loginName
	 * @return
	 */
	public User getByLoginName(User user);
	
	/****
	 * 验证登录名
	 * @param loginName
	 * @return
	 */
	public int selectconutLoginName(@Param("loginName")String loginName);
	
	/**
	 * 根据用户编号查询用户
	 * @param no
	 * @return
	 */
	public User getUserByNo(String no);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);
	
	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);
	
	public void updateUserAble(@Param("useable")String useable,@Param("id")String id);
	
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);
	
	
	/**
	 * 根据ID插入用户角色
	 * @param user
	 * @return
	 */
	public int insertUserRoleById(@Param("userId")String userId, @Param("roleId")String roleId);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);
	
	/**
	 * 插入好友
	 */
	public int insertFriend(@Param("id")String id, @Param("userId")String userId, @Param("friendId")String friendId);
	
	/**
	 * 查找好友
	 */
	public User findFriend(@Param("userId")String userId, @Param("friendId")String friendId);
	/**
	 * 删除好友
	 */
	public void deleteFriend(@Param("userId")String userId, @Param("friendId")String friendId);
	
	/**
	 * 
	 * 获取我的好友列表
	 * 
	 */
	public List<User> findFriends(User currentUser);
	
	/**
	 * 
	 * 查询用户-->用来添加到常用联系人
	 * 
	 */
	public List<User> searchUsers(User user);
	
	/**
	 * 
	 */
	
	public List<User>  findListByOffice(User user);
	
	/**
	 * 根据ownerCode和角色英文名称查找用户列表
	 * @param user
	 * @return
	 */
	public List<User> findUsersForNotice(@Param("ownerCode")String ownerCode, @Param("roleName")String roleName);

	/**
	 * 工作流配置节点权限时根据属性ID获取用户列表
	 * @return
	 */
	public List<User> findUserForFlow(@Param("condition")String condition);

	///////////////////////////////////////////////////////////////////////
	
	public List<User> findUserListByOfficeIdList(@Param("officeIdList")List<String> officeIdList);

	public List<User> findUserListByLevelIdList(@Param("levelIdList")List<String> levelIdList);

	public List<User> findUserListByPostIdList(@Param("postIdList")List<String> postIdList);

	public List<User> findUserListByRoleIdList(@Param("roleIdList")List<String> roleIdList);

	public List<User> findUserListByUserIdList(@Param("userIdList")List<String> userIdList);

	public List<UserSsoSubsystem> getUserSso(@Param("user")User user);

	public List<UserSsoSubsystem> getUserSsoList(@Param("user")User user);

	public List<User> findUserListByOrgIdList(@Param("orgIdList")List<String> orgIdList);
}
