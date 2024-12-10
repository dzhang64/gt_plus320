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
import com.gt_plus.modules.oa.entity.test.ActLeave;
import com.gt_plus.modules.oa.service.test.ActLeaveService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 请假表单Controller
 * @author GT0155
 * @version 2017-11-29
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/test/actLeave")
public class ActLeaveController extends BaseController {

	@Autowired
	private ActLeaveService actLeaveService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public ActLeave get(@RequestParam(required=false) String id) {
		ActLeave entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = actLeaveService.get(id);
		}
		if (entity == null){
			entity = new ActLeave();
		}
		return entity;
	}
	
	/**
	 * 请假表单列表页面
	 */
	@RequiresPermissions("oa:test:actLeave:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, ActLeave actLeave, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(ActLeaveService.PATH_QUERY)) {
			return "modules/oa/test/actLeaveListQuery";
		} else {
			return "modules/oa/test/actLeaveList";
		}
	}
	
	/**
	 * 请假表单列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actLeave:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, ActLeave actLeave, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActLeave> page = new Page<ActLeave>();
		if(path.equalsIgnoreCase(ActLeaveService.PATH_QUERY)) {
			actLeave.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = actLeaveService.findPage(new Page<ActLeave>(request, response), actLeave); 
		} else {
			actLeave.getSqlMap().put("dsf", this.getDataScope());
			page = actLeaveService.findPage(new Page<ActLeave>(request, response), actLeave, path); 
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑请假表单表单页面
	 */
	@RequiresPermissions(value={"oa:test:actLeave:view","oa:test:actLeave:add","oa:test:actLeave:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ActLeave actLeave, Model model) {
		model.addAttribute("userList", actLeaveService.getStartingUserList(actLeave));
		actLeaveService.setAct(actLeave);
		actLeaveService.setRuleArgs(actLeave);
		model.addAttribute("actLeave", actLeave);
		return "modules/oa/test/actLeaveForm";
	}

	/**
	 * 保存请假表单
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:test:actLeave:add","oa:test:actLeave:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ActLeave actLeave, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, actLeave)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!actLeave.getIsNewRecord()){
			//修改保存
			ActLeave t = actLeaveService.get(actLeave.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) actLeaveService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(actLeave, t);
				actLeaveService.saveAct(t);
			}
		}else{
			//新建保存
			actLeave.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			actLeaveService.saveAct(actLeave);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存请假表单信息成功","请假表单"));
		//保存成功后处理逻辑
		this.afterSave("请假表单", actLeave);
		return j;
	}
	
	/**
	 * 删除请假表单
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actLeave:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ActLeave actLeave, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			actLeave.setDelFlag(Global.YES);
			actLeaveService.saveV(actLeave); 
		}
		actLeaveService.delete(actLeave);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除请假表单信息成功","请假表单"));
		return j;
	}
	
	/**
	 * 批量删除请假表单
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actLeave:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ActLeave obj = actLeaveService.get(id);
				obj.setDelFlag(Global.YES);
				actLeaveService.saveV(obj); 
			}
			actLeaveService.delete(actLeaveService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除请假表单信息成功","请假表单"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:actLeave:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ActLeave actLeave, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "请假表单"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ActLeave> page = actLeaveService.findPage(new Page<ActLeave>(request, response, -1), actLeave);
    		new ExportExcel("请假表单", ActLeave.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出请假表单信息记录失败！", "请假表单") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:test:actLeave:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ActLeave> list = ei.getDataList(ActLeave.class);
			for (ActLeave actLeave : list){
				try{
					actLeaveService.save(actLeave);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条请假表单记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条请假表单记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入请假表单失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/actLeave/?repage";
    }
	
	/**
	 * 下载导入请假表单数据模板
	 */
	@RequiresPermissions("oa:test:actLeave:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "请假表单数据导入模板.xlsx";
    		List<ActLeave> list = Lists.newArrayList(); 
    		new ExportExcel("请假表单数据", ActLeave.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/actLeave/?repage";
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
	private void afterSave(String title, ActLeave actLeave) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/test/actLeave");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, actLeave.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:test:actLeave:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(ActLeave actLeave, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		LinkedHashMap<String, Object> targetUserInfo = actLeaveService.getTargetUserList(actLeave);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}