/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.oa.web.setting;

import java.io.File;
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
import com.gt_plus.modules.oa.entity.setting.Sequence;
import com.gt_plus.modules.oa.service.setting.SequenceService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 文单序列Controller
 * @author GT0291
 * @version 2017-12-25
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/setting/sequence")
public class SequenceController extends BaseController {

	@Autowired
	private SequenceService sequenceService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public Sequence get(@RequestParam(required=false) String id) {
		Sequence entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sequenceService.get(id);
		}
		if (entity == null){
			entity = new Sequence();
		}
		return entity;
	}
	
	/**
	 * 文单序列列表页面
	 */
	@RequiresPermissions("oa:setting:sequence:list")
	@RequestMapping(value = {"list", ""})
	public String list(Sequence sequence, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/oa/setting/sequenceList";
	}
	
	/**
	 * 文单序列列表数据
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:sequence:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(Sequence sequence, HttpServletRequest request, HttpServletResponse response, Model model) {
		sequence.getSqlMap().put("dsf", this.getDataScope());
		Page<Sequence> page = sequenceService.findPage(new Page<Sequence>(request, response), sequence); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑文单序列表单页面
	 */
	@RequiresPermissions(value={"oa:setting:sequence:view","oa:setting:sequence:add","oa:setting:sequence:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(Sequence sequence, Model model) {
		model.addAttribute("sequence", sequence);
		return "modules/oa/setting/sequenceForm";
	}
	

	/**
	 * 保存文单序列
	 */
	@ResponseBody
	@RequiresPermissions(value={"oa:setting:sequence:add","oa:setting:sequence:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(Sequence sequence, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		
		if (!beanValidator(model, sequence)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		
		if(!sequence.getIsNewRecord()){
			//修改保存
			Sequence t = sequenceService.get(sequence.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) sequenceService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(sequence, t);
				sequenceService.save(t);
			}
		}else{
			//新建保存
			sequence.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			sequenceService.save(sequence);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存文单序列信息成功","文单序列"));
		//保存成功后处理逻辑
		this.afterSave("文单序列", sequence);
		return j;
	}
	
	/**
	 * 删除文单序列
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:sequence:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(Sequence sequence, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			sequence.setDelFlag(Global.YES);
			sequenceService.saveV(sequence); 
		}
		sequenceService.delete(sequence);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除文单序列信息成功","文单序列"));
		return j;
	}
	
	/**
	 * 批量删除文单序列
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:sequence:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				Sequence obj = sequenceService.get(id);
				obj.setDelFlag(Global.YES);
				sequenceService.saveV(obj); 
			}
			sequenceService.delete(sequenceService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除文单序列信息成功","文单序列"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("oa:setting:sequence:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(Sequence sequence, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "文单序列"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<Sequence> page = sequenceService.findPage(new Page<Sequence>(request, response, -1), sequence);
    		new ExportExcel("文单序列", Sequence.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出文单序列信息记录失败！", "文单序列") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:setting:sequence:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Sequence> list = ei.getDataList(Sequence.class);
			for (Sequence sequence : list){
				try{
					sequenceService.save(sequence);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条文单序列记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条文单序列记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入文单序列失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/sequence/?repage";
    }
	
	/**
	 * 下载导入文单序列数据模板
	 */
	@RequiresPermissions("oa:setting:sequence:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "文单序列数据导入模板.xlsx";
    		List<Sequence> list = Lists.newArrayList(); 
    		new ExportExcel("文单序列数据", Sequence.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/setting/sequence/?repage";
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
	private void afterSave(String title, Sequence sequence) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/oa/setting/sequence");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		//if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, sequence.getOwnerCode(), roleMap);
	}
	
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "getSequence")
	public String getSequence(String key, Integer year, RedirectAttributes redirectAttributes) {
		return sequenceService.getSequence(key, year);
	}
	
	@ResponseBody
	@RequiresPermissions("user")
	@RequestMapping(value = "confirmSequence")
	public String confirmSequence(String key, Integer year, Integer value, RedirectAttributes redirectAttributes) {
		return String.valueOf(sequenceService.confirmSequence(key, year, value));
	}
	
}