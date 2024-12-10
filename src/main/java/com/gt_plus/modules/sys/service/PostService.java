/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.CrudService;
import com.gt_plus.modules.sys.entity.Post;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.dao.PostDao;

/**
 * 岗位Service
 * @author David
 * @version 2017-11-01
 */
@Service
@Transactional(readOnly = true)
public class PostService extends CrudService<PostDao, Post> {

	public Post get(String id) {
		return super.get(id);
	}
	
	public List<Post> findList(Post post) {
		return super.findList(post);
	}
	
	public Page<Post> findPage(Page<Post> page, Post post) {
		return super.findPage(page, post);
	}
	
	@Transactional(readOnly = false)
	public void save(Post post) {
		super.save(post);
	}
	
	@Transactional(readOnly = false)
	public void saveV(Post post) {
		super.saveV(post);
	}
	
	@Transactional(readOnly = false)
	public void delete(Post post) {
		super.delete(post);
	}
	
	public List<Post> findListByUser(User user) {
		Post post = new Post();
		post.setOfficeId(user.getOffice().getId());
		return findList(post);
	}
	
	public List<Post> findListByOfficeId(String officeId) {
		return dao.findListByOfficeId(officeId);
	}
}