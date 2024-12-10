<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
<title>分配成员</title>
<meta name="decorator" content="blank" />
<%@include file="/webpage/include/treeview.jsp"%>
<script type="text/javascript">
	
		var officeTree;
		var selectedTree;//zTree已选择对象
		var userTree;//zTree已选择对象
		var hiddenNodes=[];	 //声明变量保存隐藏的treeNode
		
		// 初始化
		$(document).ready(function(){
			officeTree = $.fn.zTree.init($("#officeTree"), setting, officeNodes);
			selectedTree = $.fn.zTree.init($("#selectedTree"), setting, selectedNodes);
			$("#search-bt").click(filter);
		});

		var setting = {view: {selectedMulti:false,nameIsHTML:true,showTitle:false,dblClickExpand:false},
				data: {simpleData: {enable: true}},
				callback: {onClick: treeOnClick}};
		
		var officeNodes=[
	            <c:forEach items="${officeList}" var="office">
	            {id:"${office.id}",
	             pId:"${not empty office.parent?office.parent.id:0}", 
	             name:"${office.name}"},
	            </c:forEach>];
	
		var pre_selectedNodes =[
   		        <c:forEach items="${userList}" var="user">
   		        {id:"${user.id}",
   		         pId:"0",
   		         name:"<font color='red' style='font-weight:bold;'>${user.name}</font>"},
   		        </c:forEach>];
		
		var selectedNodes =[
		        <c:forEach items="${usertoorgList}" var="user">
		        {id:"${user.id}",
		         pId:"0",
		         name:"<font color='red' style='font-weight:bold;'>${user.name}</font>"},
		        </c:forEach>  ];
		
		var pre_ids = "${selectIds}".split(",");
		var ids = new Array();
	
		
		function filter(){
		
		if(typeof userTree == "undefined" || userTree == null || userTree == "")
		{
		     alert("请先选择部门");
		     return false;    
		}
	     //显示上次搜索后背隐藏的结点
	     userTree.showNodes(hiddenNodes);
	     //查找不符合条件的叶子节点
	     function filterFunc(node){
		   var _keywords=$("#dicKey").val();
		      if(node.isParent||node.name.indexOf(_keywords)!=-1) return false;
		      return true;		
	         };
	        //获取不符合条件的叶子结点         
	       hiddenNodes=userTree.getNodesByFilter(filterFunc);
	
	      //隐藏不符合条件的叶子结点
	       userTree.hideNodes(hiddenNodes);
          };
		
		//点击选择项回调
		function treeOnClick(event, treeId, treeNode, clickFlag){
			$.fn.zTree.getZTreeObj(treeId).expandNode(treeNode);
			if("officeTree"==treeId){
				$.get("${ctx}/userinfo/userInfo/users?officeId=" + treeNode.id, function(userNodes){
					userTree = $.fn.zTree.init($("#userTree"), setting, userNodes);
				});
				
			
			}
			if("userTree"==treeId){
				//alert(treeNode.id + " | " + ids);
				//alert(typeof ids[0] + " | " +  typeof treeNode.id);
				if($.inArray(String(treeNode.id), pre_ids)<0){
				 
					 if($.inArray(String(treeNode.id), ids)<0){
					 
						selectedTree.addNodes(null, treeNode);
						ids.push(String(treeNode.id));
					}
					else{
					 alert("该人员已在添加列表");
					}
				} 
				else{
				alert("该人员已添加");
				}
			};
			if("selectedTree"==treeId){
				alert("请在上一界面删除人员，新添加则需要重新打开页面");
			}
		};
		function clearAssign(){
			var submit = function (v, h, f) {
			    if (v == 'ok'){
					var tips="";
					if(pre_ids.sort().toString() == ids.sort().toString()){
						tips = "未给角色【${role.name}】分配新成员！";
					}else{
						tips = "已选人员清除成功！";
					}
					ids=pre_ids.slice(0);
					selectedNodes=pre_selectedNodes;
					$.fn.zTree.init($("#selectedTree"), setting, selectedNodes);
			    	top.$.jBox.tip(tips, 'info');
			    } else if (v == 'cancel'){
			    	// 取消
			    	top.$.jBox.tip("取消清除操作！", 'info');
			    }
			    return true;
			};
			tips="确定清除角色【${role.name}】下的已选人员？";
			top.$.jBox.confirm(tips, "清除确认", submit);
		};
	</script>
</head>
<body>


	<!--    搜索框 -->
	<div id="userDiv" class="row wrapper wrapper-content">
	<td>
		按名字过滤：<input type="text" style="width:100px" id="dicKey" value="姓名" />
		<button type="button" id="search-bt" class="btn btn-primary btn-sm">搜索</button>
		</td>
	</div>


	<div id="assignRole" class="row wrapper wrapper-content">
		<div class="col-sm-4" style="border-right: 1px solid #A8A8A8;">
			<p>所在部门：</p>
			<div id="officeTree" class="ztree"></div>
		</div>
		<div class="col-sm-4">
			<p>待选人员：</p>
			<div id="userTree" class="ztree"></div>
		</div>
		<div class="col-sm-4"
			style="padding-left:16px;border-left: 1px solid #A8A8A8;">
			<p>已选人员：</p>
			<div id="selectedTree" class="ztree"></div>
		</div>
	</div>
</body>
</html>
