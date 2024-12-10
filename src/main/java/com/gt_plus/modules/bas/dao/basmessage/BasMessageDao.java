/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.dao.basmessage;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.bas.entity.basmessage.BasMessage;

/**
 * 消息DAO接口
 * @author GT0291
 * @version 2017-08-03
 */
@MyBatisDao
public interface BasMessageDao extends CrudDao<BasMessage> {

	
}