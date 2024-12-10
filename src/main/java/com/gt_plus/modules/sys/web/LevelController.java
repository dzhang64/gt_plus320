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
import com.gt_plus.modules.sys.entity.Level;
import com.gt_plus.modules.sys.service.LevelService;
/**
 * 职务Controller
 * @author David
 * @version 2017-11-01
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/level")
public class LevelController extends BaseController {

	@Autowired
	private LevelService levelService;
	
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public Level get(@RequestParam(required=false) String id) {
		Level entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = levelService.get(id);
		}
		if (entity == null){
			entity = new Level();
		}
		return entity;
	}
	
	/**
	 * 职务列表页面
	 */
	@RequiresPermissions("sys:level:list")
	@RequestMapping(value = {"list", ""})
	public String list(Level level, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/sys/levelList";
	}
	
	/**
	 * 职务列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sys:level:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Level level, HttpServletRequest request, HttpServletResponse response, Model model) {
		level.getSqlMap().put("dsf", this.getDataScope());
		Page<Level> page = levelService.findPage(new Page<Level>(request, response), level); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑职务表单页面
	 */
	@RequiresPermissions(value={"sys:level:view","sys:level:add","sys:level:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Level level, Model model) {
		model.addAttribute("level", level);
		return "modules/sys/levelForm";
	}

	/**
	 * 保存职务
	 */
	@ResponseBody
	@RequiresPermissions(value={"sys:level:add","sys:level:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Level level, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, level)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!level.getIsNewRecord()){
			//修改保存
			Level t = levelService.get(level.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) levelService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(level, t);
				levelService.save(t);
			}
		}else{
			//新建保存
			levelService.save(level);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存职务信息成功","职务"));
		//保存成功后处理逻辑
		this.afterSave("职务", level);
		return j;
	}
	
	/**
	 * 删除职务
	 */
	@ResponseBody
	@RequiresPermissions("sys:level:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Level level, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			level.setDelFlag(Global.YES);
			levelService.saveV(level); 
		}
		levelService.delete(level);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除职务信息成功","职务"));
		return j;
	}
	
	/**
	 * 批量删除职务
	 */
	@ResponseBody
	@RequiresPermissions("sys:level:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Level obj = levelService.get(id);
				obj.setDelFlag(Global.YES);
				levelService.saveV(obj); 
			}
			levelService.delete(levelService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除职务信息成功","职务"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sys:level:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Level level, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "职务"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Level> page = levelService.findPage(new Page<Level>(request, response, -1), level);
    		new ExportExcel("职务", Level.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出职务信息记录失败！", "职务") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sys:level:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Level> list = ei.getDataList(Level.class);
			for (Level level : list){
				try{
					levelService.save(level);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条职务记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条职务记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入职务失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/level/?repage";
    }
	
	/**
	 * 下载导入职务数据模板
	 */
	@RequiresPermissions("sys:level:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "职务数据导入模板.xlsx";
    		List<Level> list = Lists.newArrayList(); 
    		new ExportExcel("职务数据", Level.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sys/level/?repage";
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
	private void afterSave(String title, Level level) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sys/level");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, level.getOwnerCode(), roleMap);
	}
}