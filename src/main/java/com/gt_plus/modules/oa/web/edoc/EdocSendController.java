/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.edoc;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.Encodes;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;
import com.gt_plus.modules.oa.entity.edoc.EdocSend;
import com.gt_plus.modules.oa.entity.edoc.EdocTpl;
import com.gt_plus.modules.oa.service.edoc.EdocSendService;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.service.SystemService;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.FileUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
/**
 * 发文管理Controller
 * @author GT0155
 * @version 2017-12-08
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/edoc/edocSend")
public class EdocSendController extends BaseController {

	@Autowired
	private EdocSendService edocSendService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private EdocTplService edocTplService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public EdocSend get(@RequestParam(required=false) String id) {
		EdocSend entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = edocSendService.get(id);
		}
		if (entity == null){
			entity = new EdocSend();
		}
		return entity;
	}
	
	/**
	 * 发文管理列表页面
	 */
	@RequiresPermissions("oa:edoc:edocSend:list")
	@RequestMapping(value = "list/{path}")
	public String list(@PathVariable("path")String path, EdocSend edocSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		model.addAttribute("path", path);
		if(path.equalsIgnoreCase(EdocSendService.PATH_QUERY)) {
			return "modules/oa/edoc/edocSendListQuery";
		} else {
			return "modules/oa/edoc/edocSendList";
		}
	}
	
	/**
	 * 发文管理列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocSend:list")
	@RequestMapping(value = "data/{path}")
	public Map<String, Object> data(@PathVariable("path")String path, EdocSend edocSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<EdocSend> page = new Page<EdocSend>();
		if(path.equalsIgnoreCase(EdocSendService.PATH_QUERY)) {
			edocSend.getSqlMap().put("dsf", this.getDataScopeForAct());
			page = edocSendService.findPage(new Page<EdocSend>(request, response), edocSend); 
		} else {
			edocSend.getSqlMap().put("dsf", this.getDataScope());
			page = edocSendService.findPage(new Page<EdocSend>(request, response), edocSend, path); 
			page.setCount(page.getList().size());
		}
		//首页统计数量---begin---2017-12-14
		if ("count".equalsIgnoreCase(path)) {
			path = "TodoAndDoing";
			Map<String, Object> map = Maps.newHashMap();
			edocSend.getSqlMap().put("dsf", this.getDataScope());
			page = edocSendService.findPage(new Page<EdocSend>(request, response), edocSend, path); 
			map.put("count", page.getList().size());
			return map;
		}
		//首页统计数量---end
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑发文管理表单页面
	 */
	@RequiresPermissions(value={"oa:edoc:edocSend:view","oa:edoc:edocSend:add","oa:edoc:edocSend:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(EdocSend edocSend, Model model, HttpServletRequest request, HttpServletResponse response) {
		/*if (StringUtils.isEmpty(edocSend.getProcInsId())) {
			LinkedHashMap<String, Object> map = edocSendService.getStartingUserList(edocSend);
			model.addAttribute("userList", new Gson().toJson(map.get("userList")));
			model.addAttribute("type", map.get("type"));
		}*/
		
		String realPath = FileUtils.getRealPath(request);
		String relativePath = null;
		if (edocSend.getIsNewRecord()) {
			//2017-12-12新增正文相关
			relativePath = FileUtils.getRelativePath(request, FileUtils.getUuidToString());
			new File(realPath + relativePath).mkdirs();
			edocSend.setContentUrl(Encodes.urlEncode(relativePath));
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
		
		edocSendService.setAct(edocSend);
		edocSendService.setRuleArgs(edocSend);
		model.addAttribute("edocSend", edocSend);
		model.addAttribute("token", SystemService.entryptDesPassword(UserUtils.getPrincipal().getNo()));
		return "modules/oa/edoc/edocSendForm";
	}
	
	/**
	 * 编辑正文
	 */
	@RequestMapping(value = "content")
	public String content(EdocSend edocSend, Model model, String uploadPath, String token, HttpServletRequest request, HttpServletResponse response) {
		//2017-12-17新增模版相关//2017-12-19第二版
		String realPath = FileUtils.getRealPath(request);
		String tplUrl = edocSend.getRemarks().substring(
				edocSend.getRemarks().indexOf(Global.getConfig("fileUploadFolder"))+ Global.getConfig("fileUploadFolder").length(),
				edocSend.getRemarks().length());
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
		model.addAttribute("id", edocSend.getId());
		model.addAttribute("path", "/oa/edoc/edocSend");
		if (StringUtils.isEmpty(edocSend.getProcInsId())) {
			model.addAttribute("editType", "1");
		} else {
			if (edocSend.getRuleArgs() != null && edocSend.getRuleArgs().get("content") != null
					&& edocSend.getRuleArgs().get("content").get("editType") != null) {
			model.addAttribute("editType", edocSend.getRuleArgs().get("content").get("editType"));
			} else {
				model.addAttribute("editType", "1");
			}
		}
		
		model.addAttribute("edocSend", edocSend);
		return "modules/sys/webOfficeContent";
	}

	/**
	 * 保存发文管理
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:edoc:edocSend:add","oa:edoc:edocSend:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(EdocSend edocSend, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		if (false == StringUtils.isEmpty(edocSend.getCountersign())) {
			String info = edocSend.getCountersign().trim() 
					+ " [ " + UserUtils.getUser().getName() + " ] " 
					+  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
					+ "</br>";
			edocSend.setCountersign(info);
		}
		if (false == StringUtils.isEmpty(edocSend.getSign())) {
			String info = edocSend.getSign().trim() 
					+ " [ " + UserUtils.getUser().getName() + " ] "
					+  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
					+ "</br>";
			edocSend.setSign(info);
		}
		
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, edocSend)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!edocSend.getIsNewRecord()){
			//修改保存
			EdocSend t = edocSendService.get(edocSend.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) edocSendService.saveV(t);   
				edocSend.setCountersign(t.getCountersign() + edocSend.getCountersign());
				edocSend.setSign(t.getSign() + edocSend.getSign());
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(edocSend, t);
				edocSendService.saveAct(t);
			}
		}else{
			//新建保存
			edocSend.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			edocSendService.saveAct(edocSend);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存发文管理信息成功","发文管理"));
		//保存成功后处理逻辑
		this.afterSave("发文管理", edocSend);
		return j;
	}
	
	/**
	 * 删除发文管理
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocSend:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(EdocSend edocSend, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			edocSend.setDelFlag(Global.YES);
			edocSendService.saveV(edocSend); 
		}
		edocSendService.delete(edocSend);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除发文管理信息成功","发文管理"));
		return j;
	}
	
	/**
	 * 批量删除发文管理
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocSend:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				EdocSend obj = edocSendService.get(id);
				obj.setDelFlag(Global.YES);
				edocSendService.saveV(obj); 
			}
			edocSendService.delete(edocSendService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除发文管理信息成功","发文管理"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocSend:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(EdocSend edocSend, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "发文管理"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<EdocSend> page = edocSendService.findPage(new Page<EdocSend>(request, response, -1), edocSend);
    		new ExportExcel("发文管理", EdocSend.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出发文管理信息记录失败！", "发文管理") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:edoc:edocSend:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<EdocSend> list = ei.getDataList(EdocSend.class);
			for (EdocSend edocSend : list){
				try{
					edocSendService.save(edocSend);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条发文管理记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条发文管理记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入发文管理失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/edoc/edocSend/?repage";
    }
	
	/**
	 * 下载导入发文管理数据模板
	 */
	@RequiresPermissions("oa:edoc:edocSend:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "发文管理数据导入模板.xlsx";
    		List<EdocSend> list = Lists.newArrayList(); 
    		new ExportExcel("发文管理数据", EdocSend.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/edoc/edocSend/?repage";
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
	private void afterSave(String title, EdocSend edocSend) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/edoc/edocSend");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, edocSend.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("oa:edoc:edocSend:list")
	@RequestMapping(value = "getUserList")
	public AjaxJson getUserList(EdocSend edocSend, HttpServletRequest request, HttpServletResponse response, Model model) {
		AjaxJson j = new AjaxJson();
		LinkedHashMap<String, Object> targetUserInfo = edocSendService.getTargetUserList(edocSend);
		j.setBody(targetUserInfo);
		j.setSuccess(true);
		return j;
	}

}