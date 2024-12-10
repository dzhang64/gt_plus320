package com.gt_plus.modules.gen.service;


import com.gt_plus.common.ds.DataSourceContextHolder;
import com.gt_plus.common.persistence.Page;
import com.gt_plus.common.service.BaseService;
import com.gt_plus.common.utils.StringUtils;
import com.gt_plus.modules.gen.dao.GenDataBaseDictDao;
import com.gt_plus.modules.gen.dao.GenTableColumnDao;
import com.gt_plus.modules.gen.dao.GenTableDao;
import com.gt_plus.modules.gen.entity.GenTable;
import com.gt_plus.modules.gen.entity.GenTableColumn;
//import com.gt_plus.modules.gen.util.a;
import com.gt_plus.modules.gen.util.GenUtils;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class GenTableService extends BaseService
{

  @Autowired
  private GenTableDao genTableDao;

  @Autowired
  private GenTableColumnDao genTableColumnDao;

  @Autowired
  private GenDataBaseDictDao genDataBaseDictDao;
  
  public GenTable get(String id)
  {
    GenTable genTable = (GenTable)this.genTableDao.get(id);
    GenTableColumn genTableColumn = new GenTableColumn();
    genTableColumn.setGenTable(new GenTable(genTable.getId()));
    genTable.setColumnList(this.genTableColumnDao.findList(genTableColumn));
    return genTable;
  }

  public Page<GenTable> find(Page<GenTable> page, GenTable genTable) {
    genTable.setPage(page);
    page.setList(this.genTableDao.findList(genTable));
    return page;
  }

  public List<GenTable> findAll() {
    return this.genTableDao.findAllList(new GenTable());
  }

  public List<GenTable> findTableListFormDb(GenTable genTable)
  {
    return this.genDataBaseDictDao.findTableList(genTable);
  }

  public boolean checkTableName(String tableName)
  {
    if (StringUtils.isBlank(tableName)) {
    	return true;
    }
    else {
    	GenTable genTable = new GenTable();
    	genTable.setName(tableName);
    	return (0 == this.genTableDao.findList(genTable).size());
    }
  }

  public boolean checkTableNameFromDB(String tableName)
  {
	  if (StringUtils.isBlank(tableName)) {
	    return true;
	  }
	  else {
	    GenTable genTable = new GenTable();
	    genTable.setName(tableName);
	    return (0 == this.genDataBaseDictDao.findTableList(genTable).size());
	  }
  }

  public GenTable getTableFormDb(GenTable genTable)
  {
	  if (StringUtils.isNotBlank(genTable.getName())){
			
			List<GenTable> list = genDataBaseDictDao.findTableList(genTable);
			if (list.size() > 0){
				
				// 如果是新增，初始化表属性
				if (StringUtils.isBlank(genTable.getId())){
					genTable = list.get(0);
					// 设置字段说明
					if (StringUtils.isBlank(genTable.getComments())){
						genTable.setComments(genTable.getName());
					}
					genTable.setClassName(StringUtils.toCapitalizeCamelCase(genTable.getName()));
				}
				
				// 添加新列
				List<GenTableColumn> columnList = genDataBaseDictDao.findTableColumnList(genTable);
				for (GenTableColumn column : columnList){
					boolean b = false;
					for (GenTableColumn e : genTable.getColumnList()){
						if (e.getName().equals(column.getName())){
							b = true;
						}
					}
					if (!b){
						genTable.getColumnList().add(column);
					}
				}
				
				// 删除已删除的列
				for (GenTableColumn e : genTable.getColumnList()){
					boolean b = false;
					for (GenTableColumn column : columnList){
						if (column.getName().equals(e.getName())){
							b = true;
						}
					}
					if (!b){
						e.setDelFlag(GenTableColumn.DEL_FLAG_DELETE);
					}
				}
				
				// 获取主键
				genTable.setPkList(genDataBaseDictDao.findTablePK(genTable));
				
				// 初始化列属性字段
				//GenUtils.initColumnField(genTable);
				
				//Modified by houxl on 2017-07-03
				//a.a(genTable);
				GenUtils.initColumnField(genTable);
				
			}
		}
	    /*
    if (StringUtils.isNotBlank(genTable.getName()))
    {
      List list;
      if ((
        list = this.genDataBaseDictDao.findTableList(genTable))
        .size() > 0)
      {
        if (StringUtils.isBlank(genTable.getId()))
        {
          if (StringUtils.isBlank((
            genTable = (GenTable)list.get(0))
            .getComments())) {
            genTable.setComments(genTable.getName());
          }
          genTable.setClassName(StringUtils.toCapitalizeCamelCase(genTable.getName()));
        }
        List columnList;
        Iterator localIterator2;
        GenTableColumn e;
        for (GenTableColumn column : columnList = this.genDataBaseDictDao.findTableColumnList(genTable))
        {
          boolean b = false;
          for (localIterator2 = genTable.getColumnList().iterator(); localIterator2.hasNext(); ) {
            if (((
              e = (GenTableColumn)localIterator2.next())
              .getName() != null) && (e.getName().equals(column.getName()))) {
              b = true;
            }
          }
          if (!b) {
            genTable.getColumnList().add(column);
          }

        }
        //////////////////
        for (GenTableColumn e : genTable.getColumnList()) {
          boolean b = false;
          for (localIterator2 = columnList.iterator(); localIterator2.hasNext(); ) {
            if ((
              e = (GenTableColumn)localIterator2.next())
              .getName().equals(e.getName())) {
              b = true;
            }
          }
          if (!b) {
            e.setDelFlag("1");
          }

        }

        genTable.setPkList(this.genDataBaseDictDao.findTablePK(genTable));

        a.a(genTable);
      }
    }
*/
    return genTable;
  }

  @Transactional(readOnly=false)
  public void save(GenTable genTable) {
    boolean isSync = true;

    if (StringUtils.isBlank(genTable.getId())) {
      isSync = false;
    }
    else
    {
      GenTable oldTable = get(genTable.getId());
      if (oldTable.getColumnList().size() != genTable.getColumnList().size() 
    		  || (false == oldTable.getName().equals(genTable.getName())) 
    		 || (false == oldTable.getComments().equals(genTable.getComments()))
    	 )
      {
    	  isSync = false;
      }
      //if (((
      //  oldTable = get(genTable.getId()))
      //  .getColumnList().size() != genTable.getColumnList().size()) || (!oldTable.getName().equals(genTable.getName())) || (!oldTable.getComments().equals(genTable.getComments())))
      //  isSync = false;
      else {
    	//for (Iterator columnList = genTable.getColumnList().iterator(); columnList.hasNext();) {
    	  for (GenTableColumn column : genTable.getColumnList()){
    		//GenTableColumn column = (GenTableColumn)columnList.next();
    		GenTableColumn oldColumn = this.genTableColumnDao.get(column.getId());
    		if (StringUtils.isBlank(oldColumn.getId()) 
    				|| (false == oldColumn.getName().equals(column.getName()))
    				|| (false == oldColumn.getJdbcType().equals(column.getJdbcType()))
    				|| (false == oldColumn.getIsPk().equals(column.getIsPk()))
    				|| (false == oldColumn.getComments().equals(column.getComments()))
    				) {
    			isSync = false;
    		}
    	}
    	/*
        for (oldTable = genTable.getColumnList().iterator(); oldTable.hasNext(); )
        {
          GenTableColumn oldColumn;
          if ((StringUtils.isBlank((
            column = (GenTableColumn)oldTable.next())
            .getId())) || 
            (!(
            oldColumn = (GenTableColumn)this.genTableColumnDao.get(column.getId()))
            .getName().equals(column.getName())) || 
            (!oldColumn.getJdbcType().equals(column.getJdbcType())) || 
            (!oldColumn.getIsPk().equals(column.getIsPk())) || 
            (!oldColumn.getComments().equals(column.getComments()))) {
            isSync = false;
          }

        }
		*/
      }

    }

    if (!isSync) {
      genTable.setIsSync("0");
    }
    /*
    if (StringUtils.isBlank(genTable.getId())) {
      genTable.preInsert();
      this.genTableDao.insert(genTable);
    } else {
      genTable.preUpdate();
      this.genTableDao.update(genTable);
    }

    this.genTableColumnDao.deleteByGenTable(genTable);

    for (GenTableColumn column = genTable.getColumnList().iterator(); column.hasNext(); )
    {
      GenTableColumn column;
      (
        column = (GenTableColumn)column.next())
        .setGenTable(genTable);

      column.setId(null);
      column.preInsert();
      this.genTableColumnDao.insert(column);
    }*/
    if (StringUtils.isBlank(genTable.getId())){
		genTable.preInsert();
		this.genTableDao.insert(genTable);
	}else{
		genTable.preUpdate();
		this.genTableDao.update(genTable);
	}
	// 保存列
	for (GenTableColumn column : genTable.getColumnList()){
		column.setGenTable(genTable);
		if (StringUtils.isBlank(column.getId())){
			column.preInsert();
			this.genTableColumnDao.insert(column);
		}else{
			column.preUpdate();
			this.genTableColumnDao.update(column);
		}
	}
  }

  @Transactional(readOnly=false)
  public void syncSave(GenTable genTable)
  {
    genTable.setIsSync("1");
    this.genTableDao.update(genTable);
  }

  @Transactional(readOnly=false)
  public void saveFromDB(GenTable genTable)
  {
    genTable.preInsert();
    this.genTableDao.insert(genTable);

    //for (Iterator localIterator = genTable.getColumnList().iterator(); localIterator.hasNext(); )
    //{
    //  GenTableColumn column;
    //  (
    //    column = (GenTableColumn)localIterator.next())
    //    .setGenTable(genTable);
    for (GenTableColumn column : genTable.getColumnList()){
      column.setGenTable(genTable);
      //end
      column.setId(null);
      column.preInsert();
      this.genTableColumnDao.insert(column);
    }
  }

  @Transactional(readOnly=false)
  public void delete(GenTable genTable)
  {
    this.genTableDao.delete(genTable);
    this.genTableColumnDao.deleteByGenTable(genTable);
  }

  @Transactional(readOnly=false)
  public void buildTable(String sql) {
    this.genTableDao.buildTable(sql);
  }

}