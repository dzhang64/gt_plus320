<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>附件测试管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
			if(validateForm.form()){
				var defaultPValue = '10';
		    	if (!pName && !pValue) {
					//添加
				} else {
					//保存，办理
					if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
				}
		      
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
					jp.post("${ctx}/test/testwf/testWf/save",$('#inputForm').serialize(),function(data){
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
			
			laydate({
				elem: '#submitDate', //目标元素。由于laydate.js封装了一个轻量级的选择器引擎，因此elem还允许你传入class、tag但必须按照这种方式 '#id .class'
				event: 'focus' //响应事件。如果没有传入event，则按照默认的click
			});
			//控制Form组件是否可编辑
			if ($("#viewFlag")) {
				var viewFlag = $("#viewFlag").val();
				if (viewFlag.indexOf("set") != -1) {
					$("fieldset[class='set']").attr("disabled","disabled");
				}
				if (viewFlag.indexOf("audit") != -1) {
					$("fieldset[class='audit']").attr("disabled","disabled");
				}
			}
		});
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="testWf" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${testWf.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		
		<a class="btn btn-success btn-sm" onclick="openContent();" style="margin-top: 10px;">正文</a>
		<script>
			function openContent(){
				window.open("${ctx }/test/testwf/testWf/content", "正文", "", false);
			}
		</script>
		
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>名称：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">提交日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="submitDate" name="submitDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${testWf.submitDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">附件1：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="files1" name="files1" value='${testWf.files1}' />
						<sys:gtfinder input="files1" uploadPath="/test/testwf/testWf"></sys:gtfinder>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">附件2：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="files2" name="files2" value='${testWf.files2}' />
						<sys:gtfinderpdf input="files2" uploadPath="/test/testwf/testWf"></sys:gtfinderpdf>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">状态：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:select path="status" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('yes_no',testWf.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">审核信息：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="audits" htmlEscape="false" rows="4"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
		 	</tbody>
		</table>
	</form:form>
</body>
</html>