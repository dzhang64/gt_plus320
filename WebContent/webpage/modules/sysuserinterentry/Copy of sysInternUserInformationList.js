<%@ page contentType="text/html;charset=UTF-8" %>
	<script>
$(document).ready(function() {
	$('#sysInternUserInformationTable').bootstrapTable({
		method: 'get',               //请求方法
		dataType: "json",            //类型json
		showRefresh: true,           //显示刷新按钮
		showToggle: true,            //显示切换手机试图按钮
		showColumns: true,           //显示 内容列下拉框
		showExport: true,            //显示到处按钮
		showPaginationSwitch: true,  //显示切换分页按钮
		minimumCountColumns: 2,      //最低显示2行
		striped: true,               //是否显示行间隔色
		cache: false,                //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*） 
		pagination: true,            //是否显示分页（*）  
		sortOrder: "asc",            //排序方式 
		pageNumber:1,                //初始化加载第一页，默认第一页
		pageSize: 10,                //每页的记录行数（*）
		pageList: [10, 25, 50, 100], //可供选择的每页的行数（*）
		url: "${ctx}/sysuserinterentry/sysInternUserInformation/data/${path==null?'Todo':path}", //这个接口需要处理bootstrap table传递的固定参数,并返回特定格式的json数据
		//默认值为 'limit',传给服务端的参数为：limit, offset, search, sort, order Else
		//queryParamsType:'',   
		//查询参数,每次调用是会带上这个参数，可自定义
		queryParams : function(params) {
			var searchParam = $("#searchForm").serializeJson();
			searchParam.pageNo = params.limit === undefined? "1" :params.offset/params.limit+1;
			searchParam.pageSize = params.limit === undefined? -1 : params.limit;
			searchParam.orderBy = params.sort === undefined? "" : params.sort+ " "+  params.order;
			return searchParam;
		},
		sidePagination: "server",        //分页方式：client客户端分页，server服务端分页（*）
		contextMenuTrigger:"right",      //pc端 按右键弹出菜单
		contextMenuTriggerMobile:"press",//手机端 弹出菜单，click：单击， press：长按。
		contextMenu: '#context-menu',
		onContextMenuItem: function(row, $el){
			if($el.data("item") == "edit"){
				edit(row.id);
			}else if($el.data("item") == "delete"){
				jp.confirm('确认要删除该实习生入职记录吗？', function(){
					jp.loading();
					jp.get("${ctx}/sysuserinterentry/sysInternUserInformation/delete?id="+row.id, function(data){
						if(data.success){
							$('#sysInternUserInformationTable').bootstrapTable('refresh');
							jp.success(data.msg);
						}else{
							jp.error(data.msg);
						}
					});
				});
			}
		},
		onClickRow: function(row, $el){ },
		onLoadSuccess: function () { },
		columns: [
			{
				checkbox: true
				,visible: false
			}
			,{
				field: 'name',
				title: '姓名',
				sortable: true
				
				,formatter:function(value, row , index){
					return "<a href='javascript:edit(\""+row.id+"\",true)'>"+value+"</a>";
				}
			}
			,{
				field: 'idcardNum',
				title: '身份证编号',
				sortable: true
				
			}
			,{
				field: 'gender',
				title: '性别',
				sortable: true,
				
				formatter:function(value, row , index){
					return jp.getDictLabel(${fns:toJson(fns:getDictList('sex'))}, value, "-");
				}
			}
			,{
				field: 'nativePlace',
				title: '籍贯',
				sortable: true
				
			}
			,{
				field: 'loginName',
				title: '登录名',
				sortable: true
				
			}
			,{
				field: 'nameCode',
				title: '工号',
				sortable: true
				
			}
			,{
				field: 'comMail',
				title: '公司邮箱',
				sortable: true
				
			}
			,{
				field: 'tel',
				title: '手机号码',
				sortable: true
				
			}
			,{
				field: 'office.name',
				title: '部门',
				sortable: true
				
			}
			,{
				field: 'area.name',
				title: '区域',
				sortable: true
				
			}
			,{
				field: 'salaryCardNum',
				title: '工资卡号',
				sortable: true
				
			}
			,{
				field: 'userStatus',
				title: '员工状态',
				sortable: true,
				
				formatter:function(value, row , index){
					return jp.getDictLabel(${fns:toJson(fns:getDictList('sys_user_status'))}, value, "-");
				}
			}
			,{
				field: 'userType',
				title: '员工类型',
				sortable: true,
				
				formatter:function(value, row , index){
					return jp.getDictLabel(${fns:toJson(fns:getDictList('sys_user_type'))}, value, "-");
				}
			}
			,{
				field: 'salary.level',
				title: '薪资级别',
				sortable: true
				
			}
			,{
				field: 'salaryLevel',
				title: '等级工资',
				sortable: true
				
			}
			,{
				field: 'ownEmail',
				title: '个人邮箱',
				sortable: true
				
			}
			,{
				field: 'emergencyUserName',
				title: '紧急联系人',
				sortable: true
				
			}
			,{
				field: 'emergencyUserRelation',
				title: '紧急联系人关系',
				sortable: true
				
			}
			,{
				field: 'emergencyUserPhone',
				title: '紧急联系人电话',
				sortable: true
				
			}
			,{
				field: 'address',
				title: '通信地址',
				sortable: true
				
			}
			,{
				field: 'entryDate',
				title: '入职日期',
				sortable: true
				
			}
			,{
				field: 'serviceYear',
				title: '工作年限',
				sortable: true
				
			}
			,{
				field: 'gradeSchool',
				title: '学生院校',
				sortable: true
				
			}
			,{
				field: 'major',
				title: '学生专业',
				sortable: true
				
			}
			,{
				field: 'ed',
				title: '学历',
				sortable: true
				
			}
			,{
				field: 'bachelor',
				title: '学位',
				sortable: true
				
			}
			,{
				field: 'foreignlanguageLevel',
				title: '外语等级',
				sortable: true,
				
				formatter:function(value, row , index){
					return jp.getDictLabel(${fns:toJson(fns:getDictList('sys_user_language'))}, value, "-");
				}
			}
			,{
				field: 'contractSartDate',
				title: '合同开始日期',
				sortable: true
				
			}
			,{
				field: 'contractEndDate',
				title: '合同结束日期',
				sortable: true
				
			}
			,{
				field: 'contract',
				title: '合同编号',
				sortable: true
				
			}
			,{
				field: 'contractType',
				title: '合同类型',
				sortable: true,
				
				formatter:function(value, row , index){
					return jp.getDictLabel(${fns:toJson(fns:getDictList('sys_user_contract'))}, value, "-");
				}
			}
			,{
				field: 'contractLimit',
				title: '合同期限类型',
				sortable: true,
				
				formatter:function(value, row , index){
					return jp.getDictLabel(${fns:toJson(fns:getDictList('sys_user_contract_year'))}, value, "-");
				}
			}
			,{
				field: 'socailsecurityCity.name',
				title: '社保交纳地',
				sortable: true
				
			}
			,{
				field: 'internBeginDate',
				title: '实习开始日期',
				sortable: true
				
			}
			,{
				field: 'internEndDate',
				title: '实习结束日期',
				sortable: true
				
			}
			,{ //操作列
				field: 'operate',
				title: '操作',
				events: {
					'click .view': function (e, value, row, index) {
						jp.openDialogView('查看实习生入职', '${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set_audit&id=' + row.id, '1000px', '100%');
					},
					'click .edit': function (e, value, row, index) {
						var windowName = $(".edit").attr("dealname")+'实习生入职';
						var paramStr = getParamStr(row.procTaskPermission.operation);
						jp.openDialogFlow(windowName, '${ctx}/sysuserinterentry/sysInternUserInformation/form?id=' + row.id, '1000px', '100%', $('#sysInternUserInformationTable'), paramStr);
					},
					'click .edit2': function (e, value, row, index) {
						var windowName = $(".edit2").attr("dealname")+'实习生入职';
						var paramStr = getParamStr(row.procTaskPermission.operation);
						jp.openDialogFlow(windowName, '${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set&id=' + row.id, '1000px', '100%', $('#sysInternUserInformationTable'), paramStr);
					},
					'click .del': function (e, value, row, index) {
						del(row.id);
					}
				},
				formatter: function operateFormatter(value, row, index) {
					var editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					var delLink = ' <a href="#"  class="btn btn-danger btn-xs del"><i class="fa fa-trash"></i> 删除</a>';
					if ("${path==null?'Todo':path}"=="Todo"){
						editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
					} else if ("${path}"=="Doing") {
						editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
					} else if ("${path}"=="Done") {
						editLink = '';
					} else if ("${path}"=="Sent") {
						editLink = '';
					}
					if (("${path}"=="Doing" || "${path==null?'Todo':path}"=="Todo") 
							&& row.procTaskPermission == "modify") {
						editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					}
					return [
						<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:view">
						'<a href="#" class="btn btn-info btn-xs view" ><i class="fa fa-search-plus"></i> 查看</a>',
						</shiro:hasPermission>
						<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:edit">
						editLink,
						</shiro:hasPermission>
						<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:del"> 
						delLink,  
						</shiro:hasPermission>
					].join('');
				}
			}
			<%--
			,{ //操作列
				field: 'operate',
				title: '操作',
				events: {
					'click .view': function (e, value, row, index) {
						jp.openDialogView('查看实习生入职', '${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set_audit&id=' + row.id, '1000px', '100%');
					},
					'click .edit': function (e, value, row, index) {
						var windowName = $(".edit").attr("dealname")+'实习生入职';
						var btns = $(".edit").attr("dealbtn");
						var params = $(".edit").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/sysuserinterentry/sysInternUserInformation/form?id=' + row.id, '1000px', '100%', $('#sysInternUserInformationTable'), btns, params);
					},
					'click .edit2': function (e, value, row, index) {
						var windowName = $(".edit2").attr("dealname")+'实习生入职';
						var btns = $(".edit2").attr("dealbtn");
						var params = $(".edit2").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set&id=' + row.id, '1000px', '100%', $('#sysInternUserInformationTable'), btns, params);
					},
					'click .edit3': function (e, value, row, index) {
						var windowName = $(".edit3").attr("dealname")+'实习生入职';
						var btns = $(".edit3").attr("dealbtn");
						var params = $(".edit3").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set&id=' + row.id, '1000px', '100%', $('#sysInternUserInformationTable'), btns, params);
					},
					'click .del': function (e, value, row, index) {
						del(row.id);
					}
				},
				formatter: function operateFormatter(value, row, index) {
					var editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
					var delLink = ' <a href="#"  class="btn btn-danger btn-xs del"><i class="fa fa-trash"></i> 删除</a>';
					
						if ("${path}"=="Todo"){
							editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
						} else if ("${path}"=="Doing") {
							editLink = ' <a href="#" class="btn btn-success btn-xs edit2" dealname="办理"><i class="fa fa-edit"></i> 办理</a>';
						} else if ("${path}"=="Done") {
							editLink = '';
						} else if ("${path}"=="Sent") {
							editLink = '';
						}
						if (row.procTaskPermission == "modify") {
							editLink = ' <a href="#" class="btn btn-success btn-xs edit" dealname="编辑"><i class="fa fa-edit"></i> 编辑</a>';
						}
					
					return [
						<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:view">
						'<a href="#" class="btn btn-info btn-xs view" ><i class="fa fa-search-plus"></i> 查看</a>',
						</shiro:hasPermission>
						<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:edit">
						editLink,
						</shiro:hasPermission>
						<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:del"> 
						delLink,  
						</shiro:hasPermission>
					].join('');
				}
			}
			--%>
			,{
				field: 'rules',
				title: '参数列',
				sortable: false,
				visible: false
				,formatter:function(value, row , index){
					return "";
				}
			}
		]
	}); //结束绑定列表数据
	
	//如果是移动端
	if(navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)){ 
		$('#sysInternUserInformationTable').bootstrapTable("toggleView");
	}
	$('#sysInternUserInformationTable').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table', function () {
		$('#remove').prop('disabled', ! $('#sysInternUserInformationTable').bootstrapTable('getSelections').length);
		$('#edit').prop('disabled', $('#sysInternUserInformationTable').bootstrapTable('getSelections').length!=1);
	});
	
	//导入数据	  
	$("#btnImport").click(function(){
		top.layer.open({
			type: 1, 
			area: [500, 300],
			title:"导入数据",
			content:$("#importBox").html() ,
			btn: ['下载模板','确定', '关闭'],
			btn1: function(index, layero){
				window.location='${ctx}/sysuserinterentry/sysInternUserInformation/import/template';
			},
			btn2: function(index, layero){
				var inputForm =top.$("#importForm");
				var top_iframe = top.getActiveTab().attr("name");//获取当前active的tab的iframe 
				inputForm.attr("target",top_iframe);             //表单提交成功后，从服务器返回的url在当前tab中展示
				inputForm.onsubmit = function(){
					jp.loading('  正在导入，请稍等...');
				}
				inputForm.submit();
				jp.close(index);
			},
			btn3: function(index){ 
				jp.close(index);
			}
		}); 
	});
	
	//绑定查询按扭 
	$("#search").click("click", function() { 
		$('#sysInternUserInformationTable').bootstrapTable('refresh');
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input[name!='ownerCode']").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		$('#sysInternUserInformationTable').bootstrapTable('refresh');
	});
}); //结束document.ready

//获取选择列id		
function getIdSelections() {
	return $.map($("#sysInternUserInformationTable").bootstrapTable('getSelections'), function (row) {
		return row.id
	});
}

//批量删除
function deleteAll(){
	jp.confirm('确认要删除选中实习生入职记录吗？', function(){
		//jp.loading();  	
		jp.get("${ctx}/sysuserinterentry/sysInternUserInformation/deleteAll?ids=" + getIdSelections(), function(data){
			if(data.success){
				$('#sysInternUserInformationTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

//添加   
function add(){
	var btns = '暂存,提交,取消';
	var params = 'flag:暂存,提交';
	jp.openDialog('添加实习生入职', "${ctx}/sysuserinterentry/sysInternUserInformation/form",'1000px', '100%', $('#sysInternUserInformationTable'), btns, params);
}

//编辑，没有权限时，不显示保存按钮
function edit(id,isView){
	if(id == undefined){
		id = getIdSelections();
	}
	if (isView) {
		jp.openDialogView('查看实习生入职', "${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set_audit&id=" + id, '1000px', '100%');
	} else {
	<shiro:hasPermission name="sysuserinterentry:sysInternUserInformation:edit">
		jp.openDialog('编辑实习生入职', "${ctx}/sysuserinterentry/sysInternUserInformation/form?id=" + id, '1000px', '100%', $('#sysInternUserInformationTable'));
	</shiro:hasPermission>
	<shiro:lacksPermission name="sysuserinterentry:sysInternUserInformation:edit">
		jp.openDialogView('查看实习生入职', "${ctx}/sysuserinterentry/sysInternUserInformation/form?viewFlag=set_audit&id=" + id, '1000px', '100%');
	</shiro:lacksPermission>
	}
}

//单条删除
function del(id){
	jp.confirm('确认要删除该实习生入职吗？',  function(){
		jp.get("${ctx}/sysuserinterentry/sysInternUserInformation/delete?id=" + id, function(data){
			if(data.success){
				$('#sysInternUserInformationTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

function getParamStr(operation){
	var paramStr = '';
	var btns = operation.split(',');
	for (var i = 0; i < btns.length; i++) {
		paramStr += '{"button":"' + btns[i].split('_')[0] 
				 + '","type":"' + btns[i].split('_')[1] 
				 + '","flag":"' + btns[i].split('_')[1] 
				 + '"};';
	}
	return paramStr;
}
</script>