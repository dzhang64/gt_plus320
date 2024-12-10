/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusertransferapplication.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysusertransferapplication.entity.SysUserTransferApplication;

/**
 * 调薪申请DAO接口
 * @author wl
 * @version 2018-03-29
 */
@MyBatisDao
public interface SysUserTransferApplicationDao extends CrudDao<SysUserTransferApplication> {

	
	List<SysUserTransferApplication> findListByProc(@Param("sysUserTransferApplication")SysUserTransferApplication sysUserTransferApplication, @Param("procInsIds")List<String> procInsIds);
	
	List<SysUserTransferApplication> findListByProcIsNull(@Param("sysUserTransferApplication")SysUserTransferApplication sysUserTransferApplication);
}