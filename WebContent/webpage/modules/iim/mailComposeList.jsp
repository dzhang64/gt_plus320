<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<!DOCTYPE html>
<html>

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    

    <title>已发送</title>
   	<meta name="decorator" content="default"/>

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content">
        <div class="row">
            <div class="col-sm-3">
                <div class="ibox float-e-margins">
                    <div class="ibox-content mailbox-content">
                        <div class="file-manager">
                            <a class="btn btn-block btn-primary compose-mail" href="${ctx}/iim/mailCompose/sendLetter">写信</a>
                            <div class="space-25"></div>
                            <h5>文件夹</h5>
                            <ul class="folder-list m-b-md" style="padding: 0">
                                <li>
                                    <a href="${ctx}/iim/mailBox/list?orderBy=sendtime desc"> <i class="fa fa-inbox "></i> 收件箱 <span class="label label-warning pull-right">${noReadCount}/${mailBoxCount}</span>
                                    </a>
                                </li>
                                <li>
                                    <a href="${ctx}/iim/mailCompose/list?status=1&orderBy=sendtime desc"> <i class="fa fa-envelope-o"></i> 已发送<span class="label label-info pull-right">${mailComposeCount}</span></a>
                                </li>
                                <!-- 
                                <li>
                                    <a href="${ctx}/iim/mailBox/list"> <i class="fa fa-envelope"></i> 群邮件</a>
                                </li>
                                 -->
                                <li>
                                    <a href="${ctx}/iim/mailCompose/list?status=0&orderBy=sendtime desc"> <i class="fa fa-file-text-o"></i> 草稿箱 <span class="label label-danger pull-right">${mailDraftCount}</span>
                                    </a>
                                </li>
                                 <!-- 等待下个版本升级 byDavid 
                                <li>
                                    <a href="mailbox.html"> <i class="fa fa-trash-o"></i> 垃圾箱</a>
                                </li>
                                -->
                            </ul>
                            <h5>分类</h5>
                            <ul class="category-list" style="padding: 0">
                                <li>
                                    <a href="##"> <i class="fa fa-circle text-navy"></i> 工作</a>
                                </li>
                                <li>
                                    <a href="##"> <i class="fa fa-circle text-danger"></i> 文档</a>
                                </li>
                                <li>
                                    <a href="##"> <i class="fa fa-circle text-primary"></i> 社交</a>
                                </li>
                                <li>
                                    <a href="##"> <i class="fa fa-circle text-info"></i> 广告</a>
                                </li>
                                <li>
                                    <a href="##"> <i class="fa fa-circle text-warning"></i> 客户端</a>
                                </li>
                            </ul>

                            <h5 class="tag-title">标签</h5>
                            <ul class="tag-list" style="padding: 0">
                                <li><a href="#"><i class="fa fa-tag"></i> 朋友</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 工作</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 家庭</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 孩子</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 假期</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 音乐</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 照片</a>
                                </li>
                                <li><a href="#"><i class="fa fa-tag"></i> 电影</a>
                                </li>
                            </ul>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-9 animated fadeInRight">
                <div class="mail-box-header">

                    <form:form  id="searchForm" modelAttribute="mailCompose" action="${ctx}/iim/mailCompose/?status=1" method="post" class="pull-right mail-search">
                    		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
						<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
						<table:sortColumn  id="orderBy" name="orderBy" value="${page.orderBy}" callback="sortOrRefresh();"></table:sortColumn><!-- 支持排序 -->
                        <div class="input-group">
                        	<form:input path="mail.title" htmlEscape="false" maxlength="128"  class=" form-control input-sm" placeholder="搜索邮件标题，正文等"/>
                            <div class="input-group-btn">
                                <button id="btnSubmit" type="submit" class="btn btn-sm btn-primary">
                                    搜索
                                </button>
                            </div>
                        </div>
                    </form:form>
                    <h2>
                    已发送(${mailComposeCount})
                </h2>
                    <div class="mail-tools tooltip-demo m-t-md">
                        <div class="btn-group pull-right">
                        	${page }

                        </div>
                          <button class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新邮件列表"><i class="fa fa-refresh"></i> 刷新</button>
                        <button class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="top" title="标为已读"><i class="fa fa-eye"></i>
                        </button>
                        <button class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="top" title="标为重要"><i class="fa fa-exclamation"></i>
                        </button>
                        <table:delRow url="${ctx}/iim/mailCompose/deleteAllCompose" id="contentTable"></table:delRow><!-- 删除按钮 -->

                    </div>
                </div>
                <div class="mail-box">

                    <table id="contentTable" class="table table-hover table-mail">
                    	<thead> 
                    		<tr>
                    			<th class="check-mail">
	                                <input type="checkbox" class="i-checks">
	                            </th>
                    			<th  class="sort-column receiver.name">收件人</th>
                    			<th  class="sort-column title">标题</th>
                    			<th  class="sort-column overview">内容</th>
                    			<th  class="sort-column sendtime">时间</th>
                    			<th>操作</th>
                    		</tr>
                    	</thead>
                        <tbody>
                        
                        	<c:forEach items="${page.list}" var="mailCompose">
								<tr>
									<td class="check-mail">
	                                    <input id="${mailCompose.id}" type="checkbox" class="i-checks">
	                                </td>
	                                <td class=""><a href="${ctx}/iim/mailCompose/detail?id=${mailCompose.id}">
                                		${mailCompose.receiver.name}
									</a></td>
                                	<td class="mail-ontact"><a href="${ctx}/iim/mailCompose/detail?id=${mailCompose.id}">
                                		
										${mailCompose.mail.title}
									</a></td>
									<td class="mail-subject"><a href="${ctx}/iim/mailCompose/detail?id=${mailCompose.id}">
										${mailCompose.mail.overview}
									</a>
	                                </td>
	                                <td class="mail-date">${fns:formatDateTime(mailCompose.sendtime)}</td>
									
									<td>
										<a href="${ctx}/iim/mailCompose/delete?id=${mailCompose.id}" onclick="return confirmx('确认要删除该站内信吗？', this.href)"   class="btn btn-info btn-xs btn-danger"><i class="fa fa-trash"></i> 删除</a>
									</td>
								</tr>
							</c:forEach>
                         
                        </tbody>
                    </table>


                </div>
            </div>
        </div>
    </div>



    <script>

        function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
    </script>


</body>

</html>