<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/webpage/include/taglib.jsp"%>
<div id="moreDiv" style="display: block;">
<table class="table table-striped table-bordered table-condensed">
	<tr>
		<th>执行环节</th>
		<th>执行人</th>
		<th>开始时间</th>
		<th>结束时间</th>
		<th>任务历时</th>
		<th>提交意见</th>
	</tr>
	<c:forEach items="${histoicFlowList}" var="act">
		<tr>
			<td>${act.histIns.activityName}</td>
			<td>
				<c:if var="assigneeNameflag" test="${act.assigneeName == null || act.assigneeName == ''}">
					-
				</c:if>
				<c:if test="${!assigneeNameflag }">
					${act.assigneeName }
				</c:if>
			</td>
			<td><fmt:formatDate value="${act.histIns.startTime}" type="both"/></td>
			<td><fmt:formatDate value="${act.histIns.endTime}" type="both"/></td>
			<td>
				<c:if var="durationTimeflag" test="${act.durationTime == null || act.durationTime == ''}">
					小于1秒
				</c:if>
				<c:if test="${!durationTimeflag }">
					${act.durationTime }
				</c:if>
			</td>
			<td style="word-wrap:break-word;word-break:break-all;">
				<c:if var="commentflag" test="${act.comment == null || act.comment == ''}">
					-
				</c:if>
				<c:if test="${!commentflag }">
					${act.comment}
				</c:if>
			</td>
		</tr>
	</c:forEach>
</table>
</div>