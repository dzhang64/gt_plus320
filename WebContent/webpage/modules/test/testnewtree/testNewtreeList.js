<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	var $testNewtreeTreeTable=null;  
	$(document).ready(function() {
		$testNewtreeTreeTable=$('#testNewtreeTreeTable').treeTable({  
			theme:'vsStyle',	           
			expandLevel : 5,
			column:0,
			checkbox: false,
			url:'${ctx}/test/testnewtree/testNewtree/getChildren?parentId=',  
			callback:function(item) { 
				var treeTableTpl= $("#testNewtreeTreeTableTpl").html();
				var result = laytpl(treeTableTpl).render({
					row: item
				});
				return result;                   
			},  
			beforeClick: function($testNewtreeTreeTable, id) { 
				//异步获取数据 这里模拟替换处理  
				$testNewtreeTreeTable.refreshPoint(id);  
			},  
			beforeExpand : function($testNewtreeTreeTable, id) {   
			},  
			afterExpand : function($testNewtreeTreeTable, id) {  
			},  
			beforeClose : function($testNewtreeTreeTable, id) {    
		            	
			}  
		});
		$testNewtreeTreeTable.initParents('${parentIds}', "0");//在保存编辑时定位展开当前节点
	});
	
	//添加   
	function add(){
		jp.openDialog('添加测试树', "${ctx}/test/testnewtree/testNewtree/form",'800px', '500px', $testNewtreeTreeTable);
	}
		
	function del(con,id){  
		jp.confirm('确认要删除测试树吗？', function(){
			jp.loading();
			$.get("${ctx}/test/testnewtree/testNewtree/delete?id="+id, function(data){
				if(data.success){
					$testNewtreeTreeTable.del(id);
					jp.success(data.msg);
				}else{
					jp.error(data.msg);
				}
			});
		});
	} 
		
	function refresh(){//刷新
		var index = jp.loading("正在加载，请稍等...");
		$testNewtreeTreeTable.refresh();
		jp.close(index);
	}
</script>
<script type="text/html" id="testNewtreeTreeTableTpl">
			<td><a  href="#" onclick="jp.openDialogView('查看测试树', '${ctx}/test/testnewtree/testNewtree/form?viewFlag=set_audit&id={{d.row.id}}','800px', '500px')">
				{{d.row.name}}
			</a></td>
			<td>
				{{d.row.sort}}
			</td>
			<td>
				{{d.row.remarks}}
			</td>
			<td>
				<div class="btn-group">
			 		<button type="button" class="btn  btn-primary btn-xs dropdown-toggle" data-toggle="dropdown">
						<i class="fa fa-cog"></i>
						<span class="fa fa-chevron-down"></span>
					</button>
				  <ul class="dropdown-menu" role="menu">
					<shiro:hasPermission name="test:testnewtree:testNewtree:view">
						<li><a href="#" onclick="jp.openDialogView('查看测试树', '${ctx}/test/testnewtree/testNewtree/form?viewFlag=set_audit&id={{d.row.id}}','800px', '500px')"><i class="fa fa-search-plus"></i> 查看</a></li>
					</shiro:hasPermission>
					<shiro:hasPermission name="test:testnewtree:testNewtree:edit">
						<li><a href="#" onclick="jp.openDialog('编辑测试树', '${ctx}/test/testnewtree/testNewtree/form?id={{d.row.id}}','800px', '500px', $testNewtreeTreeTable)"><i class="fa fa-edit"></i> 编辑</a></li>
		   			</shiro:hasPermission>
		   			<shiro:hasPermission name="test:testnewtree:testNewtree:del">
		   				<li><a  onclick="return del(this, '{{d.row.id}}')"><i class="fa fa-trash"></i> 删除</a></li>
					</shiro:hasPermission>
		   			<shiro:hasPermission name="test:testnewtree:testNewtree:add">
						<li><a href="#" onclick="jp.openDialog('添加下级测试树', '${ctx}/test/testnewtree/testNewtree/form?parent.id={{d.row.id}}','800px', '500px', $testNewtreeTreeTable)"><i class="fa fa-plus"></i> 添加下级测试树</a></li>
					</shiro:hasPermission>
				  </ul>
				</div>
			</td>
	</script>