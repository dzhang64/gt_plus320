<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>项目立项管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="projectApproveList.js" %>
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
		
		//导出EXCEL
		function excelexport(){
		  var param =null;
		  var url ='${ctx}/projectapprove/projectApprove/export';
		  var $iframe = $('<iframe id="down-file-iframe" />');
          var $form = $('<form target="down-file-iframe" method="post"/>');
          $form.attr('action', url);
          if(param!=null){
	           for (var key in param) {
	             $form.append('<input type="hidden" name="' + key + '" value="' + param[key] + '" />');
	          }
          }
          $iframe.append($form);
          $(document.body).append($iframe);
          $form[0].submit();
          $iframe.remove();
		}
		
	</script>
</head>

<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>项目立项列表 </h5>
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
	<form:form id="searchForm" modelAttribute="projectApprove" action="${ctx}/projectapprove/projectApprove/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<%--
		<!-- 组合查询开始 -->
			<div class="form-group">
			<span>项目名称：</span>
				<form:input path="projectName" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>
			</div>
			<div class="form-group">
			<span>项目编号：</span>
				<form:input path="projectNum" htmlEscape="false" maxlength="64"  class=" form-control input-sm"/>
			</div>
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
		<shiro:hasPermission name="projectapprove:projectApprove:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
			<script>
				function add(){
					var params = '{"button":"暂存","type":"save","flag":"save"};' 
							   + '{"button":"提交","type":"saveAndStart","flag":"saveAndStart"};'
							   + '{"button":"取消","type":"cancel","flag":"cancel"};';
					jp.openDialogFlow('添加项目立项', "${ctx}/projectapprove/projectApprove/form",'1000px', '100%', $('#projectApproveTable'), params);
				}
			</script>
		</shiro:hasPermission>
		
		<div id="oa-btn-group" class="btn-group">
			<shiro:hasPermission name="projectapprove:projectApprove:edit">
		    	<a id="Unsent" href="${ctx}/projectapprove/projectApprove/list/Unsent" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-edit"></i> 待发</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="projectapprove:projectApprove:edit">
			    <a id="Sent" href="${ctx}/projectapprove/projectApprove/list/Sent" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-ok"></i> 已发</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="projectapprove:projectApprove:edit">
			    <a id="Todo" href="${ctx}/projectapprove/projectApprove/list/Todo" class="btn btn-sm btn-success"><i class="glyphicon glyphicon-edit"></i> 待办</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="projectapprove:projectApprove:edit">
			    <a id="Doing" href="${ctx}/projectapprove/projectApprove/list/Doing" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-edit"></i> 在办</a>
		    </shiro:hasPermission>
			<shiro:hasPermission name="projectapprove:projectApprove:edit">
			    <a id="Done" href="${ctx}/projectapprove/projectApprove/list/Done" class="btn btn-sm btn-white"><i class="glyphicon glyphicon-ok"></i> 已办</a>
		    </shiro:hasPermission>
		</div>
	
		<shiro:hasPermission name="projectapprove:projectApprove:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="projectapprove:projectApprove:import">
		</shiro:hasPermission>
		
		<shiro:hasPermission name="projectapprove:projectApprove:export">
		</shiro:hasPermission>
		
		
	</div>

	<!-- 表格 -->
	<table id="projectApproveTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>