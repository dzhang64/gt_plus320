<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>快捷设置</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<link href="${ctxStatic}/plugin/awesome-bootstrap-checkbox/awesome-bootstrap-checkbox.css" type="text/css" rel="stylesheet">
	<script type="text/javascript">
	//ajax取得url的数据
	$(function(){
		$.ajax({  
			type: "post",  
			url: "${ctx}/sys/user/getShortcutData",  
			async: true, // 使用同步方式  
			data: {},
			dataType: "json",  
			success: function(data) { 
				var bodys = data.body.hrefMenuList;
				checkedBox(bodys);
				add();
			}
		}); 
	})
	//checkbox操作选中与不选中
	function add(){
		$("#bodys div div div ins").each(function(){
			$(this).click(function(){
				var h=$(this).parent();
				h.toggleClass("checked");
			})
		})
	}
	//获取ajax拿到的数组进行拼接
	function checkedBox(bodys){
		var html='<div class="row">';
		for(var i=0;i<bodys.length;i++){
			html+='<div class="col-md-3"><div class="i-checks"><div value="';
			if(1==bodys[i].isShow) {
				html+=bodys[i].id;
				html+='" class="icheckbox_square-green checked" style="position: relative;"><input type="checkbox" value="" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> <i></i> ';
				html+=bodys[i].name;
			}else{
				html+=bodys[i].id;
				html+='" class="icheckbox_square-green" style="position: relative;"><input type="checkbox" value="" style="position: absolute; opacity: 0;"><ins class="iCheck-helper" style="position: absolute; top: 0%; left: 0%; display: block; width: 100%; height: 100%; margin: 0px; padding: 0px; background: rgb(255, 255, 255); border: 0px; opacity: 0;"></ins></div> <i></i> ';
				html+=bodys[i].name;
			}
			html+='</div></div>';
		}
		$("#bodys").html(html);
	}
	//响应确认按钮
	function confirm() {
		var newstr=",";
		$("#bodys div div div").each(function(){
			var h = $(this).attr("class");
			if(h=='icheckbox_square-green checked') {
				newstr+=$(this).attr("value")+",";
			}
		})
		$.post("${ctx}/sys/user/saveShortcut",{"userShortcutString":newstr}, 
				function(data){     
					jp.success(data.msg);
		         },"json"    
		); 
	}
	</script>
</head>

<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>快捷设置 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#" style="display:none">
				<i class="fa fa-wrench"></i>
			</a>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    <div class="ibox-content">
		<!--保存按钮-->
		<div class="row">
			<div class="col-sm-12">
				<form:form id="searchForm" class="form-inline">
					<div class="form-group pull-right">
						<a id="confirm" class="btn btn-primary btn-rounded  btn-bordered btn-sm" onclick="confirm()"> <i class="fa fa-floppy-o"></i> 保 存</a>
					</div>
				</form:form>
			</div>
		</div>
		<br />
		<div class="container">
			<div id="bodys"> 
				<!-- checkbox模块加入区域 -->
			</div>
		</div>
	</div>
	</div>
</div>
</body>
</html>