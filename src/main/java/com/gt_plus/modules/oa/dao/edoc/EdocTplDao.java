/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.dao.edoc;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;

/**
 * 模版类型DAO接口
 * @author GT0155
 * @version 2017-12-17
 */
@MyBatisDao
public interface EdocTplDao extends CrudDao<EdocTpl> {

	
}