<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:url value="/login" var="loginUrl"/>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<title>${labels.getString("nz.co.senanque.login.LoginInfo.title")}</title>
<link rel="stylesheet" type="text/css" href="resources/login.css"/>
<link rel="stylesheet" type="text/css" media="only screen and (-webkit-min-device-pixel-ratio: 3.0) and (max-device-width: 1080px), screen and (max-device-width: 360px)" href="resources/galaxyS4.css" />
<link rel="stylesheet" type="text/css" media="only screen and (min-device-width : 768px) and (max-device-width : 1024px)" href="resources/ipadMini.css" />
<link rel="shortcut icon" type="image/png" href="resources/favicon.png" />
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="pragma" content="no-cache" />
<meta name="viewport" content="width=device-width,maximum-scale=1.0,minimum-scale=1.0"/>
</head>
<body onload='document.loginForm.user.focus();'>
<%-- 	<p>${pageContext.response.locale}</p> --%>
	<div>
		<form name='loginForm' id='loginf' action='${loginUrl}' method='post'>
			<div>
				<label>
					~FLAGS
				</label>
				<a class="help" href="https://github.com/RogerParkinson/madura-vaadin-support/tree/master/madura-login" target="_blank">${labels.getString("nz.co.senanque.login.LoginInfo.help")}</a>
				<label><span class="heading">${labels.getString("nz.co.senanque.login.LoginInfo.title")}</span></label>
				<label> 
					<span>${labels.getString("nz.co.senanque.login.LoginInfo.username")}</span><input type="text" name="username" autofocus />
				</label> 
				<label>
					<span>${labels.getString("nz.co.senanque.login.LoginInfo.password")}</span><input type="password" name="password" />
				</label> 
				<label> <span class="error">${param.error}${param.msg}</span></label>
				<label class="submit"> 
					<input type="submit" value='${labels.getString("nz.co.senanque.login.LoginInfo.login")}' />
				</label>
			</div>
			<div class="logo" title="${applicationVersion}"></div>
		</form>
	</div>
</body>
</html>
<!-- Vaadin-Refresh -->