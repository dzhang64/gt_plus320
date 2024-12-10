<%@ page contentType="text/html;charset=UTF-8" %>
	<script>
$(document).ready(function() {
	$('#userInfoTable').bootstrapTable({
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
		url: "${ctx}/userinfo/userInfo/data", //这个接口需要处理bootstrap table传递的固定参数,并返回特定格式的json数据
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
				jp.confirm('确认要删除该员工信息记录吗？', function(){
					jp.loading();
					jp.get("${ctx}/userinfo/userInfo/delete?id="+row.id, function(data){
						if(data.success){
							$('#userInfoTable').bootstrapTable('refresh');
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
				field: 'loginName',
				title: '登录名',
				sortable: true
				
			}
			,{
				field: 'idCardNum',
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
				field: 'nameCode',
				title: '工号',
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
				field: 'ssc.name',
				title: '社保交纳地',
				sortable: true
				
			}
			,{
				field: 'salaryCardNum',
				title: '工资卡号',
				sortable: true
				
			}
			,{
				field: 'coordinateNum',
				title: '统筹号',
				sortable: true
				
			}
			,{
				field: 'providentNum',
				title: '公积金账号',
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
				field: 'comMail',
				title: '公司邮箱',
				sortable: true
				
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
				field: 'internBeginDate',
				title: '实习开始日期',
				sortable: true
				
			}
			,{
				field: 'internEndDate',
				title: '实习结束日期',
				sortable: true
				
			}
			,{
				field: 'trialBeginDate',
				title: '试用期开始日期',
				sortable: true
				
			}
			,{
				field: 'trialEndDate',
				title: '试用期结束日期',
				sortable: true
				
			}
			,{
				field: 'turnPositiveDate',
				title: '转正日期',
				sortable: true
				
			}
			,{
				field: 'gradeSchool',
				title: '毕业院校',
				sortable: true
				
			}
			,{
				field: 'major',
				title: '毕业专业',
				sortable: true
				
			}
			,{
				field: 'gradeDay',
				title: '毕业日期',
				sortable: true
				
			}
			,{
				field: 'ed',
				title: '学历',
				sortable: true
				
			}
			,{
				field: 'Bachelor',
				title: '学位',
				sortable: true
				
			}
			,{
				field: 'foreignLanguageLevel',
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
			,{ //操作列
				field: 'operate',
				title: '操作',
				events: {
					'click .view': function (e, value, row, index) {
						jp.openDialogView('查看员工信息', '${ctx}/userinfo/userInfo/form?viewFlag=set_audit&id=' + row.id, '1000px', '100%');
					},
					'click .edit': function (e, value, row, index) {
						var windowName = $(".edit").attr("dealname")+'员工信息';
						var btns = $(".edit").attr("dealbtn");
						var params = $(".edit").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/userinfo/userInfo/form?id=' + row.id, '1000px', '100%', $('#userInfoTable'), btns, params);
					},
					'click .editchange': function (e, value, row, index) {
						var windowName = $(".editchange").attr("dealname")+'员工信息';
						var btns = $(".editchange").attr("dealbtn");
						var params = $(".editchange").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/userinfo/userInfo/formchange?id=' + row.id, '1000px', '70%', $('#userInfoTable'), btns, params);
					},
					'click .edit2': function (e, value, row, index) {
						var windowName = $(".edit2").attr("dealname")+'员工信息';
						var btns = $(".edit2").attr("dealbtn");
						var params = $(".edit2").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/userinfo/userInfo/form?viewFlag=set&id=' + row.id, '1000px', '100%', $('#userInfoTable'), btns, params);
					},
					'click .edit3': function (e, value, row, index) {
						var windowName = $(".edit3").attr("dealname")+'员工信息';
						var btns = $(".edit3").attr("dealbtn");
						var params = $(".edit3").attr("dealdata");
						jp.openDialog(windowName, '${ctx}/userinfo/userInfo/form?viewFlag=set&id=' + row.id, '1000px', '100%', $('#userInfoTable'), btns, params);
					},
					'click .del': function (e, value, row, index) {
						del(row.id);
					}
				},
				formatter: function operateFormatter(value, row, index) {
					var editLink = ' <a href="#" class="btn btn-success btn-xs editchange" dealname="编辑"><i class="fa fa-edit"></i> 修改</a>';
					return [
						<shiro:hasPermission name="userinfo:userInfo:view">
						'<a href="#" class="btn btn-info btn-xs view" ><i class="fa fa-search-plus"></i> 查看</a>',
						</shiro:hasPermission>
				        <shiro:hasPermission name="userinfo:userInfo:edit">
						editLink,
						</shiro:hasPermission>
					].join('');
				}
			}
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
		$('#userInfoTable').bootstrapTable("toggleView");
	}
	$('#userInfoTable').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
		'check-all.bs.table uncheck-all.bs.table', function () {
		$('#remove').prop('disabled', ! $('#userInfoTable').bootstrapTable('getSelections').length);
		$('#edit').prop('disabled', $('#userInfoTable').bootstrapTable('getSelections').length!=1);
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
				window.location='${ctx}/userinfo/userInfo/import/template';
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
		$('#userInfoTable').bootstrapTable('refresh');
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input[name!='ownerCode']").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		$('#userInfoTable').bootstrapTable('refresh');
	});
}); //结束document.ready

//获取选择列id		
function getIdSelections() {
	return $.map($("#userInfoTable").bootstrapTable('getSelections'), function (row) {
		return row.id
	});
}

//批量删除
function deleteAll(){
	jp.confirm('确认要删除选中员工信息记录吗？', function(){
		//jp.loading();  	
		jp.get("${ctx}/userinfo/userInfo/deleteAll?ids=" + getIdSelections(), function(data){
			if(data.success){
				$('#userInfoTable').bootstrapTable('refresh');
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}

//添加   
function add(){
	jp.openDialog('添加员工信息', "${ctx}/userinfo/userInfo/form",'1000px', '100%', $('#userInfoTable'));
}

//编辑，没有权限时，不显示保存按钮
function edit(id,isView){
	if(id == undefined){
		id = getIdSelections();
	}
	if (isView) {
		jp.openDialogView('查看员工信息', "${ctx}/userinfo/userInfo/form?viewFlag=set_audit&id=" + id, '1000px', '100%');
	} else {
	<shiro:hasPermission name="userinfo:userInfo:edit">
		jp.openDialog('编辑员工信息', "${ctx}/userinfo/userInfo/form?id=" + id, '1000px', '100%', $('#userInfoTable'));
	</shiro:hasPermission>
	<shiro:lacksPermission name="userinfo:userInfo:edit">
		jp.openDialogView('查看员工信息', "${ctx}/userinfo/userInfo/form?viewFlag=set_audit&id=" + id, '1000px', '100%');
	</shiro:lacksPermission>
	}
}

//单条删除
function del(id){
	jp.confirm('确认要删除该员工信息吗？',  function(){
		jp.get("${ctx}/userinfo/userInfo/delete?id=" + id, function(data){
			if(data.success){
				$('#userInfoTable').bootstrapTable('refresh');
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