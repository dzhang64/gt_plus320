package com.gt_plus.modules.gen.service;

import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.BaseService;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.gen.dao.GenSchemeDao;
import com.gt_plus.modules.gen.dao.GenTableColumnDao;
import com.gt_plus.modules.gen.dao.GenTableDao;
import com.gt_plus.modules.gen.entity.GenConfig;
import com.gt_plus.modules.gen.entity.GenScheme;
import com.gt_plus.modules.gen.entity.GenTable;
import com.gt_plus.modules.gen.entity.GenTableColumn;
import com.gt_plus.modules.gen.entity.GenTemplate;
import com.gt_plus.modules.gen.util.GenUtils;
import com.gt_plus.modules.sys.entity.Menu;
import com.gt_plus.modules.sys.service.SystemService;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GenSchemeService extends BaseService {

	@Autowired
	private GenSchemeDao genSchemeDao;

	@Autowired
	private GenTableDao genTableDao;

	@Autowired
	private GenTableColumnDao genTableColumnDao;

	@Autowired
	private SystemService systemService;

	public GenScheme get(String id) {
		return (GenScheme) this.genSchemeDao.get(id);
	}

	public Page<GenScheme> find(Page<GenScheme> page, GenScheme genScheme) {
		GenUtils.getTemplatePath();
		genScheme.setPage(page);
		page.setList(genSchemeDao.findList(genScheme));
		return page;
	}

	@Transactional(readOnly = false)
	public String save(GenScheme genScheme) {
		if (StringUtils.isBlank(genScheme.getId())) {
			genScheme.preInsert();
			this.genSchemeDao.insert(genScheme);
		} else {
			genScheme.preUpdate();
			this.genSchemeDao.update(genScheme);
		}
		return generateCode(genScheme);
	}

	@Transactional(readOnly = false)
	public void createMenu(GenScheme genScheme, Menu topMenu) {
		// Menu addMenu;
		String permissionPrefix = StringUtils.lowerCase(genScheme
				.getModuleName())
				+ (StringUtils.isNotBlank(genScheme.getSubModuleName()) ? ":"
						+ StringUtils.lowerCase(genScheme.getSubModuleName())
						: "")
				+ ":"
				+ StringUtils.uncapitalize(genScheme.getGenTable()
						.getClassName());
		String url = "/"
				+ StringUtils.lowerCase(genScheme.getModuleName())
				+ (StringUtils.isNotBlank(genScheme.getSubModuleName()) ? "/"
						+ StringUtils.lowerCase(genScheme.getSubModuleName())
						: "")
				+ "/"
				+ StringUtils.uncapitalize(genScheme.getGenTable()
						.getClassName());

		topMenu.setName(genScheme.getFunctionName());
		topMenu.setHref(url);
		topMenu.setIsShow("1");
		topMenu.setPermission(permissionPrefix + ":list");
		this.systemService.saveMenu(topMenu);

		/*
		 * ( addMenu = new Menu()) .setName("增加"); addMenu.setIsShow("0");
		 * addMenu.setSort(Integer.valueOf(30));
		 * addMenu.setPermission(permissionPrefix + ":add");
		 * addMenu.setParent(topMenu); this.systemService.saveMenu(addMenu);
		 */
		Menu addMenu = new Menu();
		addMenu.setName("添加");
		addMenu.setIsShow("0");
		addMenu.setSort(Integer.valueOf(30));
		addMenu.setPermission(permissionPrefix + ":add");
		addMenu.setParent(topMenu);
		this.systemService.saveMenu(addMenu);

		/*
		 * Menu delMenu; ( delMenu = new Menu()) .setName("删除");
		 * delMenu.setIsShow("0"); delMenu.setSort(Integer.valueOf(60));
		 * delMenu.setPermission(permissionPrefix + ":del");
		 * delMenu.setParent(topMenu); this.systemService.saveMenu(delMenu);
		 */
		Menu delMenu = new Menu();
		delMenu.setName("删除");
		delMenu.setIsShow("0");
		delMenu.setSort(Integer.valueOf(60));
		delMenu.setPermission(permissionPrefix + ":del");
		delMenu.setParent(topMenu);
		this.systemService.saveMenu(delMenu);

		Menu editMenu = new Menu();
		editMenu.setName("编辑");
		editMenu.setIsShow("0");
		editMenu.setSort(Integer.valueOf(90));
		editMenu.setPermission(permissionPrefix + ":edit");
		editMenu.setParent(topMenu);
		this.systemService.saveMenu(editMenu);

		Menu viewMenu = new Menu();
		viewMenu.setName("查看");
		viewMenu.setIsShow("0");
		viewMenu.setSort(Integer.valueOf(120));
		viewMenu.setPermission(permissionPrefix + ":view");
		viewMenu.setParent(topMenu);
		this.systemService.saveMenu(viewMenu);

		Menu importMenu = new Menu();
		importMenu.setName("导入");
		importMenu.setIsShow("0");
		importMenu.setSort(Integer.valueOf(150));
		importMenu.setPermission(permissionPrefix + ":import");
		importMenu.setParent(topMenu);
		this.systemService.saveMenu(importMenu);

		Menu exportMenu = new Menu();
		exportMenu.setName("导出");
		exportMenu.setIsShow("0");
		exportMenu.setSort(Integer.valueOf(180));
		exportMenu.setPermission(permissionPrefix + ":export");
		exportMenu.setParent(topMenu);
		this.systemService.saveMenu(exportMenu);
	}

	@Transactional(readOnly = false)
	public void delete(GenScheme genScheme) {
		this.genSchemeDao.delete(genScheme);
	}

	private String generateCode(GenScheme genScheme) {

		StringBuilder result = new StringBuilder();

		// 查询主表及字段列
		GenTable genTable = genTableDao.get(genScheme.getGenTable().getId());
		genTable.setColumnList(genTableColumnDao.findList(new GenTableColumn(
				new GenTable(genTable.getId()))));

		// 获取所有代码模板
		GenConfig config = GenUtils.getConfig();

		// 获取模板列表
		List<GenTemplate> templateList = GenUtils.getTemplateList(config,
				genScheme.getCategory(), false);
		List<GenTemplate> childTableTemplateList = GenUtils.getTemplateList(
				config, genScheme.getCategory(), true);

		// 如果有子表模板，则需要获取子表列表
		if (childTableTemplateList.size() > 0) {
			GenTable parentTable = new GenTable();
			parentTable.setParentTable(genTable.getName());
			genTable.setChildList(genTableDao.findList(parentTable));
		}

		// 生成子表模板代码
		for (GenTable childTable : genTable.getChildList()) {
			childTable.setParent(genTable);
			childTable.setColumnList(genTableColumnDao
					.findList(new GenTableColumn(new GenTable(childTable
							.getId()))));
			genScheme.setGenTable(childTable);
			Map<String, Object> childTableModel = GenUtils
					.getDataModel(genScheme);
			for (GenTemplate tpl : childTableTemplateList) {
				result.append(GenUtils.generateToFile(tpl, childTableModel,
						genScheme.getReplaceFile()));
			}
		}

		// 生成主表模板代码
		genScheme.setGenTable(genTable);
		Map<String, Object> model = GenUtils.getDataModel(genScheme);
		for (GenTemplate tpl : templateList) {
			result.append(GenUtils.generateToFile(tpl, model,
					genScheme.getReplaceFile()));
		}
		return result.toString();
	}

	public GenScheme findUniqueByProperty(String propertyName, String value) {
		return (GenScheme) this.genSchemeDao.findUniqueByProperty(propertyName,
				value);
	}
}