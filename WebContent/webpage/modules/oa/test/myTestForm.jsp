<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>测试OA管理</title>
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
					jp.post("${ctx}/oa/test/myTest/save",$('#inputForm').serialize(),function(data){
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
		});
		
		function actInit() {
			if (jp.checkTaskPermission("${myTest.procTaskPermission}")) {
				$('#commentTextarea').attr('style','');
			} else {
				$('#commentTextarea').attr('style','display: none;');
			}
		}
		
		function getUserList(pValue){
			//点击提交、上报等按钮时
			if (jp.selectUserButton().indexOf(pValue) != -1) {
				//不是新记录时，手动获取userList
				if ($('#id').val() != null && $('#id').val().trim() != ''
						&& $('#procInsId').val() != null && $('#procInsId').val().trim() != '') {
					$.ajax({
						type:'post',
						url:'${ctx}/oa/test/myTest/getUserList',
						data:'id='+$('#id').val()+'&procInsId='+$('#procInsId').val()+'&act.flag='+pValue,
						dataType:'json',
						success:function(data){
							var userList = null;
							if (data != null && data.body != null && data.body.userList) {
								userList = data.body.userList;
							}
							if (userList != null) {
								$('#userSelect').html('');
								$('#userSelect').append(
									$('<option></option>').attr('selected', 'selected')
								);
								for (var i = 0; i < userList.length; i++) {
									$('#userSelect').append(
										$('<option></option>').val(userList[i].loginName)
											.html(userList[i].name)
									);
								}
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
	</script>
</head>
<body class="hideScroll">
		<form:form id="inputForm" modelAttribute="myTest" class="form-horizontal">
		<form:hidden path="id"/>
		
		<input type="hidden" id="act.taskId" name="act.taskId" value="${myTest.act.taskId }"/>
		<input type="hidden" id="act.taskName" name="act.taskName" value="${myTest.act.taskName }"/>
		<input type="hidden" id="act.taskDefKey" name="act.taskDefKey" value="${myTest.act.taskDefKey }"/>
		<input type="hidden" id="procInsId" name="act.procInsId" value="${myTest.act.procInsId }"/>
		<input type="hidden" id="act.procDefId" name="act.procDefId" value="${myTest.act.procDefId }"/>
		<input type="hidden" id="flag" name="act.flag"/>
		<input type="hidden" id="tempLoginName" name="tempLoginName"/>
		
		<input type="hidden" name="dirtyUpdateDate" value="${myTest.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right"><font color="red">*</font>名称：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:input path="name" htmlEscape="false"    class="form-control required"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">备注信息：</label></td>
			
					<td class="width-35" colspan="3">
						<fieldset class="set">
						<form:textarea path="remarks" htmlEscape="false" rows="4"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr id="commentTextarea" style="display: none;">
					<td class="width-15 active"><label class="pull-right">意见：</label></td>
					<td class="width-35" colspan="3">
						<textarea name="act.comment" class="form-control" rows="3" cols="4"></textarea>
					</td>
				</tr>
				<c:if test="${not empty myTest.id}">
				<tr>
					<td class="width-15 active"><label class="pull-right">流程日志：</label></td>
					<td class="width-35" colspan="3">
							<a class="btn btn-white btn-sm " href="javascript:openDialog('查看流程历史','${ctx}/act/task/trace/photo/${myTest.act.procDefId}/${myTest.procInsId}','1000px', '600px')">流程图</a>
							<div style="margin-bottom:5px;"></div>
							<act:histoicFlowCustom procInsId="${myTest.act.procInsId}" />
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
		            <div class="modal-body">
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