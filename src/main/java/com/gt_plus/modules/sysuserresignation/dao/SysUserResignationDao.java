/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserresignation.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysuserresignation.entity.SysUserResignation;

/**
 * 离职申请DAO接口
 * @author wl
 * @version 2018-03-21
 */
@MyBatisDao
public interface SysUserResignationDao extends CrudDao<SysUserResignation> {

	
	List<SysUserResignation> findListByProc(@Param("sysUserResignation")SysUserResignation sysUserResignation, @Param("procInsIds")List<String> procInsIds);
	
	List<SysUserResignation> findListByProcIsNull(@Param("sysUserResignation")SysUserResignation sysUserResignation);
}