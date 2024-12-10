<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>请假单管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="formLeaveList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
	        laydate({
	            elem: '#beginBeginDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus' //响应事件。如果没有传入event，则按照默认的click
	        });
	        laydate({
	            elem: '#endBeginDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
	            event: 'focus' //响应事件。如果没有传入event，则按照默认的click
	        });
					
		
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>请假单列表 </h5>
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
	<form:form id="searchForm" modelAttribute="formLeave" action="${ctx}/test/one/formLeave/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
			<div class="form-group">
			<span>员工：</span>
				<sys:treeselect id="tuser" name="tuser.id" value="${formLeave.tuser.id}" labelName="tuser.name" labelValue="${formLeave.tuser.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="form-control input-sm" allowClear="true" notAllowSelectParent="true"/>
			</div>
			<div class="form-group">
			<span>归属部门：</span>
				<sys:treeselect id="office" name="office.id" value="${formLeave.office.id}" labelName="office.name" labelValue="${formLeave.office.name}"
					title="部门" url="/sys/office/treeData?type=2" cssClass="form-control input-sm" allowClear="true" notAllowSelectParent="true"/>
			</div>
			<div class="form-group">
			<span>归属区域：</span>
				<sys:treeselect id="area" name="area.id" value="${formLeave.area.id}" labelName="area.name" labelValue="${formLeave.area.name}"
					title="区域" url="/sys/area/treeData" cssClass="form-control input-sm" allowClear="true" notAllowSelectParent="true"/>
			</div>
			<div class="form-group">
			<span>请假开始日期：</span>
				<input id="beginBeginDate" name="beginBeginDate" type="text" maxlength="20" class="laydate-icon form-control layer-date input-sm"
					value="<fmt:formatDate value="${formLeave.beginBeginDate}" pattern="yyyy-MM-dd" />"/> - 
				<input id="endBeginDate" name="endBeginDate" type="text" maxlength="20" class="laydate-icon form-control layer-date input-sm"
					value="<fmt:formatDate value="${formLeave.endBeginDate}" pattern="yyyy-MM-dd" />"/>
			</div>
			<div class="form-group">
			<span>状态：</span>
				<form:select path="status"  class="form-control m-b">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('bas_3workflow')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
		<shiro:hasPermission name="test:one:formLeave:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="test:one:formLeave:edit">
		</shiro:hasPermission>
		<shiro:hasPermission name="test:one:formLeave:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="test:one:formLeave:import">
		</shiro:hasPermission>
	</div>

	<!-- 表格 -->
	<table id="formLeaveTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="test:one:formLeave:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="test:one:formLeave:del">
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