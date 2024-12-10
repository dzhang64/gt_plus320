/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercommonentry.dao;

import com.gt_plus.modules.salarylevel.entity.SalaryLevel;
import java.util.List;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysusercommonentry.entity.SysCommonUserInformation;

/**
 * 员工信息表DAO接口
 * @author zdy
 * @version 2018-02-22
 */
@MyBatisDao
public interface SysCommonUserInformationDao extends CrudDao<SysCommonUserInformation> {

	
	
	List<SysCommonUserInformation> findListByProc(@Param("sysCommonUserInformation")SysCommonUserInformation sysCommonUserInformation, @Param("procInsIds")List<String> procInsIds);
	
	List<SysCommonUserInformation> findListByProcIsNull(@Param("sysCommonUserInformation")SysCommonUserInformation sysCommonUserInformation);
}