<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>调薪申请管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var validateForm;
		var $table;   // 父页面table表格id
		var $topIndex;//弹出窗口的 index
		//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
		function doSubmit(table,index,pName,pValue){
			if(validateForm.form()){

				$table = table;
				$topIndex = index;
				$('#flag').val(pValue);
				var param = eval('(' + pValue + ')');
				var ruleUserResult = ruleArgsUser();
				getUserList(param, ruleUserResult);
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
					jp.post("${ctx}/sysusertransferapplication/sysUserTransferApplication/save",$('#inputForm').serialize(),function(data){
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
				elem: '#adjustTime',
				event: 'focus',format: 'YYYY-MM-DD', istime : true
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
			//actInit();
			ruleArgsInit();
		});

		/*function actInit() {
			if (jp.checkTaskPermission("${sysUserTransferApplication.procTaskPermission}")) {
				$('#commentTextarea').attr('style','');
			} else {
				$('#commentTextarea').attr('style','display: none;');
			}
		}*/
		
		function ruleArgsInit(){
				var ruleArgs = eval('('+ '${sysUserTransferApplication.ruleArgsJson}' +')');
				if (ruleArgs != null && ruleArgs.form !=null) {
					$.each(ruleArgs.form, function(key, value){
						//==0 向上3级移除
						if (value == 0 ) {
							$('#'+key).parent().parent().parent().remove();
						} 
						//==1 向上1级可编辑
						else if (value == 1) {
							$('#'+key).parent().removeAttr('disabled');
						} 
						//==2 向上1级不可编辑
						else if (value == 2) {
							$('#'+key).parent().attr('disabled', 'disabled');
							$('#'+key).attr('style','background-color:#F8F8F8;');
						}
						//==3 向上1级隐藏
						else if (value == 3) {
						$('#'+key).parent().attr('style', 'display: none;');
						$('#'+key).parent().parent().attr('style', 'display: none;'); 
						$('#'+key).parent().parent().prev().attr('style', 'display: none;'); 	
						}
						//==4 向上1级显示
						else if (value == 4) {
							$('#'+key).parent().attr('style', '');
						}
						//==5 向上1级显示&可编辑
						else if (value == 5) {
							$('#'+key).parent().removeAttr('disabled');
							$('#'+key).removeAttr('disabled');
							$('#'+key).attr('style', '');
							$('#'+key).attr('class','form-control required')
						}
					
					});
				}
		}
		
		
		              
       function ruleArgsUser() {
		var ruleArgs =  eval('('+ '${sysUserTransferApplication.ruleArgsJson}' + ')');
		var stringUser = new Array();
		//获取当前页面的规则变量的值
		if (ruleArgs != null && ruleArgs.user != null) {
			var rlu;
			$.each(ruleArgs.user, function(key, value) {

			
				var rlu = '{\"type\":\"' + key + '\",\"value\":\"'
						+ $('#' + key).val().trim() + '\",\"result\":\"'
						+ value + '\"}';
				stringUser.push(rlu);
			});
			$("#userKey").val(stringUser);

			return true;
		}

		else {

			return false;
		}
	}
		
		
		function getUserList(param, databool) {
		if (jp.isNeedUserList(param.type)) {
			//构造data
			var data;
			if (databool) {
				data = 'id=' + $('#id').val() + '&procInsId='
						+ $('#procInsId').val() + '&act.flag=' + param.flag
						+ '&userKey=' + $('#userKey').val();
			} else {
				data = 'id=' + $('#id').val() + '&procInsId='
						+ $('#procInsId').val() + '&act.flag=' + param.flag;
			}
			//异步查询用户列表
			$.ajax({
						type : 'post',
						url:'${ctx}/sysusertransferapplication/sysUserTransferApplication/getUserList',
						data : data,
						dataType : 'json',
						success : function(data) {

							if (data.body == null || data.body.userList == null) {
								realSubmit();
							}
							//data.body.userList.length==1,直接提交唯一用户
							else if (data.body.userList.length == 1) {
								$('#tempLoginName').val(data.body.userList[0].loginName);
								realSubmit();
							}
							//data.body.userList.length!=1,data.body.type==single,单选
							else if (data.body.userList.length != 1
									&& data.body.type == 'single') {
								buildSingleBox(data.body.userList);
							}
							//data.body.userList.length!=1,data.body.type==multi,多选
							else if (data.body.userList.length != 1
									&& data.body.type == 'multi') {
								buildMultiBox(data.body.userList);
							}
						}
					});
		} else if (jp.isAlreadyExistUser(param.type)) {
			//退回时使用目标节点用户
			realSubmit();
		} else {
			//其他情况直接提交
			realSubmit();
		}
	}
	
		function buildSingleBox(userList){
			var select = $('<select></select>')
				.attr('id', 'userSelect')
				.attr('class', 'form-control')
				.attr('onchange', '$(\'#tempLoginName\').val(this.value)')
				.append(
					$('<option></option>').attr('selected', 'selected')
				);
			for (var i = 0; i < userList.length; i++) {
				select.append(
					$('<option></option>')
						.val(userList[i].loginName)
						.html(userList[i].name)
				);
			}
			$('#userSelectDiv').html('')
				.attr('style', '')
				.attr('class', 'modal-body')
				.append(select);
			$('#modalBtn').click();
		}
		
		function buildMultiBox(userList){
			var unselect = $('<select></select>')
				.attr('id', 'unselect')
				.attr('class', 'form-control')
				.attr('style', 'height: 200px;')
				.attr('multiple','multiple');
			for (var i = 0; i < userList.length; i++) {
				unselect.append(
					$('<option></option>')
						.attr('id', userList[i].loginName)
						.attr('class', 'unselected')
						.attr('ondblclick', 'fillSelected(this);')
						/* .attr('onclick', '$("#selectedTempId").val(this.value)') */
						.val(userList[i].loginName)
						.html(userList[i].name)
				);
			}
			var selected = $('<select></select>')
				.attr('id', 'selected_')
				.attr('class', 'form-control')
				.attr('style', 'height: 200px;')
				.attr('multiple','multiple');
			var selectBtn = $('<div></div>')
				.attr('style', 'float: left; width: 8%; margin: 60px 0px 0px 2%;')
				.append(
					$('<a></a>')
						.attr('class', 'btn btn-white btn-sm')
						.attr('data-toggle', 'tooltip')
						.attr('data-placement', 'left')
						.attr('title', '选择')
						.attr('onclick', 'fillSelectedValuesForButton()')
						.append(
							$('<i></i>').attr('class', 'glyphicon glyphicon-chevron-right')
						)
				)
				.append(
					$('<a></a>')
						.attr('class', 'btn btn-white btn-sm')
						.attr('data-toggle', 'tooltip')
						.attr('data-placement', 'left')
						.attr('title', '移除')
						.attr('onclick', 'removeSelectedValuesForButton()')
						.append(
							$('<i></i>').attr('class', 'glyphicon glyphicon-chevron-left')
						)
				);
			$('#userSelectDiv')
				.attr('class', 'modal-body')
				.attr('style', 'height: 240px;')
				.html('');
			$('#userSelectDiv')
				.append(
					$('<div></div>')
						.attr('style', 'float: left; width: 45%;')
						.append(unselect)
				)
				.append(selectBtn)
				.append(
					$('<div></div>')
						.attr('style', 'float: left; width: 45%;')
						.append(selected)
				);
			$('#modalBtn').click();
		}
		
		function fillSelected(obj){
			if ($('#selected_').html().indexOf($(obj).val()) == -1) {
				$('#selected_').append(
					$('<option></option>').val($(obj).attr('id'))
						.attr('class', 'selected')
						.attr('id', $(obj).attr('id'))
						//.attr('onclick', '$(\'#removeTempId\').val($(this).attr(\'id\'))')
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
			//alert($('option[id=\'' + $('#selectedTempId').val() + '\'][class=\'unselected\']').val());
			var arr = $('#unselect').val();
			for (var i = 0; i < arr.length; i++) {
				$('option[id=\'' + arr[i] + '\'][class=\'unselected\']').dblclick();
			}
		}
		
		function removeSelectedValuesForButton(){
			//$('option[id=\'' + $('#removeTempId').val() + '\'][class=\'selected\']').dblclick();
			var arr = $('#selected_').val();
			for (var i = 0; i < arr.length; i++) {
				$('option[id=\'' + arr[i] + '\'][class=\'selected\']').dblclick();
			}
		}
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
		
		//gridselect选择后执行
		function getGridSelectItem(objstr) {
		 $("#nameCode").val(objstr[2]); 
		 $("#officeName").val(objstr[3]); 
		 $("#beforeSalaryId").val(objstr[7]); 
		 $("#beforeSalaryLevel").val(objstr[8]); 
		}
	</script>
	<style type="text/css">
		.unit {float:left;}
		.unit1 {width:230px;float:left;}
		.unit2 {width:460px;float:left;}
		.unit3 {width:690px;float:left;}
		.unit4 {width:100%;float:left;}
		.unit-label {margin-top:8px;}
	</style>
</head>
<body class="hideScroll">
        <input type="hidden" id="userKey" value="" />
		<form:form id="inputForm" modelAttribute="sysUserTransferApplication" class="form-horizontal">
		<form:hidden path="id"/>
		
		<input type="hidden" id="act.taskId" name="act.taskId" value="${sysUserTransferApplication.act.taskId }"/>
		<input type="hidden" id="act.taskName" name="act.taskName" value="${sysUserTransferApplication.act.taskName }"/>
		<input type="hidden" id="act.taskDefKey" name="act.taskDefKey" value="${sysUserTransferApplication.act.taskDefKey }"/>
		<input type="hidden" id="procInsId" name="act.procInsId" value="${sysUserTransferApplication.act.procInsId }"/>
		<input type="hidden" id="act.procDefId" name="act.procDefId" value="${sysUserTransferApplication.act.procDefId }"/>
		<input type="hidden" id="flag" name="act.flag"/>
		<input type="hidden" id="selectedTempId" />
		<input type="hidden" id="removeTempId" />
		<input type="hidden" id="tempLoginName" name="tempLoginName"/>
		<div id="tempLoginNameDiv"></div>
		
		<input type="hidden" name="dirtyUpdateDate" value="${sysUserTransferApplication.updateDate}" />
		<form:hidden path="viewFlag" />
		<sys:message content="${message}"/>	
		<table class="table table-bordered  table-condensed dataTables-example dataTable no-footer">
		   <tbody>
				<tr>
					<td class="width-15 active"><label class="pull-right">姓名：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:gridselectcustomU url="${ctx}/userinfo/userInfo/selectUserInfo" id="name" name="name.id"  value="${sysUserTransferApplication.name.id}"  title="选择姓名" labelName="name.name" 
						 labelValue="${sysUserTransferApplication.name.name}" cssClass="form-control required" fieldLabels="姓名|工号|部门|区域|员工状态|员工类型|薪资级别|等级工资" fieldKeys="name|nameCode|office|area|userStatus|userType|salary|salaryLevel" fieldTypes="0|0|0|0|sys_user_status|sys_user_type|0|0" filter="" searchLabel="姓名" searchKey="name"></sys:gridselectcustomU>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">调整时间：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="adjustTime" name="adjustTime" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${sysUserTransferApplication.adjustTime}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">工号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="nameCode" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">部门：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="officeName" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">原薪资级别：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="beforeSalaryId" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">原等级工资：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="beforeSalaryLevel" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">调整后薪资级别：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:gridselectcustom url="${ctx}/salarylevel/salaryLevel/selectsalary" id="afterSalary" name="afterSalary.id"  value="${sysUserTransferApplication.afterSalary.id}"  title="选择调整后薪资级别" labelName="afterSalary.level" 
						 labelValue="${sysUserTransferApplication.afterSalary.level}" cssClass="form-control required" fieldLabels="等级|基本工资|绩效工资|补贴" fieldKeys="level|basicWage|performancePay|subsidy"  fieldTypes="0|0|0|0" filter=""  searchLabel="等级" searchKey="level" ></sys:gridselectcustom>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">调整后等级工资：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="afterSalaryLevel" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
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
				<c:if test="${not empty sysUserTransferApplication.id}">
				<tr>
					<td class="width-15 active"><label class="pull-right">流程日志：</label></td>
					<td class="width-35" colspan="3">
						<a class="btn btn-white btn-sm " href="javascript:$('#histoicFlowList').css('display','block');">查看日志</a>
						<a class="btn btn-white btn-sm " href="javascript:openDialog('查看流程历史','${ctx}/act/task/trace/photo/${sysUserTransferApplication.act.procDefId}/${sysUserTransferApplication.procInsId}','1000px', '600px')">流程图</a>
						<div style="margin-bottom:5px;"></div>
						<act:histoicFlowCustom procInsId="${sysUserTransferApplication.act.procInsId}" />
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