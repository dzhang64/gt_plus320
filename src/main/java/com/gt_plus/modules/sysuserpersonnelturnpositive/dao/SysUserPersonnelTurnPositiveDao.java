/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserpersonnelturnpositive.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysuserpersonnelturnpositive.entity.SysUserPersonnelTurnPositive;

/**
 * 人员转正DAO接口
 * @author wl
 * @version 2018-03-19
 */
@MyBatisDao
public interface SysUserPersonnelTurnPositiveDao extends CrudDao<SysUserPersonnelTurnPositive> {

	
	List<SysUserPersonnelTurnPositive> findListByProc(@Param("sysUserPersonnelTurnPositive")SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, @Param("procInsIds")List<String> procInsIds);
	
	List<SysUserPersonnelTurnPositive> findListByProcIsNull(@Param("sysUserPersonnelTurnPositive")SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive);
}