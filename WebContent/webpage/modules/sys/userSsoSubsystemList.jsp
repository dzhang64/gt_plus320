<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>用户SSO子系统关系管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="userSsoSubsystemList.js" %>
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
		<h5>用户SSO子系统关系列表 </h5>
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
	<form:form id="searchForm" modelAttribute="userSsoSubsystem" action="${ctx}/sys/userSsoSubsystem/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>用户ID：</span>
				<form:input path="userId" htmlEscape="false" maxlength="50"  class=" form-control input-sm"/>
			</div>
			<div class="form-group">
			<span>子系统ID：</span>
				<form:input path="ssoSubsystem.id" htmlEscape="false" maxlength="50"  class=" form-control input-sm"/>
			</div>
			<div class="form-group">
			<span>用户名：</span>
				<form:input path="loginName" htmlEscape="false" maxlength="50"  class=" form-control input-sm"/>
			</div>
			<div class="form-group">
			<span>密码：</span>
				<form:input path="password" htmlEscape="false" maxlength="50"  class=" form-control input-sm"/>
			</div>
			<div class="form-group">
			<span>是否允许登录：</span>
				<form:select path="isAllow"  class="form-control m-b">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
		<shiro:hasPermission name="sys:userSsoSubsystem:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		
		<shiro:hasPermission name="sys:userSsoSubsystem:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 编辑
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:userSsoSubsystem:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:userSsoSubsystem:import">
		</shiro:hasPermission>
		
	</div>

	<!-- 表格 -->
	<table id="userSsoSubsystemTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
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