/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectbg.dao.bgentry;

import com.gt_plus.common.persistence.TreeDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.projectbg.entity.bgentry.ProjectBgEntry;

/**
 * 预算条目字典DAO接口
 * @author zdy
 * @version 2018-03-19
 */
@MyBatisDao
public interface ProjectBgEntryDao extends TreeDao<ProjectBgEntry> {
	
}