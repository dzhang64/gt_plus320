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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gt_plus.common.utils.CacheUtils;
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
import com.gt_plus.modules.sys.entity.Dict;
import com.gt_plus.modules.sys.service.DictService;
/**
 * 字典Controller
 * @author GT0291
 * @version 2017-07-25
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/dict")
public class DictController extends BaseController {

	@Autowired
	private DictService dictService;
	

	private void setDbType() {
		//DataSourceContextHolder.setDbType("dataSourceApp");
	}
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public Dict get(@RequestParam(required=false) String id) {
		this.setDbType();
		Dict entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dictService.get(id);
		}
		if (entity == null){
			entity = new Dict();
		}
		return entity;
	}
	
	/**
	 * 字典列表页面
	 */
	@RequiresPermissions("sys:dict:list")
	@RequestMapping(value = {"list", ""})
	public String list(Dict dict, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		List<String> typeList = dictService.findTypeList();
		model.addAttribute("typeList", typeList);
		return "modules/sys/dictList";
	}
	
	/**
	 * 字典列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:dict:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Dict dict, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		dict.getSqlMap().put("dsf", this.getDataScope());
		Page<Dict> page = dictService.findPage(new Page<Dict>(request, response), dict); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑字典表单页面
	 */
	@RequiresPermissions(value={"sys:dict:view","sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Dict dict, Model model) {
		this.setDbType();
		model.addAttribute("dict", dict);
		return "modules/sys/dictForm";
	}

	/**
	 * 保存字典
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:dict:add","sys:dict:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Dict dict, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		this.setDbType();
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, dict)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		*/
		if(!dict.getIsNewRecord()){
			//修改保存
			Dict t = dictService.get(dict.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg("记录已经被修改！");
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) dictService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(dict, t);
				dictService.save(t);
			}
		}else{
			//新建保存
			dictService.save(dict);
		}
		j.setSuccess(true);
		j.setMsg("保存字典成功");
		//字典改变，清除缓存
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
		return j;
	}
	
	/**
	 * 删除字典
	 */
	@ResponseBody
	@RequiresPermissions("sys:dict:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Dict dict, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			dict.setDelFlag(Global.YES);
			dictService.saveV(dict); 
		}
		dictService.delete(dict);
		j.setMsg("删除字典成功");
		//字典改变，清除缓存
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
		return j;
	}
	
	/**
	 * 批量删除字典
	 */
	@ResponseBody
	@RequiresPermissions("sys:dict:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Dict obj = dictService.get(id);
				obj.setDelFlag(Global.YES);
				dictService.saveV(obj); 
			}
			dictService.delete(dictService.get(id));
		}
		j.setMsg("删除字典成功");
		//字典改变，清除缓存
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:dict:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Dict dict, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "字典"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Dict> page = dictService.findPage(new Page<Dict>(request, response, -1), dict);
    		new ExportExcel("字典", Dict.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出字典记录失败！失败信息："+e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:dict:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Dict> list = ei.getDataList(Dict.class);
			for (Dict dict : list){
				try{
					dictService.save(dict);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条字典记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条字典记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入字典失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/dict/?repage";
    }
	
	/**
	 * 下载导入字典数据模板
	 */
	@RequiresPermissions("sys:dict:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
            String fileName = "字典数据导入模板.xlsx";
    		List<Dict> list = Lists.newArrayList(); 
    		new ExportExcel("字典数据", Dict.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/dict/?repage";
    }
	
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String type, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		Dict dict = new Dict();
		dict.setTypes(type);
		List<Dict> list = dictService.findList(dict);
		for (int i=0; i<list.size(); i++){
			Dict e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", e.getParent().getId());
			map.put("name", StringUtils.replace(e.getLabel(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}
	
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<Dict> listData(@RequestParam(required=false) String type) {
		Dict dict = new Dict();
		dict.setTypes(type);
		return dictService.findList(dict);
	}
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
}