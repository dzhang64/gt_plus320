<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>功能按钮管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		function doSubmit(){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		  if(validateForm.form()){
			  $("#inputForm").submit();
			  return true;
		  }
	
		  return false;
		}
		$(document).ready(function() {
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		function addRow(list, idx, tpl, row){
			$(list).append(Mustache.render(tpl, {
				idx: idx, delBtn: true, row: row
			}));
		}
		function delRow(obj, prefix){
			var id = $(prefix+"_id");
			var delFlag = $(prefix+"_delFlag");
			if (id.val() == ""){
				$(obj).parent().parent().remove();
			}else if(delFlag.val() == "0"){
				delFlag.val("1");
				$(obj).html("&divide;").attr("title", "撤销删除");
				$(obj).parent().parent().addClass("error");
			}else if(delFlag.val() == "1"){
				delFlag.val("0");
				$(obj).html("&times;").attr("title", "删除");
				$(obj).parent().parent().removeClass("error");
			}
		}
	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="menu" action="${ctx}/sys/menu/saveMenuFunctions" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">菜单名称：</label></td>
					<td class="width-35">
						${menu.name}
					</td>
					<td class="width-15 active"><label class="pull-right">链接：</label></td>
					<td class="width-35">
						${menu.href}
					</td>
				</tr>
		 	</tbody>
		</table>
		<div class="tabs-container">
            <ul class="nav nav-tabs">
				<li class="active"><a data-toggle="tab" href="#tab-1" aria-expanded="true">功能按钮：</a>
                </li>
            </ul>
            <div class="tab-content">
				<div id="tab-1" class="tab-pane active">
			<a class="btn btn-white btn-sm" onclick="addRow('#children', childRowIdx, childTpl);childRowIdx = childRowIdx + 1;" title="添加"><i class="fa fa-plus"></i> 添加</a>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th class="hide"></th>
						<th>名称</th>
						<th>排序</th>
						<th>权限标识</th>
						<th width="10"></th>
					</tr>
				</thead>
				<tbody id="children">
				</tbody>
			</table>
			<script type="text/template" id="childTpl">//<!--
				<tr id="children{{idx}}">
					<td class="hide">
						<input id="children{{idx}}_id" name="children[{{idx}}].id" type="hidden" value="{{row.id}}"/>
						<input id="children{{idx}}_delFlag" name="children[{{idx}}].delFlag" type="hidden" value="0"/>
					</td>
					<td>
						<input id="children{{idx}}_name" name="children[{{idx}}].name" type="text" value="{{row.name}}"  class="form-control required"/>
					</td>
					<td>
						<input id="children{{idx}}_sort" name="children[{{idx}}].sort" type="text" value="{{row.sort}}"  class="form-control required"/>
					</td>
					<td>
						<input id="children{{idx}}_permission" name="children[{{idx}}].permission" type="text" value="{{row.permission}}"  class="form-control required"/>
					</td>
					<td class="text-center">
						{{#delBtn}}<span class="close" onclick="delRow(this, '#children{{idx}}')" title="删除">&times;</span>{{/delBtn}}
					</td>
				</tr>//-->
			</script>
			<script type="text/javascript">
				var childRowIdx = 0, childTpl = $("#childTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
				$(document).ready(function() {
					var data = ${fns:toJson(menu.children)};
					for (var i=0; i<data.length; i++){
						addRow('#children', childRowIdx, childTpl, data[i]);
						childRowIdx = childRowIdx + 1;
					}
				});
			</script>
			</div>
		</div>
		</div>
	</form:form>
</body>
</html>