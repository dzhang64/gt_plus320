/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.test;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.test.MyTest;

/**
 * 测试OADAO接口
 * @author David
 * @version 2017-11-24
 */
@MyBatisDao
public interface MyTestDao extends CrudDao<MyTest> {

	
	List<MyTest> findListByProc(@Param("myTest")MyTest myTest, @Param("procInsIds")List<String> procInsIds);
	
	List<MyTest> findListByProcIsNull(@Param("myTest")MyTest myTest);
}