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
import com.gt_plus.modules.oa.entity.test.TestActFlow;
import com.gt_plus.modules.oa.service.test.TestActFlowService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 测试002Controller
 * @author GT0155
 * @version 2017-11-28
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/test/testActFlow")
public class TestActFlowController extends BaseController {

	@Autowired
	private TestActFlowService testActFlowService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public TestActFlow get(@RequestParam(required=false) String id) {
		TestActFlow entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = testActFlowService.get(id);
		}
		if (entity == null){
			entity = new TestActFlow();
		}
		return entity;
	}
	
	/**
	 * 测试002列表页面
	 */
	@RequiresPermissions("oa:test:testActFlow:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, TestActFlow testActFlow, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(TestActFlowService.PATH_QUERY)) {
			return "modules/oa/test/testActFlowListQuery";
		} else {
			return "modules/oa/test/testActFlowList";
		}
	}
	
	/**
	 * 测试002列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:testActFlow:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, TestActFlow testActFlow, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TestActFlow> page = new Page<TestActFlow>();
		if(path.equalsIgnoreCase(TestActFlowService.PATH_QUERY)) {
			testActFlow.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = testActFlowService.findPage(new Page<TestActFlow>(request, response), testActFlow); 
		} else {
			testActFlow.getSqlMap().put("dsf", this.getDataScope());
			page = testActFlowService.findPage(new Page<TestActFlow>(request, response), testActFlow, path); 
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑测试002表单页面
	 */
	@RequiresPermissions(value={"oa:test:testActFlow:view","oa:test:testActFlow:add","oa:test:testActFlow:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(TestActFlow testActFlow, Model model) {
		model.addAttribute("userList", testActFlowService.getStartingUserList(testActFlow));
		testActFlowService.setAct(testActFlow);
		testActFlowService.setRuleArgs(testActFlow);
		model.addAttribute("testActFlow", testActFlow);
		return "modules/oa/test/testActFlowForm";
	}

	/**
	 * 保存测试002
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:test:testActFlow:add","oa:test:testActFlow:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(TestActFlow testActFlow, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, testActFlow)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!testActFlow.getIsNewRecord()){
			//修改保存
			TestActFlow t = testActFlowService.get(testActFlow.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) testActFlowService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(testActFlow, t);
				testActFlowService.saveAct(t);
			}
		}else{
			//新建保存
			testActFlow.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			testActFlowService.saveAct(testActFlow);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存测试002信息成功","测试002"));
		//保存成功后处理逻辑
		this.afterSave("测试002", testActFlow);
		return j;
	}
	
	/**
	 * 删除测试002
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:testActFlow:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(TestActFlow testActFlow, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			testActFlow.setDelFlag(Global.YES);
			testActFlowService.saveV(testActFlow); 
		}
		testActFlowService.delete(testActFlow);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除测试002信息成功","测试002"));
		return j;
	}
	
	/**
	 * 批量删除测试002
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:testActFlow:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				TestActFlow obj = testActFlowService.get(id);
				obj.setDelFlag(Global.YES);
				testActFlowService.saveV(obj); 
			}
			testActFlowService.delete(testActFlowService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除测试002信息成功","测试002"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:testActFlow:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(TestActFlow testActFlow, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "测试002"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TestActFlow> page = testActFlowService.findPage(new Page<TestActFlow>(request, response, -1), testActFlow);
    		new ExportExcel("测试002", TestActFlow.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出测试002信息记录失败！", "测试002") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:test:testActFlow:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TestActFlow> list = ei.getDataList(TestActFlow.class);
			for (TestActFlow testActFlow : list){
				try{
					testActFlowService.save(testActFlow);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条测试002记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条测试002记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入测试002失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/testActFlow/?repage";
    }
	
	/**
	 * 下载导入测试002数据模板
	 */
	@RequiresPermissions("oa:test:testActFlow:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "测试002数据导入模板.xlsx";
    		List<TestActFlow> list = Lists.newArrayList(); 
    		new ExportExcel("测试002数据", TestActFlow.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/testActFlow/?repage";
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
	private void afterSave(String title, TestActFlow testActFlow) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/test/testActFlow");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, testActFlow.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:test:testActFlow:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(TestActFlow testActFlow, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		LinkedHashMap<String, Object> targetUserInfo = testActFlowService.getTargetUserList(testActFlow);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}