<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>柱状图管理</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="chartBarList.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
	<div class="ibox">
	<div class="ibox-title">
		<h5>柱状图列表 </h5>
		<div class="ibox-tools">
			<a class="collapse-link">
				<i class="fa fa-chevron-up"></i>
			</a>
			<a class="dropdown-toggle" data-toggle="dropdown" href="#" style="display:none">
				<i class="fa fa-wrench"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="#">选项1</a>
				</li>
				<li><a href="#">选项2</a>
				</li>
			</ul>
			<a class="close-link">
				<i class="fa fa-times"></i>
			</a>
		</div>
	</div>
    
    <div class="ibox-content">
	<sys:message content="${message}"/>
	
	<!--查询条件-->
	<div class="row">
	<div class="col-sm-12">
	<%-- <form:form id="searchForm" modelAttribute="chartBar" action="${ctx}/chart/bar/chartBar/" method="post" class="form-inline">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ownerCode" name="ownerCode" type="hidden" value="${ownerCode}" />
		<table:sortColumn id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
		<div class="form-group pull-right">
			<a id="search" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><i class="fa fa-search"></i> 查询</a>
			<a id="reset" class="btn btn-primary btn-rounded  btn-bordered btn-sm" ><i class="fa fa-refresh"></i> 重置</a>
		</div>
	</form:form> --%>
	<br/>
	</div>
	</div>
	
	<!-- 工具栏 -->
	<%-- <div id="toolbar">
		<shiro:hasPermission name="chart:bar:chartBar:add">
		</shiro:hasPermission>
		<shiro:hasPermission name="chart:bar:chartBar:edit">
		</shiro:hasPermission>
		<shiro:hasPermission name="chart:bar:chartBar:del">
		</shiro:hasPermission>
		<shiro:hasPermission name="chart:bar:chartBar:import">
		</shiro:hasPermission>
	</div> --%>

	<!-- 表格 -->
	<!-- <table id="chartBarTable"   data-toolbar="#toolbar" data-id-field="id"></table> -->
	
	<!-- 柱状图 -->
	<%--
	<chart:bar barData="${bar }"></chart:bar>
	--%>
	<div id="bar" style="width: 100%; height:300%;"></div>
<script src="${ctxStatic }/common/echarts.js"></script>
<script type="text/javascript">
var myChart = echarts.init(document.getElementById('bar'));
var xAxisData = [];
var data1 = [];
var data2 = [];
for (var i = 0; i < 100; i++) {
    xAxisData.push('类目' + i);
    data1.push((Math.sin(i / 5) * (i / 5 -10) + i / 6) * 5);
    data2.push((Math.cos(i / 5) * (i / 5 -10) + i / 6) * 5);
}
myChart.setOption({
    title: {
        text: '${barData.title}',
        subtext: '${barData.subTitle}'
    },
    legend: {
        data: [${barData.barName}],
        align: 'left'
    },
    toolbox: {
        // y: 'bottom',
        feature: {
            magicType: {
                type: ['stack', 'tiled']
            },
            restore: {},
            saveAsImage: {
                pixelRatio: 2
            }
        }
    },
    tooltip: {},
    xAxis: {
        data: ${barData.xAxisData},
        silent: false,
        splitLine: {
            show: false
        }
    },
    yAxis: {
    },
    series: [{
        name: 'bar1',
        type: 'bar',
        data: ${barData.bar1},
        animationDelay: function (idx) {
            return idx * 10;
        }
    }, {
        name: 'bar2',
        type: 'bar',
        data: ${barData.bar2},
        animationDelay: function (idx) {
            return idx * 10 + 100;
        }
    }],
    animationEasing: 'elasticOut',
    animationDelayUpdate: function (idx) {
        return idx * 5;
    }
});
</script>

	<!-- context menu -->
    <ul id="context-menu" class="dropdown-menu" style="display:none">
    	<%-- 用于右键菜单
    	<shiro:hasPermission name="chart:bar:chartBar:edit">
        <li data-item="edit"><a>编辑</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="chart:bar:chartBar:del">
        <li data-item="delete"><a>删除</a></li>
        </shiro:hasPermission>
        <li data-item="action1"><a>取消</a></li>
        --%>
    </ul>  
	<br/>
	<br/>
	</div>
	</div>
</div>
</body>
</html>