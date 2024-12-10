/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusercontractrenew.web;

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
import com.gt_plus.modules.sysusercontractrenew.entity.SysUserContractRenew;
import com.gt_plus.modules.sysusercontractrenew.service.SysUserContractRenewService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.sys.utils.FileUtils;
/**
 * 合同续签Controller
 * @author wl
 * @version 2018-03-27
 */
@Controller
@RequestMapping(value = "${adminPath}/sysusercontractrenew/sysUserContractRenew")
public class SysUserContractRenewController extends BaseController {

	@Autowired
	private SysUserContractRenewService sysUserContractRenewService;
	
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
	public SysUserContractRenew get(@RequestParam(required=false) String id) {
		SysUserContractRenew entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysUserContractRenewService.get(id);
		}
		if (entity == null){
			entity = new SysUserContractRenew();
		}
		return entity;
	}
	
	/**
	 * 合同续签列表页面
	 */
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, SysUserContractRenew sysUserContractRenew, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(SysUserContractRenewService.PATH_QUERY)) {
			return "modules/sysusercontractrenew/sysUserContractRenewListQuery";
		} else {
			return "modules/sysusercontractrenew/sysUserContractRenewList";
		}
	}
	
	/**
	 * 合同续签列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, SysUserContractRenew sysUserContractRenew, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysUserContractRenew> page = new Page<SysUserContractRenew>();
		if(path.equalsIgnoreCase(SysUserContractRenewService.PATH_QUERY)) {
			sysUserContractRenew.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = sysUserContractRenewService.findPage(new Page<SysUserContractRenew>(request, response), sysUserContractRenew); 
		} else {
			sysUserContractRenew.getSqlMap().put("dsf", this.getDataScope());
			page = sysUserContractRenewService.findPage(new Page<SysUserContractRenew>(request, response), sysUserContractRenew, path); 
			page.setCount(page.getList().size());
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑合同续签表单页面
	 */
	@RequiresPermissions(value={"sysusercontractrenew:sysUserContractRenew:view","sysusercontractrenew:sysUserContractRenew:add","sysusercontractrenew:sysUserContractRenew:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SysUserContractRenew sysUserContractRenew, Model model,HttpServletRequest request) {
		String realPath = FileUtils.getRealPath(request);
		String relativePath = null;
		if (sysUserContractRenew.getIsNewRecord()) {
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
		
		sysUserContractRenewService.setAct(sysUserContractRenew);
		sysUserContractRenewService.setRuleArgs(sysUserContractRenew);
		model.addAttribute("sysUserContractRenew", sysUserContractRenew);
		return "modules/sysusercontractrenew/sysUserContractRenewForm";
	}
	
	/**
	 * 编辑正文
	 */
	@RequiresPermissions(value={"sysusercontractrenew:sysUserContractRenew:view","sysusercontractrenew:sysUserContractRenew:add","sysusercontractrenew:sysUserContractRenew:edit"},logical=Logical.OR)
	@RequestMapping(value = "content")
	public String content(SysUserContractRenew sysUserContractRenew, Model model, String uploadPath, String token, HttpServletRequest request, HttpServletResponse response) {
		//2017-12-17新增模版相关//2017-12-19第二版
		String realPath = FileUtils.getRealPath(request);
		String tplUrl = sysUserContractRenew.getRemarks().substring(
				sysUserContractRenew.getRemarks().indexOf(Global.getConfig("fileUploadFolder"))+ Global.getConfig("fileUploadFolder").length(),
				sysUserContractRenew.getRemarks().length());
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
		model.addAttribute("id", sysUserContractRenew.getId());
		model.addAttribute("path", "sysusercontractrenew/sysUserContractRenew");
		if (StringUtils.isEmpty(sysUserContractRenew.getProcInsId())) {
			model.addAttribute("editType", "1");
		} else {
			if (sysUserContractRenew.getRuleArgs() != null && sysUserContractRenew.getRuleArgs().get("content") != null
					&& sysUserContractRenew.getRuleArgs().get("content").get("editType") != null) {
				model.addAttribute("editType", sysUserContractRenew.getRuleArgs().get("content").get("editType"));
			} else {
				model.addAttribute("editType", "1");
			}
		}
		
		model.addAttribute("sysUserContractRenew", sysUserContractRenew);
		return "modules/sys/webOfficeContent";
	}

	/**
	 * 保存合同续签
	 */
	@ResponseBody
	@RequiresPermissions(value={"sysusercontractrenew:sysUserContractRenew:add","sysusercontractrenew:sysUserContractRenew:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SysUserContractRenew sysUserContractRenew, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, sysUserContractRenew)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!sysUserContractRenew.getIsNewRecord()){
			//修改保存
			SysUserContractRenew t = sysUserContractRenewService.get(sysUserContractRenew.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) sysUserContractRenewService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(sysUserContractRenew, t);
				sysUserContractRenewService.saveAct(t);
			}
		}else{
			//新建保存
			sysUserContractRenew.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			sysUserContractRenewService.saveAct(sysUserContractRenew);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存合同续签信息成功","合同续签"));
		//保存成功后处理逻辑
		this.afterSave("合同续签", sysUserContractRenew);
		return j;
	}
	
	/**
	 * 删除合同续签
	 */
	@ResponseBody
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SysUserContractRenew sysUserContractRenew, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			sysUserContractRenew.setDelFlag(Global.YES);
			sysUserContractRenewService.saveV(sysUserContractRenew); 
		}
		sysUserContractRenewService.delete(sysUserContractRenew);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除合同续签信息成功","合同续签"));
		return j;
	}
	
	/**
	 * 批量删除合同续签
	 */
	@ResponseBody
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SysUserContractRenew obj = sysUserContractRenewService.get(id);
				obj.setDelFlag(Global.YES);
				sysUserContractRenewService.saveV(obj); 
			}
			sysUserContractRenewService.delete(sysUserContractRenewService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除合同续签信息成功","合同续签"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SysUserContractRenew sysUserContractRenew, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "合同续签"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SysUserContractRenew> page = sysUserContractRenewService.findPage(new Page<SysUserContractRenew>(request, response, -1), sysUserContractRenew);
    		new ExportExcel("合同续签", SysUserContractRenew.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出合同续签信息记录失败！", "合同续签") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SysUserContractRenew> list = ei.getDataList(SysUserContractRenew.class);
			for (SysUserContractRenew sysUserContractRenew : list){
				try{
					sysUserContractRenewService.save(sysUserContractRenew);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条合同续签记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条合同续签记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入合同续签失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysusercontractrenew/sysUserContractRenew/?repage";
    }
	
	/**
	 * 下载导入合同续签数据模板
	 */
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "合同续签数据导入模板.xlsx";
    		List<SysUserContractRenew> list = Lists.newArrayList(); 
    		new ExportExcel("合同续签数据", SysUserContractRenew.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysusercontractrenew/sysUserContractRenew/?repage";
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
	private void afterSave(String title, SysUserContractRenew sysUserContractRenew) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sysusercontractrenew/sysUserContractRenew");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, sysUserContractRenew.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("sysusercontractrenew:sysUserContractRenew:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(SysUserContractRenew sysUserContractRenew, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		String[] ids = request.getParameterValues("userKey");
		LinkedHashMap<String, Object> targetUserInfo = sysUserContractRenewService.getTargetUserList(sysUserContractRenew,ids);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}