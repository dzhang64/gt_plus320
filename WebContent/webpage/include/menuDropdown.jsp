<c:forEach items="menus" var="menu">
	<c:if test="${menu.isShow eq '1'}">
		<c:if test="${menu.parentId eq '1'}">
			<li class="dropdown">
		</c:if>
		<c:if test="${menu.parentId ne '1'}">
			<li class="dropdown-submenu">
		</c:if>
			<c:choose>
				<c:when test="${empty menu.href}">  
					<a aria-expanded="false" role="button" href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-gear"></i>${menu.name}<c:if test="${menu.parentId eq '1'}"><span class="caret"></span></c:if></a>  
				</c:when>
				<c:otherwise> 
					<a class="J_menuItem"  href="${ctx}${menu.href}"><i class="fa fa-gear"></i>${menu.name}</a>
				</c:otherwise>
			</c:choose>
			<ul role="menu" class="dropdown-menu">
				<c:set var="menus" value="${menu.children}" scope="request"/>
                <%@ include file="/webpage/include/menuDropdown.jsp"%>
			</ul>
		</li>
	</c:if>
</c:forEach>