/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.edoc;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.edoc.EdocReceive;

/**
 * 收文管理DAO接口
 * @author GT0155
 * @version 2017-12-08
 */
@MyBatisDao
public interface EdocReceiveDao extends CrudDao<EdocReceive> {

	
	List<EdocReceive> findListByProc(@Param("edocReceive")EdocReceive edocReceive, @Param("procInsIds")List<String> procInsIds);
	
	List<EdocReceive> findListByProcIsNull(@Param("edocReceive")EdocReceive edocReceive);
}