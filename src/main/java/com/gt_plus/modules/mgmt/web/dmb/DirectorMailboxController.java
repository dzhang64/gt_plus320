/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.mgmt.web.dmb;

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
import com.gt_plus.modules.mgmt.entity.dmb.DirectorMailbox;
import com.gt_plus.modules.mgmt.service.dmb.DirectorMailboxService;
import com.gt_plus.modules.sys.entity.Office;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 局长信箱Controller
 * @author GT0155
 * @version 2017-11-28
 */
@Controller
@RequestMapping(value = "${adminPath}/mgmt/dmb/directorMailbox")
public class DirectorMailboxController extends BaseController {

	@Autowired
	private DirectorMailboxService directorMailboxService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = true; 
	
	@ModelAttribute
	public DirectorMailbox get(@RequestParam(required=false) String id) {
		DirectorMailbox entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = directorMailboxService.get(id);
		}
		if (entity == null){
			entity = new DirectorMailbox();
		}
		return entity;
	}
	
	/**
	 * 局长信箱列表页面
	 */
	@RequiresPermissions("mgmt:dmb:directorMailbox:list")
	@RequestMapping(value = {"list", ""})
	public String list(DirectorMailbox directorMailbox, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/mgmt/dmb/directorMailboxList";
	}
	
	/**
	 * 局长信箱列表数据
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:dmb:directorMailbox:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(DirectorMailbox directorMailbox, HttpServletRequest request, HttpServletResponse response, Model model) {
		directorMailbox.getSqlMap().put("dsf", this.getDataScope());
		Page<DirectorMailbox> page = directorMailboxService.findPage(new Page<DirectorMailbox>(request, response), directorMailbox); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑局长信箱表单页面
	 */
	@RequiresPermissions(value={"mgmt:dmb:directorMailbox:view","mgmt:dmb:directorMailbox:add","mgmt:dmb:directorMailbox:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(DirectorMailbox directorMailbox, Model model) {
		model.addAttribute("directorMailbox", directorMailbox);
		return "modules/mgmt/dmb/directorMailboxForm";
	}

	/**
	 * 保存局长信箱
	 */
	@ResponseBody
	@RequiresPermissions(value={"mgmt:dmb:directorMailbox:add","mgmt:dmb:directorMailbox:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(DirectorMailbox directorMailbox, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, directorMailbox)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!directorMailbox.getIsNewRecord()){
			//修改保存
			DirectorMailbox t = directorMailboxService.get(directorMailbox.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) directorMailboxService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(directorMailbox, t);
				directorMailboxService.save(t);
			}
		}else{
			//新建保存
			directorMailbox.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			directorMailboxService.save(directorMailbox);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存局长信箱信息成功","局长信箱"));
		//保存成功后处理逻辑
		this.afterSave("局长信箱", directorMailbox);
		return j;
	}
	
	/**
	 * 删除局长信箱
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:dmb:directorMailbox:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(DirectorMailbox directorMailbox, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			directorMailbox.setDelFlag(Global.YES);
			directorMailboxService.saveV(directorMailbox); 
		}
		directorMailboxService.delete(directorMailbox);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除局长信箱信息成功","局长信箱"));
		return j;
	}
	
	/**
	 * 批量删除局长信箱
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:dmb:directorMailbox:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				DirectorMailbox obj = directorMailboxService.get(id);
				obj.setDelFlag(Global.YES);
				directorMailboxService.saveV(obj); 
			}
			directorMailboxService.delete(directorMailboxService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除局长信箱信息成功","局长信箱"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("mgmt:dmb:directorMailbox:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(DirectorMailbox directorMailbox, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "局长信箱"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<DirectorMailbox> page = directorMailboxService.findPage(new Page<DirectorMailbox>(request, response, -1), directorMailbox);
    		new ExportExcel("局长信箱", DirectorMailbox.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出局长信箱信息记录失败！", "局长信箱") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("mgmt:dmb:directorMailbox:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<DirectorMailbox> list = ei.getDataList(DirectorMailbox.class);
			for (DirectorMailbox directorMailbox : list){
				try{
					directorMailboxService.save(directorMailbox);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条局长信箱记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条局长信箱记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入局长信箱失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/mgmt/dmb/directorMailbox/?repage";
    }
	
	/**
	 * 下载导入局长信箱数据模板
	 */
	@RequiresPermissions("mgmt:dmb:directorMailbox:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "局长信箱数据导入模板.xlsx";
    		List<DirectorMailbox> list = Lists.newArrayList(); 
    		new ExportExcel("局长信箱数据", DirectorMailbox.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/mgmt/dmb/directorMailbox/?repage";
    }
	
	
	
	/**
	 * 创建数据范围
	 */
	private String getDataScope() {
		StringBuilder sqlString = new StringBuilder();
		sqlString.append(" AND a.owner_code = '" + officeService.get(UserUtils.getUser().getCompany().getId()).getCode() + "'");
		return sqlString.toString();
	}
	
	
	/**
	 * 保存成功后处理逻辑
	 */
	private void afterSave(String title, DirectorMailbox directorMailbox) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/mgmt/dmb/directorMailbox");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, directorMailbox.getOwnerCode(), roleMap);
	}
	
	/**
	 * 局长信箱新增接口
	 */
	@RequestMapping(value ="saveObj",method=RequestMethod.GET)
    @ResponseBody
    public String saveObj(DirectorMailbox directorMailbox){
		if(directorMailbox.getComp()!=null){
			try{
				directorMailboxService.save(directorMailbox);
				return "callback({'id':'" + directorMailbox.getId() + "'});";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "callback({'id':''});";
    }
	
	/**
	 * 局长信箱更新接口
	 */
	@RequestMapping(value ="updateObj",method=RequestMethod.GET)
    @ResponseBody
    public String updateObj(DirectorMailbox directorMailbox){
		if(directorMailbox.getId()!=null){
			try{
				directorMailboxService.save(directorMailbox);
				return "callback({'msg':'提交成功'});";
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return "callback({'msg':'提交失败'});";
    }
	
}