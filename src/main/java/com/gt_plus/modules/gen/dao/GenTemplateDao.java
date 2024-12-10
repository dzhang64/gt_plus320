package com.gt_plus.modules.gen.dao;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.gen.entity.GenTemplate;

/**
 * 代码模板DAO接口
 * @author ThinkGem
 * @version 2013-10-15
 */
@MyBatisDao
public abstract interface GenTemplateDao extends CrudDao<GenTemplate>
{
}