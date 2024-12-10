/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusersscentry.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysusersscentry.entity.SysUserSscEntry;

/**
 * 社保统筹补录入DAO接口
 * @author wl
 * @version 2018-03-30
 */
@MyBatisDao
public interface SysUserSscEntryDao extends CrudDao<SysUserSscEntry> {

	
}