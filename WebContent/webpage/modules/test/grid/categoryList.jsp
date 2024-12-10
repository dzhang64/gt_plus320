<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>商品类型管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="categoryList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>商品类型列表 </h5>
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
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="category" action="${ctx}/test/grid/category/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>类型名：</span>
				<form:input path="name" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>
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
		<shiro:hasPermission name="test:grid:category:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:grid:category:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 修改
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:grid:category:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:grid:category:import">
			<button id="btnImport" class="btn btn-sm btn-info"><i class="fa fa-folder-open-o"></i> 导入</button>
			<div id="importBox" class="hide">
				<form id="importForm" action="${ctx}/test/grid/category/import" method="post" enctype="multipart/form-data"
					style="padding-left:20px;text-align:center;" ><br/>
					<input id="uploadFile" name="file" type="file" style="width:330px"/>导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！<br/>	
				</form>
			</div>
		</shiro:hasPermission>
	</div>

	<!-- 表格 -->
	<table id="categoryTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="test:grid:category:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="test:grid:category:del">
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