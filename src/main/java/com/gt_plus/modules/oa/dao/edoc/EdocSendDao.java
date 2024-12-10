/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.edoc;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.edoc.EdocSend;

/**
 * 发文管理DAO接口
 * @author GT0155
 * @version 2017-12-08
 */
@MyBatisDao
public interface EdocSendDao extends CrudDao<EdocSend> {

	
	List<EdocSend> findListByProc(@Param("edocSend")EdocSend edocSend, @Param("procInsIds")List<String> procInsIds);
	
	List<EdocSend> findListByProcIsNull(@Param("edocSend")EdocSend edocSend);
}