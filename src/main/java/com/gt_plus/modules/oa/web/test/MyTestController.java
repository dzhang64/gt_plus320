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
import com.gt_plus.modules.oa.entity.test.MyTest;
import com.gt_plus.modules.oa.service.test.MyTestService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 测试OAController
 * @author David
 * @version 2017-11-24
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/test/myTest")
public class MyTestController extends BaseController {

	@Autowired
	private MyTestService myTestService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public MyTest get(@RequestParam(required=false) String id) {
		MyTest entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = myTestService.get(id);
		}
		if (entity == null){
			entity = new MyTest();
		}
		return entity;
	}
	
	/**
	 * 测试OA列表页面
	 */
	@RequiresPermissions("oa:test:myTest:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, MyTest myTest, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(MyTestService.PATH_QUERY)) {
			return "modules/oa/test/myTestListQuery";
		} else {
			return "modules/oa/test/myTestList";
		}
	}
	
	/**
	 * 测试OA列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:myTest:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, MyTest myTest, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<MyTest> page = new Page<MyTest>();
		if(path.equalsIgnoreCase(MyTestService.PATH_QUERY)) {
			myTest.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = myTestService.findPage(new Page<MyTest>(request, response), myTest); 
		} else {
			myTest.getSqlMap().put("dsf", this.getDataScope());
			page = myTestService.findPage(new Page<MyTest>(request, response), myTest, path); 
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑测试OA表单页面
	 */
	@RequiresPermissions(value={"oa:test:myTest:view","oa:test:myTest:add","oa:test:myTest:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(MyTest myTest, Model model) {
		model.addAttribute("userList", myTestService.getStartingUserList(myTest));
		myTestService.setAct(myTest);
		model.addAttribute("myTest", myTest);
		return "modules/oa/test/myTestForm";
	}

	/**
	 * 保存测试OA
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:test:myTest:add","oa:test:myTest:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(MyTest myTest, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, myTest)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!myTest.getIsNewRecord()){
			//修改保存
			MyTest t = myTestService.get(myTest.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) myTestService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(myTest, t);
				myTestService.saveAct(t);
			}
		}else{
			//新建保存
			myTest.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			myTestService.saveAct(myTest);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存测试OA信息成功","测试OA"));
		//保存成功后处理逻辑
		this.afterSave("测试OA", myTest);
		return j;
	}
	
	/**
	 * 删除测试OA
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:myTest:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(MyTest myTest, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			myTest.setDelFlag(Global.YES);
			myTestService.saveV(myTest); 
		}
		myTestService.delete(myTest);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除测试OA信息成功","测试OA"));
		return j;
	}
	
	/**
	 * 批量删除测试OA
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:myTest:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				MyTest obj = myTestService.get(id);
				obj.setDelFlag(Global.YES);
				myTestService.saveV(obj); 
			}
			myTestService.delete(myTestService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除测试OA信息成功","测试OA"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:test:myTest:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(MyTest myTest, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "测试OA"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<MyTest> page = myTestService.findPage(new Page<MyTest>(request, response, -1), myTest);
    		new ExportExcel("测试OA", MyTest.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出测试OA信息记录失败！", "测试OA") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:test:myTest:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<MyTest> list = ei.getDataList(MyTest.class);
			for (MyTest myTest : list){
				try{
					myTestService.save(myTest);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条测试OA记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条测试OA记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入测试OA失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/myTest/?repage";
    }
	
	/**
	 * 下载导入测试OA数据模板
	 */
	@RequiresPermissions("oa:test:myTest:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "测试OA数据导入模板.xlsx";
    		List<MyTest> list = Lists.newArrayList(); 
    		new ExportExcel("测试OA数据", MyTest.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/test/myTest/?repage";
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
	private void afterSave(String title, MyTest myTest) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/test/myTest");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, myTest.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:test:myTest:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(MyTest myTest, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		//LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
		//map.put("userList", myTestService.getTargetUserList(myTest));
		//j.setBody(map);
		LinkedHashMap<String, Object> targetUserInfo = myTestService.getTargetUserList(myTest);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}