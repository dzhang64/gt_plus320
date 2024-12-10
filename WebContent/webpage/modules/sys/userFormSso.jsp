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
		function addRow(list, idx, tpl, row){
			if (tpl=="select") {
				tpl = $("#userSsoSubsystemStartTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"")
					+ $("#userSsoSubsystemSelectTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"")
					+ $("#userSsoSubsystemEndTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			} else if (tpl=="input") {
				tpl = $("#userSsoSubsystemStartTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"")
					+ $("#userSsoSubsystemInputTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"")
					+ $("#userSsoSubsystemEndTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			}
			$(list).append(Mustache.render(tpl, {
				idx: idx, delBtn: true, row: row
			}));
			$(list+idx).find("select").each(function(){
				$(this).val($(this).attr("data-value"));
			});
			$(list+idx).find("input[type='checkbox'], input[type='radio']").each(function(){
				var ss = $(this).attr("data-value").split(',');
				for (var i=0; i<ss.length; i++){
					if($(this).val() == ss[i]){
						$(this).attr("checked","checked");
					}
				}
			});
		}
		function delRow(obj, prefix){
			var id = $(prefix+"_id");
			var delFlag = $(prefix+"_delFlag");
			if (id.val() == ""){
				$(obj).parent().parent().remove();
				makeUserSsoSubsystemTemp();
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
		      <%-- <tr>
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
		      </c:if> --%>
			</tbody>
		</table>
			<div class="tabs-container">
				<ul class="nav nav-tabs">
					<li class="active"><a data-toggle="tab" href="#tab-1"
						aria-expanded="true">SSO子系统：</a></li>
				</ul>
				<div class="tab-content">
					<div id="tab-1" class="tab-pane active">
						<a class="btn btn-white btn-sm"
							onclick="addRow('#userSsoSubsystemList', userSsoSubsystemRowIdx, 'select');userSsoSubsystemRowIdx = userSsoSubsystemRowIdx + 1;"
							title="添加"><i class="fa fa-plus"></i> 添加</a>
						<table id="contentTable"
							class="table table-striped table-bordered table-condensed">
							<thead>
								<tr>
									<th class="hide"></th>
									<th>系统名称</th>
									<!-- <th>归属单位</th>
									<th>请求地址</th>
									<th>是否可用</th>
									<th>是否允许同步密码</th> -->
									<th>用户名</th>
									<th>密码</th>
									<th>是否SSO登录</th>
									<th width="10">&nbsp;</th>
								</tr>
							</thead>
							<tbody id="userSsoSubsystemList">
							</tbody>
						</table>
				<script type="text/template" id="userSsoSubsystemStartTpl">//<!--
				<tr id="userSsoSubsystemList{{idx}}">
					<td class="hide">
						<input id="userSsoSubsystemList{{idx}}_id" name="userSsoSubsystemList[{{idx}}].id" type="hidden" value="{{row.id}}"/>
						<input id="userSsoSubsystemList{{idx}}_delFlag" name="userSsoSubsystemList[{{idx}}].delFlag" type="hidden" value="0"/>
						<input id="userSsoSubsystemList{{idx}}_userId" name="userSsoSubsystemList[{{idx}}].userId" type="hidden" value="${user.id}"/>
					</td>
					<td>//-->
				</script>
				<script type="text/template" id="userSsoSubsystemSelectTpl">//<!--
						<select id="select{{idx}}" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.id" class="selectSsoId form-control" onchange="checkSsoId(this);">
							<option></option>
							<c:forEach items="${ssolist}" var="sso">
								<option id="${sso.ssoSubsystem.id}" value="${sso.ssoSubsystem.id}">${sso.ssoSubsystem.name}</option>
							</c:forEach>
						</select>//-->
				</script>
				<script type="text/template" id="userSsoSubsystemInputTpl">//<!--
						<input type="hidden" id="userSsoSubsystemList{{idx}}_ssoSubsystem.id" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.id" value="{{row.ssoSubsystem.id}}"    class="selectSsoId form-control"/>
						<input disabled="disabled" id="userSsoSubsystemList{{idx}}_ssoSubsystem.name" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.name" type="text" value="{{row.ssoSubsystem.name}}"    class="form-control"/>
					//-->
				</script>
				<script type="text/template" id="userSsoSubsystemEndTpl">//<!--
				</td>

					<%-- 
					<td  class="max-width-250">
						<input disabled="disabled" id="userSsoSubsystemList{{idx}}_ssoSubsystem.org.name" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.org.name" type="text" value="{{row.ssoSubsystem.org.name}}"    class="form-control"/>
					</td>

					<td>
						<input disabled="disabled" id="userSsoSubsystemList{{idx}}_ssoSubsystem.url" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.url" type="text" value="{{row.ssoSubsystem.url}}"    class="form-control"/>
					</td>

					<td>
						
						<select id="userSsoSubsystemList{{idx}}_ssoSubsystem.isInuse" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.isInuse" data-value="{{row.ssoSubsystem.isInuse}}" class="form-control m-b  ">
							<option value=""></option>
							<c:forEach items="${fns:getDictListEdit('yes_no',row.userSsoSubsystemList.ssoSubsystem.id)}" var="dict">
								<option value="${dict.value}">${dict.label}</option>
							</c:forEach>
						</select>
						<input disabled="disabled" id="userSsoSubsystemList{{idx}}_ssoSubsystem.isInuse" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.isInuse" type="text" value="${fns:getDictLabel(row.ssoSubsystem.isInuse, 'yes_no', '')}"    class="form-control"/>
					</td>

					<td>
						<select id="userSsoSubsystemList{{idx}}_ssoSubsystem.isSync" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.isSync" data-value="{{row.ssoSubsystem.isSync}}" class="form-control m-b  ">
							<option value=""></option>
							<c:forEach items="${fns:getDictListEdit('yes_no',row.userSsoSubsystemList.ssoSubsystem.id)}" var="dict">
								<option value="${dict.value}">${dict.label}</option>
							</c:forEach>
						</select>
						<input disabled="disabled" id="userSsoSubsystemList{{idx}}_ssoSubsystem.isSync" name="userSsoSubsystemList[{{idx}}].ssoSubsystem.isSync" type="text" value="${fns:getDictLabel(row.ssoSubsystem.isSync, 'yes_no', '')}"    class="form-control"/>
					</td>
					--%>

					<td>
						<input id="userSsoSubsystemList{{idx}}_loginName" name="userSsoSubsystemList[{{idx}}].loginName" type="text" value="{{row.loginName}}"    class="form-control"/>
					</td>

					<td>
						<input id="userSsoSubsystemList{{idx}}_password" name="userSsoSubsystemList[{{idx}}].password" type="text" value="{{row.password}}"    class="form-control"/>
					</td>

					<td>
						<select id="userSsoSubsystemList{{idx}}_isAllow" name="userSsoSubsystemList[{{idx}}].isAllow" data-value="{{row.isAllow}}" class="form-control m-b  ">
							<option value=""></option>
							<c:forEach items="${fns:getDictListEdit('yes_no',row.userSsoSubsystemList.id)}" var="dict">
								<option value="${dict.value}">${dict.label}</option>
							</c:forEach>
						</select>
						<%--
						<input id="userSsoSubsystemList{{idx}}_isAllow" name="userSsoSubsystemList[{{idx}}].isAllow" type="text" value="{{row.isAllow}}"    class="form-control"/>
						--%>
					</td>
					<td class="text-center" width="10">
						{{#delBtn}}<span class="close" onclick="delRow(this, '#userSsoSubsystemList{{idx}}')" title="删除">&times;</span>{{/delBtn}}
					</td>
				</tr>//-->
		</script>
		<script type="text/javascript">
			var userSsoSubsystemRowIdx = 0, userSsoSubsystemTpl = "input";
			//获取全局存在的子系统
			var userSsoSubsystemTemp = '';
			var userSsoSubsystem = '';
			$(document).ready(function() {
				var data = ${fns:toJson(user.userSsoSubsystemList)};
				for (var i=0; i<data.length; i++){
					addRow('#userSsoSubsystemList', userSsoSubsystemRowIdx, userSsoSubsystemTpl, data[i]);
					userSsoSubsystemRowIdx = userSsoSubsystemRowIdx + 1;
				}
				makeUserSsoSubsystemTemp();
			});
			function checkSsoId(obj){
				var arr = userSsoSubsystem.split(',');
				for (var i = 0; i < arr.length; i++) {
					if (arr[i] == $(obj).val()) {
						jp.error('当前子系统已存在');
						$(obj).val('');
						$(obj).parent().parent().remove();
						userSsoSubsystemRowIdx = userSsoSubsystemRowIdx - 1;
					}
				}
				makeUserSsoSubsystemTemp();
			}
			function makeUserSsoSubsystemTemp(){
				userSsoSubsystemTemp = $('.selectSsoId');
				userSsoSubsystem = '';
				for (var i = 0; i < userSsoSubsystemTemp.length; i++) {
					if (userSsoSubsystem != '') {
						userSsoSubsystem = userSsoSubsystem + ',' + userSsoSubsystemTemp[i].value;
					} else {
						userSsoSubsystem = userSsoSubsystem + userSsoSubsystemTemp[i].value;
					}
				}
			}
		</script>
					</div>
		</fieldset>
	</form:form>
</body>
</html>