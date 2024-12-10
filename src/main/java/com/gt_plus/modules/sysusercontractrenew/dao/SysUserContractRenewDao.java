/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercontractrenew.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysusercontractrenew.entity.SysUserContractRenew;

/**
 * 合同续签DAO接口
 * @author wl
 * @version 2018-03-27
 */
@MyBatisDao
public interface SysUserContractRenewDao extends CrudDao<SysUserContractRenew> {

	
	List<SysUserContractRenew> findListByProc(@Param("sysUserContractRenew")SysUserContractRenew sysUserContractRenew, @Param("procInsIds")List<String> procInsIds);
	
	List<SysUserContractRenew> findListByProcIsNull(@Param("sysUserContractRenew")SysUserContractRenew sysUserContractRenew);
}