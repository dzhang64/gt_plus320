/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.teamwork;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.teamwork.Teamwork;

/**
 * 协同管理DAO接口
 * @author LS0077
 * @version 2017-12-11
 */
@MyBatisDao
public interface TeamworkDao extends CrudDao<Teamwork> {

	
	List<Teamwork> findListByProc(@Param("teamwork")Teamwork teamwork, @Param("procInsIds")List<String> procInsIds);
	
	List<Teamwork> findListByProcIsNull(@Param("teamwork")Teamwork teamwork);
}