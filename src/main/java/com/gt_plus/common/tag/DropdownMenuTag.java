package com.gt_plus.common.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.utils.SpringContextHolder;
import com.gt_plus.modules.sys.entity.Menu;
import com.gt_plus.modules.sys.utils.UserUtils;


/**
 * 
 * 类描述：菜单标签
 * 
 * David
 * @date： 日期：2015-1-23 时间：上午10:17:45
 * @version 1.0
 */
public class DropdownMenuTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	protected Menu menu;//菜单Map
	
	

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public int doStartTag() throws JspTagException {
		return EVAL_PAGE;
	}

	public int doEndTag() throws JspTagException {
		try {
			JspWriter out = this.pageContext.getOut();
			String menu = (String) this.pageContext.getSession().getAttribute("menu");
			if(menu!=null){
				out.print(menu);
			}else{
				menu=end().toString();
				out.print(menu);
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public StringBuffer end() {
		StringBuffer sb = new StringBuffer();
		sb.append(getChildOfTree(menu,0, UserUtils.getMenuList()));
		
		System.out.println(sb);
		return sb;
		
	}
	
	private static String getChildOfTree(Menu parent, int level, List<Menu> menuList) {
		StringBuffer menuString = new StringBuffer();
		String href = "";
		if (!parent.hasPermisson())
			return "";
		if (level > 0) {//level 为0是功能菜单

			ServletContext context = SpringContextHolder
					.getBean(ServletContext.class);
			if (parent.getHref() != null && parent.getHref().length() > 0) {
				
				
				if (parent.getHref().startsWith("http://")) {// 如果是互联网资源
					href =  parent.getHref();
				} else if(parent.getHref().endsWith(".html")&&!parent.getHref().endsWith("ckfinder.html")){//如果是静态资源并且不是ckfinder.html，直接访问不加adminPath
					href = context.getContextPath() + parent.getHref();
				}else{
					href = context.getContextPath() + Global.getAdminPath()
					+ parent.getHref();
				}
			}
		}


		if ((parent.getHref() == null || parent.getHref().trim().equals("")) && parent.getIsShow().equals("1")) {//如果是父节点且显示
			if (level > 0) {
				//<a aria-expanded="false" role="button" href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-gear"></i>${menu.name}<c:if test="${menu.parentId eq '1'}"><span class="caret"></span></c:if></a>
				if(parent.getParentId().equals("1")) {
					//如果是一级菜单
					menuString.append("<li class=\"dropdown\" style=\"display:none\">");
				} else {
					menuString.append("<li class=\"dropdown-submenu\">");
				}
				
				menuString
					.append("<a aria-expanded=\"false\" role=\"button\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\""
							+ "#" //+ href
							+ "\"><i class=\"fa "+parent.getIcon()+"\"></i>"
							+ parent.getName());
				if (parent.getParentId().equals("1")) {
					//一级菜单，加向下的小箭头
					menuString.append("<span class=\"caret\"></span>");
				}
				menuString.append("</a>");
			}
			/*
			if (level == 1) {
				menuString.append("<ul class=\"nav nav-second-level\">");
			} else if (level == 2) {
				menuString.append("<ul class=\"nav nav-third-level\">");
			} else if (level == 3) {
				menuString.append("<ul class=\"nav nav-forth-level\">");
			} else if (level == 4) {
				menuString.append("<ul class=\"nav nav-fifth-level\">");
			}
			*/
			if (level > 0) {
				menuString.append("<ul role=\"menu\" class=\"dropdown-menu\">");
			}
			for (Menu child : menuList) {
				if (child.getParentId().equals(parent.getId())&&child.getIsShow().equals("1")) {
					menuString.append(getChildOfTree(child, level + 1, menuList));
				}
			}
			if (level > 0) {
				menuString.append("</ul>");
			}
		} else {
			//<a class="J_menuItem"  href="${ctx}${menu.href}"><i class="fa fa-gear"></i>${menu.name}</a>
			if(parent.getParentId().equals("1")) {
				//如果是一级菜单
				menuString.append("<li class=\"dropdown\">");
			} else {
				menuString.append("<li>");
			}
			menuString.append("<a class=\"J_menuItem\"  href=\"" + href
					+ "\" ><i class=\"fa "+parent.getIcon()+"\"></i>"+parent.getName()+"</a>");
		}
		
		if (level > 0) {
			menuString.append("</li>");
		}

		return menuString.toString();
	}
	
	

}
