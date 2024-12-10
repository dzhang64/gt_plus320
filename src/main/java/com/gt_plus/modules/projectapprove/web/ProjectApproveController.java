/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectapprove.web;

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
import com.gt_plus.modules.projectapprove.entity.ProjectApprove;
import com.gt_plus.modules.projectapprove.service.ProjectApproveService;
import com.gt_plus.modules.projectinfo.dao.ProjectInfoDao;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.sys.utils.FileUtils;
/**
 * 项目立项Controller
 * @author zdy
 * @version 2018-03-17
 */
@Controller
@RequestMapping(value = "${adminPath}/projectapprove/projectApprove")
public class ProjectApproveController extends BaseController {

	@Autowired
	private ProjectApproveService projectApproveService;
	
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
	public ProjectApprove get(@RequestParam(required=false) String id) {
		ProjectApprove entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectApproveService.get(id);
		}
		if (entity == null){
			entity = new ProjectApprove();
		}
		return entity;
	}
	
	/**
	 * 项目立项列表页面
	 */
	@RequiresPermissions("projectapprove:projectApprove:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, ProjectApprove projectApprove, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(ProjectApproveService.PATH_QUERY)) {
			return "modules/projectapprove/projectApproveListQuery";
		} else {
			return "modules/projectapprove/projectApproveList";
		}
	}
	
	/**
	 * 项目立项列表数据
	 */
	@ResponseBody
	@RequiresPermissions("projectapprove:projectApprove:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, ProjectApprove projectApprove, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProjectApprove> page = new Page<ProjectApprove>();
		if(path.equalsIgnoreCase(ProjectApproveService.PATH_QUERY)) {
			projectApprove.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = projectApproveService.findPage(new Page<ProjectApprove>(request, response), projectApprove); 
		} else {
			projectApprove.getSqlMap().put("dsf", this.getDataScope());
			page = projectApproveService.findPage(new Page<ProjectApprove>(request, response), projectApprove, path); 
			page.setCount(page.getList().size());
		}
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑项目立项表单页面
	 */
	@RequiresPermissions(value={"projectapprove:projectApprove:view","projectapprove:projectApprove:add","projectapprove:projectApprove:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ProjectApprove projectApprove, Model model,HttpServletRequest request) {
		String realPath = FileUtils.getRealPath(request);
		String relativePath = null;
		if (projectApprove.getIsNewRecord()) {
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
		
		projectApproveService.setAct(projectApprove);
		projectApproveService.setRuleArgs(projectApprove);
		model.addAttribute("projectApprove", projectApprove);
		return "modules/projectapprove/projectApproveForm";
	}
	
	/**
	 * 编辑正文
	 */
	@RequiresPermissions(value={"projectapprove:projectApprove:view","projectapprove:projectApprove:add","projectapprove:projectApprove:edit"},logical=Logical.OR)
	@RequestMapping(value = "content")
	public String content(ProjectApprove projectApprove, Model model, String uploadPath, String token, HttpServletRequest request, HttpServletResponse response) {
		//2017-12-17新增模版相关//2017-12-19第二版
		String realPath = FileUtils.getRealPath(request);
		String tplUrl = projectApprove.getRemarks().substring(
				projectApprove.getRemarks().indexOf(Global.getConfig("fileUploadFolder"))+ Global.getConfig("fileUploadFolder").length(),
				projectApprove.getRemarks().length());
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
		model.addAttribute("id", projectApprove.getId());
		model.addAttribute("path", "projectapprove/projectApprove");
		if (StringUtils.isEmpty(projectApprove.getProcInsId())) {
			model.addAttribute("editType", "1");
		} else {
			if (projectApprove.getRuleArgs() != null && projectApprove.getRuleArgs().get("content") != null
					&& projectApprove.getRuleArgs().get("content").get("editType") != null) {
				model.addAttribute("editType", projectApprove.getRuleArgs().get("content").get("editType"));
			} else {
				model.addAttribute("editType", "1");
			}
		}
		
		model.addAttribute("projectApprove", projectApprove);
		return "modules/sys/webOfficeContent";
	}

	/**
	 * 保存项目立项
	 */
	@ResponseBody
	@RequiresPermissions(value={"projectapprove:projectApprove:add","projectapprove:projectApprove:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ProjectApprove projectApprove, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, projectApprove)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!projectApprove.getIsNewRecord()){
			//修改保存
			ProjectApprove t = projectApproveService.get(projectApprove.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) projectApproveService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(projectApprove, t);
				projectApproveService.saveAct(t);
			}
		}else{
			//新建保存
            if(projectApproveService.checkProjectInfo(projectApprove)){
        		projectApprove.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
    			projectApproveService.saveAct(projectApprove);	
            }
            else{
            	j.setSuccess(false);
    			j.setMsg(DictUtils.getDictLabel("存在同名或者同编号项目", "msg_bas", "存在同名或者同编号项目"));
    			return j;
            }
	
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存项目立项信息成功","项目立项"));
		//保存成功后处理逻辑
		this.afterSave("项目立项", projectApprove);
		return j;
	}
	
	/**
	 * 删除项目立项
	 */
	@ResponseBody
	@RequiresPermissions("projectapprove:projectApprove:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ProjectApprove projectApprove, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			projectApprove.setDelFlag(Global.YES);
			projectApproveService.saveV(projectApprove); 
		}
		projectApproveService.delete(projectApprove);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除项目立项信息成功","项目立项"));
		return j;
	}
	
	/**
	 * 批量删除项目立项
	 */
	@ResponseBody
	@RequiresPermissions("projectapprove:projectApprove:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ProjectApprove obj = projectApproveService.get(id);
				obj.setDelFlag(Global.YES);
				projectApproveService.saveV(obj); 
			}
			projectApproveService.delete(projectApproveService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除项目立项信息成功","项目立项"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("projectapprove:projectApprove:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ProjectApprove projectApprove, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "项目立项"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ProjectApprove> page = projectApproveService.findPage(new Page<ProjectApprove>(request, response, -1), projectApprove);
    		new ExportExcel("项目立项", ProjectApprove.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出项目立项信息记录失败！", "项目立项") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("projectapprove:projectApprove:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ProjectApprove> list = ei.getDataList(ProjectApprove.class);
			for (ProjectApprove projectApprove : list){
				try{
					projectApproveService.save(projectApprove);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条项目立项记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条项目立项记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入项目立项失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/projectapprove/projectApprove/?repage";
    }
	
	/**
	 * 下载导入项目立项数据模板
	 */
	@RequiresPermissions("projectapprove:projectApprove:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "项目立项数据导入模板.xlsx";
    		List<ProjectApprove> list = Lists.newArrayList(); 
    		new ExportExcel("项目立项数据", ProjectApprove.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/projectapprove/projectApprove/?repage";
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
	private void afterSave(String title, ProjectApprove projectApprove) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/projectapprove/projectApprove");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, projectApprove.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("projectapprove:projectApprove:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(ProjectApprove projectApprove, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		String[] ids = request.getParameterValues("userKey");
		LinkedHashMap<String, Object> targetUserInfo = projectApproveService.getTargetUserList(projectApprove,ids);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}
}