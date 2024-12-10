package com.gt_plus.modules.gen.dao;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.gen.entity.GenTable;
import com.gt_plus.modules.gen.entity.GenTableColumn;

/**
 * 业务表字段DAO接口
 * @author ThinkGem
 * @version 2013-10-15
 */
@MyBatisDao
public abstract interface GenTableColumnDao extends CrudDao<GenTableColumn>
{
  public abstract void deleteByGenTable(GenTable paramGenTable);
}