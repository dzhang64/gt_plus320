<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		function doSubmit(table, index){//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
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
			$("#no").focus();
			validateForm = $("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')}//设置了远程验证，在初始化时必须预先调用一次。
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"},
					confirmNewSsoDesPassword: {equalTo: "输入与上面相同的密码"}
				},
				submitHandler: function(form){
					jp.post("${ctx}/sys/user/save",$('#inputForm').serialize(),function(data){
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

			//在ready函数中预先调用一次远程校验函数，是一个无奈的回避案。
			//否则打开修改对话框，不做任何更改直接submit,这时再触发远程校验，耗时较长，
			//submit函数在等待远程校验结果然后再提交，而layer对话框不会阻塞会直接关闭同时会销毁表单，因此submit没有提交就被销毁了导致提交表单失败。
			$("#inputForm").validate().element($("#loginName"));
			
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
	<form:form id="inputForm" modelAttribute="user" class="form-horizontal">
		<fieldset class="set">
		<form:hidden path="id"/>
		<input type="hidden" name="dirtyUpdateDate" value="${user.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
		   	  <tr>
		         <td class="width-15 active">
		         	<label class="pull-right"><font color="red">*</font>归属单位：</label>
		         </td>
		         <td class="width-35">
		         	<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
					title="公司" url="/sys/office/treeData?type=1" cssClass="form-control required"/>
				 </td>
		         <td  class="width-15 active">
		         	<label class="pull-right"><font color="red">*</font>归属部门：</label></td>
		         <td class="width-35">
		         	<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
					title="部门" url="/sys/office/treeData?type=2" cssClass="form-control required" notAllowSelectParent="true"/>
				 </td>
		      </tr>
		      <tr>
		         <td class="width-15 active">
		         	<label class="pull-right"><font color="red">*</font>用户编号：</label>
		         </td>
		         <td class="width-35">
		         	<form:input path="no" htmlEscape="false" maxlength="50" class="form-control required"/>
		         </td>
		         <td  class="width-15 active">	
		         	<label class="pull-right"><font color="red">*</font>姓名：</label>
		         </td>
		         <td class="width-35">
		         	<form:input path="name" htmlEscape="false" maxlength="50" class="form-control required"/>
		         </td>
		      </tr>
		      <tr>
		         <td class="width-15 active">
		         	<label class="pull-right"><font color="red">*</font>登录名：</label>
		         </td>
		         <td class="width-35">
		         	<input id="oldLoginName" name="oldLoginName" type="hidden" value="${user.loginName}">
					<form:input path="loginName" htmlEscape="false" maxlength="50" class="form-control required userName"/>
		         </td>
		      	 <td  class="width-15 active">	
		         	<label class="pull-right">邮箱：</label>
		         </td>
		         <td class="width-35">
		         	<form:input path="email" htmlEscape="false" maxlength="100" class="form-control email"/>
		         </td>
		      </tr>
		      <tr>
		         <td class="active"><label class="pull-right"><c:if test="${empty user.id}"><font color="red">*</font></c:if>密码：</label></td>
		         <td><input id="newPassword" name="newPassword" type="password" value="" maxlength="50" minlength="3" class="form-control ${empty user.id?'required':''}"/>
					<c:if test="${not empty user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if></td>
		         <td class="active"><label class="pull-right"><c:if test="${empty user.id}"><font color="red">*</font></c:if>确认密码：</label></td>
		         <td><input id="confirmNewPassword" name="confirmNewPassword" type="password"  class="form-control ${empty user.id?'required':''}" value="" maxlength="50" minlength="3" equalTo="#newPassword"/></td>
		      </tr>
		      <tr>
		         <td class="active"><label class="pull-right"><c:if test="${empty user.id}"><font color="red">*</font></c:if>SSO密码：</label></td>
		         <td><input id="newSsoDesPassword" name="newSsoDesPassword" type="password" value="" maxlength="50" minlength="3" class="form-control ${empty user.id?'required':''}"/>
					<c:if test="${not empty user.id}"><span class="help-inline">若不修改SSO密码，请留空。</span></c:if></td>
		         <td class="active"><label class="pull-right"><c:if test="${empty user.id}"><font color="red">*</font></c:if>确认SSO密码：</label></td>
		         <td><input id="confirmNewSsoDesPassword" name="confirmNewSsoDesPassword" type="password"  class="form-control ${empty user.id?'required':''}" value="" maxlength="50" minlength="3" equalTo="#newSsoDesPassword"/></td>
		      </tr>
		      <tr>
		         <td class="active"><label class="pull-right">电话：</label></td>
		         <td><form:input path="phone" htmlEscape="false" maxlength="100" class="form-control"/></td>
		         <td class="active"><label class="pull-right">手机：</label></td>
		         <td><form:input path="mobile" htmlEscape="false" maxlength="100" class="form-control"/></td>
		      </tr>
		      <tr>
		        <td class="active"><label class="pull-right">是否允许登录：</label></td>
		        <td><form:select path="loginFlag"  class="form-control">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select></td>
				<td class="active"><label class="pull-right">允许SSO登录：</label></td>
		        <td><form:select path="ssoLoginFlag"  class="form-control">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				  </form:select>
				</td>
		      </tr>
		      <tr>
		         <td class="active"><label class="pull-right">用户类型：</label></td>
		         <td><form:select path="userType"  class="form-control">
					<form:option value="" label=""/>
						<form:options items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select></td>
				 <td class="active"><label class="pull-right"><font color="red">*</font>职务：</label></td>
		         <td><form:select path="level"  class="form-control required">
					<form:option value="" label=""/>
						<form:options items="${levelList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
					</form:select></td>
			  </tr>
			  <tr>
		         <td class="active"><label class="pull-right">主岗：</label></td>
		         <td><form:select path="post"  class="form-control">
					<form:option value="" label=""/>
						<form:options items="${postList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
					</form:select></td>
				 <td class="active"><label class="pull-right">副岗：</label></td>
		         <td><form:select path="partPost"  class="form-control">
					<form:option value="" label=""/>
						<form:options items="${postList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
					</form:select></td>
			  </tr>
			  <tr>
		         <td class="active"><label class="pull-right"><font color="red">*</font>状态：</label></td>
		         <td><form:select path="useable"  class="form-control required">
					<form:option value="" label=""/>
						<form:options items="${fns:getDictList('sys_useable')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select></td>
				 <td class="active"><label class="pull-right">排序号：</label></td>
		         <td><form:input path="sort" htmlEscape="false" class="form-control digits"/></td>
			  </tr>
			  <tr>
		         <td class="active"><label class="pull-right"><font color="red">*</font>用户角色：</label></td>
		         <td colspan="3">
		         	<form:checkboxes path="roleIdList" items="${allRoles}" itemLabel="name" itemValue="id" htmlEscape="false" cssClass="i-checks required"/>
		         	<label id="roleIdList-error" class="error" for="roleIdList"></label>
		         </td>
		      </tr>
		      <tr>
				 <td class="active"><label class="pull-right">备注：</label></td>
		         <td colspan="3"><form:textarea path="remarks" htmlEscape="false" rows="2" class="form-control "/></td>
			  </tr>
		      <tr>
		      </tr>
		      <c:if test="${not empty user.id}">
		        <tr>
		          <td class=""><label class="pull-right">创建时间：</label></td>
		          <td><span class="lbl"><fmt:formatDate value="${user.createDate}" type="both" dateStyle="full"/></span></td>
		          <td class=""><label class="pull-right">最后登录：</label></td>
		          <td><span class="lbl">IP: ${user.loginIp}&nbsp;&nbsp;&nbsp;&nbsp;时间：<fmt:formatDate value="${user.loginDate}" type="both" dateStyle="full"/></span></td>
		        </tr>
		      </c:if>
			</tbody>
		</table>
		</fieldset>
	</form:form>
</body>
</html>