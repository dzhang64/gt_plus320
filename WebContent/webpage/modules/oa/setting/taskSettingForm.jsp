<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>节点权限管理</title>
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
					jp.post("${ctx}/oa/setting/taskSetting/save",$('#inputForm').serialize(),function(data){
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
			/* if ($("#viewFlag")) {
				var viewFlag = $("#viewFlag").val();
				if (viewFlag.indexOf("set") != -1) {
					$("fieldset[class='set']").attr("disabled","disabled");
				}
				if (viewFlag.indexOf("audit") != -1) {
					$("fieldset[class='audit']").attr("disabled","disabled");
				}
			} */
			//调用初始化方法
			selectedValueInit();
			ruleArgsInit();
		});
		/**
		 *	初始化方法，加载已选择select标签内全部option标签
		 */
		function selectedValueInit(){
			if ($('#settingValue').val() != null && $('#settingValue').val() != '') {
				var settingValues = eval('(' + $('#settingValue').val() + ')');
				for(var i = 0; i < settingValues.length; i++){
					$('#selectedValue').append(
						$('<option></option>').attr('id',settingValues[i].type + ':' + settingValues[i].id)
							.attr('class','selected')
							.attr('onclick','fillRemoveTempId(this)')
							.attr('ondblclick','removeSelectedValue(this)')
							.html(settingValues[i].name)
					);
				}
			}
		}
		/**
		 *	向已选择select标签内填充一个option标签，并填充一条json字符串
		 */
		function fillSelectedValues(obj){
			if ($('#selectedValue').html().indexOf($(obj).attr('id')) == -1) {
				$('#selectedValue').append(
					$('<option></option>').attr('id',$(obj).attr('id'))
						.attr('class', 'selected')
						.attr('onclick', 'fillRemoveTempId(this)')
						.attr('ondblclick', 'removeSelectedValue(this)')
						.html($(obj).val())
				);
				var settingValue = '';
				var newSettingValue = '{\"type\":\"' + $(obj).attr('id').split(':')[0]
					 + '\",\"id\":\"' +  $(obj).attr('id').split(':')[1]
					 + '\",\"name\":\"' +  $(obj).val()
					 + '\"}';
				var oldSettingValue = $('#settingValue').val();
				if (oldSettingValue != null && oldSettingValue != '') {
					oldSettingValue = oldSettingValue.substring(0, oldSettingValue.lastIndexOf(']'));
					settingValue = oldSettingValue + ',' + newSettingValue + ']';
				} else {
					settingValue = '[' + newSettingValue + ']';
				}
				$('#settingValue').val(settingValue);
			}
		}
		/**
		 *	从已选择select标签内移除一个option标签，并移除一条json字符串
		 */
		function removeSelectedValue(obj){
			var settingValue = '';
			var settingValues = eval('(' + $('#settingValue').val() + ')');
			if (settingValues.length > 1) {
				settingValue = '[';
				for(var i = 0; i < settingValues.length; i++){
					if(settingValues[i].id != $(obj).attr('id').split(':')[1]){
						settingValue += '{\"type\":\"' + settingValues[i].type
							 + '\",\"id\":\"' + settingValues[i].id
							 + '\",\"name\":\"' + settingValues[i].name
							 + '\"},';
					}
				}
				settingValue = settingValue.substring(0, settingValue.lastIndexOf(',')) + ']';
			}
			$('#settingValue').val(settingValue);
			$(obj).remove();
		}
		/**
		 *	向用户select标签内填充一组option标签
		 */
		function fillSelectedValueUsers(obj){
			$.ajax({
				type:'post',
				url:'${ctx}/oa/setting/taskSetting/getUsersByIdIndex',
				data:'objId=' + $(obj).attr('id'),
				dataType:'json',
				success:function(data){
					$(obj).parent().parent().parent().children('[class=\'users\']').children().html('');
					for (var i = 0; i < data.length; i++) {
						$(obj).parent().parent().parent().children('[class=\'users\']').children().append(
							$('<option></option>').attr('id', 'user:' + data[i].id)
								.attr('class', 'unselected')
								.attr('onclick', 'fillSelectedTempId(this)')
								.attr('ondblclick', 'fillSelectedValues(this)')
								.html(data[i].name)
						);
					}
				}
			});
		}
		/**
		 *	相对角色向已选择select标签内填充一个option前置方法，构造全新option对象
		 */
		function fillSelectedValuesForRelative(obj){
			if ($('#relativeTempId').val() != null && $('#relativeTempId').val() != '') {
				var relativeTempId = $('#relativeTempId').val();
				relativeTempId = relativeTempId.substring(0, relativeTempId.lastIndexOf('_'));
				fillSelectedValues(
					$('<option></option>').attr('id', relativeTempId + $(obj).attr('id'))
						.html($('#relativeTempName').val() + $(obj).val())
				);
			}
		}
		/**
		 *	1.向相对角色隐藏域内填充一条最后一次选择的相对角色信息，相对角色用户select标签构造全新option对象时使用
		 *	2.根据相对角色控制用户select标签显示或隐藏
		 */
		function fillRelativeTempValue(obj){
			$('#relativeTempId').val($(obj).attr('id'));
			$('#relativeTempName').val($(obj).val());
			/* if ($(obj).attr('id').indexOf('empty') != -1) {
				$('#optionSwitch').attr('style','display:none;');
			} else {
				$('#optionSwitch').attr('style','');
			} */
			if ($(obj).attr('id').indexOf('Org') == -1) {
				$('#optionSwitch1').attr('style','');
				$('#optionSwitch2').attr('style','display:none;');
			} else {
				$('#optionSwitch1').attr('style','display:none;');
				$('#optionSwitch2').attr('style','');
			}
		}
		/**
		 *	向当前选中隐藏域内填充一条最后一次选择的，非已选择select标签内option标签的option标签信息
		 */
		function fillSelectedTempId(obj){
			$('#selectedTempId').val($(obj).attr('id'));
		}
		/**
		 *	选择按钮向已选择select标签内填充一个option标签前置方法
		 */
		function fillSelectedValuesForButton(){
			$('option[id=\'' + $('#selectedTempId').val() + '\'][class=\'unselected\']').dblclick();
		}
		/**
		 *	向当前选中隐藏域内填充一条最后一次选择的，已选择select标签内option标签的option标签信息
		 */
		function fillRemoveTempId(obj){
			$('#removeTempId').val($(obj).attr('id'));
		}
		/**
		 *	选择按钮从已选择select标签内移除一个option标签前置方法
		 */		
		function removeSelectedValuesForButton(){
			$('option[id=\'' + $('#removeTempId').val() + '\'][class=\'selected\']').dblclick();
		}
		/**
		 *	初始化方法，加载已添加的规则变量
		 */
		function ruleArgsInit(){
			if($('#ruleArgs').val() != null && $('#ruleArgs').val()!=''){
				var ruleArgs = eval('(' + $('#ruleArgs').val() + ')');
				for(var i = 0; i < ruleArgs.length; i++){
					$('#ruleArgsTbody').append(
						$('<tr></tr>').attr('id', ruleArgs[i].key)
							.append(
								$('<td></td>').html(ruleArgs[i].key)
							).append(
								$('<td></td>').html(ruleArgs[i].value)
							).append(
								$('<td></td>').append(
									$('<a></a>').attr('class', 'btn btn-white btn-sm')
										.attr('title', '移除')
										.attr('onclick', 'ruleArgsRemove(this)')
										.append(
											$('<i></i>').attr('class', 'fa fa-minus')
												.html(' 移除')
										)
								)
							)
					);
				}
			}
		}
		/**
		 *	添加一条规则变量，添加一条json字符串
		 */
		function ruleArgsAdd(){
			if ($('#ruleArgsKey').val() != null && $('#ruleArgsKey').val().trim() != ''
				&& $('#ruleArgsValue').val() != null && $('#ruleArgsValue').val().trim() != ''
				&& $('tr#' + $('#ruleArgsKey').val()).attr('id') == undefined) {
				$('#ruleArgsTbody').append(
					$('<tr></tr>').attr('id', $('#ruleArgsKey').val())
						.append(
							$('<td></td>').html($('#ruleArgsKey').val())
						).append(
							$('<td></td>').html($('#ruleArgsValue').val())
						).append(
							$('<td></td>').append(
								$('<a></a>').attr('class', 'btn btn-white btn-sm')
									.attr('title', '移除')
									.attr('onclick', 'ruleArgsRemove(this)')
									.append(
										$('<i></i>').attr('class', 'fa fa-minus')
											.html(' 移除')
									)
							)
						)
				);
				var ruleArgs = '';
				var newRuleArgs = '{\"key\":\"' + $('#ruleArgsKey').val().trim()
					 + '\",\"value\":\"' +  $('#ruleArgsValue').val().trim()
					 + '\"}';
				var oldRuleArgs = $('#ruleArgs').val();
				if (oldRuleArgs != null && oldRuleArgs != '') {
					oldRuleArgs = oldRuleArgs.substring(0, oldRuleArgs.lastIndexOf(']'));
					ruleArgs = oldRuleArgs + ',' + newRuleArgs + ']';
				} else {
					ruleArgs = '[' + newRuleArgs + ']';
				}
				$('#ruleArgs').val(ruleArgs);
				//清空
				$('#ruleArgsKey').val('');
				$('#ruleArgsValue').val('');
			}
		}
		/**
		 *	移除一条规则变量，移除一条json字符串
		 */
		function ruleArgsRemove(obj){
			var ruleArg = '';
			var ruleArgs = eval('(' + $('#ruleArgs').val() + ')');
			if (ruleArgs.length > 1) {
				ruleArg = '[';
				for(var i = 0; i < ruleArgs.length; i++){
					if(ruleArgs[i].key != $(obj).parent().parent().attr('id')){
						ruleArg += '{\"key\":\"' + ruleArgs[i].key
							 + '\",\"value\":\"' + ruleArgs[i].value
							 + '\"},';
					}
				}
				ruleArg = ruleArg.substring(0, ruleArg.lastIndexOf(',')) + ']';
			}
			$('#ruleArgs').val(ruleArg);
			$(obj).parent().parent().remove();
		}
		/**
		 *	异步保存
		 */
		function ajaxSave(){
			$.ajax({
				type:'post',
				url:'${ctx}/oa/setting/taskSetting/save',
				data:$('#inputForm').serialize(),
				dataType:'json',
				success:function(data){
					if ($('#id').val() == null || $('#id').val().trim() == '') {
						$('#id').val(data.body.id);
					}
					layer.msg('保存节点权限成功', {icon:1});
				},
				error:function(data){
					layer.msg('保存节点权限失败', {icon:2});
				}
			});
		}
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="taskSetting" class="form-horizontal">
		<%-- <form:hidden path="id"/> --%>
		<input type="hidden" id="id" name="id" value="${taskSetting.id }"/>
		<input type="hidden" name="procDefKey" value="${taskSetting.procDefKey }"/>
		<input type="hidden" name="userTaskId" value="${taskSetting.userTaskId }"/>
		<input type="hidden" name="userTaskName" value="${taskSetting.userTaskName }"/>
		<input type="hidden" name="dirtyUpdateDate" value="${taskSetting.updateDate}" />
		<%-- <form:hidden path="viewFlag" /> --%>
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
		   		<tr>
		   			<td class="width-15 active"><label class="pull-right">流程定义：</label></td>
			
			        <td class="width-35" colspan="3">
						${taskSetting.procDefKey }
						<div class="layui-layer-btn pull-right"><a href="javascript:ajaxSave();" class="layui-layer-btn0">保存设置</a></div>
					</td>
					<!-- <td colspan="2">
						<div class="layui-layer-btn"><a href="javascript:ajaxSave();" class="layui-layer-btn0">保存</a></div>
					</td> -->
		   		</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">用户活动：</label></td>
			
			        <td class="width-35">
						${taskSetting.userTaskId }
					</td>
					<td class="width-15 active"><label class="pull-right">活动名称：</label></td>
			
			        <td class="width-35">
						${taskSetting.userTaskName }
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>权限类型：</label></td>
			
			        <td class="width-35">
			        	<script>
			        		function getTaskPermissionList(category) {
			        			$.ajax({
			        				type:'post',
			        				url:'${ctx}/oa/setting/taskSetting/getTaskPermissionList/' + category,
			        				dataType:'json',
			        				success:function(data){
			        					$('#permission').html('');
			        					var taskPermissionList = data.body.taskPermissionList;
			        					var id = $('#id').val();
			        					for (var i = 0; i < taskPermissionList.length; i++) {
			        						var option = $('<option></option>')
			        							.attr('value',taskPermissionList[i].id)
			        							.html(taskPermissionList[i].name);
			        						$('#permission').append(option);
			        					}
			        				}
			        			});
			        		}
			        	</script>
						<fieldset class="set">
						<form:select path="permissionType" class="form-control required" onchange="getTaskPermissionList(this.value)">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('oa_task_permission_category',taskSetting.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>节点权限：</label></td>
			
			        <td class="width-35">
			        	<select class="form-control" name="permission" id="permission">
			        		<c:forEach items="${taskPermissionList }" var="taskPermission">
			        			<option value="${taskPermission.id }"<c:if test="${taskSetting.permission==taskPermission.id }">selected="selected"</c:if>>${taskPermission.name }</option>
			        		</c:forEach>
			        	</select>
						<%-- <form:select path="permission" cssClass="form-control ">
							<form:option value=""></form:option>
							<form:options items="${fns:getDictList('act_permission')}" itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select> --%>
					</td>
					<!-- <td colspan="2"></td> -->
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>人员范围：</label></td>
					
					<td colspan="3">
						<div class="tabs-container" style="width: 60%; float: left;">
				            <ul class="nav nav-tabs">
								<li class="active"><a data-toggle="tab" href="#tab-1" aria-expanded="true">部门</a>
				                </li>
								<li class=""><a data-toggle="tab" href="#tab-2" aria-expanded="false">岗位</a>
				                </li>
								<li class=""><a data-toggle="tab" href="#tab-3" aria-expanded="false">职务</a>
				                </li>
								<li class=""><a data-toggle="tab" href="#tab-4" aria-expanded="false">相对角色</a>
				                </li>
				                <li class=""><a data-toggle="tab" href="#tab-5" aria-expanded="false">组织</a>
				                </li>
								<li class=""><a data-toggle="tab" href="#tab-6" aria-expanded="false">角色</a>
				                </li>
				            </ul>
            				<div class="tab-content">
								<div id="tab-1" class="tab-pane active">
									<div style="float: left; width: 50%;">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<c:forEach items="${officeList }" var="office">
												<option id="office:${office.id }" class="unselected" onclick="fillSelectedTempId(this),fillSelectedValueUsers(this);" ondblclick="fillSelectedValues(this);">${office.name }</option>
											</c:forEach>
										</select>
									</div>	
									<div style="float: left; width: 50%;" class="users">
										<select multiple="multiple" class="form-control " style="height: 240px;">
										</select>
									</div>
								</div>
								<div id="tab-2" class="tab-pane">
									<div style="float: left; width: 50%;">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<c:forEach items="${postList }" var="post">
												<option id="post:${post.id }" class="unselected" onclick="fillSelectedTempId(this),fillSelectedValueUsers(this);" ondblclick="fillSelectedValues(this);">${post.name }</option>
											</c:forEach>
										</select>
									</div>
									<div style="float: left; width: 50%;" class="users">
										<select multiple="multiple" class="form-control " style="height: 240px;">
										</select>
									</div>
								</div>
								<div id="tab-3" class="tab-pane">
									<div style="float: left; width: 50%;">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<c:forEach items="${levelList }" var="level">
												<option id="level:${level.id }" class="unselected" onclick="fillSelectedTempId(this),fillSelectedValueUsers(this);" ondblclick="fillSelectedValues(this);">${level.name }</option>
											</c:forEach>
										</select>
									</div>
									<div style="float: left; width: 50%;" class="users">
										<select multiple="multiple" class="form-control " style="height: 240px;">
										</select>
									</div>
								</div>
								<div id="tab-4" class="tab-pane">
									<input type="hidden" id="relativeTempId">
									<input type="hidden" id="relativeTempName">
									<div style="float: left; width: 50%;" class="relative">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<option id="relative:creater_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);" ondblclick="fillSelectedValues(this);">发起者</option>
											<option id="relative:pre_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">上节点</option>
											<%-- <option id="relative:empty_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);" ondblclick="fillSelectedValues(this);">空节点</option> --%>
											<option id="relative:createrSuperDept_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">发起者上级部门</option>
											<option id="relative:createrDeptMain_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">发起者主管各部门</option>
											<option id="relative:createrDeptOther_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">发起者分管各部门</option>
											<option id="relative:preSuperDept_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">上节点上级部门</option>
											<option id="relative:preDeptMain_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">上节点主管各部门</option>
											<option id="relative:preDeptOther_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">上节点分管各部门</option>

											<option id="relative:createrOrg_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">发起者所属组织</option>
											<option id="relative:createrOrgMain_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">发起者负责组织</option>
											<option id="relative:preOrg_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">上节点所属组织</option>
											<option id="relative:preOrgMain_" class="unselected" onclick="fillSelectedTempId(this),fillRelativeTempValue(this);">上节点负责组织</option>
										</select>
									</div>
									<div style="float: left; width: 50%;">
										<div id="optionSwitch1">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<option id="DeptMember" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">部门成员</option>
											<option id="DeptLeaderMain" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">部门主管</option>
											<option id="DeptLeaderOther" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">部门分管领导</option>
											<%-- 
											<option id="Admin" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">部门管理员</option>
											<option id="Clerk" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">部门公文收发员</option>
											--%>
										</select>
										</div>
										<div id="optionSwitch2" style="display: none;">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<option id="OrgMember" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">组织成员</option>
											<option id="OrgLeaderMain" class="unselected" onclick="fillSelectedTempId(this)" ondblclick="fillSelectedValuesForRelative(this);">组织负责人</option>
										</select>
										</div>
									</div>
								</div>
								<div id="tab-5" class="tab-pane">
									<div style="float: left; width: 50%;">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<c:forEach items="${orgList }" var="org">
												<option id="org:${org.id }" class="unselected" onclick="fillSelectedTempId(this),fillSelectedValueUsers(this);" ondblclick="fillSelectedValues(this);">${org.orgName }</option>
											</c:forEach>
										</select>
									</div>
									<div style="float: left; width: 50%;" class="users">
										<select multiple="multiple" class="form-control " style="height: 240px;">
										</select>
									</div>
								</div>
								<div id="tab-6" class="tab-pane">
									<div style="float: left; width: 50%;">
										<select multiple="multiple" class="form-control " style="height: 240px;">
											<c:forEach items="${roleList }" var="role">
												<option id="role:${role.id }" class="unselected" onclick="fillSelectedTempId(this),fillSelectedValueUsers(this);" ondblclick="fillSelectedValues(this);">${role.name }</option>
											</c:forEach>
										</select>
									</div>
									<div style="float: left; width: 50%;" class="users">
										<select multiple="multiple" class="form-control " style="height: 240px;">
										</select>
									</div>
								</div>
							</div>
						</div>
						<div class="tabs-container" style="width: 5%; float: left; text-align: center; padding-top: 120px;">
							<div>
								<input type="hidden" id="selectedTempId">
								<a onclick="fillSelectedValuesForButton();" class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left" title="选择"><i class="glyphicon glyphicon-chevron-right"></i></a>
							</div>
							<div>
								<input type="hidden" id="removeTempId">
								<a onclick="removeSelectedValuesForButton();" class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left" title="移除"><i class="glyphicon glyphicon-chevron-left"></i></a>
							</div>
						</div>
						<div class="tabs-container" style="width: 35%; float: left;">
				            <ul class="nav nav-tabs">
								<li><a>已选择</a></li>
				            </ul>
            				<div class="tab-content">
								<div id="tab-6" class="tab-pane active">
									<input type="hidden" id="settingValue" name="settingValue" value="${taskSetting.settingValue }">
									<select id="selectedValue" multiple="multiple" class="form-control " style="height: 240px;">
									</select>
								</div>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">规则变量：</label></td>
					
					<td colspan="3">
						<input type="hidden" id="ruleArgs" name="ruleArgs" value="${taskSetting.ruleArgs }">
						<table class="table table-striped table-bordered table-condensed">
							<thead>
								<tr>
									<th>键</th>
									<th>值</th>
									<th>操作</th>
								</tr>
							</thead>
							<tbody id="ruleArgsTbody">
								<tr>
									<td>
										<input id="ruleArgsKey" htmlEscape="false"    class="form-control "/>
									</td>
									<td>
										<input id="ruleArgsValue" htmlEscape="false"    class="form-control "/>
									</td>
									<td>
										<a class="btn btn-white btn-sm" onclick="ruleArgsAdd();" title="添加"><i class="fa fa-plus"></i> 添加</a>
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
		 	</tbody>
		</table>
		<%-- 
		<textarea id="settingValue" name="settingValue" cols="60" rows="3">${taskSetting.settingValue }</textarea>
		--%>
	</form:form>
	<div class="layui-layer-btn"><a href="javascript:ajaxSave();" class="layui-layer-btn0">保存设置</a></div>
</body>
</html>