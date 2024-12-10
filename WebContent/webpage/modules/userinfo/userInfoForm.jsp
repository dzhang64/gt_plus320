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
				/*var defaultPValue = '10';
		    	if (!pName && !pValue) {
					//添加
				} else {
					//保存，办理
					if (pName && pValue && $("#" + pName) && pValue != '') $("#" + pName).val(pValue);
				}*/
		      
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
			
			laydate({
				elem: '#entryDate',
				event: 'focus'
			});
			laydate({
				elem: '#internBeginDate',
				event: 'focus'
			});
			laydate({
				elem: '#internEndDate',
				event: 'focus'
			});
			laydate({
				elem: '#trialBeginDate',
				event: 'focus'
			});
			laydate({
				elem: '#trialEndDate',
				event: 'focus'
			});
			laydate({
				elem: '#turnPositiveDate',
				event: 'focus'
			});
			laydate({
				elem: '#gradeDay',
				event: 'focus'
			});
			laydate({
				elem: '#contractSartDate',
				event: 'focus'
			});
			laydate({
				elem: '#contractEndDate',
				event: 'focus'
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
						<form:input path="name" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">身份证编号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="idCardNum" htmlEscape="false"    class="form-control "/>
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
					<td class="width-15 active"><label class="pull-right">性别：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="gender" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('sex',userInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">籍贯：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="nativePlace" htmlEscape="false"    class="form-control "/>
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
					<td class="width-15 active"><label class="pull-right">手机号码：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="tel" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">部门：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="office" name="office.id" value="${userInfo.office.id}" labelName="office.name" labelValue="${userInfo.office.name}"
							title="部门" url="/sys/office/treeData?type=2" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">区域：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="area" name="area.id" value="${userInfo.area.id}" labelName="area.name" labelValue="${userInfo.area.name}"
							title="区域" url="/sys/area/treeData" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">社保交纳地：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<sys:treeselect id="ssc" name="ssc.id" value="${userInfo.ssc.id}" labelName="ssc.name" labelValue="${userInfo.ssc.name}"
							title="区域" url="/sys/area/treeData" cssClass="form-control " allowClear="true" notAllowSelectParent="true"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">工资卡号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="salaryCardNum" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">统筹号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="coordinateNum" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">公积金账号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="providentNum" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">员工状态：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="userStatus" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('sys_user_status',userInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">员工类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="userType" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('sys_user_type',userInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">公司邮箱：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="comMail" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">薪资级别：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">                                                                                                                                                                                                                                             
                           <sys:gridselectcustom url="${ctx}/salarylevel/salaryLevel/selectsalary" id="salary" name="salary.id"  value="${userInfo.salary.id}"  title="选择薪资级别" labelName="salary.level"                                                                           
                           labelValue="${userInfo.salary.level}" cssClass="form-control required" fieldLabels="等级|基本工资|绩效工资|补贴" fieldKeys="level|basicWage|performancePay|subsidy" fieldTypes="0|0|0|0"  filter=""  searchLabel="等级" searchKey="level" ></sys:gridselectcustom>
                        </fieldset>  
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">等级工资：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="salaryLevel" htmlEscape="false"    class="form-control "/>
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
					<td class="width-15 active"><label class="pull-right">入职日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="entryDate" name="entryDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.entryDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">工作年限：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="serviceYear" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">实习开始日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="internBeginDate" name="internBeginDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.internBeginDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">实习结束日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="internEndDate" name="internEndDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.internEndDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">试用期开始日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="trialBeginDate" name="trialBeginDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.trialBeginDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">试用期结束日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="trialEndDate" name="trialEndDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.trialEndDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">转正日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="turnPositiveDate" name="turnPositiveDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.turnPositiveDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">毕业院校：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="gradeSchool" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">毕业专业：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="major" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">毕业日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="gradeDay" name="gradeDay" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.gradeDay}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">学历：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="ed" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">学位：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="Bachelor" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">外语等级：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="foreignLanguageLevel" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('sys_user_language',userInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">合同开始日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="contractSartDate" name="contractSartDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.contractSartDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">合同结束日期：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<input id="contractEndDate" name="contractEndDate" type="text" maxlength="20" class="laydate-icon form-control layer-date "
							value="<fmt:formatDate value="${userInfo.contractEndDate}" pattern="yyyy-MM-dd" />"/>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">合同编号：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:input path="contract" htmlEscape="false"    class="form-control "/>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td class="width-15 active"><label class="pull-right">合同类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="contractType" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('sys_user_contract',userInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
					<td class="width-15 active"><label class="pull-right">合同期限类型：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
						<form:select path="contractLimit" class="form-control ">
							<form:option value="" label=""/>
							<form:options items="${fns:getDictListEdit('sys_user_contract_year',userInfo.id)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
						</form:select>
						</fieldset>
					</td>
				</tr>
				<tr>
				<td class="width-15 active"><label class="pull-right">登录名：</label></td>
			
			        <td class="width-35">
						<fieldset class="set">
					      <form:input path="loginName" htmlEscape="false"    class="form-control "/>
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