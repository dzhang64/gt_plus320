/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserinterntranscription.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sysuserinterntranscription.entity.SysUserInternTranscription;

/**
 * 实习生转录用DAO接口
 * @author wl
 * @version 2018-03-20
 */
@MyBatisDao
public interface SysUserInternTranscriptionDao extends CrudDao<SysUserInternTranscription> {

	
	List<SysUserInternTranscription> findListByProc(@Param("sysUserInternTranscription")SysUserInternTranscription sysUserInternTranscription, @Param("procInsIds")List<String> procInsIds);
	
	List<SysUserInternTranscription> findListByProcIsNull(@Param("sysUserInternTranscription")SysUserInternTranscription sysUserInternTranscription);
}