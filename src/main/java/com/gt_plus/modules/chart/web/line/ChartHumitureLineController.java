/**
 * Copyright &copy; 2015-2020 <a href="http://www.gt_plus.org/">Gt_Plus</a> All rights reserved.
 */
package com.gt_plus.modules.chart.web.line;

import java.util.ArrayList;
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
import com.gt_plus.modules.sys.utils.ChartUtils;
import com.gt_plus.modules.sys.utils.DictUtils;
import com.gt_plus.modules.sys.utils.UserUtils;
import com.gt_plus.modules.chart.entity.line.ChartHumitureLine;
import com.gt_plus.modules.chart.service.line.ChartHumitureLineService;
import com.gt_plus.modules.sys.service.OfficeService;
/**
 * 温湿度曲线图Controller
 * @author GT0155
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/chart/line/chartHumitureLine")
public class ChartHumitureLineController extends BaseController {

	@Autowired
	private ChartHumitureLineService chartHumitureLineService;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private BasMessageService basMessageService;
	
	//是否版本化数据
	private static boolean isAutoVersion = false; 
	
	@ModelAttribute
	public ChartHumitureLine get(@RequestParam(required=false) String id) {
		ChartHumitureLine entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = chartHumitureLineService.get(id);
		}
		if (entity == null){
			entity = new ChartHumitureLine();
		}
		return entity;
	}
	
	/**
	 * 温湿度曲线图列表页面
	 */
	@RequiresPermissions("chart:line:chartHumitureLine:list")
	@RequestMapping(value = {"list", ""})
	public String list(ChartHumitureLine chartHumitureLine, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(!UserUtils.getUser().isAdmin()) model.addAttribute("ownerCode", officeService.get(UserUtils.getUser().getCompany()).getCode());
		
		String title = "温湿度曲线图";
		String subTitle = "小标题";
		
		List<String> date = new ArrayList<String>();//日期
		List<String> temperature = new ArrayList<String>();//温度
		List<String> humidity = new ArrayList<String>();//湿度
		for (int i = 1; i <= 12; i++){
			for(int j = 1; j <= 30; j++){
				date.add("'" + i + "月" + j + "日'");
				temperature.add((i + j) / 9 -i + 20 + "");
				humidity.add((i + j) / 9 -i + 60 + "");
			}
		}
		
		Map<String, Object> line = ChartUtils.getLine(title, subTitle, date, temperature, humidity);
		model.addAttribute("line", line);
		
		return "modules/chart/line/chartHumitureLineList";
	}
	
	/**
	 * 温湿度曲线图列表数据
	 */
	@ResponseBody
	@RequiresPermissions("chart:line:chartHumitureLine:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(ChartHumitureLine chartHumitureLine, HttpServletRequest request, HttpServletResponse response, Model model) {
		chartHumitureLine.getSqlMap().put("dsf", this.getDataScope());
		Page<ChartHumitureLine> page = chartHumitureLineService.findPage(new Page<ChartHumitureLine>(request, response), chartHumitureLine); 
		return getBootstrapData(page);
	}
	
	/**
	 * 查看，增加，编辑温湿度曲线图表单页面
	 */
	@RequiresPermissions(value={"chart:line:chartHumitureLine:view","chart:line:chartHumitureLine:add","chart:line:chartHumitureLine:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(ChartHumitureLine chartHumitureLine, Model model) {
		model.addAttribute("chartHumitureLine", chartHumitureLine);
		return "modules/chart/line/chartHumitureLineForm";
	}

	/**
	 * 保存温湿度曲线图
	 */
	@ResponseBody
	@RequiresPermissions(value={"chart:line:chartHumitureLine:add","chart:line:chartHumitureLine:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(ChartHumitureLine chartHumitureLine, Model model, RedirectAttributes redirectAttributes, String dirtyUpdateDate) throws Exception{
		AjaxJson j = new AjaxJson();
		/*
		if (!beanValidator(model, chartHumitureLine)){
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("非法参数", "msg_bas", "非法参数！"));
			return j;
		}
		*/
		if(!chartHumitureLine.getIsNewRecord()){
			//修改保存
			ChartHumitureLine t = chartHumitureLineService.get(chartHumitureLine.getId());
			if(!t.getUpdateDate().toString().equals(dirtyUpdateDate)) 
			{
				//保存前记录被其他人修改
				j.setSuccess(false);
				j.setMsg(DictUtils.getDictLabel("记录已经被修改", "msg_bas", "记录已经被修改"));
				return j;
			} else {
				//保存历史版本
				if(isAutoVersion) chartHumitureLineService.saveV(t);   
				//将编辑表单中的非NULL值覆盖数据库记录中的值 
				MyBeanUtils.copyBeanNotNull2Bean(chartHumitureLine, t);
				chartHumitureLineService.save(t);
			}
		}else{
			//新建保存
			chartHumitureLine.setOwnerCode(officeService.get(UserUtils.getUser().getCompany().getId()).getCode());
			chartHumitureLineService.save(chartHumitureLine);
		}
		j.setSuccess(true);
		j.setMsg(DictUtils.getDictLabel("保存?信息成功", "msg_bas", "保存温湿度曲线图信息成功","温湿度曲线图"));
		//保存成功后处理逻辑
		this.afterSave("温湿度曲线图", chartHumitureLine);
		return j;
	}
	
	/**
	 * 删除温湿度曲线图
	 */
	@ResponseBody
	@RequiresPermissions("chart:line:chartHumitureLine:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(ChartHumitureLine chartHumitureLine, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		//保存历史版本
		if(isAutoVersion) {
			chartHumitureLine.setDelFlag(Global.YES);
			chartHumitureLineService.saveV(chartHumitureLine); 
		}
		chartHumitureLineService.delete(chartHumitureLine);
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除温湿度曲线图信息成功","温湿度曲线图"));
		return j;
	}
	
	/**
	 * 批量删除温湿度曲线图
	 */
	@ResponseBody
	@RequiresPermissions("chart:line:chartHumitureLine:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			//保存历史版本
			if(isAutoVersion) {
				ChartHumitureLine obj = chartHumitureLineService.get(id);
				obj.setDelFlag(Global.YES);
				chartHumitureLineService.saveV(obj); 
			}
			chartHumitureLineService.delete(chartHumitureLineService.get(id));
		}
		j.setMsg(DictUtils.getDictLabel("删除?信息成功", "msg_bas", "删除温湿度曲线图信息成功","温湿度曲线图"));
		return j;
	}
	
	/**
	 * 导出excel文件
	 */
	@ResponseBody
	@RequiresPermissions("chart:line:chartHumitureLine:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(ChartHumitureLine chartHumitureLine, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "温湿度曲线图"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ChartHumitureLine> page = chartHumitureLineService.findPage(new Page<ChartHumitureLine>(request, response, -1), chartHumitureLine);
    		new ExportExcel("温湿度曲线图", ChartHumitureLine.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg(DictUtils.getDictLabel("导出成功", "msg_bas", "导出成功！"));
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(DictUtils.getDictLabel("导出?信息记录失败", "msg_bas", "导出温湿度曲线图信息记录失败！", "温湿度曲线图") + "失败信息：" + e.getMessage());
		}
		return j;
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("chart:line:chartHumitureLine:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ChartHumitureLine> list = ei.getDataList(ChartHumitureLine.class);
			for (ChartHumitureLine chartHumitureLine : list){
				try{
					chartHumitureLineService.save(chartHumitureLine);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条温湿度曲线图记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条温湿度曲线图记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入温湿度曲线图失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/line/chartHumitureLine/?repage";
    }
	
	/**
	 * 下载导入温湿度曲线图数据模板
	 */
	@RequiresPermissions("chart:line:chartHumitureLine:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "温湿度曲线图数据导入模板.xlsx";
    		List<ChartHumitureLine> list = Lists.newArrayList(); 
    		new ExportExcel("温湿度曲线图数据", ChartHumitureLine.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/chart/line/chartHumitureLine/?repage";
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
	private void afterSave(String title, ChartHumitureLine chartHumitureLine) {
		Map<String, String> roleMap = new HashMap<String, String>();
		String subSystemCode = "wz";
		//roleMap.put("role_enname", "/chart/line/chartHumitureLine");
		//subSystemCode = "子系统或者模块标识，例如wz_sec，表示安保";
		if(roleMap.size() > 0) basMessageService.saveMessage(subSystemCode, title, chartHumitureLine.getOwnerCode(), roleMap);
	}
}