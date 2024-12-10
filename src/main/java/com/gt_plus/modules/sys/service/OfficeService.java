/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.service;

import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gt_plus.common.service.TreeService;
import com.gt_plus.modules.sys.dao.OfficeDao;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Post;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 机构Service
 * @author gt_plus
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {
	
	
	@Autowired
	private PostService postService;
	
	@Override
	public Office get(String id) {
		Office office = super.get(id);
		if (office != null) office.setPostList(postService.findListByOfficeId(id));
		return office;
	}

	@Override
	public Office get(Office entity) {
		Office office = super.get(entity);
		office.setPostList(postService.findListByOfficeId(office.getId()));
		return office;
	}
	
	
	public List<Office> findAll(){
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll){
		if (isAll != null && isAll){
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList();
		}
	}
	
	@Transactional(readOnly = true)
	public List<Office> findList(Office office){
		//office.setParentIds(office.getParentIds()+"%");
		if (office != null && false == StringUtils.isEmpty(office.getId())) {
			//修改时，将ParentIds还原
			String parentIds = office.getParentIds();
			if (parentIds.indexOf(office.getId()) != -1) {
				office.setParentIds(parentIds.replaceFirst("," + office.getId(), ""));
			}
			return dao.findByParentIdsLike(office);
		} else {
			return UserUtils.getOfficeList();
		}
	}
	
	@Transactional(readOnly = true)
	public Office getByCode(String code){
		return dao.getByCode(code);
	}
	
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		super.save(office);
		//更新岗位列表
		if(false == office.getIsNewRecord()) {
			dao.deletePostList(office);
		}
		if(office.getPostList() != null && office.getPostList().size() > 0) {
			for(Post post : office.getPostList()) {
				dao.insertOfficePost(office.getId(), post.getId());
			}
		}	
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Office office) {
		//先删除岗位列表
		dao.deletePostList(office);
		super.delete(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
	}
	
	/**
	 * 根据上级OwnerCode获取单位列表，当parentOwnerCode为空时，默认获取根节点下第一级子节点列表（部门除外）
	 * @param ownerCode
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Office> findCompanyListByOwnerCode(String ownerCode){
		if(StringUtils.isEmpty(ownerCode)) {
			//查询根节点下的第一级单位列表
			String rootOwnerCode = UserUtils.getUser().getCompany().getCode();
			if (false == StringUtils.isEmpty(rootOwnerCode) && rootOwnerCode.length() >= 3) {
				ownerCode = rootOwnerCode.substring(0,3);
			} else {
				ownerCode = "001";
			}
		}
		Office office = new Office();
		office.setCode(ownerCode);
		return dao.findCompanyListByOwnerCode(office);
	}
}
