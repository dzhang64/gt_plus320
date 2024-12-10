/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.entity.Post;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.PostService;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;

/**
 * 机构Controller
 * @author gt_plus
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private PostService postService;
	
	@ModelAttribute("office")
	public Office get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return officeService.get(id);
		}else{
			return new Office();
		}
	}

	@RequiresPermissions("sys:office:index")
	@RequestMapping(value = {""})
	public String index(Office office, Model model, String rootId) {
//        model.addAttribute("list", officeService.findAll());
		model.addAttribute("rootId", rootId);
		return "modules/sys/officeIndex";
	}

	@RequiresPermissions("sys:office:index")
	@RequestMapping(value = {"list"})
	public String list(Office office, Model model, String rootId, String refresh) {
		List<Office> officeList = null;
		//刷新
		if ((office == null || office.getParentIds() == null || office.getParentIds().equals("")) && false == StringUtils.isEmpty(rootId)) {
			office = officeService.get(rootId);
		}
		
		//点击左侧机构树
		if (rootId == null && (office != null && office.getParentIds() != null)) rootId = office.getId(); 
		
		if(office == null || office.getParentIds() == null || StringUtils.isEmpty(rootId)){
			//model.addAttribute("list", officeService.findList(false));
			//office无值，则查询该用户权限范围内的机构列表
			officeList = officeService.findList(false);
		}else{
			//model.addAttribute("list", officeService.findList(office));
			//office有值，查询该机构节点，以及子节点列表
			officeList = officeService.findList(office);
			//if(officeList != null && officeList.size() > 0 && false == UserUtils.getUser().isAdmin()) {
				//列表有值，并且该用户不是admin，将第一个节点保存到Model中？
			//	model.addAttribute("office", officeList.get(0));
			//}
		}
		if(officeList != null && officeList.size() > 0) {
			//将第一个office对象作为根节点，保存到Model中
			model.addAttribute("office", officeList.get(0));
			//分级授权用户，首次进入页面，避免显示空列表
			if (false == UserUtils.getUser().isAdmin() && StringUtils.isEmpty(rootId)) {
				rootId = officeList.get(0).getId();
			}
		}
		//model.addAttribute("list", officeList);
		model.addAttribute("rootId", rootId);
		//if (false == StringUtils.isEmpty(refresh)) model.addAttribute("refresh", refresh);
		return "modules/sys/officeList";
	}
	
	/**
	 * 组织机构列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:office:index")
	@RequestMapping(value = "data")
	public List<Office> data(Office office, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Office> list = officeService.findList(office); 
		return list;
	}
	
	@RequiresPermissions(value={"sys:office:view","sys:office:add","sys:office:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Office office, Model model, String rootId) {
		office.buildPostParam(); //根据岗位列表创建用于grid显示的对象
		User user = UserUtils.getUser();
		if (office.getParent()==null || office.getParent().getId()==null){
			office.setParent(user.getOffice());
		}
		office.setParent(officeService.get(office.getParent().getId()));
		if (office.getArea()==null){
			office.setArea(user.getOffice().getArea());
		}
		// 自动获取排序号
		if (StringUtils.isBlank(office.getId())&&office.getParent()!=null){
			int size = 0;
			List<Office> list = officeService.findAll();
			for (int i=0; i<list.size(); i++){
				Office e = list.get(i);
				if (e.getParent()!=null && e.getParent().getId()!=null
						&& e.getParent().getId().equals(office.getParent().getId())){
					size++;
				}
			}
			office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size+1 : 1), 3, "0"));
		}
		
		model.addAttribute("office", office);
		model.addAttribute("rootId", rootId);
		return "modules/sys/officeForm";
	}
	
	@ResponseBody
	@RequiresPermissions(value={"sys:office:add","sys:office:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Office office, Model model, RedirectAttributes redirectAttributes, String rootId) {
		office.buildPostList(); //根据grid传的参数，创建PostList，用于更新机构所拥有的岗位列表
		AjaxJson j = new AjaxJson();
		//if(Global.isDemoMode()){
		//	addMessage(redirectAttributes, "演示模式，不允许操作！");
		//	return "redirect:" + adminPath + "/sys/office/";
		//}
		if (!beanValidator(model, office)){
			//return form(office, model, rootId);
			j.setSuccess(false);
			j.setMsg("表单验证失败！");
		}
		officeService.save(office);

		if(office.getChildDeptList()!=null){
			Office childOffice = null;
			for(String id : office.getChildDeptList()){
				childOffice = new Office();
				childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
				childOffice.setParent(office);
				childOffice.setArea(office.getArea());
				childOffice.setType("2");
				childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade())+1));
				childOffice.setUseable(Global.YES);
				officeService.save(childOffice);
			}
		}
		j.setSuccess(true);
		j.setMsg("保存机构“" + office.getName() + "”成功");
		
		/*
		addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
		//String id = "0".equals(office.getParentId()) ? "" : office.getParentId();
		String id = "0".equals(office.getParentId()) ? "" : office.getId();
		if(false == StringUtils.isEmpty(rootId)) {
			//rootId不为空，从左侧部门树点击进入
			office = officeService.get(rootId);
			id = office.getId();
		}
		*/
		//return "redirect:" + adminPath + "/sys/office/list?id="+id+"&parentIds="+office.getParentIds()+"&rootId="+rootId+"&refresh=y";
		return j;
	}
	
	@ResponseBody
	@RequiresPermissions("sys:office:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Office office, RedirectAttributes redirectAttributes, String rootId) {
		AjaxJson j = new AjaxJson();
		//if(Global.isDemoMode()){
		//	addMessage(redirectAttributes, "演示模式，不允许操作！");
		//	return "redirect:" + adminPath + "/sys/office/list";
		//}
//		if (Office.isRoot(id)){
//			addMessage(redirectAttributes, "删除机构失败, 不允许删除顶级机构或编号空");
//		}else{
			officeService.delete(office);
			//addMessage(redirectAttributes, "删除机构成功");
			j.setSuccess(true);
			j.setMsg("删除机构成功");
//		}
		
		//if(false == StringUtils.isEmpty(rootId)) {
			//rootId不为空，从左侧部门树点击进入
		//	office = officeService.get(rootId);
		//}
		//return "redirect:" + adminPath + "/sys/office/list?id="+office.getId()+"&parentIds="+office.getParentIds()+"&rootId="+rootId+"&refresh=y";
		return j;
	}

	/**
	 * 获取机构JSON数据。
	 * @param extId 排除的ID
	 * @param type	类型（1：公司；2：部门/小组/其它：3：用户）
	 * @param grade 显示级别
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, @RequestParam(required=false) String type,
			@RequestParam(required=false) Long grade, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Office> list = officeService.findList(isAll);
		for (int i=0; i<list.size(); i++){
			Office e = list.get(i);
			if ((StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1))
					&& (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
					&& (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
					&& Global.YES.equals(e.getUseable())){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				map.put("fullPathName", e.getFullPathName());
				if (type != null && "3".equals(type)){
					map.put("isParent", true);
				}
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 选择岗位
	 */
	@RequestMapping(value = "selectpost")
	public String selectpost(Post post, String url,String postParamId,String checkName, String fieldLabels, String fieldKeys, String searchLabel, String searchKey, HttpServletRequest request, HttpServletResponse response, Model model) {
		//Page<Post> page = officeService.findPageBypost(new Page<Post>(request, response),  post);
		Page<Post> page = postService.findPage(new Page<Post>(request, response),  post);
		try {
			fieldLabels = URLDecoder.decode(fieldLabels, "UTF-8");
			fieldKeys = URLDecoder.decode(fieldKeys, "UTF-8");
			searchLabel = URLDecoder.decode(searchLabel, "UTF-8");
			searchKey = URLDecoder.decode(searchKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		model.addAttribute("labelNames", fieldLabels.split("\\|"));
		model.addAttribute("labelValues", fieldKeys.split("\\|"));
		model.addAttribute("fieldLabels", fieldLabels);
		model.addAttribute("postParamId", postParamId);
		model.addAttribute("checkName", checkName);
		model.addAttribute("fieldKeys", fieldKeys);
		model.addAttribute("url", url);
		model.addAttribute("searchLabel", searchLabel);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("obj", post);
		model.addAttribute("page", page);
		return "modules/sys/postGridselect";
	}
	
	
}
