<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>预算条目字典管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $projectBgEntryTreeTable; // 父页面table表格id
		var $topIndex;//openDialog的 dialog index
		function doSubmit(treeTable, index, pName, pValue){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
			if(validateForm.form()){
				var defaultPValue = '10';
				if (!pName && !pValue) {
					//添加
				} else {
					//保存，办理
					if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
				}
				$projectBgEntryTreeTable = treeTable;
				$topIndex = index;
				jp.loading();
				$("#inputForm").submit();
				return true;
			}
			return false;
		}
		
		$(document).ready(function() {
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/projectbg/bgentry/projectBgEntry/save",$('#inputForm').serialize(),function(data){
						if(data.success){
							var current_id = data.body.projectBgEntry.id;
							var target = $projectBgEntryTreeTable.get(current_id);
							var old_parent_id = target.attr("pid") == undefined?'1':target.attr("pid");
							var current_parent_id = data.body.projectBgEntry.parentId;
							var current_parent_ids = data.body.projectBgEntry.parentIds;
		                    	
							if(old_parent_id == current_parent_id){
								if(current_parent_id == '0'){
									$projectBgEntryTreeTable.refreshPoint(-1);
								}else{
									$projectBgEntryTreeTable.refreshPoint(current_parent_id);
								}
							}else{
								$projectBgEntryTreeTable.del(current_id);//刷新删除旧节点
								$projectBgEntryTreeTable.initParents(current_parent_ids, "0");
							}
							jp.success(data.msg);
						}else {
							jp.error(data.msg);
						}
						jp.close($topIndex);//关闭dialog
					});
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
			
			//控制Form组件是否可编辑
			if ($("#viewFlag")) {
				var viewFlag = $("#viewFlag").val();
				if (viewFlag.indexOf("set") != -1) $("fieldset[class='set']").attr("disabled","disabled");
				if (viewFlag.indexOf("audit") != -1) $("fieldset[class='audit']").attr("disabled","disabled");
			}
		});
	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="projectBgEntry" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${projectBgEntry.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>
		<table class="table table-bordered table-condensed dataTables-example dataTable no-footer">
			<tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>上级：</label></td>
				    <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="parent" name="parent.id" value="${projectBgEntry.parent.id}" labelName="parent.name" labelValue="${projectBgEntry.parent.name}"
							title="上级" url="/projectbg/bgentry/projectBgEntry/treeData" extId="${projectBgEntry.id}" cssClass="form-control " allowClear="true"/>
							</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>名称：</label></td>
				    <td class="width-35">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control required"/>
							</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>排序：</label></td>
				    <td class="width-35">
						<fieldset class="set">
						<form:input path="sort" htmlEscape="false"    class="form-control required"/>
							</fieldset>
					</td>
			</tbody>
		</table>
		</form:form>
</body>
</html>