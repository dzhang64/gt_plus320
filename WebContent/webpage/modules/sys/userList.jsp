<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="userList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body>
	<div class="wrapper wrapper-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/list" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group">
			<span>归属机构：</span>
				<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.fullPathName" labelValue="${user.office.name}" 
				title="部门" url="/sys/office/treeData?type=2" cssClass=" form-control input-sm" allowClear="true" notAllowSelectParent="false" 
				showFullPathName="yes" />
		</div>
		<div class="form-group">
			<span>登录名：</span>
				<form:input path="loginName" htmlEscape="false" maxlength="50" class=" form-control input-sm"/>
		</div>
		
		<div class="form-group">
			<span>姓名：</span>
				<form:input path="name" htmlEscape="false" maxlength="50" class=" form-control input-sm"/>
		</div>	
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
	</form:form>
	
	<!-- 工具栏 -->
	<div id="toolbar">
		<shiro:hasPermission name="sys:user:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:user:edit">
			<button id="edit" class="btn btn-sm btn-success" disabled onclick="edit()">
				<i class="glyphicon glyphicon-edit"></i> 编辑
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:user:edit">
			<button id="editSso" class="btn btn-sm btn-success" disabled onclick="editSso()">
				<i class="glyphicon glyphicon-edit"></i> SSO账号
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:user:del">
			<button id="remove" class="btn btn-sm btn-danger" disabled onclick="deleteAll()">
				<i class="glyphicon glyphicon-remove"></i> 删除
			</button>
		</shiro:hasPermission>
		<shiro:hasPermission name="sys:user:import">
			<button id="btnImport" class="btn btn-sm btn-info"><i class="fa fa-folder-open-o"></i> 导入</button>
			<div id="importBox" class="hide">
				<form id="importForm" action="${ctx}/sys/user/import" method="post" enctype="multipart/form-data"
					style="padding-left:20px;text-align:center;" ><br/>
					<input id="uploadFile" name="file" type="file" style="width:330px"/>导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！<br/>	
				</form>
			</div>
		</shiro:hasPermission>
	</div>

	<!-- 表格 -->
	<table id="userTable"   data-toolbar="#toolbar" data-id-field="id"></table>
	
	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="sys:user:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="sys:user:del">
        <li data-item="delete"><a>删除</a></li>
        </shiro:hasPermission>
        <li data-item="action1"><a>取消</a></li>
        --%>
    </ul>  
	</div>
</body>
</html>