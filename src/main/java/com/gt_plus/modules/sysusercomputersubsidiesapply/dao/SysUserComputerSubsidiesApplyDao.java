/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercomputersubsidiesapply.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysusercomputersubsidiesapply.entity.SysUserComputerSubsidiesApply;

/**
 * 电脑补贴申请DAO接口
 * @author wl
 * @version 2018-03-23
 */
@MyBatisDao
public interface SysUserComputerSubsidiesApplyDao extends CrudDao<SysUserComputerSubsidiesApply> {

	
	List<SysUserComputerSubsidiesApply> findListByProc(@Param("sysUserComputerSubsidiesApply")SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply, @Param("procInsIds")List<String> procInsIds);
	
	List<SysUserComputerSubsidiesApply> findListByProcIsNull(@Param("sysUserComputerSubsidiesApply")SysUserComputerSubsidiesApply sysUserComputerSubsidiesApply);
}