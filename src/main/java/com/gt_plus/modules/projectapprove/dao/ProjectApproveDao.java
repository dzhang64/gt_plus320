/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectapprove.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.projectapprove.entity.ProjectApprove;

/**
 * 项目立项DAO接口
 * @author zdy
 * @version 2018-03-17
 */
@MyBatisDao
public interface ProjectApproveDao extends CrudDao<ProjectApprove> {

	
	List<ProjectApprove> findListByProc(@Param("projectApprove")ProjectApprove projectApprove, @Param("procInsIds")List<String> procInsIds);
	
	List<ProjectApprove> findListByProcIsNull(@Param("projectApprove")ProjectApprove projectApprove);
}