<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>区域列表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/webpage/include/treetable.jsp" %>
	<%@include file="areaList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
			bindTable();
		});
		
		function bindTable(){
			$.ajax({
				type: "POST",
				url: "${ctx}/sys/area/data",
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
							types: getDictLabel(${fns:toJson(fns:getDictList('sys_area_type'))}, row.type)
						}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
		
		areaTable = {
			refresh:function() {
				bindTable();
				bindTable();
			}
		};
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
			<h5>区域列表 </h5>
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
	<form:form id="searchForm" modelAttribute="area" class="form-inline">
		
	</form:form>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<div id="toolbar">
		<shiro:hasPermission name="sys:area:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
	</div>
	
	<table id="treeTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th>区域名称</th>
				<th>区域编码</th>
				<th>区域类型</th>
				<th>备注信息</th>
				<shiro:hasPermission name="sys:area:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a  href="#" onclick="jp.openDialogView('查看区域', '${ctx}/sys/area/form?viewFlag=set&id={{row.id}}','800px', '500px')">
				{{row.name}}
			</a></td>
			<td>
				{{row.code}}
			</td>
			<td>
				{{dict.types}}
			</td>
			<td>
				{{row.remarks}}
			</td>
			<td>
			<shiro:hasPermission name="sys:area:view">
				<a href="#" onclick="jp.openDialogView('查看区域', '${ctx}/sys/area/form?viewFlag=set&id={{row.id}}','800px', '500px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i>  查看</a>
				</shiro:hasPermission>
			<shiro:hasPermission name="sys:area:edit">
   				<a href="#" onclick="jp.openDialog('修改区域', '${ctx}/sys/area/form?id={{row.id}}','800px', '500px', areaTable)" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
   			</shiro:hasPermission>
   			<shiro:hasPermission name="sys:area:del">
				<a href="#" onclick="del('{{row.id}}');" class="btn btn-danger btn-xs" ><i class="fa fa-trash"></i> 删除</a>
			</shiro:hasPermission>
   			<shiro:hasPermission name="sys:area:add">
				<a href="#" onclick="jp.openDialog('添加下级区域', '${ctx}/sys/area/form?parent.id={{row.id}}','800px', '500px', areaTable)" class="btn btn-primary btn-xs" ><i class="fa fa-plus"></i> 添加下级区域</a>
			</shiro:hasPermission>
			</td>
		</tr>
	</script>
</body>
</html>