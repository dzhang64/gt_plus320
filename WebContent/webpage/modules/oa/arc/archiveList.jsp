<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>档案管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="archiveList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body >
	<div class="wrapper wrapper-content">
	<!-- <div class="ibox">
	<div class="ibox-title">
		<h5>档案列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#" style="display:none">
				<i class="fa fa-wrench"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="#">选项1</a>
				</li>
				<li><a href="#">选项2</a>
				</li>
			</ul>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div> -->
    
    <!-- <div class="ibox-content"> -->
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="archive" action="${ctx}/oa/arc/archive/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>档案目录：</span>
				<%-- <sys:gridselect url="${ctx}/oa/arc/archive/selectarc" id="arc" name="arc"  value="${archive.arc.id}"  title="选择档案目录" labelName="arc.name" 
				labelValue="${archive.arc.name}" cssClass="form-control input-sm required" fieldLabels="目录名称" fieldKeys="name" searchLabel="目录名称" searchKey="name" ></sys:gridselect> --%>
				<sys:treeselect id="arc" name="arc" value="${archive.arc.id}" labelName="arc.name" labelValue="${archive.arc.name}"
				title="档案栏目" url="/oa/arc/arcCategory/treeData?type=2" cssClass="form-control input-sm" allowClear="true" notAllowSelectParent="true"/>
			</div> 
			<div class="form-group">
			<span>标题：</span>
				<form:input path="title" htmlEscape="false" maxlength="255"  class=" form-control input-sm"/>
			</div>
			
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div id="toolbar">
		<shiro:hasPermission name="oa:arc:archive:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:arc:archive:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 编辑
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:arc:archive:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:arc:archive:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:arc:archive:import">
		</shiro:hasPermission>
	</div>

	<!-- 表格 -->
	<table id="archiveTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="oa:arc:archive:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="oa:arc:archive:del">
        <li data-item="delete"><a>删除</a></li>
        </shiro:hasPermission>
        <li data-item="action1"><a>取消</a></li>
        --%>
    </ul>  
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>