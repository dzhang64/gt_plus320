/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.sysusersscentry.web;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.gt_plus.common.config.Global;
import com.gt_plus.common.json.AjaxJson;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.DateUtils;
import com.gt_plus.common.utils.MyBeanUtils;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.utils.excel.ExportExcel;
import com.gt_plus.common.utils.excel.ImportExcel;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.bas.service.basmessage.BasMessageService;
import com.gt_plus.modules.oa.service.edoc.EdocTplService;
import com.gt_plus.modules.sys.service.OfficeService;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.sysusersscentry.entity.SysUserSscEntry;
import com.gt_plus.modules.sysusersscentry.service.SysUserSscEntryService;
/**
 * 社保统筹补录入Controller
 * @author wl
 * @version 2018-03-30
 */
@Controller
@RequestMapping(value = "${adminPath}/sysusersscentry/sysUserSscEntry")
public class SysUserSscEntryController extends BaseController {

	@Autowired
	private SysUserSscEntryService sysUserSscEntryService;
	
	@Autowired
	private EdocTplService edocTplService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public SysUserSscEntry get(@RequestParam(required=false) String id) {
		SysUserSscEntry entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysUserSscEntryService.get(id);
		}
		if (entity == null){
			entity = new SysUserSscEntry();
		}
		return entity;
	}
	
	/**
	 * 社保统筹补录入列表页面
	 */
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:list")
	@RequestMapping(value = {"list", ""})
	public String list(SysUserSscEntry sysUserSscEntry, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/sysusersscentry/sysUserSscEntryList";
	}
	
	/**
	 * 社保统筹补录入列表数据
	 */
	@ResponseBody
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(SysUserSscEntry sysUserSscEntry, HttpServletRequest request, HttpServletResponse response, Model model) {
		sysUserSscEntry.getSqlMap().put("dsf", this.getDataScope());
		Page<SysUserSscEntry> page = sysUserSscEntryService.findPage(new Page<SysUserSscEntry>(request, response), sysUserSscEntry); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑社保统筹补录入表单页面
	 */
	@RequiresPermissions(value={"sysusersscentry:sysUserSscEntry:view","sysusersscentry:sysUserSscEntry:add","sysusersscentry:sysUserSscEntry:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(SysUserSscEntry sysUserSscEntry, Model model,HttpServletRequest request) {
		model.addAttribute("sysUserSscEntry", sysUserSscEntry);
		return "modules/sysusersscentry/sysUserSscEntryForm";
	}
	

	/**
	 * 保存社保统筹补录入
	 */
	@ResponseBody
	@RequiresPermissions(value={"sysusersscentry:sysUserSscEntry:add","sysusersscentry:sysUserSscEntry:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(SysUserSscEntry sysUserSscEntry, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, sysUserSscEntry)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!sysUserSscEntry.getIsNewRecord()){
			//修改保存
			SysUserSscEntry t = sysUserSscEntryService.get(sysUserSscEntry.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) sysUserSscEntryService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(sysUserSscEntry, t);
				sysUserSscEntryService.save(t);
			}
		}else{
			//新建保存
			sysUserSscEntry.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			sysUserSscEntryService.save(sysUserSscEntry);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存社保统筹补录入信息成功","社保统筹补录入"));
		//保存成功后处理逻辑
		this.afterSave("社保统筹补录入", sysUserSscEntry);
		return j;
	}
	
	/**
	 * 删除社保统筹补录入
	 */
	@ResponseBody
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(SysUserSscEntry sysUserSscEntry, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			sysUserSscEntry.setDelFlag(Global.YES);
			sysUserSscEntryService.saveV(sysUserSscEntry); 
		}
		sysUserSscEntryService.delete(sysUserSscEntry);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除社保统筹补录入信息成功","社保统筹补录入"));
		return j;
	}
	
	/**
	 * 批量删除社保统筹补录入
	 */
	@ResponseBody
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				SysUserSscEntry obj = sysUserSscEntryService.get(id);
				obj.setDelFlag(Global.YES);
				sysUserSscEntryService.saveV(obj); 
			}
			sysUserSscEntryService.delete(sysUserSscEntryService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除社保统筹补录入信息成功","社保统筹补录入"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(SysUserSscEntry sysUserSscEntry, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "社保统筹补录入"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<SysUserSscEntry> page = sysUserSscEntryService.findPage(new Page<SysUserSscEntry>(request, response, -1), sysUserSscEntry);
    		new ExportExcel("社保统筹补录入", SysUserSscEntry.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出社保统筹补录入信息记录失败！", "社保统筹补录入") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<SysUserSscEntry> list = ei.getDataList(SysUserSscEntry.class);
			for (SysUserSscEntry sysUserSscEntry : list){
				try{
					sysUserSscEntryService.save(sysUserSscEntry);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条社保统筹补录入记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条社保统筹补录入记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入社保统筹补录入失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysusersscentry/sysUserSscEntry/?repage";
    }
	
	/**
	 * 下载导入社保统筹补录入数据模板
	 */
	@RequiresPermissions("sysusersscentry:sysUserSscEntry:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "社保统筹补录入数据导入模板.xlsx";
    		List<SysUserSscEntry> list = Lists.newArrayList(); 
    		new ExportExcel("社保统筹补录入数据", SysUserSscEntry.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/sysusersscentry/sysUserSscEntry/?repage";
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
	private void afterSave(String title, SysUserSscEntry sysUserSscEntry) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/sysusersscentry/sysUserSscEntry");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, sysUserSscEntry.getOwnerCode(), roleMap);
	}
	
}