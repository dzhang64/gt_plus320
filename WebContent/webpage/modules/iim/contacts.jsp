<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<html>
<head>
<title>通讯录</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
		function search(n,s){
			$("#searchForm").attr("action","${ctx}/iim/contact/index");
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>

<body class="gray-bg">
	<div class="wrapper wrapper-content  animated fadeInRight">
		<div class="row">
			<div class="col-sm-12">
				<div class="ibox">
					<div class="ibox-title">
						<h5>我的通讯录</h5>
					</div>
					<div class="ibox-content">
						<p>可以向通讯录中的人发送，站内信，短消息和发起会话。</p>
						<form:form id="searchForm" modelAttribute="user"
							action="${ctx}/iim/contact/index" method="post"
							class="input-group">
							<form:input path="name" htmlEscape="false" maxlength="50"
								placeholder="查找联系人" class="input form-control" />
							<span class="input-group-btn">
								<button type="button" class="btn btn btn-primary"
									onclick="return search();">
									<i class="fa fa-search"></i> 搜索
								</button> </span>
						</form:form>
						<div class="clients-list">
							<ul class="nav nav-tabs">
								<span class="pull-right small text-muted">${fn:length(list)}
									个联系人</span>
							</ul>
							<div class="tab-content">
								<div id="tab-1" class="tab-pane active">
									<div class="full-height-scroll">
										<div class="table-responsive">
											<table class="table table-striped table-hover">
												<tbody>

													<c:forEach items="${list}" var="user">
														<tr>
															<td class="client-avatar"><img alt="image"
																src="${user.photo}"></td>
															<td><a data-toggle="tab" href="#contact-1"
																class="client-link">${user.name}</a>
															<td>${user.office.name}</td>
															<td class="contact-type"><i class="fa fa-envelope">
															</i></td>
															<td>${user.email}</td>
															<td class="contact-type"><i class="fa fa-mobile">
															</i></td>
															<td>${user.mobile}</td>
															<td class="contact-type"><i class="fa fa-phone">
															</i></td>
															<td>${user.phone}</td>
															<td class="contact-type"><a
																href="${ctx}/iim/mailCompose/sendLetter?id=${user.id}"
																class="btn btn-info btn-xs"><i
																	class="fa fa-envelope"> 站内信</i>
															</a></td>
															<td class="contact-type"><a
																class="btn btn-info btn-xs"><i class="fa fa-qq">
																		即时聊天</i>
															</a></td>
															<td class="client-status"><c:if
																	test="${user.loginFlag == '1'}">
																	<span class="label label-primary">激活</span>
																</c:if> <c:if test="${user.loginFlag == '0'}">
																	<span class="label label-error">未激活</span>
																</c:if></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>

							</div>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 全局js -->
	<script>
        $(function () {
            $('.full-height-scroll').slimScroll({
                height: '100%'
            });
        });
    </script>
</body>

</html>