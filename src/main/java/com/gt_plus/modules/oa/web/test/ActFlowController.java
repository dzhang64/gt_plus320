/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.google.common.collect.Maps;
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
import com.gt_plus.modules.oa.entity.test.ActFlow;
import com.gt_plus.modules.oa.service.test.ActFlowService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * act工作流Controller
 * @author GT0155
 * @version 2017-11-29
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/test/actFlow")
public class ActFlowController extends BaseController {

	@Autowired
	private ActFlowService actFlowService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public ActFlow get(@RequestParam(required=false) String id) {
		ActFlow entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = actFlowService.get(id);
		}
		if (entity == null){
			entity = new ActFlow();
		}
		return entity;
	}
	
	/**
	 * act工作流列表页面
	 */
	@RequiresPermissions("oa:test:actFlow:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, ActFlow actFlow, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(ActFlowService.PATH_QUERY)) {
			return "modules/oa/test/actFlowListQuery";
		} else {
			return "modules/oa/test/actFlowList";
		}
	}
	
	/**
	 * act工作流列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actFlow:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, ActFlow actFlow, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActFlow> page = new Page<ActFlow>();
		if(path.equalsIgnoreCase(ActFlowService.PATH_QUERY)) {
			actFlow.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = actFlowService.findPage(new Page<ActFlow>(request, response), actFlow); 
		} else {
			actFlow.getSqlMap().put("dsf", this.getDataScope());
			page = actFlowService.findPage(new Page<ActFlow>(request, response), actFlow, path); 
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑act工作流表单页面
	 */
	@RequiresPermissions(value={"oa:test:actFlow:view","oa:test:actFlow:add","oa:test:actFlow:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ActFlow actFlow, Model model) {
		model.addAttribute("userList", actFlowService.getStartingUserList(actFlow));
		actFlowService.setAct(actFlow);
		actFlowService.setRuleArgs(actFlow);
		model.addAttribute("actFlow", actFlow);
		return "modules/oa/test/actFlowForm";
	}

	/**
	 * 保存act工作流
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:test:actFlow:add","oa:test:actFlow:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ActFlow actFlow, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, actFlow)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!actFlow.getIsNewRecord()){
			//修改保存
			ActFlow t = actFlowService.get(actFlow.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) actFlowService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(actFlow, t);
				actFlowService.saveAct(t);
			}
		}else{
			//新建保存
			actFlow.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			actFlowService.saveAct(actFlow);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存act工作流信息成功","act工作流"));
		//保存成功后处理逻辑
		this.afterSave("act工作流", actFlow);
		return j;
	}
	
	/**
	 * 删除act工作流
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actFlow:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ActFlow actFlow, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			actFlow.setDelFlag(Global.YES);
			actFlowService.saveV(actFlow); 
		}
		actFlowService.delete(actFlow);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除act工作流信息成功","act工作流"));
		return j;
	}
	
	/**
	 * 批量删除act工作流
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actFlow:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ActFlow obj = actFlowService.get(id);
				obj.setDelFlag(Global.YES);
				actFlowService.saveV(obj); 
			}
			actFlowService.delete(actFlowService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除act工作流信息成功","act工作流"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actFlow:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ActFlow actFlow, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "act工作流"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ActFlow> page = actFlowService.findPage(new Page<ActFlow>(request, response, -1), actFlow);
    		new ExportExcel("act工作流", ActFlow.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出act工作流信息记录失败！", "act工作流") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:test:actFlow:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ActFlow> list = ei.getDataList(ActFlow.class);
			for (ActFlow actFlow : list){
				try{
					actFlowService.save(actFlow);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条act工作流记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条act工作流记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入act工作流失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/actFlow/?repage";
    }
	
	/**
	 * 下载导入act工作流数据模板
	 */
	@RequiresPermissions("oa:test:actFlow:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "act工作流数据导入模板.xlsx";
    		List<ActFlow> list = Lists.newArrayList(); 
    		new ExportExcel("act工作流数据", ActFlow.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/actFlow/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	private String getDataScopeForAct() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, ActFlow actFlow) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/test/actFlow");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, actFlow.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:test:actFlow:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(ActFlow actFlow, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		LinkedHashMap<String, Object> targetUserInfo = actFlowService.getTargetUserList(actFlow);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}