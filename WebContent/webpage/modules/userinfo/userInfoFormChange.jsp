<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>员工信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table, index, pName, pValue){
			if(validateForm.form()){
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
					jp.post("${ctx}/userinfo/userInfo/save",$('#inputForm').serialize(),function(data){
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

		//gridselect选择后执行
		function afterGridSelect(callbackKey, item) {
		}
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="userInfo" class="form-horizontal">
		<form:hidden path="id"/>

		<input type="hidden" name="dirtyUpdateDate" value="${userInfo.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">姓名：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control " readonly="true" style="background-color:#F8F8F8;" />
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">身份证编号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="idCardNum" htmlEscape="false"    class="form-control "  readonly="true" style="background-color:#F8F8F8;" />
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">照片：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="photo" name="photo" value='${userInfo.photo}' />
						<sys:gtview input="photo" uploadPath="/userinfo/userInfo"></sys:gtview>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">手机号码：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="tel" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				<td class="width-15 active"><label class="pull-right">个人邮箱：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="ownEmail" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">紧急联系人：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="emergencyUserName" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">紧急联系人关系：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="emergencyUserRelation" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">紧急联系人电话：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="emergencyUserPhone" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">通信地址：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="address" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">毕业证：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="gradeCertificate" name="gradeCertificate" value='${userInfo.gradeCertificate}' />
						<sys:gtfinder input="gradeCertificate" uploadPath="/userinfo/userInfo"></sys:gtfinder>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">身份证：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="idCardFile" name="idCardFile" value='${userInfo.idCardFile}' />
						<sys:gtfinder input="idCardFile" uploadPath="/userinfo/userInfo"></sys:gtfinder>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">人员简历：</label></td>
			
					<td class="width-35" colspan="3">
						
						<input type="hidden" id="personResume" name="personResume" value='${userInfo.personResume}' />
						<sys:gtfinder input="personResume" uploadPath="/userinfo/userInfo"></sys:gtfinder>
					</td>
				</tr>
		 	</tbody>
		</table>
		
	</form:form>
</body>
</html>