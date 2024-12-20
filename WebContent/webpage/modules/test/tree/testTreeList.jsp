<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>机构列表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/webpage/include/treetable.jsp" %>
	<%@include file="testTreeList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
			bindTable();
		});
		
		function bindTable(){
			$.ajax({
				type: "POST",
				url: "${ctx}/test/tree/testTree/data",
				data: $("#searchForm").serializeJson(),
				dataType: "json",
				success: function(data){
					$('#treeTableList').empty();
					var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
					var ids = [], rootIds = [];
					for (var i=0; i<data.length; i++){
						ids.push(data[i].id);
					}
					ids = ',' + ids.join(',') + ',';
					for (var i=0; i<data.length; i++){
						if (ids.indexOf(','+data[i].parentId+',') == -1){
							if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){
								rootIds.push(data[i].parentId);
							}
						}
					}
					for (var i=0; i<rootIds.length; i++){
						addRow("#treeTableList", tpl, data, rootIds[i], true);
					}
					$("#treeTable").treeTable({expandLevel : 5});
				}
			});
		}
		
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
						blank123:0}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
		
		testTreeTable = {
			refresh:function() {
				bindTable();
			}
		};
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
			<h5>机构列表 </h5>
			<div class="ibox-tools">
				<a class="collapse-link">
					<i class="fa fa-chevron-up"></i>
				</a>
				<a class="dropdown-toggle" data-toggle="dropdown" href="form_basic.html#" style="display:none">
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
	<form:form id="searchForm" modelAttribute="testTree" action="${ctx}/test/tree/testTree/" method="post" class="form-inline">
				<div class="form-group">
				<label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="form-control input-sm"/>
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
		<shiro:hasPermission name="test:tree:testTree:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
	</div>
	
	<table id="treeTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th>名称</th>
				<th>父级编号</th>
				<th>备注信息</th>
				<shiro:hasPermission name="test:tree:testTree:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a  href="#" onclick="jp.openDialogView('查看机构', '${ctx}/test/tree/testTree/form?id={{row.id}}','800px', '500px')">
				{{row.name}}
			</a></td>
			<td>
				{{row.parent.id}}
			</td>
			<td>
				{{row.remarks}}
			</td>
			<td>
			<shiro:hasPermission name="test:tree:testTree:view">
				<a href="#" onclick="jp.openDialogView('查看机构', '${ctx}/test/tree/testTree/form?id={{row.id}}','800px', '500px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i>  查看</a>
				</shiro:hasPermission>
			<shiro:hasPermission name="test:tree:testTree:edit">
   				<a href="#" onclick="jp.openDialog('修改机构', '${ctx}/test/tree/testTree/form?id={{row.id}}','800px', '500px', testTreeTable)" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
   			</shiro:hasPermission>
   			<shiro:hasPermission name="test:tree:testTree:del">
				<a href="${ctx}/test/tree/testTree/delete?id={{row.id}}" onclick="return confirmx('确认要删除该机构及所有子机构吗？', this.href)" class="btn btn-danger btn-xs" ><i class="fa fa-trash"></i> 删除</a>
			</shiro:hasPermission>
   			<shiro:hasPermission name="test:tree:testTree:add">
				<a href="#" onclick="jp.openDialog('添加下级机构', '${ctx}/test/tree/testTree/form?parent.id={{row.id}}','800px', '500px', testTreeTable)" class="btn btn-primary btn-xs" ><i class="fa fa-plus"></i> 添加下级机构</a>
			</shiro:hasPermission>
			</td>
		</tr>
	</script>
</body>
</html>