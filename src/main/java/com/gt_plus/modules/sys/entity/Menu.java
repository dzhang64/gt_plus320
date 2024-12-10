/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.entity;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 菜单Entity
 * @author gt_plus
 * @version 2013-05-15
 */
public class Menu extends DataEntity<Menu> {

	private static final long serialVersionUID = 1L;
	
	public static final String MY_SHORTCUT_MENU_NAME = "我的快捷";
	
	private Menu parent;	// 父级菜单
	private String parentIds; // 所有父级编号
	private List<Menu> children;	// 父级菜单
	private String name; 	// 名称
	private String href; 	// 链接
	private String target; 	// 目标（ mainFrame、_blank、_self、_parent、_top）
	private String icon; 	// 图标
	private Integer sort; 	// 排序
	private String isShow; 	// 是否在菜单中显示（1：显示；0：不显示）
	private String permission; // 权限标识
	
	private String userId;
	
	private String subSystemCodeList; //子系统编码列表
	private List<SubSystem> subSystemList = Lists.newArrayList(); //子系统列表
	
	public Menu(){
		super();
		this.sort = 30;
		this.isShow = "1";
	}
	
	public Menu(String id){
		super(id);
	}
	
	@JsonBackReference
	@NotNull
	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	@Length(min=1, max=2000)
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	@Length(min=1, max=100)
	public String getName() {
		if(false == UserUtils.getUser().isAdmin()){
			if (name.indexOf("（") != -1) {
				return name.substring(0,name.indexOf("（"));
			} else if (name.indexOf("(") != -1) {
				return name.substring(0,name.indexOf("("));
			} else {
				return name;
			}
		} else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=0, max=2000)
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Length(min=0, max=20)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	@Length(min=0, max=100)
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	@NotNull
	public Integer getSort() {
		return sort;
	}
	
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	
	@Length(min=1, max=1)
	public String getIsShow() {
		return isShow;
	}

	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}

	@Length(min=0, max=200)
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}

	@JsonIgnore
	public boolean hasPermisson(){
		List<Menu> menuList = UserUtils.getMenuList();
		for(Menu menu:menuList){
			if(menu.getId().equals(this.getId()))
				return true;
		}
		return false;
	}
	
	@JsonIgnore
	public static void sortList(List<Menu> list, List<Menu> sourcelist, String parentId, boolean cascade){
		for (int i=0; i<sourcelist.size(); i++){
			Menu e = sourcelist.get(i);
			if (e.getParent()!=null && e.getParent().getId()!=null
					&& e.getParent().getId().equals(parentId)){
				list.add(e);
				if (cascade){
					// 判断是否还有子节点, 有则继续获取子节点
					for (int j=0; j<sourcelist.size(); j++){
						Menu child = sourcelist.get(j);
						if (child.getParent()!=null && child.getParent().getId()!=null
								&& child.getParent().getId().equals(e.getId())){
							sortList(list, sourcelist, e.getId(), true);
							break;
						}
					}
				}
			}
		}
	}

	@JsonIgnore
	public static String getRootId(){
		return "1";
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getSubSystemCodeList() {
		return subSystemCodeList;
	}

	public void setSubSystemCodeList(String subSystemCodeList) {
		this.subSystemCodeList = subSystemCodeList;
		if (false == StringUtils.isEmpty(subSystemCodeList)) {
			subSystemList = Lists.newArrayList(); 
			String tempList[] = subSystemCodeList.split(",");
			for (int i = 0; i < tempList.length; i++) {
				subSystemList.add(new SubSystem(tempList[i]));
			}
		}
	}
	
	public List<SubSystem> getSubSystemList() {
		return subSystemList;
	}

	public void setSubSystemList(List<SubSystem> subSystemList) {
		this.subSystemList = subSystemList;
		if (subSystemList != null && subSystemList.size() > 0) {
			String temp = "";
			for(int i=0; i<subSystemList.size(); i++) {
				if (0 == i) {
					temp = subSystemList.get(i).getCode();
				} else {
					temp = "," + subSystemList.get(i).getCode();
				}
			}
			this.subSystemCodeList = temp;
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}

	public List<Menu> getChildren() {
		return children;
	}
}