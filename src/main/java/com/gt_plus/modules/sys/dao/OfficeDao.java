/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gt_plus.common.persistence.TreeDao;
import com.gt_plus.common.persistence.annotation.MyBatisDao;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Post;

/**
 * 机构DAO接口
 * @author gt_plus
 * @version 2014-05-16
 */
@MyBatisDao
public interface OfficeDao extends TreeDao<Office> {
	
	public Office getByCode(String code);
	
	
	public List<Office> findCompanyListByOwnerCode(Office office);
	
	//插入机构岗位关系
	public void insertOfficePost(@Param("officeId") String officeId, @Param("postId") String postId);
	
	//删除机构岗位列表
	public void deletePostList(Office office);
	
	public List<Office> findListOfficeByName(Office office);
	
}
