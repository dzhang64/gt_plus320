<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
	<title>首页</title>
	<meta name="decorator" content="default"/>
	<%@ include file="/webpage/include/bootstraptable.jsp"%>
	<%@ include file="/webpage/modules/bas/tasknotice/basTaskNoticeListHome.js" %>
	<script type="text/javascript">
		$(document).ready(function() {
		     WinMove();
		});
		
		var base = "${ctx}";
		function test(href, name) {
			var taskMenu = $("#taskMenu", window.top.document);
			var currentTaskMenu = $("#currentTaskMenu", window.top.document);
			if(currentTaskMenu && taskMenu) {
				currentTaskMenu.attr("href", base + href);
				currentTaskMenu.html(name);
				taskMenu.trigger("click");
			}
		}
	</script>
</head>
<body class="gray-bg">
	<div class="wrapper wrapper-content">
   <div class="row">
        <div class="col-sm-12">
        	<div class="ibox float-e-margins">
	        	<div class="ibox-title">
					<h5>任务通知</h5>
					<div class="ibox-tools" style="display:none">
						<a class="collapse-link">
							<i class="fa fa-chevron-up"></i>
						</a>
						<a class="close-link">
							<i class="fa fa-times"></i>
						</a>
					</div>
				</div>
				<!-- 工具栏 -->
				<div id="toolbar">
					<shiro:hasPermission name="bas:tasknotice:basTaskNotice:edit">
						<button id="remove" class="btn btn-sm btn-success" disabled onclick="edit('1',true)">
							<i class="glyphicon glyphicon-edit"></i> 置为已读
						</button>
					</shiro:hasPermission>
				</div>
				<div class="ibox-content">
	            	<table id="basTaskNoticeTable" data-toolbar="#toolbar" data-id-field="id"></table>
	            </div>
            </div>
        </div>
    </div>
      
    
        <div class="row">
            <div class="col-sm-4">

                <div class="ibox float-e-margins">
                     <div class="ibox-title">
                        <h5>Gt_Plus 技术特点</h5> 
                        <div class="ibox-tools">
                            <a class="collapse-link">
                                <i class="fa fa-chevron-up"></i>
                            </a>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="index.html#">
                                <i class="fa fa-wrench"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-user">
                                <li><a href="index.html#">选项1</a>
                                </li>
                                <li><a href="index.html#">选项2</a>
                                </li>
                            </ul>
                            <a class="close-link">
                                <i class="fa fa-times"></i>
                            </a>
                        </div>
                    </div>
                    <div class="ibox-content">
                        <p> Gt_Plus开发平台采用 SpringMVC + MyBatis + BootStrap + Apache Shiro + Ehcache 开发组件 的基础架构,采用面向声明的开发模式， 基于泛型编写极少代码即可实现复杂的数据展示、数据编辑、
表单处理等功能，再配合代码生成器的使用,将J2EE的开发效率提高5倍以上，可以将代码减少50%以上。

                        <ol>
						<li>代码生成器，支持多种数据模型,根据表生成对应的Entity,Service,Dao,Action,JSP等,增删改查/排序/导出导入Excel/权限控制/功能生成直接使用</li>
						<li>基础用户权限，强大数据权限，操作权限控制精密细致，对所有管理链接都进行权限验证，可控制到按钮，对指定数据集权限进行过滤</li>
						<li>简易Excel导入导出，支持单表导出和一对多表模式导出，生成的代码自带导入导出Excel功能</li>
						<li>查询过滤器，查询功能自动生成，后台动态拼SQL追加查询条件；支持多种匹配方式（全匹配/模糊查询/包含查询/不匹配查询） </li>
						<li>UI标签库，针对WEB UI进行标准封装，页面统一采用UI标签实现功能：封装了bootstrap风格的table，检索，部门选择 ，人员选择等常用控件。</li>
						<li>集成百度Echarts，实现曲线图，柱状图，数据等报表</li>
						<li>系统日志监控，详细记录操作日志，可支持追查表修改日志</li>
						<li>WebSocket集成：集成在线聊天系统。</li>
						<li>提供常用工具类封装，日志、缓存、验证、字典、组织机构等，常用标签（taglib），获取当前组织机构、字典等数据。</li>
						<li>工作流组件</li>
						<li>连接池监视：监视当期系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。</li>
						<li>提供APP接口，可以快速和移动APP整合。</li>
						<li>支持多种浏览器: IE, 火狐, Google 等浏览器访问速度都很快</li>
						<li>支持数据库: Mysql,Oracle数据库的支持，但不限于数据库，平台留有其它数据库支持接口等</li>
						<li>要求JDK1.6+</li>
                        </ol>
                    </div>
                </div>
              
            </div>
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                     <div class="ibox-title">
                        <h5>升级日志</h5> <span class="label label-primary">K+</span>
                        <div class="ibox-tools">
                            <a class="collapse-link">
                                <i class="fa fa-chevron-up"></i>
                            </a>
                            <a class="dropdown-toggle" data-toggle="dropdown" href="index.html#">
                                <i class="fa fa-wrench"></i>
                            </a>
                            <ul class="dropdown-menu dropdown-user">
                                <li><a href="index.html#">选项1</a>
                                </li>
                                <li><a href="index.html#">选项2</a>
                                </li>
                            </ul>
                            <a class="close-link">
                                <i class="fa fa-times"></i>
                            </a>
                        </div>
                    </div>
                    <div class="ibox-content">
                        <div class="panel-body">
                            <div class="panel-group" id="version">
                            
                            	<div class="panel panel-default">
                                    <div class="panel-heading">
                                        <h5 class="panel-title">
                                                <a data-toggle="collapse" data-parent="#version" href="#v2.0">v2.0</a><code class="pull-right">2017.5.2更新</code>
                                            </h5>
                                    </div>
                                    <div id="v2.0" class="panel-collapse collapse in">
                                        <div class="panel-body">
                                            <ol>
                                                <li>表单：增加了根据字段显示类型判断是否独占行的逻辑</li>
                                            	<li>修复Bolb字段SQL映射的bug</li>
                                            	<li>修复达梦数据库日期类型的bug</li>
                                            	<li>修复不能修改密码的bug</li>
                                            	<li>修复ace下不显示layim的bug。</li>
                                                <li>修复一对多生成子表时，子表不支持gridselect的bug</li>
                                                <li>修复layim发送文件在ie下的bug</li>
                                            	<li>修复ace菜单构建bug</li>
                                            	<li>优化登录，减少了关联sql查询，大幅减少了首次登录耗时</li>
                                                <li>.........</li>
                                            </ol>
                                        </div>
                                    </div>
                                </div>
                            
                            	
                               
                </div>
            </div>
            </div>
            </div>
            </div>
            <div class="col-sm-4">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>授权信息 </h5>
                    </div>
                    <div class="ibox-content">
                        <p>Gt_Plus快速开发平台，包括以下内容</p>
                        <ol>
                            <li>源码(带注释)；</li>
                            <li>代码生成器；</li>
                            <li>开发者文档；</li>
                            <li>……</li>
                        </ol>
                        <hr>
                    

                    </div>
                </div>
                  
            </div>
        </div>
    </div>
	</div>
</body>
</html>