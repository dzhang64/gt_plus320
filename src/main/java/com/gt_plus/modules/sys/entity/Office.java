/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.cxf.common.util.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.gt_plus.common.persistence.TreeEntity;

/**
 * 机构Entity
 * @author gt_plus
 * @version 2013-05-15
 */
public class Office extends TreeEntity<Office> {

	private static final long serialVersionUID = 1L;
//	private Office parent;	// 父级编号
//	private String parentIds; // 所有父级编号
	private Area area;		// 归属区域
	private String code; 	// 机构编码
//	private String name; 	// 机构名称
//	private Integer sort;		// 排序
	private String shortName; 	// 机构简称
	private String type; 	// 机构类型（1：公司；2：部门；3：小组）
	private String grade; 	// 机构等级（1：一级；2：二级；3：三级；4：四级）
	private String address; // 联系地址
	private String zipCode; // 邮政编码
	private String master; 	// 负责人
	private String phone; 	// 电话
	private String fax; 	// 传真
	private String email; 	// 邮箱
	private String useable;//是否可用
	private User primaryPerson;//主负责人
	private User deputyPerson;//副负责人
	private List<String> childDeptList;//快速添加子部门
	private Post postParam;
	private List<Post> postList; //机构岗位列表

	public Office(){
		super();
//		this.sort = 30;
		this.type = "2";
	}

	public Office(String id){
		super(id);
	}
	
	public List<String> getChildDeptList() {
		return childDeptList;
	}

	public void setChildDeptList(List<String> childDeptList) {
		this.childDeptList = childDeptList;
	}

	public String getUseable() {
		return useable;
	}

	public void setUseable(String useable) {
		this.useable = useable;
	}

	public User getPrimaryPerson() {
		return primaryPerson;
	}

	public void setPrimaryPerson(User primaryPerson) {
		this.primaryPerson = primaryPerson;
	}

	public User getDeputyPerson() {
		return deputyPerson;
	}

	public void setDeputyPerson(User deputyPerson) {
		this.deputyPerson = deputyPerson;
	}

//	@JsonBackReference
//	@NotNull
	public Office getParent() {
		return parent;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}
//
//	@Length(min=1, max=2000)
//	public String getParentIds() {
//		return parentIds;
//	}
//
//	public void setParentIds(String parentIds) {
//		this.parentIds = parentIds;
//	}

	@NotNull
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
//
//	@Length(min=1, max=100)
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public Integer getSort() {
//		return sort;
//	}
//
//	public void setSort(Integer sort) {
//		this.sort = sort;
//	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=1, max=1)
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Length(min=0, max=255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min=0, max=100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min=0, max=100)
	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	@Length(min=0, max=200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min=0, max=200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min=0, max=200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min=0, max=100)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

//	public String getParentId() {
//		return parent != null && parent.getId() != null ? parent.getId() : "0";
//	}
	
	@Override
	public String toString() {
		return name;
	}

	public List<Post> getPostList() {
		/*
		if (this.postParam != null) {
			String[] ids = postParam.getId().split(",");
			List<Post> postList = Lists.newArrayList();
			for(int i = 0;i < ids.length;i++) {
				Post post = new Post();
				post.setId(ids[i]);
				postList.add(post);
			}
			this.postList = postList;
		}
		*/
		return this.postList;
	}

	public void setPostList(List<Post> postList) {
		this.postList = postList;
		/*
		Post post = new Post();
		String postIds = "";
		String postNames = "";
		if(this.postList != null) {
			for(Post thePost : this.postList) {
				postIds = postIds  + "," + thePost.getId();
				postNames = postNames + "," + thePost.getName();
			}
			if (postIds.indexOf(",") != -1) {
				postIds = postIds.substring(1);
				postNames = postNames.substring(1);
			}
		}
		post.setId(postIds);
		post.setName(postNames);
		this.postParam = post;
		*/
	}
	
	public Post getPostParam() {
		/*
		if (this.postList != null && this.postList.size() > 0) {
			Post post = new Post();
			String postIds = "";
			String postNames = "";
			
			for(Post thePost : this.postList) {
				postIds = postIds  + "," + thePost.getId();
				postNames = postNames + "," + thePost.getName();
			}
			if (postIds.indexOf(",") != -1) {
				postIds = postIds.substring(1);
				postNames = postNames.substring(1);
			}
			
			post.setId(postIds);
			post.setName(postNames);
			return post;
		} else {
			return this.postParam;
		}
		*/
		return this.postParam;
	}

	public void setPostParam(Post postParam) {
		this.postParam = postParam;
		/*
		String[] ids = postParam.getId().split(",");
		List<Post> postList = Lists.newArrayList();
		for(int i = 0;i < ids.length;i++) {
			Post post = new Post();
			post.setId(ids[i]);
			postList.add(post);
		}
		this.postList = postList;
		*/
	}
	
	public void buildPostParam() {
		if (this.postList != null && this.postList.size() > 0) {
			Post post = new Post();
			String postIds = "";
			String postNames = "";
			
			for(Post thePost : this.postList) {
				postIds = postIds  + "," + thePost.getId();
				postNames = postNames + "," + thePost.getName();
			}
			if (postIds.indexOf(",") != -1) {
				postIds = postIds.substring(1);
				postNames = postNames.substring(1);
			}
			
			post.setId(postIds);
			post.setName(postNames);
			this.postParam = post;
		} 
	}
	
	public void buildPostList() {
		if (this.postParam != null && postParam.getId() !=null && !StringUtils.isEmpty(postParam.getId())) {
			String[] ids = postParam.getId().split(",");
			List<Post> postList = Lists.newArrayList();
			for(int i = 0;i < ids.length;i++) {
				Post post = new Post();
				post.setId(ids[i]);
				postList.add(post);
			}
			this.postList = postList;
		}
	}
}