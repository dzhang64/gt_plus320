<%@ page contentType="text/html;charset=UTF-8" %>
<script>
$(document).ready(function() {
	
	//绑定查询按扭 
	$("#search").click("click", function() { 
		bindTable();
		bindTable();
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		bindTable();
		bindTable();
	});
});

//添加   
function add(){
	jp.openDialog('添加区域', "${ctx}/sys/area/form",'800px', '500px', areaTable);
}

//编辑，没有权限时，不显示保存按钮
function edit(id,isView){
	if(id == undefined){
		return;
	}
	if (isView) {
		jp.openDialogView('查看区域', "${ctx}/sys/area/form?viewFlag=set&id=" + id,'800px', '500px');
	} else {
	<shiro:hasPermission name="sys:area:edit">
		jp.openDialog('编辑区域', "${ctx}/sys/area/form?id=" + id,'800px', '500px', areaTable);
	</shiro:hasPermission>
	<shiro:lacksPermission name="sys:area:edit">
		jp.openDialogView('查看区域', "${ctx}/sys/area/form?viewFlag=set&id=" + id,'800px', '500px');
	</shiro:lacksPermission>
	}
}

//单条删除
function del(id){
	jp.confirm('确认要删除该区域及所有子区域吗？',  function(){
		jp.get("${ctx}/sys/area/delete?id=" + id, function(data){
			if(data.success){
				bindTable();
				bindTable();
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}
</script>