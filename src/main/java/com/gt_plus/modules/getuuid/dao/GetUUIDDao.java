/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.getuuid.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.getuuid.entity.GetUUID;

/**
 * 生成UUIDDAO接口
 * @author zdyt
 * @version 2018-01-30
 */
@MyBatisDao
public interface GetUUIDDao extends CrudDao<GetUUID> {

	
}