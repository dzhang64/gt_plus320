/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.bas.web.basmessage;

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
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.bas.entity.basmessage.BasMessage;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;
/**
 * 消息Controller
 * @author GT0291
 * @version 2017-08-03
 */
@Controller
@RequestMapping(value = "${adminPath}/bas/basmessage/basMessage")
public class BasMessageController extends BaseController {

	@Autowired
	private BasMessageService basMessageService;
	

	private void setDbType() {
		//DataSourceContextHolder.setDbType("dataSourceApp");
	}
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public BasMessage get(@RequestParam(required=false) String id) {
		this.setDbType();
		BasMessage entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = basMessageService.get(id);
		}
		if (entity == null){
			entity = new BasMessage();
		}
		return entity;
	}
	
	/**
	 * 消息列表页面
	 */
	@RequiresPermissions("bas:basmessage:basMessage:list")
	@RequestMapping(value = {"list", ""})
	public String list(BasMessage basMessage, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		return "modules/bas/basmessage/basMessageList";
	}
	
	/**
	 * 消息列表数据
	 */
	@ResponseBody
	@RequiresPermissions("bas:basmessage:basMessage:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(BasMessage basMessage, HttpServletRequest request, HttpServletResponse response, Model model) {
		this.setDbType();
		basMessage.getSqlMap().put("dsf", this.getDataScope());
		Page<BasMessage> page = basMessageService.findPage(new Page<BasMessage>(request, response), basMessage); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑消息表单页面
	 */
	@RequiresPermissions(value={"bas:basmessage:basMessage:view","bas:basmessage:basMessage:add","bas:basmessage:basMessage:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(BasMessage basMessage, Model model) {
		this.setDbType();
		model.addAttribute("basMessage", basMessage);
		return "modules/bas/basmessage/basMessageForm";
	}

	/**
	 * 保存消息
	 */
	@ResponseBody
	@RequiresPermissions(value={"bas:basmessage:basMessage:add","bas:basmessage:basMessage:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(BasMessage basMessage, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		this.setDbType();
		basMessage.setContent(basMessage.getContent().replaceAll("&quot;", "\""));
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, basMessage)){
			j.setSuccess(false);
			j.setMsg("非法参数！");
			return j;
		}
		*/
		if(!basMessage.getIsNewRecord()){
			//修改保存
			BasMessage t = basMessageService.get(basMessage.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg("记录已经被修改！");
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) basMessageService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(basMessage, t);
				basMessageService.save(t);
			}
		}else{
			//新建保存
			basMessageService.save(basMessage);
		}
		j.setSuccess(true);
		j.setMsg("保存消息成功");
		return j;
	}
	
	/**
	 * 删除消息
	 */
	@ResponseBody
	@RequiresPermissions("bas:basmessage:basMessage:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(BasMessage basMessage, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			basMessage.setDelFlag(Global.YES);
			basMessageService.saveV(basMessage); 
		}
		basMessageService.delete(basMessage);
		j.setMsg("删除消息成功");
		return j;
	}
	
	/**
	 * 处理消息
	 */
	@ResponseBody
	@RequiresPermissions("bas:basmessage:basMessage:edit")
	@RequestMapping(value = "deal")
	public AjaxJson deal(BasMessage basMessage, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		basMessageService.anlyzeMessage(null, true,basMessage.getId()); //管理员，强制处理，重发通知
		j.setMsg("处理消息成功");
		return j;
	}
	
	/**
	 * 批量删除消息
	 */
	@ResponseBody
	@RequiresPermissions("bas:basmessage:basMessage:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				BasMessage obj = basMessageService.get(id);
				obj.setDelFlag(Global.YES);
				basMessageService.saveV(obj); 
			}
			basMessageService.delete(basMessageService.get(id));
		}
		j.setMsg("删除消息成功");
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("bas:basmessage:basMessage:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(BasMessage basMessage, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "消息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<BasMessage> page = basMessageService.findPage(new Page<BasMessage>(request, response, -1), basMessage);
    		new ExportExcel("消息", BasMessage.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("导出成功！");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("导出消息记录失败！失败信息："+e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("bas:basmessage:basMessage:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<BasMessage> list = ei.getDataList(BasMessage.class);
			for (BasMessage basMessage : list){
				try{
					basMessageService.save(basMessage);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条消息记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条消息记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入消息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/bas/basmessage/basMessage/?repage";
    }
	
	/**
	 * 下载导入消息数据模板
	 */
	@RequiresPermissions("bas:basmessage:basMessage:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		this.setDbType();
		try {
            String fileName = "消息数据导入模板.xlsx";
    		List<BasMessage> list = Lists.newArrayList(); 
    		new ExportExcel("消息数据", BasMessage.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/bas/basmessage/basMessage/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
}