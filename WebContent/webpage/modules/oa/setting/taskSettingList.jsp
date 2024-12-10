<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>节点权限管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%-- <%@ include file="taskSettingList.js" %> --%>
	<script type="text/javascript">
		$(document).ready(function() {
		});
		function edit(processId,userTaskId,userTaskName){
			jp.openDialog('编辑节点权限', "${ctx}/oa/setting/taskSetting/form?processId=" + processId + "&userTaskId=" + userTaskId + "&userTaskName=" + userTaskName,'1000px', '600px');
		}
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>节点权限列表 </h5>
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
	<form:form id="searchForm" modelAttribute="taskSetting" action="${ctx}/oa/setting/taskSetting/" method="post" class="form-inline">
		<input type="hidden" id="procDefId" name="procDefId">
		<input type="hidden" id="userTaskId" name="userTaskId">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
	</form:form>
	<br/>
	</div>
	</div> --%>
	
	<!-- 工具栏 -->
	<%-- <div id="toolbar">
		<shiro:hasPermission name="oa:setting:taskSetting:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:setting:taskSetting:edit">
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:setting:taskSetting:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:setting:taskSetting:import">
		</shiro:hasPermission>
	</div> --%>

	<!-- 表格 -->
	<!-- <table id="taskSettingTable"   data-toolbar="#toolbar" data-id-field="id"></table> -->
	<table id="contentTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th>用户活动</th>
				<th>活动名称</th>
				<th>流程定义</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list }" var="userTask">
				<tr>
					<td>${userTask.flowElement.id }</td>
					<td>${userTask.flowElement.name }</td>
					<td>${userTask.processId }</td>
					<td style="">
						<!-- <a href="#" class="btn btn-info btn-xs view"><i class="fa fa-search-plus"></i> 查看</a> -->
						<a onclick="edit('${userTask.processId }','${userTask.flowElement.id }','${userTask.flowElement.name }');" href="#" class="btn btn-success btn-xs" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>
						<!-- <a href="#" class="btn btn-danger btn-xs del"><i class="fa fa-trash"></i> 删除</a> -->
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="oa:setting:taskSetting:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="oa:setting:taskSetting:del">
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