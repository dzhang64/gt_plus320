/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectinfo.dao;


import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.userinfo.entity.UserInfo;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;

/**
 * 项目信息DAO接口
 * @author zdy
 * @version 2018-02-24
 */
@MyBatisDao
public interface ProjectInfoDao extends CrudDao<ProjectInfo> {

	/*dao层*/
	List<UserInfo> findUserToPro(@Param("id") String id);
	
	void deleteUserToPro(@Param("userId") String userId,@Param("projectNum") String projectNum);
	
	void insertUserToPro(@Param("projectNo") String projectNo,@Param("id")  String id);
	
	int checkPro(ProjectInfo projectInfo);
	
	ProjectInfo getByProjectNo(ProjectInfo projectInfo);
	
	List<ProjectInfo> findListByprojectInfo(ProjectInfo projectInfo);
}