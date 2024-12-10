/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysuserpersonnelturnpositive.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.io.IOUtils;
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
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sysuserpersonnelturnpositive.entity.SysUserPersonnelTurnPositive;
import com.gt_plus.modules.sysuserpersonnelturnpositive.service.SysUserPersonnelTurnPositiveService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.sys.utils.FileUtils;
/**
 * 人员转正Controller
 * @author wl
 * @version 2018-03-19
 */
@Controller
@RequestMapping(value = "${adminPath}/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositive")
public class SysUserPersonnelTurnPositiveController extends BaseController {

	@Autowired
	private SysUserPersonnelTurnPositiveService sysUserPersonnelTurnPositiveService;
	
	@Autowired
	private EdocTplService edocTplService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	@Autowired
	private SystemService systemService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public SysUserPersonnelTurnPositive get(@RequestParam(required=false) String id) {
		SysUserPersonnelTurnPositive entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysUserPersonnelTurnPositiveService.get(id);
		}
		if (entity == null){
			entity = new SysUserPersonnelTurnPositive();
		}
		return entity;
	}
	
	/**
	 * 人员转正列表页面
	 */
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(SysUserPersonnelTurnPositiveService.PATH_QUERY)) {
			return "modules/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositiveListQuery";
		} else {
			return "modules/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositiveList";
		}
	}
	
	/**
	 * 人员转正列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysUserPersonnelTurnPositive> page = new Page<SysUserPersonnelTurnPositive>();
		if(path.equalsIgnoreCase(SysUserPersonnelTurnPositiveService.PATH_QUERY)) {
			sysUserPersonnelTurnPositive.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = sysUserPersonnelTurnPositiveService.findPage(new Page<SysUserPersonnelTurnPositive>(request, response), sysUserPersonnelTurnPositive); 
		} else {
			sysUserPersonnelTurnPositive.getSqlMap().put("dsf", this.getDataScope());
			page = sysUserPersonnelTurnPositiveService.findPage(new Page<SysUserPersonnelTurnPositive>(request, response), sysUserPersonnelTurnPositive, path); 
			page.setCount(page.getList().size());
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑人员转正表单页面
	 */
	@RequiresPermissions(value={"sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:view","sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:add","sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, Model model,HttpServletRequest request) {
		String realPath = FileUtils.getRealPath(request);
		String relativePath = null;
		if (sysUserPersonnelTurnPositive.getIsNewRecord()) {
		}
		model.addAttribute("realPath", Encodes.urlEncode(realPath));
		
		//2017-12-17新增模版相关
		EdocTpl edocTpl = new EdocTpl();
		edocTpl.setType("send");
		List<EdocTpl> list = edocTplService.findList(edocTpl);
		List<Map<String, String>> tplList = Lists.newArrayList();
		for (EdocTpl tpl : list) {
			String files = Encodes.unescapeHtml(tpl.getFiles());
			@SuppressWarnings("unchecked")
			List<Map<String, String>> fromJson = new Gson().fromJson(files, List.class);
			for (Map<String, String> map : fromJson) {
				tplList.add(map);
			}
		}
		model.addAttribute("tplList", tplList);
		
		sysUserPersonnelTurnPositiveService.setAct(sysUserPersonnelTurnPositive);
		sysUserPersonnelTurnPositiveService.setRuleArgs(sysUserPersonnelTurnPositive);
		model.addAttribute("sysUserPersonnelTurnPositive", sysUserPersonnelTurnPositive);
		return "modules/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositiveForm";
	}
	
	/**
	 * 编辑正文
	 */
	@RequiresPermissions(value={"sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:view","sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:add","sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:edit"},logical=Logical.OR)
	@RequestMapping(value = "content")
	public String content(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, Model model, String uploadPath, String token, HttpServletRequest request, HttpServletResponse response) {
		//2017-12-17新增模版相关//2017-12-19第二版
		String realPath = FileUtils.getRealPath(request);
		String tplUrl = sysUserPersonnelTurnPositive.getRemarks().substring(
				sysUserPersonnelTurnPositive.getRemarks().indexOf(Global.getConfig("fileUploadFolder"))+ Global.getConfig("fileUploadFolder").length(),
				sysUserPersonnelTurnPositive.getRemarks().length());
		String filename = tplUrl.substring(tplUrl.lastIndexOf("/") + 1, tplUrl.length());
		String filetype = filename.substring(filename.indexOf("."), filename.length());
		String uploadFilename = "/DOCUMENT";
		try {
			if (!new File(uploadPath + uploadFilename + filetype).exists()) {
				IOUtils.copy(new FileInputStream(new File(realPath + tplUrl)),
						new FileOutputStream(new File(uploadPath + uploadFilename
								+ filetype)));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute("filename", uploadFilename.substring(1, uploadFilename.length()));
		model.addAttribute("filetype", filetype);
		
		model.addAttribute("uploadPath", Encodes.urlEncode(uploadPath));
		//model.addAttribute("userName", UserUtils.getUser().getName());
		model.addAttribute("userName", systemService.getUserByNo(SystemService.decryptDesPassword(token)).getName());
		model.addAttribute("id", sysUserPersonnelTurnPositive.getId());
		model.addAttribute("path", "sysuserpersonnelturnpositive/sysUserPersonnelTurnPositive");
		if (StringUtils.isEmpty(sysUserPersonnelTurnPositive.getProcInsId())) {
			model.addAttribute("editType", "1");
		} else {
			if (sysUserPersonnelTurnPositive.getRuleArgs() != null && sysUserPersonnelTurnPositive.getRuleArgs().get("content") != null
					&& sysUserPersonnelTurnPositive.getRuleArgs().get("content").get("editType") != null) {
				model.addAttribute("editType", sysUserPersonnelTurnPositive.getRuleArgs().get("content").get("editType"));
			} else {
				model.addAttribute("editType", "1");
			}
		}
		
		model.addAttribute("sysUserPersonnelTurnPositive", sysUserPersonnelTurnPositive);
		return "modules/sys/webOfficeContent";
	}

	/**
	 * 保存人员转正
	 */
	@ResponseBody
	@RequiresPermissions(value={"sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:add","sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, sysUserPersonnelTurnPositive)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!sysUserPersonnelTurnPositive.getIsNewRecord()){
			//修改保存
			SysUserPersonnelTurnPositive t = sysUserPersonnelTurnPositiveService.get(sysUserPersonnelTurnPositive.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) sysUserPersonnelTurnPositiveService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(sysUserPersonnelTurnPositive, t);
				sysUserPersonnelTurnPositiveService.saveAct(t);
			}
		}else{
			//新建保存
			sysUserPersonnelTurnPositive.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			sysUserPersonnelTurnPositiveService.saveAct(sysUserPersonnelTurnPositive);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存人员转正信息成功","人员转正"));
		//保存成功后处理逻辑
		this.afterSave("人员转正", sysUserPersonnelTurnPositive);
		return j;
	}
	
	/**
	 * 删除人员转正
	 */
	@ResponseBody
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			sysUserPersonnelTurnPositive.setDelFlag(Global.YES);
			sysUserPersonnelTurnPositiveService.saveV(sysUserPersonnelTurnPositive); 
		}
		sysUserPersonnelTurnPositiveService.delete(sysUserPersonnelTurnPositive);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除人员转正信息成功","人员转正"));
		return j;
	}
	
	/**
	 * 批量删除人员转正
	 */
	@ResponseBody
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SysUserPersonnelTurnPositive obj = sysUserPersonnelTurnPositiveService.get(id);
				obj.setDelFlag(Global.YES);
				sysUserPersonnelTurnPositiveService.saveV(obj); 
			}
			sysUserPersonnelTurnPositiveService.delete(sysUserPersonnelTurnPositiveService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除人员转正信息成功","人员转正"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "人员转正"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SysUserPersonnelTurnPositive> page = sysUserPersonnelTurnPositiveService.findPage(new Page<SysUserPersonnelTurnPositive>(request, response, -1), sysUserPersonnelTurnPositive);
    		new ExportExcel("人员转正", SysUserPersonnelTurnPositive.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出人员转正信息记录失败！", "人员转正") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SysUserPersonnelTurnPositive> list = ei.getDataList(SysUserPersonnelTurnPositive.class);
			for (SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive : list){
				try{
					sysUserPersonnelTurnPositiveService.save(sysUserPersonnelTurnPositive);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条人员转正记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条人员转正记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入人员转正失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositive/?repage";
    }
	
	/**
	 * 下载导入人员转正数据模板
	 */
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "人员转正数据导入模板.xlsx";
    		List<SysUserPersonnelTurnPositive> list = Lists.newArrayList(); 
    		new ExportExcel("人员转正数据", SysUserPersonnelTurnPositive.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositive/?repage";
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
	private void afterSave(String title, SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sysuserpersonnelturnpositive/sysUserPersonnelTurnPositive");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, sysUserPersonnelTurnPositive.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("sysuserpersonnelturnpositive:sysUserPersonnelTurnPositive:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(SysUserPersonnelTurnPositive sysUserPersonnelTurnPositive, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		String[] ids = request.getParameterValues("userKey");
		LinkedHashMap<String, Object> targetUserInfo = sysUserPersonnelTurnPositiveService.getTargetUserList(sysUserPersonnelTurnPositive,ids);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}