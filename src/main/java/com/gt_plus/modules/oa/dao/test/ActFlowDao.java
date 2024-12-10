/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.test;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.test.ActFlow;

/**
 * act工作流DAO接口
 * @author GT0155
 * @version 2017-11-29
 */
@MyBatisDao
public interface ActFlowDao extends CrudDao<ActFlow> {

	
	List<ActFlow> findListByProc(@Param("actFlow")ActFlow actFlow, @Param("procInsIds")List<String> procInsIds);
	
	List<ActFlow> findListByProcIsNull(@Param("actFlow")ActFlow actFlow);
}