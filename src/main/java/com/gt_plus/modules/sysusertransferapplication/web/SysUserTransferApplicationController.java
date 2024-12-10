/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusertransferapplication.web;

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
import com.gt_plus.modules.sysusertransferapplication.entity.SysUserTransferApplication;
import com.gt_plus.modules.sysusertransferapplication.service.SysUserTransferApplicationService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.sys.utils.FileUtils;
/**
 * 调薪申请Controller
 * @author wl
 * @version 2018-03-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sysusertransferapplication/sysUserTransferApplication")
public class SysUserTransferApplicationController extends BaseController {

	@Autowired
	private SysUserTransferApplicationService sysUserTransferApplicationService;
	
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
	public SysUserTransferApplication get(@RequestParam(required=false) String id) {
		SysUserTransferApplication entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysUserTransferApplicationService.get(id);
		}
		if (entity == null){
			entity = new SysUserTransferApplication();
		}
		return entity;
	}
	
	/**
	 * 调薪申请列表页面
	 */
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, SysUserTransferApplication sysUserTransferApplication, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(SysUserTransferApplicationService.PATH_QUERY)) {
			return "modules/sysusertransferapplication/sysUserTransferApplicationListQuery";
		} else {
			return "modules/sysusertransferapplication/sysUserTransferApplicationList";
		}
	}
	
	/**
	 * 调薪申请列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, SysUserTransferApplication sysUserTransferApplication, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysUserTransferApplication> page = new Page<SysUserTransferApplication>();
		if(path.equalsIgnoreCase(SysUserTransferApplicationService.PATH_QUERY)) {
			sysUserTransferApplication.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = sysUserTransferApplicationService.findPage(new Page<SysUserTransferApplication>(request, response), sysUserTransferApplication); 
		} else {
			sysUserTransferApplication.getSqlMap().put("dsf", this.getDataScope());
			page = sysUserTransferApplicationService.findPage(new Page<SysUserTransferApplication>(request, response), sysUserTransferApplication, path); 
			page.setCount(page.getList().size());
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑调薪申请表单页面
	 */
	@RequiresPermissions(value={"sysusertransferapplication:sysUserTransferApplication:view","sysusertransferapplication:sysUserTransferApplication:add","sysusertransferapplication:sysUserTransferApplication:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SysUserTransferApplication sysUserTransferApplication, Model model,HttpServletRequest request) {
		String realPath = FileUtils.getRealPath(request);
		String relativePath = null;
		if (sysUserTransferApplication.getIsNewRecord()) {
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
		
		sysUserTransferApplicationService.setAct(sysUserTransferApplication);
		sysUserTransferApplicationService.setRuleArgs(sysUserTransferApplication);
		model.addAttribute("sysUserTransferApplication", sysUserTransferApplication);
		return "modules/sysusertransferapplication/sysUserTransferApplicationForm";
	}
	
	/**
	 * 编辑正文
	 */
	@RequiresPermissions(value={"sysusertransferapplication:sysUserTransferApplication:view","sysusertransferapplication:sysUserTransferApplication:add","sysusertransferapplication:sysUserTransferApplication:edit"},logical=Logical.OR)
	@RequestMapping(value = "content")
	public String content(SysUserTransferApplication sysUserTransferApplication, Model model, String uploadPath, String token, HttpServletRequest request, HttpServletResponse response) {
		//2017-12-17新增模版相关//2017-12-19第二版
		String realPath = FileUtils.getRealPath(request);
		String tplUrl = sysUserTransferApplication.getRemarks().substring(
				sysUserTransferApplication.getRemarks().indexOf(Global.getConfig("fileUploadFolder"))+ Global.getConfig("fileUploadFolder").length(),
				sysUserTransferApplication.getRemarks().length());
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
		model.addAttribute("id", sysUserTransferApplication.getId());
		model.addAttribute("path", "sysusertransferapplication/sysUserTransferApplication");
		if (StringUtils.isEmpty(sysUserTransferApplication.getProcInsId())) {
			model.addAttribute("editType", "1");
		} else {
			if (sysUserTransferApplication.getRuleArgs() != null && sysUserTransferApplication.getRuleArgs().get("content") != null
					&& sysUserTransferApplication.getRuleArgs().get("content").get("editType") != null) {
				model.addAttribute("editType", sysUserTransferApplication.getRuleArgs().get("content").get("editType"));
			} else {
				model.addAttribute("editType", "1");
			}
		}
		
		model.addAttribute("sysUserTransferApplication", sysUserTransferApplication);
		return "modules/sys/webOfficeContent";
	}

	/**
	 * 保存调薪申请
	 */
	@ResponseBody
	@RequiresPermissions(value={"sysusertransferapplication:sysUserTransferApplication:add","sysusertransferapplication:sysUserTransferApplication:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SysUserTransferApplication sysUserTransferApplication, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, sysUserTransferApplication)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!sysUserTransferApplication.getIsNewRecord()){
			//修改保存
			SysUserTransferApplication t = sysUserTransferApplicationService.get(sysUserTransferApplication.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) sysUserTransferApplicationService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(sysUserTransferApplication, t);
				sysUserTransferApplicationService.saveAct(t);
			}
		}else{
			//新建保存
			sysUserTransferApplication.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			sysUserTransferApplicationService.saveAct(sysUserTransferApplication);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存调薪申请信息成功","调薪申请"));
		//保存成功后处理逻辑
		this.afterSave("调薪申请", sysUserTransferApplication);
		return j;
	}
	
	/**
	 * 删除调薪申请
	 */
	@ResponseBody
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SysUserTransferApplication sysUserTransferApplication, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			sysUserTransferApplication.setDelFlag(Global.YES);
			sysUserTransferApplicationService.saveV(sysUserTransferApplication); 
		}
		sysUserTransferApplicationService.delete(sysUserTransferApplication);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除调薪申请信息成功","调薪申请"));
		return j;
	}
	
	/**
	 * 批量删除调薪申请
	 */
	@ResponseBody
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SysUserTransferApplication obj = sysUserTransferApplicationService.get(id);
				obj.setDelFlag(Global.YES);
				sysUserTransferApplicationService.saveV(obj); 
			}
			sysUserTransferApplicationService.delete(sysUserTransferApplicationService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除调薪申请信息成功","调薪申请"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SysUserTransferApplication sysUserTransferApplication, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "调薪申请"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SysUserTransferApplication> page = sysUserTransferApplicationService.findPage(new Page<SysUserTransferApplication>(request, response, -1), sysUserTransferApplication);
    		new ExportExcel("调薪申请", SysUserTransferApplication.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出调薪申请信息记录失败！", "调薪申请") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SysUserTransferApplication> list = ei.getDataList(SysUserTransferApplication.class);
			for (SysUserTransferApplication sysUserTransferApplication : list){
				try{
					sysUserTransferApplicationService.save(sysUserTransferApplication);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条调薪申请记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条调薪申请记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入调薪申请失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysusertransferapplication/sysUserTransferApplication/?repage";
    }
	
	/**
	 * 下载导入调薪申请数据模板
	 */
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "调薪申请数据导入模板.xlsx";
    		List<SysUserTransferApplication> list = Lists.newArrayList(); 
    		new ExportExcel("调薪申请数据", SysUserTransferApplication.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysusertransferapplication/sysUserTransferApplication/?repage";
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
	private void afterSave(String title, SysUserTransferApplication sysUserTransferApplication) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sysusertransferapplication/sysUserTransferApplication");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, sysUserTransferApplication.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("sysusertransferapplication:sysUserTransferApplication:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(SysUserTransferApplication sysUserTransferApplication, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		String[] ids = request.getParameterValues("userKey");
		LinkedHashMap<String, Object> targetUserInfo = sysUserTransferApplicationService.getTargetUserList(sysUserTransferApplication,ids);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}