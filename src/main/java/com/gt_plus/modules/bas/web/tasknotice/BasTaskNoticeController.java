/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.web.tasknotice;

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
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.bas.entity.tasknotice.BasTaskNotice;
import com.gt_plus.modules.bas.service.tasknotice.BasTaskNoticeService;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.utils.UserUtils;
/**
 * 任务通知Controller
 * @author GT0291
 * @version 2017-08-03
 */
@Controller
@RequestMapping(value = "${adminPath}/bas/tasknotice/basTaskNotice")
public class BasTaskNoticeController extends BaseController {

	@Autowired
	private BasTaskNoticeService basTaskNoticeService;
	

	private void setDbType() {
		//DataSourceContextHolder.setDbType("dataSourceApp");
	}
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public BasTaskNotice get(@RequestParam(required=false) String id) {
		this.setDbType();
		BasTaskNotice entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = basTaskNoticeService.get(id);
		}
		if (entity == null){
			entity = new BasTaskNotice();
		}
		return entity;
	}
	
	/**
	 * 任务通知列表页面
	 */
	@RequiresPermissions("bas:tasknotice:basTaskNotice:list")
	@RequestMapping(value = {"list", ""})
	public String list(BasTaskNotice basTaskNotice, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		return "modules/bas/tasknotice/basTaskNoticeList";
	}
	
	/**
	 * 任务通知列表数据
	 */
	@ResponseBody
	@RequiresPermissions("bas:tasknotice:basTaskNotice:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(BasTaskNotice basTaskNotice, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		basTaskNotice.getSqlMap().put("dsf", this.getDataScope());
		Page<BasTaskNotice> page = basTaskNoticeService.findPage(new Page<BasTaskNotice>(request, response), basTaskNotice); 
		return getBootstrapData(page);
	}
	
	/**
	 * 获取任务通知数量
	 * userId 
	 * types
	 */
	@ResponseBody
	@RequestMapping(value = "count")
	public AjaxJson count(BasTaskNotice basTaskNotice, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
		this.setDbType();
		AjaxJson j = new AjaxJson();
		User user = UserUtils.getByLoginName(basTaskNotice.getUserId());
		if(user != null){
			basTaskNotice.setUserId(user.getId());
			Page<BasTaskNotice> page = basTaskNoticeService.findPage(new Page<BasTaskNotice>(request, response), basTaskNotice);
			j.setSuccess(true);
			j.setMsg(Long.toString(page.getCount()));
		}else{
			j.setSuccess(false);
			j.setMsg("0");
		}
		return j;
	}
	
	/**
	 * 私人任务通知列表数据
	 */
	@ResponseBody
	@RequiresPermissions("bas:tasknotice:basTaskNotice:list")
	@RequestMapping(value = "dataPrivate")
	public Map<String, Object> dataPrivate(BasTaskNotice basTaskNotice, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		basTaskNotice.getSqlMap().put("dsf", this.getDataScope());
		basTaskNotice.setUserId(UserUtils.getUser().getId()); //只查询自己的任务通知
		Page<BasTaskNotice> page = basTaskNoticeService.findPage(new Page<BasTaskNotice>(request, response), basTaskNotice); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑任务通知表单页面
	 */
	@RequiresPermissions(value={"bas:tasknotice:basTaskNotice:view","bas:tasknotice:basTaskNotice:add","bas:tasknotice:basTaskNotice:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(BasTaskNotice basTaskNotice, Model model) {
		this.setDbType();
		model.addAttribute("basTaskNotice", basTaskNotice);
		return "modules/bas/tasknotice/basTaskNoticeForm";
	}

	/**
	 * 保存任务通知
	 */
	@ResponseBody
	@RequiresPermissions(value={"bas:tasknotice:basTaskNotice:add","bas:tasknotice:basTaskNotice:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(BasTaskNotice basTaskNotice, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		this.setDbType();
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, basTaskNotice)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		*/
		if(!basTaskNotice.getIsNewRecord()){
			//修改保存
			BasTaskNotice t = basTaskNoticeService.get(basTaskNotice.getId());
			/*
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg("记录已经被修改！");
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) basTaskNoticeService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(basTaskNotice, t);
				basTaskNoticeService.save(t);
			}
			*/
			MyBeanUtils.copyBeanNotNull2Bean(basTaskNotice, t);
			basTaskNoticeService.save(t);
		}else{
			//新建保存
			basTaskNoticeService.save(basTaskNotice);
		}
		j.setSuccess(true);
		j.setMsg("保存任务通知成功");
		return j;
	}
	
	/**
	 * 删除任务通知
	 */
	@ResponseBody
	@RequiresPermissions("bas:tasknotice:basTaskNotice:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(BasTaskNotice basTaskNotice, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			basTaskNotice.setDelFlag(Global.YES);
			basTaskNoticeService.saveV(basTaskNotice); 
		}
		basTaskNoticeService.delete(basTaskNotice);
		j.setMsg("删除任务通知成功");
		return j;
	}
	
	/**
	 * 批量删除任务通知
	 */
	@ResponseBody
	@RequiresPermissions("bas:tasknotice:basTaskNotice:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				BasTaskNotice obj = basTaskNoticeService.get(id);
				obj.setDelFlag(Global.YES);
				basTaskNoticeService.saveV(obj); 
			}
			basTaskNoticeService.delete(basTaskNoticeService.get(id));
		}
		j.setMsg("删除任务通知成功");
		return j;
	}
	
	/**
	 * 批量设置状态
	 */
	@ResponseBody
	@RequiresPermissions("bas:tasknotice:basTaskNotice:edit")
	@RequestMapping(value = "setRead")
	public AjaxJson setRead(String ids, String status, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				BasTaskNotice obj = basTaskNoticeService.get(id);
				obj.setDelFlag(Global.YES);
				basTaskNoticeService.saveV(obj); 
			}
			BasTaskNotice theBasTaskNotice = basTaskNoticeService.get(id);
			theBasTaskNotice.setStatus(status);
			basTaskNoticeService.save(theBasTaskNotice);
		}
		j.setMsg("设置成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("bas:tasknotice:basTaskNotice:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(BasTaskNotice basTaskNotice, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "任务通知"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<BasTaskNotice> page = basTaskNoticeService.findPage(new Page<BasTaskNotice>(request, response, -1), basTaskNotice);
    		new ExportExcel("任务通知", BasTaskNotice.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出任务通知记录失败！失败信息："+e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("bas:tasknotice:basTaskNotice:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<BasTaskNotice> list = ei.getDataList(BasTaskNotice.class);
			for (BasTaskNotice basTaskNotice : list){
				try{
					basTaskNoticeService.save(basTaskNotice);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条任务通知记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条任务通知记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入任务通知失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/bas/tasknotice/basTaskNotice/?repage";
    }
	
	/**
	 * 下载导入任务通知数据模板
	 */
	@RequiresPermissions("bas:tasknotice:basTaskNotice:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
            String fileName = "任务通知数据导入模板.xlsx";
    		List<BasTaskNotice> list = Lists.newArrayList(); 
    		new ExportExcel("任务通知数据", BasTaskNotice.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/bas/tasknotice/basTaskNotice/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
}