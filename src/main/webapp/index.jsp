<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Home — Sicurezza nelle Applicazioni</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="card">
    <h2 style="margin:0 0 12px 0;">Benvenuto 👋</h2>
    <p class="muted" style="margin-top:6px;">
        Questa è l’home dell’app “Sicurezza nelle Applicazioni”.
    </p>

    <hr style="border:none; border-top:1px solid var(--border); margin:16px 0;">

    <c:choose>
        <%-- Se l'utente è autenticato (AuthFilter valorizza authUserId in sessione) --%>
        <c:when test="${not empty sessionScope.authUserId}">
            <p>Sei autenticato. Vai alle sezioni principali:</p>
            <ul>
                <li><a href="${pageContext.request.contextPath}/proposals">Visualizza le Proposte</a></li>
                <li><a href="${pageContext.request.contextPath}/proposal_new.jsp">Crea una Nuova Proposta</a></li>
            </ul>

            <form method="post" action="${pageContext.request.contextPath}/logout" style="display:inline;">
                <button class="btn secondary" type="submit">Logout</button>
            </form>
        </c:when>

        <%-- Se NON è autenticato --%>
        <c:otherwise>
            <p>Per proseguire, effettua il login o registrati:</p>
            <div style="display:flex; gap:10px; flex-wrap:wrap;">
                <a class="btn" href="${pageContext.request.contextPath}/login.jsp">Accedi</a>
                <a class="btn" href="${pageContext.request.contextPath}/register.jsp">Registrati</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
