/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.setting;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.setting.Sequence;

/**
 * 文单序列DAO接口
 * @author GT0291
 * @version 2017-12-25
 */
@MyBatisDao
public interface SequenceDao extends CrudDao<Sequence> {

	public Sequence getSequence(Sequence sequence);
}