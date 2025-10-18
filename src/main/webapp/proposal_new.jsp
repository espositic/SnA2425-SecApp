<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nuova proposta — Sicurezza nelle Applicazioni</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="card" style="max-width:720px;">
    <h2 style="margin:0 0 12px 0;">Nuova proposta</h2>

    <c:if test="${not empty param.err}">
        <p style="color:#b00020;"><c:out value="${param.err}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/proposals/new" enctype="multipart/form-data">
        <div class="form-row">
            <label>Titolo (3–200)</label>
            <input class="input" type="text" name="title" maxlength="200" required>
        </div>

        <div class="form-row">
            <label>Allega file di testo (.txt)</label>
            <input class="input" type="file" name="textfile" accept=".txt,text/plain" required>
        </div>

        <button class="btn" type="submit">Pubblica</button>
        <a class="btn secondary" href="${pageContext.request.contextPath}/proposals" style="margin-left:8px;">Annulla</a>
    </form>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
