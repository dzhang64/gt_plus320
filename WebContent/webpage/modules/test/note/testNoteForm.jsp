<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>富文本测试管理</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/plugin/summernote/summernote.css" rel="stylesheet">
	<link href="${ctxStatic}/plugin/summernote/summernote-bs3.css" rel="stylesheet">
	<script src="${ctxStatic}/plugin/summernote/summernote.min.js"></script>
	<script src="${ctxStatic}/plugin/summernote/summernote-zh-CN.js"></script>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
		  if(validateForm.form()){
			  $("#contents").val($("#rich_contents").next().find(".note-editable").html());//取富文本的值
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
					jp.post("${ctx}/test/note/testNote/save",$('#inputForm').serialize(),function(data){
						if(data.success){
	                    	$table.bootstrapTable('refresh');
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
			
				//富文本初始化
			$('#rich_contents').summernote({
                lang: 'zh-CN',
                height: 250
            });
			$("#rich_contents").next().find(".note-editable").html(  $("#contents").val());
			$("#rich_contents").next().find(".note-editable").html(  $("#rich_contents").next().find(".note-editable").text());
		});
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="testNote" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${testNote.updateDate}" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">标题：</label></td>
			
					<td class="width-35" colspan="3">
			
						<form:input path="title" htmlEscape="false"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
			
					<td class="width-35" colspan="3">
			
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">内容：</label></td>
			
					<td class="width-35" colspan="3">
			
						<form:hidden path="contents"/>
						<div id="rich_contents">
                           

                        </div>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>