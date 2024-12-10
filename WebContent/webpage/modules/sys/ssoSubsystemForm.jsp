<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>SSO子系统管理</title>
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
					jp.post("${ctx}/sys/ssoSubsystem/save",$('#inputForm').serialize(),function(data){
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
			<%--
			//callbackKey，区分多个gridselect
			//if(callbackKey == 'gridselect_01') {
			//
			//} else if (callbackKey == 'gridselect_02') {
			//
			//}
			--%>
		}
		function makeUserList(userList, type){
			userList = eval('('+ userList + ')');
			if (userList != null && type=='single') {
				var select = $('<select></select>').attr('id', 'userSelect')
					.attr('class', 'form-control')
					.attr('onchange', '$(\'#tempLoginName\').val(this.value)')
					.append(
						$('<option></option>').attr('selected', 'selected')
					);
				for (var i = 0; i < userList.length; i++) {
					select.append(
						$('<option></option>').val(userList[i].loginName)
							.html(userList[i].name)
					);
				}
				$('#userSelectDiv').html('')
					.attr('style', '')
					.attr('class', 'modal-body')
					.append(select);
			} else if (userList != null && type=='multi') {
				//构造多选select
				var unselect = $('<select></select>').attr('id', 'unselect')
					.attr('class', 'form-control')
					.attr('style', 'height: 200px;')
					.attr('multiple','multiple');
				for (var i = 0; i < userList.length; i++) {
					unselect.append(
						$('<option></option>').val(userList[i].loginName)
							.attr('id', userList[i].loginName)
							.attr('class', 'unselected')
							.attr('onclick', '$("#selectedTempId").val(this.value)')
							.attr('ondblclick', 'fillSelected(this);')
							.html(userList[i].name)
					);
				}
				var selected = $('<select></select>').attr('id', 'selected_')
					.attr('class', 'form-control')
					.attr('style', 'height: 200px;')
					.attr('multiple','multiple');
				var selectBtn = $('<div></div>').attr('style', 'float: left; width: 8%; margin: 60px 0px 0px 2%;')
					.append(
						$('<a></a>').attr('onclick', 'fillSelectedValuesForButton()')
							.attr('class', 'btn btn-white btn-sm')
							.attr('data-toggle', 'tooltip')
							.attr('data-placement', 'left')
							.attr('title', '选择')
							.append(
								$('<i></i>').attr('class', 'glyphicon glyphicon-chevron-right')
							)
					)
					.append(
						$('<a></a>').attr('onclick', 'removeSelectedValuesForButton()')
							.attr('class', 'btn btn-white btn-sm')
							.attr('data-toggle', 'tooltip')
							.attr('data-placement', 'left')
							.attr('title', '移除')
							.append(
								$('<i></i>').attr('class', 'glyphicon glyphicon-chevron-left')
							)
					);
				$('#userSelectDiv').html('')
					.attr('style', 'height: 240px;')
					.attr('class', 'modal-body');
				$('#userSelectDiv').append(
					$('<div></div>').attr('style', 'float: left; width: 45%;')
						.append(unselect)
					)
					.append(selectBtn)
					.append(
						$('<div></div>').attr('style', 'float: left; width: 45%;')
							.append(selected)
					);
			}
		}
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="ssoSubsystem" class="form-horizontal">
		<form:hidden path="id"/>
		
		
		<input type="hidden" name="dirtyUpdateDate" value="${ssoSubsystem.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">系统名称：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">归属单位：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="org" name="org.id" value="${ssoSubsystem.org.id}" labelName="org.name" labelValue="${ssoSubsystem.org.name}"
							title="部门" url="/sys/office/treeData?type=2" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">请求地址：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="url" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">CA地址：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="caUrl" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">是否可用：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="isInuse" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('yes_no',ssoSubsystem.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">是否允许同步密码：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="isSync" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('yes_no',ssoSubsystem.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">KEY：</label></td>
			
			        <td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="key" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
		 	</tbody>
		</table>
		
	</form:form>
</body>
</html>