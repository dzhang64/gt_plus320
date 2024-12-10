<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>发文管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="testOaEdocList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
	        laydate({
	            elem: '#beginRunDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus' //响应事件。如果没有传入event，则按照默认的click
	        });
	        laydate({
	            elem: '#endRunDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus' //响应事件。如果没有传入event，则按照默认的click
	        });
					
		
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>待发发文列表 </h5>
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
	<%-- <div class="row">
	<div class="col-sm-12">
	<form:form id="searchForm" modelAttribute="testOaEdoc" action="${ctx}/test/oa/testOaEdoc/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>日期：</span>
				<input id="beginRunDate" name="beginRunDate" type="text" maxlength="20" class="laydate-icon form-control layer-date input-sm"
					value="<fmt:formatDate value="${testOaEdoc.beginRunDate}" pattern="yyyy-MM-dd" />"/> - 
				<input id="endRunDate" name="endRunDate" type="text" maxlength="20" class="laydate-icon form-control layer-date input-sm"
					value="<fmt:formatDate value="${testOaEdoc.endRunDate}" pattern="yyyy-MM-dd" />"/>
			</div>
			<div class="form-group">
			<span>标题：</span>
				<form:input path="title" htmlEscape="false" maxlength="50"  class=" form-control input-sm"/>
			</div>
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
	</form:form>
	<br/>
	</div>
	</div> --%>
	
	<!-- 工具栏 -->
	<div id="toolbar">
		<shiro:hasPermission name="test:oa:testOaEdoc:add">
			<a id="add" class="btn btn-sm btn-primary" href="${ctx}/test/oa/testOaEdoc/processList?category=10"><i class="glyphicon glyphicon-plus"></i> 拟文</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:edit">
			<a id="edit" class="btn btn-sm btn-warning" href="${ctx}/test/oa/testOaEdoc/toSend"><i class="glyphicon glyphicon-edit"></i> 待发</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:edit">
			<a id="edit" class="btn btn-sm btn-info" href="${ctx}/test/oa/testOaEdoc/send"><i class="glyphicon glyphicon-ok"></i> 已发</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:edit">
			<a id="edit" class="btn btn-sm btn-warning" href="${ctx}/test/oa/testOaEdoc/toClaim"><i class="glyphicon glyphicon-edit"></i> 待办</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:edit">
			<a id="edit" class="btn btn-sm btn-success" href="${ctx}/test/oa/testOaEdoc/todo"><i class="glyphicon glyphicon-time"></i> 在办</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:edit">
			<a id="edit" class="btn btn-sm btn-info" href="${ctx}/test/oa/testOaEdoc/historic"><i class="glyphicon glyphicon-ok"></i> 已办</a>
		</shiro:hasPermission>
		<%-- 
		<shiro:hasPermission name="test:oa:testOaEdoc:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 编辑
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:oa:testOaEdoc:import">
		</shiro:hasPermission> 
		--%>
	</div>

	<!-- 表格 -->
	<!-- <table id="testOaEdocTable"   data-toolbar="#toolbar" data-id-field="id"></table> -->
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th>标题</th>
				<th>日期</th>
				<th>更新时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="object">
				<tr>
					<td>${object.title}</td>
					<td>
						<fmt:formatDate value="${object.runDate}" pattern="yyyy-MM-dd"/>
					</td>
					<td><fmt:formatDate value="${object.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
						<a href="${ctx}/test/oa/testOaEdoc/form/${object.id}">编辑</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<!-- 分页代码 -->
	<table:page page="${page}"></table:page>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="test:oa:testOaEdoc:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="test:oa:testOaEdoc:del">
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