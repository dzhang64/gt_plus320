<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
		  if(validateForm.form()){
			  var defaultPValue = '10';
			  if (pName && $("#" + pName)) {
		      	$("#" + pName).val(defaultPValue);
		      } else {
		      	if ($("#status") && $("#status").val() == '') $("#status").val(defaultPValue);
		      }
		      if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
		      
			  $table = table;
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
					jp.post("${ctx}/test/tree/testTree/save",$('#inputForm').serialize(),function(data){
						if(data.success){
	                    	$table.refresh();
	                    	jp.success(data.msg);
	                    	jp.close($topIndex);//关闭dialog
	                    }else{
            	  			jp.error(data.msg);
	                    }
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
			
		});
	</script>
</head>
<body class="hideScroll">
	<form:form id="inputForm" modelAttribute="testTree" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${testTree.updateDate}" />
		<sys:message content="${message}"/>
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
			<tr>
				<td class="width-15 active"><label class="pull-right"><font color="red">*</font>名称：</label></td>
			    <td class="width-35">
					<form:input path="name" htmlEscape="false"    class="form-control required"/>
				</td>
				<td class="width-15 active"><label class="pull-right"><font color="red">*</font>上级父级编号：</label></td>
			    <td class="width-35">
					<sys:treeselect id="parent" name="parent.id" value="${testTree.parent.id}" labelName="parent.name" labelValue="${testTree.parent.name}"
						title="父级编号" url="/test/tree/testTree/treeData" extId="${testTree.id}" cssClass="form-control " allowClear="true"/>
				</td>
			</tr>
			<tr>
				<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
				<td class="width-35" colspan="3">
					<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
				</td>
			</tr>
		</tbody>
	</table>
	</form:form>
</body>
</html>