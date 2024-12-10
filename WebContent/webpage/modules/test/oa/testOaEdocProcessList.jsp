<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>发文管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>发文模版列表 </h5>
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
				<th>流程分类</th>
				<!-- <th>流程标识</th> -->
				<th>流程名称</th>
				<th>流程图</th>
				<th>更新时间</th>
				<!-- <th>流程版本</th> -->
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="object">
				<c:set var="process" value="${object[0]}" />
				<c:set var="deployment" value="${object[1]}" />
				<tr>
					<td>${fns:getDictLabel(process.category,'act_category','无分类')}</td>
					<%-- <td><a href="${ctx}/act/task/form?procDefId=${process.id}">${process.key}</a></td> --%>
					<td>${process.name}</td>
					<td><a  href="javascript:openDialog('流程图','${ctx}/act/process/resource/read?procDefId=${process.id}&resType=image','1000px', '600px')">${process.diagramResourceName}</a>	</td>
					<td><fmt:formatDate value="${deployment.deploymentTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<%-- <td><b title='流程版本号'>V : ${process.version}</b></td> --%>
					<td>
						<a href="${ctx}/act/task/form?procDefId=${process.id}">启动流程</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
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