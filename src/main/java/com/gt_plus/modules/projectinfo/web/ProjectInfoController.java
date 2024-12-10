/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.projectinfo.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

import com.gt_plus.modules.userinfo.entity.UserInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.utils.Collections3;
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
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.projectinfo.entity.ProjectInfo;
import com.gt_plus.modules.projectinfo.service.ProjectInfoService;
import com.gt_plus.modules.sys.entity.Role;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
/**
 * 项目信息Controller
 * @author zdy
 * @version 2018-02-24
 */
@Controller
@RequestMapping(value = "${adminPath}/projectinfo/projectInfo")
public class ProjectInfoController extends BaseController {

	@Autowired
	private ProjectInfoService projectInfoService;
	
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
	public ProjectInfo get(@RequestParam(required=false) String id) {
		ProjectInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = projectInfoService.get(id);
		}
		if (entity == null){
			entity = new ProjectInfo();
		}
		return entity;
	}
	
	/**
	 * 项目信息列表页面
	 */
	@RequiresPermissions("projectinfo:projectInfo:list")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectInfo projectInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		return "modules/projectinfo/projectInfoList";
	}
	
	/**
	 * 项目信息列表数据
	 */
	@ResponseBody
	@RequiresPermissions("projectinfo:projectInfo:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ProjectInfo projectInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			UserInfo projectmanager = new UserInfo();
			projectmanager.setId(user.getId());
			projectInfo.setProjectmanager(projectmanager);
		}
		projectInfo.getSqlMap().put("dsf", this.getDataScope());
		Page<ProjectInfo> page = projectInfoService.findPage(new Page<ProjectInfo>(request, response), projectInfo); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑项目信息表单页面
	 */
	@RequiresPermissions(value={"projectinfo:projectInfo:view","projectinfo:projectInfo:add","projectinfo:projectInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ProjectInfo projectInfo, Model model,HttpServletRequest request) {
		model.addAttribute("projectInfo", projectInfo);
		return "modules/projectinfo/projectInfoForm";
	}
	

	/**
	 * 保存项目信息
	 */
	@ResponseBody
	@RequiresPermissions(value={"projectinfo:projectInfo:add","projectinfo:projectInfo:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ProjectInfo projectInfo, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, projectInfo)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!projectInfo.getIsNewRecord()){
			//修改保存
			ProjectInfo t = projectInfoService.get(projectInfo.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) projectInfoService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(projectInfo, t);
				projectInfoService.save(t);
				j.setSuccess(true);
				j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存项目信息信息成功","项目信息"));
				//保存成功后处理逻辑
				this.afterSave("项目信息", projectInfo);
				return j;
			}
		}else{
			//新建保存
			projectInfo.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			boolean res = projectInfoService.checkAndSave(projectInfo);
			if(res){
				
				String projectId = projectInfo.getId();
				String projectManagerId = projectInfo.getProjectmanager().getId();
				String roleId = "bb833b95fccb4d34b82cc17786561d18";
				//将项目经理插入项目人员
				projectInfoService.insertUserToPro(projectId,projectManagerId);
				//将项目经理的角色设置为项目经理
				UserUtils.setUserRole(projectManagerId, roleId);	
				j.setSuccess(true);
				j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存项目信息信息成功","项目信息"));
				//保存成功后处理逻辑
				this.afterSave("项目信息", projectInfo);
				return j;
			}
			else{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("存在同名或者同编号项目", "msg_bas", "存在同名或者同编号项目"));
				return j;
				
			}	
		}
		
	}
	
	/**
	 * 选择项目信息
	 */
	@RequestMapping(value = "selectprojectInfo")
	public String selectprojectInfo(ProjectInfo projectInfo, String url, String fieldLabels, String fieldKeys, String fieldTypes, String filter, String searchLabel, String searchKey, HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			fieldLabels = URLDecoder.decode(fieldLabels, "UTF-8");
			fieldKeys = URLDecoder.decode(fieldKeys, "UTF-8");
			fieldTypes = URLDecoder.decode(fieldTypes, "UTF-8");
			filter = URLDecoder.decode(filter, "UTF-8");
			searchLabel = URLDecoder.decode(searchLabel, "UTF-8");
			searchKey = URLDecoder.decode(searchKey, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//过滤项，修改该位置的值,在XML文件需要设置过滤项的值
		/*if(filter!=null&&filter!=""&&filter.length()>0){
			
		}*/
		Page<ProjectInfo> page = projectInfoService.findPageByprojectInfo(new Page<ProjectInfo>(request, response),  projectInfo);
		model.addAttribute("labelNames", fieldLabels.split("\\|"));
		model.addAttribute("labelValues", fieldKeys.split("\\|"));
		model.addAttribute("labelTypes", fieldTypes.split("\\|"));
		model.addAttribute("fieldLabels", fieldLabels);
		model.addAttribute("fieldKeys", fieldKeys);
		model.addAttribute("url", url);
		model.addAttribute("searchLabel", searchLabel);
		model.addAttribute("searchKey", searchKey);
		model.addAttribute("obj", projectInfo);
		model.addAttribute("page", page);
		return "modules/sys/gridselect";
	}
	
	/**
	 * 删除项目信息
	 */
	@ResponseBody
	@RequiresPermissions("projectinfo:projectInfo:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ProjectInfo projectInfo, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			projectInfo.setDelFlag(Global.YES);
			projectInfoService.saveV(projectInfo); 
		}
		projectInfoService.delete(projectInfo);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除项目信息信息成功","项目信息"));
		return j;
	}
	
	/**
	 * 批量删除项目信息
	 */
	@ResponseBody
	@RequiresPermissions("projectinfo:projectInfo:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ProjectInfo obj = projectInfoService.get(id);
				obj.setDelFlag(Global.YES);
				projectInfoService.saveV(obj); 
			}
			projectInfoService.delete(projectInfoService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除项目信息信息成功","项目信息"));
		return j;
	}
	
	
	/**
	 * 成员分配页面
	 * @param role
	 * @param model
	 * @return
	 */
	@RequiresPermissions("projectinfo:projectInfo:list")
	@RequestMapping(value = "assign")
	public String assign(ProjectInfo projectInfo, Model model) {
		
		List<UserInfo> userList=projectInfoService.findUserToPro(projectInfo.getId());
		model.addAttribute("userList", userList);
		model.addAttribute("projectInfo", projectInfo);
		return "modules/projectinfo/projectUserAssign";
	}
	
	/**
	 * 角色分配 -- 打开角色分配对话框
	 * @param role
	 * @param model
	 * @return
	 */
	@RequiresPermissions("projectinfo:projectInfo:list")
	@RequestMapping(value = "usertoorg")
	public String selectUserToOrg(ProjectInfo projectInfo,Model model) {
		List<UserInfo> usertoorgList=projectInfoService.findUserToPro(projectInfo.getId());
		model.addAttribute("usertoorgList", usertoorgList);
		model.addAttribute("selectIds", Collections3.extractToString(usertoorgList, "id", ","));
		model.addAttribute("officeList", officeService.findAll());
		return "modules/projectinfo/projectUserSelect";
	}
	
	/**
	 * 成员分配
	 * @param organization
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("projectinfo:projectInfo:list")
	@RequestMapping(value = "assignorg")
	public String assignUser(ProjectInfo projectInfo, String[] idsArr,RedirectAttributes redirectAttributes) {
		
		for (int i = 0; i < idsArr.length; i++) 
		{
				projectInfoService.insertUserToPro(projectInfo.getId(), idsArr[i]);
		}
		return "redirect:" + adminPath + "/projectinfo/projectInfo/assign?id="+projectInfo.getId();
	}
	
	
	/**
	 * 删除成员
	 * @param userId
	 * @param orgId
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("projectinfo:projectInfo:list")
	@RequestMapping(value = "outuser")
	public String outUser(String userId, String projectNum, RedirectAttributes redirectAttributes) {
		projectInfoService.deleteUserToPro(userId,projectNum);
		return "redirect:" + adminPath + "/projectinfo/projectInfo/assign?id="+projectNum;
	}

	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("projectinfo:projectInfo:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ProjectInfo projectInfo, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "项目信息"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ProjectInfo> page = projectInfoService.findPage(new Page<ProjectInfo>(request, response, -1), projectInfo);
    		new ExportExcel("项目信息", ProjectInfo.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出项目信息信息记录失败！", "项目信息") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("projectinfo:projectInfo:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ProjectInfo> list = ei.getDataList(ProjectInfo.class);
			for (ProjectInfo projectInfo : list){
				try{
					projectInfo.setOwnerCode("200000007");
					boolean res = projectInfoService.checkAndSave(projectInfo);
					if(res){
						String projectId = projectInfo.getId();
						String projectManagerId = projectInfo.getProjectmanager().getId();
						String roleId = "bb833b95fccb4d34b82cc17786561d18";
						//将项目经理插入项目人员
						projectInfoService.insertUserToPro(projectId,projectManagerId);
						//将项目经理的角色设置为项目经理
						UserUtils.setUserRole(projectManagerId, roleId);	
					}
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条项目信息记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条项目信息记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入项目信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/projectinfo/projectInfo/?repage";
    }
	
	/**
	 * 下载导入项目信息数据模板
	 */
	@RequiresPermissions("projectinfo:projectInfo:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "项目信息数据导入模板.xlsx";
    		List<ProjectInfo> list = Lists.newArrayList(); 
    		new ExportExcel("项目信息数据", ProjectInfo.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/projectinfo/projectInfo/?repage";
    }
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		return sqlString.toString();
	}
	
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, ProjectInfo projectInfo) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/projectinfo/projectInfo");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, projectInfo.getOwnerCode(), roleMap);
	}
	
}