/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.dao.tasknotice;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.bas.entity.tasknotice.BasTaskNotice;

/**
 * 任务通知DAO接口
 * @author GT0291
 * @version 2017-08-03
 */
@MyBatisDao
public interface BasTaskNoticeDao extends CrudDao<BasTaskNotice> {

	
}