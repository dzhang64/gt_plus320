<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>SSO登陆验证111</title>
	<script src="<%= request.getContextPath() %>/static/jquery/jquery-2.1.1.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			var loginName = "${loginName}";
			var password = "${password}";
			window.addEventListener('message', function(e) {
				if (e.source != window.parent) return;
				var message = "";
				if (loginName != "" && password != "") message = loginName + "," + password;
				window.parent.postMessage(message, '*');
			}, false);
		});
	</script>
</head>
<body class="gray-bg">
</body>
</html>