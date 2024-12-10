/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.test;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.test.TestActFlow;

/**
 * 测试002DAO接口
 * @author GT0155
 * @version 2017-11-28
 */
@MyBatisDao
public interface TestActFlowDao extends CrudDao<TestActFlow> {

	
	List<TestActFlow> findListByProc(@Param("testActFlow")TestActFlow testActFlow, @Param("procInsIds")List<String> procInsIds);
	
	List<TestActFlow> findListByProcIsNull(@Param("testActFlow")TestActFlow testActFlow);
}