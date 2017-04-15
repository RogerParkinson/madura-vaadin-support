<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:url value="/login" var="loginUrl"/>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<title>${labels.getMessage("nz.co.senanque.login.LoginInfo.title",null, Locale.getDefault())}</title>
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
	<div>
		<form name='loginForm' id='loginf' action='${loginUrl}' method='post'>
			<div>
				<label>
				<select name="locale" class="icon-menu" onchange="this.form.submit()" style="${localeSelectBuilder.getLocale()}">
				${localeSelectBuilder.getLocaleSelect()}
				</select>
				</label>
				<a class="help" href='${labels.getMessage("nz.co.senanque.login.LoginInfo.help.url",null, Locale.getDefault())}' target="_blank">${labels.getMessage("nz.co.senanque.login.LoginInfo.help",null, Locale.getDefault())}</a>
				<label><span class="heading">${labels.getMessage("nz.co.senanque.login.LoginInfo.title",null, Locale.getDefault())}</span></label>
				<label> 
					<span>${labels.getMessage("nz.co.senanque.login.LoginInfo.username",null, Locale.getDefault())}</span><input type="text" name="username" autofocus />
				</label> 
				<label>
					<span>${labels.getMessage("nz.co.senanque.login.LoginInfo.password",null, Locale.getDefault())}</span><input type="password" name="password" />
				</label> 
				<label> <span class="error">
				<c:if test="${not empty param.error}">
				${labels.getMessage(param.error,null, param.error,Locale.getDefault())}
				</c:if>
				<c:if test="${not empty param.msg}">
				${labels.getMessage(param.msg,null, param.msg,Locale.getDefault())}
				</c:if>
				<%-- ${param.error}${param.msg}--%>
				</span>
				</label>
				<label class="submit"> 
					<input type="submit" value='${labels.getMessage("nz.co.senanque.login.LoginInfo.login",null, Locale.getDefault())}' />
				</label>
			</div>
			<div class="logo" title="${applicationVersion}"></div>
		</form>
	</div>
</body>
</html>
<!-- Vaadin-Refresh -->