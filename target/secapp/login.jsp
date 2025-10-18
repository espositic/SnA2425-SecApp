<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login — Sicurezza nelle Applicazioni</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="card" style="max-width:520px;">
    <h2 style="margin:0 0 12px 0;">Login</h2>

    <c:if test="${not empty param.err}">
        <p style="color:#b00020;"><c:out value="${param.err}"/></p>
    </c:if>
    <c:if test="${not empty param.ok}">
        <p style="color:#0a7c2f;"><c:out value="${param.ok}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-row">
            <label>Email</label>
            <input class="input" type="email" name="email" required>
        </div>
        <div class="form-row">
            <label>Password</label>
            <input class="input" type="password" name="password" required>
        </div>
        <div class="form-row">
            <label><input type="checkbox" name="remember" value="1"> Ricordami</label>
        </div>
        <button class="btn" type="submit">Entra</button>
    </form>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
