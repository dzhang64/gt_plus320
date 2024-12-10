/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.web.radar;

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
import com.gt_plus.modules.chart.entity.radar.ChartRadar;
import com.gt_plus.modules.chart.service.radar.ChartRadarService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 雷达图Controller
 * @author GT0155
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/chart/radar/chartRadar")
public class ChartRadarController extends BaseController {

	@Autowired
	private ChartRadarService chartRadarService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public ChartRadar get(@RequestParam(required=false) String id) {
		ChartRadar entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chartRadarService.get(id);
		}
		if (entity == null){
			entity = new ChartRadar();
		}
		return entity;
	}
	
	/**
	 * 雷达图列表页面
	 */
	@RequiresPermissions("chart:radar:chartRadar:list")
	@RequestMapping(value = {"list", ""})
	public String list(ChartRadar chartRadar, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		return "modules/chart/radar/chartRadarList";
	}
	
	/**
	 * 雷达图列表数据
	 */
	@ResponseBody
	@RequiresPermissions("chart:radar:chartRadar:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ChartRadar chartRadar, HttpServletRequest request, HttpServletResponse response, Model model) {
		chartRadar.getSqlMap().put("dsf", this.getDataScope());
		Page<ChartRadar> page = chartRadarService.findPage(new Page<ChartRadar>(request, response), chartRadar); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑雷达图表单页面
	 */
	@RequiresPermissions(value={"chart:radar:chartRadar:view","chart:radar:chartRadar:add","chart:radar:chartRadar:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ChartRadar chartRadar, Model model) {
		model.addAttribute("chartRadar", chartRadar);
		return "modules/chart/radar/chartRadarForm";
	}

	/**
	 * 保存雷达图
	 */
	@ResponseBody
	@RequiresPermissions(value={"chart:radar:chartRadar:add","chart:radar:chartRadar:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ChartRadar chartRadar, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, chartRadar)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!chartRadar.getIsNewRecord()){
			//修改保存
			ChartRadar t = chartRadarService.get(chartRadar.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) chartRadarService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(chartRadar, t);
				chartRadarService.save(t);
			}
		}else{
			//新建保存
			chartRadar.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			chartRadarService.save(chartRadar);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存雷达图信息成功","雷达图"));
		//保存成功后处理逻辑
		this.afterSave("雷达图", chartRadar);
		return j;
	}
	
	/**
	 * 删除雷达图
	 */
	@ResponseBody
	@RequiresPermissions("chart:radar:chartRadar:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ChartRadar chartRadar, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			chartRadar.setDelFlag(Global.YES);
			chartRadarService.saveV(chartRadar); 
		}
		chartRadarService.delete(chartRadar);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除雷达图信息成功","雷达图"));
		return j;
	}
	
	/**
	 * 批量删除雷达图
	 */
	@ResponseBody
	@RequiresPermissions("chart:radar:chartRadar:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ChartRadar obj = chartRadarService.get(id);
				obj.setDelFlag(Global.YES);
				chartRadarService.saveV(obj); 
			}
			chartRadarService.delete(chartRadarService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除雷达图信息成功","雷达图"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("chart:radar:chartRadar:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ChartRadar chartRadar, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "雷达图"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChartRadar> page = chartRadarService.findPage(new Page<ChartRadar>(request, response, -1), chartRadar);
    		new ExportExcel("雷达图", ChartRadar.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出雷达图信息记录失败！", "雷达图") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("chart:radar:chartRadar:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChartRadar> list = ei.getDataList(ChartRadar.class);
			for (ChartRadar chartRadar : list){
				try{
					chartRadarService.save(chartRadar);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条雷达图记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条雷达图记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入雷达图失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/radar/chartRadar/?repage";
    }
	
	/**
	 * 下载导入雷达图数据模板
	 */
	@RequiresPermissions("chart:radar:chartRadar:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "雷达图数据导入模板.xlsx";
    		List<ChartRadar> list = Lists.newArrayList(); 
    		new ExportExcel("雷达图数据", ChartRadar.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/radar/chartRadar/?repage";
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
	private void afterSave(String title, ChartRadar chartRadar) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/chart/radar/chartRadar");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, chartRadar.getOwnerCode(), roleMap);
	}
}