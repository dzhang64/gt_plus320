package com.gt_plus.modules.gen.web;

import com.gt_plus.common.config.Global;
import com.gt_plus.common.ds.DataSourceContextHolder;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.common.web.BaseController;
import com.gt_plus.modules.act.service.ActProcessService;
import com.gt_plus.modules.gen.dao.GenTemplateDao;
import com.gt_plus.modules.gen.entity.GenConfig;
import com.gt_plus.modules.gen.entity.GenScheme;
import com.gt_plus.modules.gen.entity.GenTable;
import com.gt_plus.modules.gen.entity.GenTableColumn;
import com.gt_plus.modules.gen.entity.GenTemplate;
import com.gt_plus.modules.gen.service.GenSchemeService;
import com.gt_plus.modules.gen.service.GenTableService;
import com.gt_plus.modules.gen.service.GenTemplateService;
import com.gt_plus.modules.gen.util.GenUtils;
import com.gt_plus.modules.sys.entity.User;
import com.gt_plus.modules.sys.utils.UserUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({ "${adminPath}/gen/genTable" })
public class GenTableController extends BaseController {

	@Autowired
	public GenTemplateService genTemplateService;

	@Autowired
	public GenTableService genTableService;

	@Autowired
	public GenSchemeService genSchemeService;

	@Autowired
	public GenTemplateDao genTemplateDao;
	
	@Autowired
	public ActProcessService actProcessService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAutoGrowCollectionLimit(1024);
	}

	/*
	@ModelAttribute
	public GenTable get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return genTableService.get(id);
		} else {
			return new GenTable();
		}
	}
	*/
	
	private GenTable getGenTable(GenTable genTable)
	{
		if (StringUtils.isNotBlank(genTable.getId())) {
			return this.genTableService.get(genTable.getId());
	    }
	    return genTable;
	}
	
	@RequiresPermissions({ "gen:genTable:list" })
	@RequestMapping({ "list", "" })
	public String list(GenTable genTable, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		if (request.getSession().getAttribute("template") == null) {
			GenTemplate t = this.genTemplateService.get("0");
			if (t == null) {
				t = new GenTemplate();
				t.setId("0");
				t.setIsNewRecord(true);
				t.setName("0");
				this.genTemplateDao.insert(t);
			}
			request.getSession().setAttribute("template", t);
		}
		genTable = getGenTable(genTable);
		User user = UserUtils.getUser();
		if (!user.isAdmin()) {
			genTable.setCreateBy(user);
		}
		Page<GenTable> page = genTableService.find(new Page<GenTable>(request, response), genTable);
		model.addAttribute("page", page);
		return "modules/gen/genTableList";
	}

	// 根据字段宽度和控件类型判断是否应该独占行
	private void buildColumnIsOneLine(GenTable genTable) {
		int j = 0;
		int currentIndex = 0;
		List<GenTableColumn> cList = genTable.getColumnList();
		for (int i = 0; i < cList.size(); i++) {
			// <#if c.isForm?? && c.isForm == "1" && (c.isNotBaseField ||
			// c.simpleJavaField == 'remarks')>
			GenTableColumn c = cList.get(i);
			//c.setIsOneLine("");
			if (StringUtils.isNotEmpty(c.getIsForm())
					&& c.getIsForm().equals("1")
					&& (c.getIsNotBaseField() || c.getSimpleJavaField().equals("remarks"))) {
				// showType: textarea umeditor fileselect
				if (c.getShowType().equals("textarea")
						|| c.getShowType().equals("umeditor")
						|| c.getShowType().equals("fileselect")
						|| c.getShowType().equals("fileselectclassic")
						|| "1".equals(c.getIsOneLine())) {
					c.setIsOneLine("1");
					// 判断上一个字段是否是奇数列，并且不是独占行，设置独占行
					if (j > 0
							&& j % 2 != 0
							&& cList.get(currentIndex).getIsOneLine()
									.equals("")) {
						cList.get(currentIndex).setIsOneLine("1");
						j++;
					}
					j++; // 注意：j++放到if语句之后，即先判断上一个字段是否需要独占行
				}
				j++; // 表单上的字段数量
				currentIndex = i;
			}
		}
		// 判断最后一个字段，是否为奇数，设置独占行
		if (j > 0 && j % 2 == 1
				&& cList.get(currentIndex).getIsOneLine().equals("")) {
			cList.get(currentIndex).setIsOneLine("1");
		}
	}

	@RequiresPermissions(value = { "gen:genTable:view", "gen:genTable:add",
			"gen:genTable:edit" }, logical = Logical.OR)
	@RequestMapping({ "form" })
	public String form(GenTable genTable, HttpServletResponse response,
			Model model) throws IOException {
		genTable = getGenTable(genTable);
		model.addAttribute("genTable", genTable);
		model.addAttribute("config", GenUtils.getConfig());
		model.addAttribute("tableList", this.genTableService.findAll());
		model.addAttribute("processDefinitionList", actProcessService.processDefinitionList(null));
		return "modules/gen/genTableForm";
	}

	@RequiresPermissions(value = { "gen:genTable:add", "gen:genTable:edit" }, logical = Logical.OR)
	@RequestMapping({ "save" })
	public String save(GenTable genTable, Model model,
			RedirectAttributes redirectAttributes, HttpServletResponse response)
			throws IOException {
		if ((StringUtils.isBlank(genTable.getId()))
				&& (!this.genTableService.checkTableName(genTable.getName()))) {
			addMessage(redirectAttributes,
					new String[] { "添加失败！" + genTable.getName() + " 记录已存在！" });
			return "redirect:" + this.adminPath + "/gen/genTable/?repage";
		}

		if ((StringUtils.isBlank(genTable.getId()))
				&& (!this.genTableService.checkTableNameFromDB(genTable
						.getName()))) {
			addMessage(redirectAttributes,
					new String[] { "添加失败！" + genTable.getName()
							+ "表已经在数据库中存在,请从数据库导入表单！" });
			return "redirect:" + this.adminPath + "/gen/genTable/?repage";
		}
		// 处理独占行字段
		this.buildColumnIsOneLine(genTable);

		this.genTableService.save(genTable);
		addMessage(redirectAttributes,
				new String[] { "保存业务表'" + genTable.getName() + "'成功" });
		return "redirect:" + this.adminPath + "/gen/genTable/?repage";
	}

	@RequiresPermissions({ "gen:genTable:importDb" })
	@RequestMapping({ "importTableFromDB" })
	public String importTableFromDB(GenTable genTable, Model model,
			RedirectAttributes redirectAttributes) {
		genTable = getGenTable(genTable);
		if (!StringUtils.isBlank(genTable.getName())) {
			if (!this.genTableService.checkTableName(genTable.getName())) {
				addMessage(redirectAttributes, new String[] { "下一步失败！"
						+ genTable.getName() + " 表已经添加！" });
				return "redirect:" + this.adminPath + "/gen/genTable/?repage";
			}
			(genTable = this.genTableService.getTableFormDb(genTable))
					.setTableType("0");
			this.genTableService.saveFromDB(genTable);
			addMessage(redirectAttributes,
					new String[] { "数据库导入表单'" + genTable.getName() + "'成功" });
			return "redirect:" + this.adminPath + "/gen/genTable/?repage";
		}

		List<GenTable> tableList = this.genTableService
				.findTableListFormDb(new GenTable());
		model.addAttribute("tableList", tableList);
		model.addAttribute("config", GenUtils.getConfig());
		return "modules/gen/importTableFromDB";
	}

	@RequiresPermissions({ "gen:genTable:del" })
	@RequestMapping({ "delete" })
	public String delete(GenTable genTable, RedirectAttributes redirectAttributes) {
		genTable = getGenTable(genTable);
		this.genTableService.delete(genTable);
		this.genSchemeService.delete(this.genSchemeService
				.findUniqueByProperty("gen_table_id", genTable.getId()));
		addMessage(redirectAttributes, new String[] { "移除业务表记录成功" });
		return "redirect:" + this.adminPath + "/gen/genTable/?repage";
	}

	@RequiresPermissions({ "gen:genTable:del" })
	@RequestMapping({ "deleteDb" })
	public String deleteDb(GenTable genTable, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode().booleanValue()) {
			addMessage(redirectAttributes, new String[] { "演示模式，不允许操作！" });
			return "redirect:" + this.adminPath + "/gen/genTable/?repage";
		}
		genTable = getGenTable(genTable);
		this.genTableService.delete(genTable);
		this.genSchemeService.delete(this.genSchemeService
				.findUniqueByProperty("gen_table_id", genTable.getId()));
		StringBuffer sql = new StringBuffer();
		String dbType = Global.getConfig("jdbc.type");
		if ("mysql".equals(dbType)) {
			sql.append("drop table if exists " + genTable.getName() + " ;");
		} else if ("oracle".equals(dbType) || "dm".equals(dbType))
			try {
				sql.append("DROP TABLE " + genTable.getName());
			} catch (Exception localException) {
			}
		else if (("mssql".equals(dbType)) || ("sqlserver".equals(dbType))) {
			sql.append("if exists (select * from sysobjects where id = object_id(N'["
					+ genTable.getName()
					+ "]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)  drop table ["
					+ genTable.getName() + "]");
		}

		this.genTableService.buildTable(sql.toString());
		addMessage(redirectAttributes, new String[] { "删除业务表记录和数据库表成功" });
		return "redirect:" + this.adminPath + "/gen/genTable/?repage";
	}

	@RequiresPermissions({ "gen:genTable:del" })
	@RequestMapping({ "deleteAll" })
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		if (ids.indexOf(",") != -1) {
			String[] id = ids.split(",");
			for (int i = 0; i < id.length; i++) {
				this.genTableService.delete(this.genTableService.get(id[i]));
			}
		}
		addMessage(redirectAttributes, new String[] { "删除业务表成功" });
		return "redirect:" + this.adminPath + "/gen/genTable/?repage";
	}

	@RequiresPermissions({ "gen:genTable:synchDb" })
	@RequestMapping({ "synchDb" })
	public String synchDb(GenTable genTable,
			RedirectAttributes redirectAttributes, Boolean isCreateVersionTable) {
		String dbType = Global.getConfig("jdbc.type");
		genTable = getGenTable(genTable);
		String tableName = genTable.getName();
		if (isCreateVersionTable == null) isCreateVersionTable = false;
		if (isCreateVersionTable) tableName = tableName + "_V";
		List<GenTableColumn> getTableColumnList = genTable.getColumnList();

		StringBuffer sql;
		//Iterator localIterator;
		if ("mysql".equals(dbType)) {
			sql = new StringBuffer();
			sql.append("drop table if exists " + tableName + " ;");
			this.genTableService.buildTable(sql.toString());
			sql = new StringBuffer();
			sql.append("create table " + tableName + " (");

			String pk = "";
			for (Iterator<GenTableColumn> localIterator = getTableColumnList.iterator(); localIterator
					.hasNext();) {
				GenTableColumn column;
				if ((column = (GenTableColumn) localIterator.next()).getIsPk()
						.equals("1")) {
					sql.append("  " + column.getName() + " "
							+ column.getJdbcType() + " comment '"
							+ column.getComments() + "',");
					pk = pk + column.getName() + ",";
					// 版本表
					if (isCreateVersionTable) {
						sql.append(" V_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,");
						pk = pk + " V_DATE,";
					}
				} else {
					sql.append("  " + column.getName() + " "
							+ column.getJdbcType() + " comment '"
							+ column.getComments() + "',");
				}
			}

			sql.append("primary key (" + pk.substring(0, pk.length() - 1)
					+ ") ");
			sql.append(") comment '" + genTable.getComments() + "'");
			this.genTableService.buildTable(sql.toString());
		} else {
			if ("oracle".equals(dbType)) {
				sql = new StringBuffer();
				try {
					sql.append("DROP TABLE " + tableName);
					this.genTableService.buildTable(sql.toString());
				} catch (Exception localException) {
				}

				sql = new StringBuffer();
				sql.append("create table " + tableName + " (");

				String pk = "";
				for (Iterator<GenTableColumn> localIterator = getTableColumnList.iterator(); localIterator
						.hasNext();) {
					GenTableColumn column;
					String jdbctype;
					if ((jdbctype = (column = (GenTableColumn) localIterator
							.next()).getJdbcType()).equalsIgnoreCase("integer"))
						jdbctype = "number(10,0)";
					else if (jdbctype.equalsIgnoreCase("datetime"))
						jdbctype = "date";
					else if (jdbctype.contains("nvarchar("))
						jdbctype = jdbctype.replace("nvarchar", "nvarchar2");
					else if (jdbctype.contains("varchar("))
						jdbctype = jdbctype.replace("varchar", "varchar2");
					else if (jdbctype.equalsIgnoreCase("double"))
						jdbctype = "float(24)";
					else if (jdbctype.equalsIgnoreCase("longblob"))
						jdbctype = "blob raw";
					else if (jdbctype.equalsIgnoreCase("longtext")) {
						jdbctype = "clob raw";
					}
					if (column.getIsPk().equals("1")) {
						sql.append("  " + column.getName() + " " + jdbctype
								+ ",");
						pk = pk + column.getName();
						// 版本表
						if (isCreateVersionTable) {
							sql.append(" V_DATE DATE NOT NULL DEFAULT SYSDATE,");
							pk = pk + ",V_DATE";
						}
					} else {
						sql.append("  " + column.getName() + " " + jdbctype
								+ ",");
					}
				}

				sql = new StringBuffer(sql.substring(0, sql.length() - 1) + ")");
				this.genTableService.buildTable(sql.toString());
				this.genTableService.buildTable("comment on table " + tableName
						+ " is  '" + genTable.getComments() + "'");
				for (GenTableColumn column : getTableColumnList) {
					this.genTableService.buildTable("comment on column "
							+ tableName + "." + column.getName() + " is  '"
							+ column.getComments() + "'");
				}

				this.genTableService.buildTable("alter table " + tableName
						+ " add constraint PK_" + tableName + "_"
						+ pk.replaceAll(",", "_") + " primary key (" + pk
						+ ") ");
			} else if ("dm".equals(dbType)) {
				sql = new StringBuffer();
				DataSourceContextHolder.setDbType(genTable.getDatasource());
				try {
					sql.append("DROP TABLE " + tableName);

					this.genTableService.buildTable(sql.toString());
				} catch (Exception localException) {
				}
				sql = new StringBuffer();
				sql.append("create table " + tableName + " (");

				String pk = "";
				for (Iterator<GenTableColumn> localIterator = getTableColumnList.iterator(); localIterator
						.hasNext();) {
					GenTableColumn column;
					String jdbctype;
					if ((jdbctype = (column = (GenTableColumn) localIterator
							.next()).getJdbcType()).equalsIgnoreCase("integer"))
						jdbctype = "number(10,0)";
					else if (jdbctype.equalsIgnoreCase("datetime"))
						jdbctype = "TIMESTAMP(0)";
					else if (jdbctype.contains("nvarchar("))
						jdbctype = jdbctype.replace("nvarchar", "varchar2");
					else if (jdbctype.contains("varchar("))
						jdbctype = jdbctype.replace("varchar", "varchar2");
					else if (jdbctype.equalsIgnoreCase("double"))
						jdbctype = "number(22,4)";
					else if (jdbctype.equalsIgnoreCase("longblob"))
						jdbctype = "blob";
					else if (jdbctype.equalsIgnoreCase("longtext")) {
						jdbctype = "text";
					}
					if (column.getIsPk().equals("1")) {
						sql.append("  " + column.getName() + " " + jdbctype
								+ ",");
						pk = pk + column.getName();
						// 版本表
						if (isCreateVersionTable) {
							sql.append(" V_DATE TIMESTAMP(0) NOT NULL DEFAULT SYSDATE(),");
							pk = pk + ",V_DATE";
						}
					} else {
						sql.append("  " + column.getName() + " " + jdbctype
								+ ",");
					}
				}

				sql = new StringBuffer(sql.substring(0, sql.length() - 1) + ")");
				this.genTableService.buildTable(sql.toString());
				this.genTableService.buildTable("comment on table " + tableName
						+ " is  '" + genTable.getComments() + "'");
				for (GenTableColumn column : getTableColumnList) {
					this.genTableService.buildTable("comment on column "
							+ tableName + "." + column.getName() + " is  '"
							+ column.getComments() + "'");
				}

				this.genTableService.buildTable("alter table " + tableName
						+ " add constraint PK_" + tableName + "_"
						+ pk.replaceAll(",", "_") + " primary key (" + pk
						+ ") ");
				DataSourceContextHolder.clearDbType();
			} else if (("mssql".equals(dbType)) || ("sqlserver".equals(dbType))) {
				// StringBuffer sql;
				(sql = new StringBuffer())
						.append("if exists (select * from sysobjects where id = object_id(N'["
								+ tableName
								+ "]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)  drop table ["
								+ tableName + "]");
				this.genTableService.buildTable(sql.toString());
				(sql = new StringBuffer()).append("create table " + tableName
						+ " (");
				String pk = "";
				for (Iterator<GenTableColumn> localIterator = getTableColumnList.iterator(); localIterator
						.hasNext();) {
					GenTableColumn column;
					if ((column = (GenTableColumn) localIterator.next())
							.getIsPk().equals("1")) {
						sql.append("  " + column.getName() + " "
								+ column.getJdbcType() + ",");
						pk = pk + column.getName() + ",";
						// 版本表
						if (isCreateVersionTable) {
							sql.append(" V_DATE datetime NOT NULL DEFAULT getdate(),");
							pk = pk + "V_DATE,";
						}
					} else {
						sql.append("  " + column.getName() + " "
								+ column.getJdbcType() + ",");
					}
				}
				sql.append("primary key (" + pk.substring(0, pk.length() - 1)
						+ ") ");
				sql.append(")");
				this.genTableService.buildTable(sql.toString());
			}
		}

		this.genTableService.syncSave(genTable);

		addMessage(redirectAttributes, new String[] { "强制同步数据库表成功" });
		return "redirect:" + this.adminPath + "/gen/genTable/?repage";
	}

	@RequiresPermissions({ "gen:genTable:synchDb" })
	@RequestMapping({ "synchDbV" })
	public String synchDbV(GenTable genTable,
			RedirectAttributes redirectAttributes) {
		Boolean isCreateVersionTable = true;
		return synchDb(genTable, redirectAttributes, isCreateVersionTable);
	}

	@RequiresPermissions({ "gen:genTable:genCode" })
	@RequestMapping({ "genCodeForm" })
	public String genCodeForm(GenScheme genScheme, Model model) {
		if (StringUtils.isBlank(genScheme.getPackageName()))
			genScheme.setPackageName("com.gt_plus.modules");
		GenScheme oldGenScheme;
		if ((oldGenScheme = this.genSchemeService.findUniqueByProperty(
				"gen_table_id", genScheme.getGenTable().getId())) != null) {
			genScheme = oldGenScheme;
		}
		model.addAttribute("genScheme", genScheme);
		model.addAttribute("config", GenUtils.getConfig());
		model.addAttribute("tableList", this.genTableService.findAll());
		return "modules/gen/genCodeForm";
	}

	@RequestMapping({ "genCode" })
	public String genCode(GenScheme genScheme, RedirectAttributes redirectAttributes) {
		String result = this.genSchemeService.save(genScheme);
		addMessage(redirectAttributes, new String[] { genScheme.getGenTable()
				.getName() + "代码生成成功<br/>" + result });
		return "redirect:" + this.adminPath + "/gen/genTable/?repage";
	}
}