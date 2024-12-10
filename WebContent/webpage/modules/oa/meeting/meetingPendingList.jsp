<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>会议安排管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="meetingPendingList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		var path = '${path}';
		if (path == null || path == '') {
			path = 'Todo';
		}
		$('#oa-btn-group').children().attr('class','btn btn-sm btn-white');
		$('a#' + path).attr('class', 'btn btn-sm btn-success');
		});
		
		//gridselect选择后执行
		function afterGridSelect(callbackKey, item) {
			<%--
			//callbackKey，区分多个gridselect
			//if(callbackKey == 'gridselect_01') {
			//
			//} else if (callbackKey == 'gridselect_02') {
			//
			//}
			--%>
		}
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>会议安排列表 </h5>
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
	<form:form id="searchForm" modelAttribute="meeting" action="${ctx}/oa/meeting/meeting/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<%--
		<!-- 组合查询开始 -->
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
		--%>
		<!-- 组合查询结束 -->
	</form:form>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div id="toolbar">
		<%-- <shiro:hasPermission name="oa:meeting:meeting:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
			<script>
				function add(){
					var btns = '暂存,提交,取消';
					var params = 'flag:暂存,提交';
					jp.openDialog('添加会议安排', "${ctx}/oa/meeting/meeting/form",'1000px', '100%', $('#meetingTable'), btns, params);
				}
			</script>
		</shiro:hasPermission> --%>
		
		
		<%--
		
		<shiro:hasPermission name="oa:meeting:meeting:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 编辑
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:meeting:meeting:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:meeting:meeting:import">
		</shiro:hasPermission>
		
		--%>
	</div>

	<!-- 表格 -->
	<table id="meetingTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
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