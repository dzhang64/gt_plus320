/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sys.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sys.entity.Post;
import com.gt_plus.modules.sys.service.PostService;
/**
 * 岗位Controller
 * @author David
 * @version 2017-11-01
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/post")
public class PostController extends BaseController {

	@Autowired
	private PostService postService;
	
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public Post get(@RequestParam(required=false) String id) {
		Post entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = postService.get(id);
		}
		if (entity == null){
			entity = new Post();
		}
		return entity;
	}
	
	/**
	 * 岗位列表页面
	 */
	@RequiresPermissions("sys:post:list")
	@RequestMapping(value = {"list", ""})
	public String list(Post post, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/sys/postList";
	}
	
	/**
	 * 岗位列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:post:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Post post, HttpServletRequest request, HttpServletResponse response, Model model) {
		post.getSqlMap().put("dsf", this.getDataScope());
		Page<Post> page = postService.findPage(new Page<Post>(request, response), post); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑岗位表单页面
	 */
	@RequiresPermissions(value={"sys:post:view","sys:post:add","sys:post:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Post post, Model model) {
		model.addAttribute("post", post);
		return "modules/sys/postForm";
	}

	/**
	 * 保存岗位
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:post:add","sys:post:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Post post, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, post)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!post.getIsNewRecord()){
			//修改保存
			Post t = postService.get(post.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) postService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(post, t);
				postService.save(t);
			}
		}else{
			//新建保存
			postService.save(post);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存岗位信息成功","岗位"));
		//保存成功后处理逻辑
		this.afterSave("岗位", post);
		return j;
	}
	
	/**
	 * 删除岗位
	 */
	@ResponseBody
	@RequiresPermissions("sys:post:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Post post, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			post.setDelFlag(Global.YES);
			postService.saveV(post); 
		}
		postService.delete(post);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除岗位信息成功","岗位"));
		return j;
	}
	
	/**
	 * 批量删除岗位
	 */
	@ResponseBody
	@RequiresPermissions("sys:post:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Post obj = postService.get(id);
				obj.setDelFlag(Global.YES);
				postService.saveV(obj); 
			}
			postService.delete(postService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除岗位信息成功","岗位"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:post:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Post post, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "岗位"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Post> page = postService.findPage(new Page<Post>(request, response, -1), post);
    		new ExportExcel("岗位", Post.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出岗位信息记录失败！", "岗位") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:post:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Post> list = ei.getDataList(Post.class);
			for (Post post : list){
				try{
					postService.save(post);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条岗位记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条岗位记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入岗位失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/post/?repage";
    }
	
	/**
	 * 下载导入岗位数据模板
	 */
	@RequiresPermissions("sys:post:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "岗位数据导入模板.xlsx";
    		List<Post> list = Lists.newArrayList(); 
    		new ExportExcel("岗位数据", Post.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/post/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, Post post) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sys/post");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, post.getOwnerCode(), roleMap);
	}
}