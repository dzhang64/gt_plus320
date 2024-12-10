<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>act工作流管理</title>
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
				getUserList(pValue);
			}
			return false;
		}
		
		function realSubmit(){
			if(validateForm.form()){
				jp.loading();
				$("#inputForm").submit();
				return true;
			}
			return false;
		}
		
		$(document).ready(function() { 
			validateForm = $("#inputForm").validate({
				submitHandler: function(form){
					jp.post("${ctx}/oa/test/actFlow/save",$('#inputForm').serialize(),function(data){
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
			//流程信息初始化
			actInit();
			userListInit();
			ruleArgsInit();
		});
		
		function actInit() {
			if (jp.checkTaskPermission("${actFlow.procTaskPermission}")) {
				$('#commentTextarea').attr('style','');
			} else {
				$('#commentTextarea').attr('style','display: none;');
			}
		}
		
		function userListInit(){
			
		}
		
		function getUserList(pValue){
			//点击提交、上报等按钮时
			if (jp.selectUserButton().indexOf(pValue) != -1) {
				//不是新记录时，手动获取userList
				if ($('#id').val() != null && $('#id').val().trim() != ''
						&& $('#procInsId').val() != null && $('#procInsId').val().trim() != '') {
					$.ajax({
						type:'post',
						url:'${ctx}/oa/test/actFlow/getUserList',
						data:'id='+$('#id').val()+'&procInsId='+$('#procInsId').val()+'&act.flag='+pValue,
						dataType:'json',
						success:function(data){
							var userList = null;
							if (data != null && data.body != null && data.body.userList) {
								userList = data.body.userList;
							}
							if (userList != null && data.body.type=='single') {
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
								$('#modalBtn').click();
							} else if (userList != null && data.body.type=='multi') {
								//构造多选select
								var unselect = $('<select></select>').attr('id', 'unselect')
									.attr('class', 'form-control')
									.attr('style', 'height: 200px;')
									.attr('multiple','multiple');;
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
								$('#modalBtn').click();
							} else {
								realSubmit();
							}
						}
					});
				} else {
					$('#modalBtn').click();
				}
			} else {
				realSubmit();
			}
		}
		function fillSelected(obj){
			if ($('#selected_').html().indexOf($(obj).val()) == -1) {
				$('#selected_').append(
					$('<option></option>').val($(obj).attr('id'))
						.attr('class', 'selected')
						.attr('id', $(obj).attr('id'))
						.attr('onclick', '$(\'#removeTempId\').val($(this).attr(\'id\'))')
						.attr('ondblclick', 'removeSelectedInput(this),$(this).remove()')
						.html($(obj).html())
				)
				$('#tempLoginNameDiv').append(
					$('<input/>').val($(obj).attr('id'))
						.attr('type', 'hidden')
						.attr('id', $(obj).attr('id'))
						.attr('name', 'tempLoginName')
						.attr('class', 'selectedInput')
						.attr('ondblclick', '$(this).remove()')
				);
			}
		}
		function removeSelectedInput(obj){
			$('input[id=\'' + $(obj).attr('id') + '\'][class=\'selectedInput\']').dblclick();
		}
		function fillSelectedValuesForButton(){
			$('option[id=\'' + $('#selectedTempId').val() + '\'][class=\'unselected\']').dblclick();
		}
		function removeSelectedValuesForButton(){
			$('option[id=\'' + $('#removeTempId').val() + '\'][class=\'selected\']').dblclick();
		}
		function ruleArgsInit(){
			if ($('#id').val() != null && $('#id').val().trim() != ''){
				var ruleArgs = eval('('+ '${actFlow.ruleArgsJson}' +')');
				if (ruleArgs != null && ruleArgs.form !=null && ruleArgs.form.length > 0) {
					$.each(ruleArgs.form, function(key, value){
						if (value == 1 ) {
							$('#'+key).parent().removeAttr('disabled');
						} else if (value == 0) {
							$('#'+key).parent().parent().parent().remove();
						}
					});
				}
			}
		}
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="actFlow" class="form-horizontal">
		<form:hidden path="id"/>
		
		<input type="hidden" id="act.taskId" name="act.taskId" value="${actFlow.act.taskId }"/>
		<input type="hidden" id="act.taskName" name="act.taskName" value="${actFlow.act.taskName }"/>
		<input type="hidden" id="act.taskDefKey" name="act.taskDefKey" value="${actFlow.act.taskDefKey }"/>
		<input type="hidden" id="procInsId" name="act.procInsId" value="${actFlow.act.procInsId }"/>
		<input type="hidden" id="act.procDefId" name="act.procDefId" value="${actFlow.act.procDefId }"/>
		<input type="hidden" id="flag" name="act.flag"/>
		<input type="hidden" id="tempLoginName" name="tempLoginName"/>
		<div id="tempLoginNameDiv"></div>
		
		<input type="hidden" name="dirtyUpdateDate" value="${actFlow.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="remarks" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				<tr id="commentTextarea" style="display: none;">
					<td class="width-15 active"><label class="pull-right">意见：</label></td>
					<td class="width-35" colspan="3">
						<textarea name="act.comment" class="form-control" rows="3" cols="4"></textarea>
					</td>
				</tr>
				<c:if test="${not empty actFlow.id}">
				<tr>
					<td class="width-15 active"><label class="pull-right">流程日志：</label></td>
					<td class="width-35" colspan="3">
							<a class="btn btn-white btn-sm " href="javascript:openDialog('查看流程历史','${ctx}/act/task/trace/photo/${actFlow.act.procDefId}/${actFlow.procInsId}','1000px', '600px')">流程图</a>
							<div style="margin-bottom:5px;"></div>
							<act:histoicFlowCustom procInsId="${actFlow.act.procInsId}" />
					</td>
				</tr>
				</c:if>
		 	</tbody>
		</table>
		
		<div style="display: none;">
			<a id="modalBtn" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal"></a>
		</div>
		<!-- 模态框（Modal）BEGIN -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		    <div class="modal-dialog">
		        <div class="modal-content">
		            <div class="modal-header">
		                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		                <h4 class="modal-title" id="myModalLabel">选择用户</h4>
		            </div>
		            <div id="userSelectDiv" class="modal-body">
		            	<select id="userSelect" class="form-control " onchange="$('#tempLoginName').val(this.value)">
		            		<option selected="selected"></option>
		            		<c:forEach items="${userList }" var="user">
		            			<option value="${user.loginName }">${user.name }</option>
		            		</c:forEach>
		            	</select>
		            </div>
		            <div class="modal-footer">
		                <button type="button" class="btn btn-primary" onclick="realSubmit();">提交</button>
		            </div>
		        </div>
		    </div>
		</div>
		<!-- 模态框（Modal）END -->
	</form:form>
</body>
</html>