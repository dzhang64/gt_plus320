/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserinterentry.dao;

import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import java.util.List;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysuserinterentry.entity.SysInternUserInformation;

/**
 * 实习生入职DAO接口
 * @author zdy
 * @version 2018-01-31
 */
@MyBatisDao
public interface SysInternUserInformationDao extends CrudDao<SysInternUserInformation> {

	
	List<SysInternUserInformation> findListByProc(@Param("sysInternUserInformation")SysInternUserInformation sysInternUserInformation, @Param("procInsIds")List<String> procInsIds);
	
	List<SysInternUserInformation> findListByProcIsNull(@Param("sysInternUserInformation")SysInternUserInformation sysInternUserInformation);
	
	
}