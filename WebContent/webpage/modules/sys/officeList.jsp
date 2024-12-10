<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>组织机构列表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/webpage/include/treetable.jsp" %>
	<%@include file="officeList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
			bindTable();
		});
		
		function bindTable(){
			$.ajax({
				type: "POST",
				url: "${ctx}/sys/office/data",
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
							type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type)
						}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
		//blank123:0
		officeTable = {
			refresh:function() {
				bindTable();
				window.parent.refreshTree();
				bindTable();
			}
		};
	</script>
</head>
<body>
	<div class="wrapper wrapper-content">
	<sys:message content="${message}"/>
	<!--查询条件-->
	<form:form id="searchForm" modelAttribute="office" class="form-inline" style="display:none">
		<form:hidden path="id" value="${office.id}" />
		<form:hidden path="parentIds" value="${office.parentIds}" />
	</form:form>
	<!-- 工具栏 -->
	<div id="toolbar">
		<shiro:hasPermission name="sys:office:add">
			<a id="add" class="btn btn-sm btn-primary" onclick="add()"><i class="glyphicon glyphicon-plus"></i> 添加</a>
		</shiro:hasPermission>
		<%--
		<button class="btn btn-white btn-sm " data-toggle="tooltip" data-placement="left" onclick="officeTable.refresh();" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
		--%>
	</div>
	
	<table id="treeTable" class="table table-striped table-bordered table-hover table-condensed dataTables-example dataTable">
		<thead>
			<tr>
				<th>机构名称</th>
				<th>归属区域</th>
				<th>机构编码</th>
				<th>机构类型</th>
				<th>序号</th>
				<shiro:hasPermission name="sys:office:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	</div>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a  href="#" onclick="jp.openDialogView('查看组织机构', '${ctx}/sys/office/form?viewFlag=set&id={{row.id}}','800px', '620px')">
				{{row.name}}
			</a></td>
			<td>
				{{row.area.name}}
			</td>
			<td>
				{{row.code}}
			</td>
			<td>
				{{dict.type}}
			</td>
			<td>
				{{row.sort}}
			</td>
			<td>
			<shiro:hasPermission name="sys:office:view">
				<a href="#" onclick="jp.openDialogView('查看组织机构', '${ctx}/sys/office/form?viewFlag=set&id={{row.id}}','800px', '620px')" class="btn btn-info btn-xs" ><i class="fa fa-search-plus"></i>  查看</a>
				</shiro:hasPermission>
			<shiro:hasPermission name="sys:office:edit">
   				<a href="#" onclick="jp.openDialog('修改组织机构', '${ctx}/sys/office/form?id={{row.id}}','800px', '620px', officeTable)" class="btn btn-success btn-xs" ><i class="fa fa-edit"></i> 修改</a>
   			</shiro:hasPermission>
   			<shiro:hasPermission name="sys:office:del">
				<a href="#" onclick="del('{{row.id}}');" class="btn btn-danger btn-xs" ><i class="fa fa-trash"></i> 删除</a>
			</shiro:hasPermission>
   			<shiro:hasPermission name="sys:office:add">
				<a href="#" onclick="jp.openDialog('添加下级组织机构', '${ctx}/sys/office/form?parent.id={{row.id}}','800px', '620px', officeTable)" class="btn btn-primary btn-xs" ><i class="fa fa-plus"></i> 添加下级组织机构</a>
			</shiro:hasPermission>
			</td>
		</tr>
	</script>
</body>
</html>