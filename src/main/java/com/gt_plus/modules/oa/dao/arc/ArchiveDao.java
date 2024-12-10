/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.arc;

import com.gt_plus.modules.oa.entity.arc.ArcCategory;
import java.util.List;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.arc.Archive;

/**
 * 档案DAO接口
 * @author LS0077
 * @version 2017-11-07
 */
@MyBatisDao
public interface ArchiveDao extends CrudDao<Archive> {

	public List<ArcCategory> findListByarc(ArcCategory arc);
	
}