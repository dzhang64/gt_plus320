<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>网上信访管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="onlinePetitionList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		<%--
		--%>
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>网上信访列表 </h5>
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
	<form:form id="searchForm" modelAttribute="onlinePetition" action="${ctx}/mgmt/op/onlinePetition/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>单位名称：</span>
				<form:input path="comp" htmlEscape="false" maxlength="100"  class=" form-control input-sm"/>
			</div>
			<div class="form-group">
			<span>标题：</span>
				<form:input path="title" htmlEscape="false" maxlength="100"  class=" form-control input-sm"/>
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
		<shiro:hasPermission name="mgmt:op:onlinePetition:add">
		</shiro:hasPermission>
		
		<shiro:hasPermission name="mgmt:op:onlinePetition:edit">
		</shiro:hasPermission>
		<shiro:hasPermission name="mgmt:op:onlinePetition:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="mgmt:op:onlinePetition:import">
		</shiro:hasPermission>
		
	</div>

	<!-- 表格 -->
	<table id="onlinePetitionTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于扩展右键菜单 --%>
    </ul>  
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>