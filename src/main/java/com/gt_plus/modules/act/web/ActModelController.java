/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.act.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.act.service.ActModelService;

/**
 * 流程模型相关Controller
 * @author gt_plus
 * @version 2013-11-03
 */
@Controller
@RequestMapping(value = "${adminPath}/act/model")
public class ActModelController extends BaseController {

	@Autowired
	private ActModelService actModelService;

	/**
	 * 流程模型列表
	 */
	@RequiresPermissions("act:model:list")
	@RequestMapping(value = { "list", "" })
	public String modelList(String category, HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<org.activiti.engine.repository.Model> page = actModelService.modelList(
				new Page<org.activiti.engine.repository.Model>(request, response), category);

		model.addAttribute("page", page);
		model.addAttribute("category", category);

		return "modules/act/actModelList";
	}

	/**
	 * 创建模型
	 */
	@RequiresPermissions("act:model:create")
	@RequestMapping(value = "create", method = RequestMethod.GET)
	public String create(Model model) {
		return "modules/act/actModelCreate";
	}
	
	/**
	 * 创建模型
	 */
	@RequiresPermissions("act:model:create")
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String create(String name, String key, String description, String category,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			actModelService.create(name, key, description, category);
			redirectAttributes.addFlashAttribute("message", "添加模型成功");
			
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "添加模型失败");
			
			logger.error("创建模型失败：", e);
		}
		return "redirect:" + adminPath + "/act/model";
	}

	/**
	 * 根据Model部署流程
	 */
	@RequiresPermissions("act:model:deploy")
	@RequestMapping(value = "deploy")
	public String deploy(String id, RedirectAttributes redirectAttributes) {
		String message = actModelService.deploy(id);
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:" + adminPath + "/act/model";
	}
	
	/**
	 * 导出model的xml文件
	 */
	@RequiresPermissions("act:model:export")
	@RequestMapping(value = "export")
	public void export(String id, HttpServletResponse response) {
		actModelService.export(id, response);
	}

	/**
	 * 更新Model分类
	 */
	@RequiresPermissions("act:model:edit")
	@RequestMapping(value = "updateCategory")
	public String updateCategory(String id, String category, RedirectAttributes redirectAttributes) {
		actModelService.updateCategory(id, category);
		redirectAttributes.addFlashAttribute("message", "设置成功，模块ID=" + id);
		return "redirect:" + adminPath + "/act/model";
	}
	
	/**
	 * 删除Model
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("act:model:del")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		actModelService.delete(id);
		redirectAttributes.addFlashAttribute("message", "删除成功，模型ID=" + id);
		return "redirect:" + adminPath + "/act/model";
	}
	
	/**
	 * 删除Model
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("act:model:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			actModelService.delete(id);
		}
		redirectAttributes.addFlashAttribute("message", "删除成功");
		return "redirect:" + adminPath + "/act/model";
	}
}
