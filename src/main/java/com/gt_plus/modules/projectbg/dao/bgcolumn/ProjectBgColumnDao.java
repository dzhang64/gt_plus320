/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.dao.bgcolumn;

import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import java.util.List;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.projectbg.entity.bgcolumn.ProjectBgColumn;

/**
 * 项目预算行DAO接口
 * @author zdy
 * @version 2018-03-21
 */
@MyBatisDao
public interface ProjectBgColumnDao extends CrudDao<ProjectBgColumn> {

	public List<ProjectInfo> findListByprojectInfo(ProjectInfo projectInfo);
	
}