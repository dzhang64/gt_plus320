<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>发文管理管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="edocSendList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
			var path = '${path}';
			if (path == null || path == '') {
				path = 'Todo';
			}
			$('#oa-btn-group').children().attr('class','btn btn-sm btn-white');
			$('a#' + path).attr('class', 'btn btn-sm btn-success');
			if (path == 'Done') {
				$('#searchDiv').attr('style', 'float: left;');
			} else {
				$('#searchDiv').attr('style', 'display: none;');
			}
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
		<h5>发文管理列表 </h5>
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
	<form:form id="searchForm" modelAttribute="edocSend" action="${ctx}/oa/edoc/edocSend/list/${path==null?'Todo':path}" method="post" class="form-inline">
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
		<div class="btn-group" style="float: left;">
			<shiro:hasPermission name="oa:edoc:edocSend:add">
				<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
				<script>
					function add(){
						var params = '{"button":"暂存","type":"save","flag":"save"};' 
								   + '{"button":"提交","type":"saveAndStart","flag":"saveAndStart"};'
								   + '{"button":"取消","type":"cancel","flag":"cancel"};';
						jp.openDialogFlow('添加发文管理', '${ctx}/oa/edoc/edocSend/form','1000px', '100%', $('#edocSendTable'), params);
					}
				</script>
			</shiro:hasPermission>
		</div>
		<div id="oa-btn-group" class="btn-group" style="float: left; margin-left: 5px;">
			<shiro:hasPermission name="oa:edoc:edocSend:edit">
		    	<a id="Unsent" href="${ctx}/oa/edoc/edocSend/list/Unsent" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-edit"></i> 待发</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="oa:edoc:edocSend:edit">
			    <a id="Sent" href="${ctx}/oa/edoc/edocSend/list/Sent" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-ok"></i> 已发</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="oa:edoc:edocSend:edit">
			    <a id="Todo" href="${ctx}/oa/edoc/edocSend/list/Todo" class="btn btn-sm btn-success"><i class="glyphicon glyphicon-edit"></i> 待办</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="oa:edoc:edocSend:edit">
			    <a id="Doing" href="${ctx}/oa/edoc/edocSend/list/Doing" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-edit"></i> 在办</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="oa:edoc:edocSend:edit">
			    <a id="Done" href="${ctx}/oa/edoc/edocSend/list/Done" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-ok"></i> 已办</a>
		    </shiro:hasPermission>
		</div>
		<div id="searchDiv" style="float: left;">
			<div class="btn-group"  style="float: left; margin-left: 5px;">
				<input id="title" class="form-control input-sm" placeholder="查询标题信息">
			</div>
			<div class="btn-group"  style="float: left; margin-left: 5px;">
				<a id="search" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-search"></i> 查询</a>
			</div>
		</div>
		<%--
		
		<shiro:hasPermission name="oa:edoc:edocSend:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 编辑
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:edoc:edocSend:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="oa:edoc:edocSend:import">
		</shiro:hasPermission>
		
		--%>
	</div>

	<!-- 表格 -->
	<table id="edocSendTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
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