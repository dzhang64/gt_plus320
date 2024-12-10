package com.gt_plus.modules.gen.dao;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.gen.entity.GenTable;
import org.apache.ibatis.annotations.Param;

/**
 * 业务表DAO接口
 * @author ThinkGem
 * @version 2013-10-15
 */
@MyBatisDao
public abstract interface GenTableDao extends CrudDao<GenTable>
{
  public abstract int buildTable(@Param("sql") String paramString);
}