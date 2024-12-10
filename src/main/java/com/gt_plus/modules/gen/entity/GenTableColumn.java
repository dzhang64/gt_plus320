package com.gt_plus.modules.gen.entity;

import com.google.common.collect.Lists;
import com.gt_plus.common.persistence.DataEntity;
import com.gt_plus.common.utils.StringUtils;
import java.util.List;
import org.hibernate.validator.constraints.Length;

public class GenTableColumn extends DataEntity<GenTableColumn>
{
  private static final long serialVersionUID = 1L;
  private GenTable genTable;
  private String name;
  private String comments;
  private String jdbcType;
  private String javaType;
  private String javaField;
  private String isPk;
  private String isInsert;
  private String isEdit;
  private String isForm;
  private String isList;
  private String isQuery;
  private String queryType;
  private String showType;
  private String dictType;
  private Integer sort;
  private String tableName;
  private String fieldLabels;
  private String fieldKeys;
  private String searchLabel;
  private String searchKey;
  private String isNull;
  private String validateType;
  private String minLength;
  private String maxLength;
  private String minValue;
  private String maxValue;
  private String isOneLine; //是否独占行
  private String magicLogic; //魔法逻辑
  private String align; //对齐方式
  
  public GenTableColumn()
  {
  }

  public GenTableColumn(String id)
  {
    super(id);
  }

  public GenTableColumn(GenTable genTable) {
    this.genTable = genTable;
  }

  public GenTable getGenTable() {
    return this.genTable;
  }

  public void setGenTable(GenTable genTable) {
    this.genTable = genTable;
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

  public String getJdbcType() {
    return StringUtils.lowerCase(this.jdbcType);
  }

  public void setJdbcType(String jdbcType) {
    this.jdbcType = jdbcType;
  }

  public String getJavaType() {
    return this.javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public String getJavaField() {
    return this.javaField;
  }

  public void setJavaField(String javaField) {
    this.javaField = javaField;
  }

  public String getIsPk() {
    return this.isPk;
  }

  public void setIsPk(String isPk) {
    this.isPk = isPk;
  }

  public String getIsNull() {
    return this.isNull;
  }

  public void setIsNull(String isNull) {
    this.isNull = isNull;
  }

  public String getIsInsert() {
    return this.isInsert;
  }

  public void setIsInsert(String isInsert) {
    this.isInsert = isInsert;
  }

  public String getIsEdit() {
    return this.isEdit;
  }

  public void setIsEdit(String isEdit) {
    this.isEdit = isEdit;
  }

  public void setIsForm(String isForm) {
    this.isForm = isForm;
  }

  public String getIsForm() {
    return this.isForm;
  }

  public String getIsList() {
    return this.isList;
  }

  public void setIsList(String isList) {
    this.isList = isList;
  }

  public String getIsQuery() {
    return this.isQuery;
  }

  public void setIsQuery(String isQuery) {
    this.isQuery = isQuery;
  }

  public String getQueryType() {
    return this.queryType;
  }

  public void setQueryType(String queryType) {
    this.queryType = queryType;
  }

  public String getShowType() {
    return this.showType;
  }

  public void setShowType(String showType) {
    this.showType = showType;
  }

  public String getDictType() {
    return this.dictType == null ? "" : this.dictType;
  }

  public void setDictType(String dictType) {
    this.dictType = dictType;
  }

  public Integer getSort() {
    return this.sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public String getNameAndComments()
  {
    return getName() + (this.comments == null ? "" : new StringBuilder("  :  ").append(this.comments).toString());
  }

  public String getDataLength()
  {
    String[] ss = StringUtils.split(StringUtils.substringBetween(getJdbcType(), "(", ")"), ",");
    if ((ss != null) && (ss.length == 1)) {
      return ss[0];
    }
    return "0";
  }

  public String getSimpleJavaType()
  {
    if ("This".equals(getJavaType())) {
      return StringUtils.capitalize(this.genTable.getClassName());
    }
    return StringUtils.indexOf(getJavaType(), ".") != -1 ? 
      StringUtils.substringAfterLast(getJavaType(), ".") : 
      getJavaType();
  }

  public String getSimpleJavaField()
  {
    return StringUtils.substringBefore(getJavaField(), ".");
  }

  public String getJavaFieldId()
  {
    return StringUtils.substringBefore(getJavaField(), "|");
  }

  public String getJavaFieldName()
  {
    String[][] ss = getJavaFieldAttrs();
    return ss.length > 0 ? getSimpleJavaField() + "." + ss[0][0] : "";
  }

  public String[][] getJavaFieldAttrs()
  {
    String[] ss = StringUtils.split(StringUtils.substringAfter(getJavaField(), "|"), "|");
    String[][] sss = new String[ss.length][2];
    if (ss != null) {
      for (int i = 0; i < ss.length; i++) {
        sss[i][0] = ss[i];
        sss[i][1] = StringUtils.toUnderScoreCase(ss[i]);
      }
    }
    return sss;
  }

  public List<String> getAnnotationList()
  {
    List list = (List)Lists.newArrayList();

    if ("This".equals(getJavaType())) {
      list.add("com.fasterxml.jackson.annotation.JsonBackReference");
    }
    if ("java.util.Date".equals(getJavaType())) {
      if (getComments().indexOf("日期") != -1) {
    	  list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"yyyy-MM-dd\",timezone=\"GMT+8\")");
      } else {
    	  list.add("com.fasterxml.jackson.annotation.JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\",timezone=\"GMT+8\")");
      }
    }

    if ((!"1".equals(getIsNull())) && (!"String".equals(getJavaType()))) {
      list.add("javax.validation.constraints.NotNull(message=\"" + getComments() + "不能为空\")");
    }
    else if ((!"1".equals(getIsNull())) && ("String".equals(getJavaType())) && 
      (this.minLength != null) && (!this.minLength.equals(""))) {
      list.add("org.hibernate.validator.constraints.Length(min=" + this.minLength + ", max=" + this.maxLength + 
        ", message=\"" + getComments() + "长度必须介于 " + this.minLength + " 和 " + this.maxLength + " 之间\")");
    }

    if ("email".equals(this.validateType)) {
      list.add("org.hibernate.validator.constraints.Email(message=\"" + getComments() + "必须为合法邮箱\")");
    }
    if ("url".equals(this.validateType)) {
      list.add("org.hibernate.validator.constraints.URL(message=\"" + getComments() + "必须为合法网址\")");
    }
    if ("creditcard".equals(this.validateType)) {
      list.add("org.hibernate.validator.constraints.CreditCardNumber(message=\"" + getComments() + "必须为合法信用卡号\")");
    }
    if (("number".equals(this.validateType)) || ("digits".equals(this.validateType))) {
      if ((this.minValue != null) && (!this.minValue.equals(""))) {
        if (this.minValue.contains(".")) {
          String minv = this.minValue.replace(".", "_digitalPoint_");
          list.add("javax.validation.constraints.Min(value=(long)" + minv + ",message=\"" + getComments() + "的最小值不能小于" + minv + "\")");
        } else {
          list.add("javax.validation.constraints.Min(value=" + this.minValue + ",message=\"" + getComments() + "的最小值不能小于" + this.minValue + "\")");
        }
      }

      if ((this.maxValue != null) && (!this.maxValue.equals(""))) {
        if (this.maxValue.contains(".")) {
          String maxv = this.maxValue.replace(".", "_digitalPoint_");
          list.add("javax.validation.constraints.Max(value=(long)" + maxv + ",message=\"" + getComments() + "的最大值不能超过" + maxv + "\")");
        } else {
          list.add("javax.validation.constraints.Max(value=" + this.maxValue + ",message=\"" + getComments() + "的最大值不能超过" + this.maxValue + "\")");
        }

      }

    }

    return list;
  }

  public List<String> getSimpleAnnotationList()
  {
    List list = (List)Lists.newArrayList();
    for (String ann : getAnnotationList()) {
      String anno = StringUtils.substringAfterLast(ann, ".");
      anno = anno.replace("_digitalPoint_", ".");
      list.add(anno);
    }
    return list;
  }

  public Boolean getIsNotBaseField()
  {
    if ((!StringUtils.equals(getSimpleJavaField(), "id")) && 
      (!StringUtils.equals(getSimpleJavaField(), "remarks")) && 
      (!StringUtils.equals(getSimpleJavaField(), "createBy")) && 
      (!StringUtils.equals(getSimpleJavaField(), "createDate")) && 
      (!StringUtils.equals(getSimpleJavaField(), "updateBy")) && 
      (!StringUtils.equals(getSimpleJavaField(), "updateDate")) && 
      (!StringUtils.equals(getSimpleJavaField(), "delFlag"))) return Boolean.valueOf(true);
    return 
      Boolean.valueOf(false);
  }
  
  public Boolean getIsNotTreeBaseField() {
	return Boolean.valueOf((!StringUtils.equals(getSimpleJavaField(), "id")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "remarks")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "createBy")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "createDate")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "updateBy")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "updateDate")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "delFlag")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "parent")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "parentIds")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "name")) && 
	      (!StringUtils.equals(getSimpleJavaField(), "sort")));
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableName() {
    return this.tableName;
  }

  public void setFieldLabels(String fieldLabels) {
    this.fieldLabels = fieldLabels;
  }

  public String getFieldLabels() {
    return this.fieldLabels;
  }

  public void setFieldKeys(String fieldKeys) {
    this.fieldKeys = fieldKeys;
  }

  public String getFieldKeys() {
    return this.fieldKeys;
  }

  public void setSearchLabel(String searchLabel) {
    this.searchLabel = searchLabel;
  }

  public String getSearchLabel() {
    return this.searchLabel;
  }

  public void setSearchKey(String searchKey) {
    this.searchKey = searchKey;
  }

  public String getSearchKey() {
    return this.searchKey;
  }

  public void setMinLength(String minLength)
  {
    this.minLength = minLength;
  }

  public String getMinLength()
  {
    return this.minLength;
  }

  public void setValidateType(String validateType)
  {
    this.validateType = validateType;
  }

  public String getValidateType()
  {
    return this.validateType;
  }

  public void setMaxLength(String maxLength)
  {
    this.maxLength = maxLength;
  }

  public String getMaxLength()
  {
    return this.maxLength;
  }

  public void setMinValue(String minValue)
  {
    this.minValue = minValue;
  }

  public String getMinValue()
  {
    return this.minValue;
  }

  public void setMaxValue(String maxValue)
  {
    this.maxValue = maxValue;
  }

  public String getMaxValue()
  {
    return this.maxValue;
  }
  
  public void setIsOneLine(String isOneLine)
  {
    this.isOneLine = isOneLine;
  }

  public String getIsOneLine()
  {
    return this.isOneLine;
  }

  public String getMagicLogic() {
	return magicLogic;
  }

  public void setMagicLogic(String magicLogic) {
	this.magicLogic = magicLogic;
  }

  public String getAlign() {
	return align;
  }
	
  public void setAlign(String align) {
	this.align = align;
  }
}