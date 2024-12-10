/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gt_plus.common.persistence.CrudDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.Post;

/**
 * 岗位DAO接口
 * @author David
 * @version 2017-11-01
 */
@MyBatisDao
public interface PostDao extends CrudDao<Post> {

	/**
	 * 根据机构编码查询该机构下的岗位列表
	 * @param officeId
	 * @return
	 */
	public List<Post> findListByOfficeId(@Param("officeId") String officeId);
	
}