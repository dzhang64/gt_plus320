package com.gt_plus.modules.gen.entity;

import com.google.common.collect.Lists;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public class GenTable extends DataEntity<GenTable>
{
  private static final long serialVersionUID = 1L;
  private String name;
  private String comments;
  private String tableType;
  private String className;
  private String parentTable;
  private String parentTableFk;
  private String isSync;
  private List<GenTableColumn> columnList = (List)Lists.newArrayList();
  private String nameLike;
  private List<String> pkList;
  private GenTable parent;
  private List<GenTable> childList = (List)Lists.newArrayList();
  
  private String extJsp;
  private String extJs;
  private String extJava;
  
  private HashMap<String,String> extJspMap;
  private HashMap<String,String> extJsMap;
  private HashMap<String,String> extJavaMap;
  
  private String isBuildAdd;     //是否生成添加按钮
  private String isBuildEdit;    //是否生成编辑按钮
  private String isBuildDel;     //是否生成删除按钮
  private String isBuildImport;  //是否生成导入按钮
  private String isBuildOperate; //是否生成按钮操作列
  
  private String datasource;     //数据源
  private String isVersion;      //是否版本化数据
  
  private String isProcessDefinition;  //是否流程表单
  private String processDefinitionKey; //流程标识
  
  public GenTable()
  {
  }

  public GenTable(String id)
  {
    super(id);
  }

  @Length(min=1, max=200)
  public String getName() {
    return StringUtils.lowerCase(this.name);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getComments() {
    return this.comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getClassName() {
    return this.className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getParentTable() {
    return StringUtils.lowerCase(this.parentTable);
  }

  public void setParentTable(String parentTable) {
    this.parentTable = parentTable;
  }

  public String getParentTableFk() {
    return StringUtils.lowerCase(this.parentTableFk);
  }

  public void setParentTableFk(String parentTableFk) {
    this.parentTableFk = parentTableFk;
  }

  public List<String> getPkList() {
    return this.pkList;
  }

  public void setPkList(List<String> pkList) {
    this.pkList = pkList;
  }

  public String getNameLike() {
    return this.nameLike;
  }

  public void setNameLike(String nameLike) {
    this.nameLike = nameLike;
  }

  public GenTable getParent() {
    return this.parent;
  }

  public void setParent(GenTable parent) {
    this.parent = parent;
  }

  public List<GenTableColumn> getColumnList() {
    return this.columnList;
  }

  public void setColumnList(List<GenTableColumn> columnList) {
    this.columnList = columnList;
  }

  public List<GenTable> getChildList() {
    return this.childList;
  }

  public void setChildList(List<GenTable> childList) {
    this.childList = childList;
  }

  public String getNameAndComments()
  {
    return getName() + (this.comments == null ? "" : new StringBuilder("  :  ").append(this.comments).toString());
  }

  public List<String> getImportList()
  {
    List importList = (List)Lists.newArrayList();
    for (GenTableColumn column : getColumnList()) {
      if ((column.getIsNotBaseField().booleanValue()) || (("1".equals(column.getIsQuery())) && ("between".equals(column.getQueryType())) && (
        ("createDate".equals(column.getSimpleJavaField())) || ("updateDate".equals(column.getSimpleJavaField())))))
      {
        if ((StringUtils.indexOf(column.getJavaType(), ".") != -1) && (!importList.contains(column.getJavaType()))) {
          importList.add(column.getJavaType());
        }
      }
      if (column.getIsNotBaseField().booleanValue())
      {
        for (String ann : column.getAnnotationList()) {
          if (!importList.contains(StringUtils.substringBeforeLast(ann, "("))) {
            importList.add(StringUtils.substringBefore(ann, "("));
          }
        }
      }
    }

    if ((getChildList() != null) && (getChildList().size() > 0)) {
      if (!importList.contains("java.util.List")) {
        importList.add("java.util.List");
      }
      if (!importList.contains("com.google.common.collect.Lists")) {
        importList.add("com.google.common.collect.Lists");
      }
    }
    return importList;
  }

  public List<String> getImportGridJavaList()
  {
    List importList = (List)Lists.newArrayList();
    for (GenTableColumn column : getColumnList()) {
      if ((column.getTableName() != null) && (!column.getTableName().equals("")))
      {
        if ((StringUtils.indexOf(column.getJavaType(), ".") != -1) && (!importList.contains(column.getJavaType()))) {
          importList.add(column.getJavaType());
        }
      }
    }
    return importList;
  }

  public List<String> getImportGridJavaDaoList() {
    boolean isNeedList = false;
    List importList = (List)Lists.newArrayList();
    for (GenTableColumn column : getColumnList()) {
      if ((column.getTableName() != null) && (!column.getTableName().equals("")))
      {
        if ((StringUtils.indexOf(column.getJavaType(), ".") != -1) && (!importList.contains(column.getJavaType()))) {
          importList.add(column.getJavaType());
          isNeedList = true;
        }
      }
    }
    if ((isNeedList) && 
      (!importList.contains("java.util.List"))) {
      importList.add("java.util.List");
    }

    return importList;
  }

  public Boolean getParentExists()
  {
    if ((this.parent != null) && (StringUtils.isNotBlank(this.parentTable)) && (StringUtils.isNotBlank(this.parentTableFk))) return Boolean.valueOf(true); return Boolean.valueOf(false);
  }

  public Boolean getCreateDateExists()
  {
    for (GenTableColumn c : this.columnList) {
      if ("create_date".equalsIgnoreCase(c.getName())) {
        return Boolean.valueOf(true);
      }
    }
    return Boolean.valueOf(false);
  }

  public Boolean getUpdateDateExists()
  {
    for (GenTableColumn c : this.columnList) {
      if ("update_date".equalsIgnoreCase(c.getName())) {
        return Boolean.valueOf(true);
      }
    }
    return Boolean.valueOf(false);
  }

  public Boolean getDelFlagExists()
  {
    for (GenTableColumn c : this.columnList) {
      if ("del_flag".equalsIgnoreCase(c.getName())) {
        return Boolean.valueOf(true);
      }
    }
    return Boolean.valueOf(false);
  }
  
  public Boolean getOwnerCodeExists()
  {
    for (GenTableColumn c : this.columnList) {
      if ("owner_code".equalsIgnoreCase(c.getName())) {
        return Boolean.valueOf(true);
      }
    }
    return Boolean.valueOf(false);
  }

  public void setIsSync(String isSync) {
    this.isSync = isSync;
  }

  public String getIsSync() {
    return this.isSync;
  }

  public void setTableType(String tableType) {
    this.tableType = tableType;
  }

  public String getTableType() {
    return this.tableType;
  }

  public String getExtJsp() {
	return extJsp;
  }

  public void setExtJsp(String extJsp) {
	this.extJsp = extJsp;
  }

  public String getExtJs() {
	return extJs;
  }

  public void setExtJs(String extJs) {
	this.extJs = extJs;
  }

  public String getExtJava() {
	return extJava;
  }

  public void setExtJava(String extJava) {
	this.extJava = extJava;
  }

  public HashMap<String,String> getExtJspMap() {
	extJspMap = new HashMap<String,String>();
	this.buildExtMap(extJspMap, this.extJsp);
	return extJspMap;
  }

  public HashMap<String,String> getExtJsMap() {
	extJsMap = new HashMap<String,String>();
	this.buildExtMap(extJsMap, this.extJs);
	return extJsMap;
  }

  public HashMap<String,String> getExtJavaMap() {
	extJavaMap = new HashMap<String,String>();
	this.buildExtMap(extJavaMap, this.extJava);
	return extJavaMap;
  }
  
  public String getIsBuildAdd() {
	return isBuildAdd;
  }

  public void setIsBuildAdd(String isBuildAdd) {
	this.isBuildAdd = isBuildAdd;
  }

  public String getIsBuildEdit() {
	return isBuildEdit;
  }

  public void setIsBuildEdit(String isBuildEdit) {
	this.isBuildEdit = isBuildEdit;
  }

  public String getIsBuildDel() {
	return isBuildDel;
  }

  public void setIsBuildDel(String isBuildDel) {
	this.isBuildDel = isBuildDel;
  }

  public String getIsBuildImport() {
	return isBuildImport;
  }

  public void setIsBuildImport(String isBuildImport) {
	this.isBuildImport = isBuildImport;
  }

  public String getIsBuildOperate() {
	return isBuildOperate;
  }

  public void setIsBuildOperate(String isBuildOperate) {
	this.isBuildOperate = isBuildOperate;
  }

  
  public String getDatasource() {
	return datasource;
  }

  public void setDatasource(String datasource) {
	this.datasource = datasource;
  }
  
  public String getIsVersion() {
	return isVersion;
  }

  public void setIsVersion(String isVersion) {
	this.isVersion = isVersion;
  }

  public String getIsProcessDefinition() {
	return isProcessDefinition;
  }

  public void setIsProcessDefinition(String isProcessDefinition) {
	this.isProcessDefinition = isProcessDefinition;
  }

  public String getProcessDefinitionKey() {
	return processDefinitionKey;
  }

  public void setProcessDefinitionKey(String processDefinitionKey) {
	this.processDefinitionKey = processDefinitionKey;
  }

/*
   * 创建HashMap，约束：<!-- ext someNameHere -->
   */
  private void buildExtMap(HashMap<String,String> extMap, String content) {
	  if (false == StringUtils.isEmpty(content)) {
		  String[] extBlocks = content.split("<!-- ext ");
		  for(int i=1; i<extBlocks.length; i++) {
			  String tempBlock = extBlocks[i];
			  String tempKey = tempBlock.substring(0, tempBlock.indexOf("-->")).trim();
			  String tempValue = tempBlock.substring(tempBlock.indexOf("-->") + 3);
			  extMap.put(tempKey, tempValue);
		  }
	  }
  }
}