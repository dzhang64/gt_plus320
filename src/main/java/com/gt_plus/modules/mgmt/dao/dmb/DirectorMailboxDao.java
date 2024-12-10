/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.dao.dmb;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.mgmt.entity.dmb.DirectorMailbox;

/**
 * 局长信箱DAO接口
 * @author GT0155
 * @version 2017-11-28
 */
@MyBatisDao
public interface DirectorMailboxDao extends CrudDao<DirectorMailbox> {

	
}