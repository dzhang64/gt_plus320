<%@ page contentType="text/html;charset=UTF-8" %>
<script>
$(document).ready(function() {
	
	//绑定查询按扭 
	$("#search").click("click", function() { 
		bindTable();
	});
	
	//绑定重置按扭
	$("#reset").click("click", function() {
		$("#searchForm  input").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		bindTable();
	});
});

//添加   
function add(){
	jp.openDialog('添加机构', "${ctx}/test/tree/testTree/form",'800px', '500px', testTreeTable);
}

//编辑，没有权限时，不显示保存按钮
function edit(id,isView){
	if(id == undefined){
		return;
	}
	if (isView) {
		jp.openDialogView('查看机构', "${ctx}/test/tree/testTree/form?id=" + id,'800px', '500px');
	} else {
	<shiro:hasPermission name="test:tree:testTree:edit">
		jp.openDialog('编辑机构', "${ctx}/test/tree/testTree/form?id=" + id,'800px', '500px', $('#testTreeTable'));
	</shiro:hasPermission>
	<shiro:lacksPermission name="test:tree:testTree:edit">
		jp.openDialogView('查看机构', "${ctx}/test/tree/testTree/form?id=" + id,'800px', '500px');
	</shiro:lacksPermission>
	}
}

//单条删除
function del(id){
	jp.confirm('确认要删除该机构吗？',  function(){
		jp.get("${ctx}/test/tree/testTree/delete?id=" + id, function(data){
			if(data.success){
				bindTable();
				jp.success(data.msg);
			}else{
				jp.error(data.msg);
			}
		});
	});
}
</script>