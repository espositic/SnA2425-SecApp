<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Proposte — Sicurezza nelle Applicazioni</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="card">
    <h2 style="margin:0 0 12px 0;">Proposte (visibili a tutti gli utenti autenticati)</h2>

    <c:if test="${not empty param.ok}">
        <p style="color:#0a7c2f;"><c:out value="${param.ok}"/></p>
    </c:if>
    <c:if test="${not empty param.err}">
        <p style="color:#b00020;"><c:out value="${param.err}"/></p>
    </c:if>

    <c:if test="${empty proposals}">
        <p class="muted">Non ci sono ancora proposte. <a href="${pageContext.request.contextPath}/proposal_new.jsp">Crea la prima</a> 🎯</p>
    </c:if>

    <c:if test="${not empty proposals}">
        <table class="table">
            <thead>
            <tr>
                <th style="width:70px;">ID</th>
                <th style="width:260px;">Titolo</th>
                <th>Contenuto (.txt)</th>
                <th style="width:120px;">Autore (id)</th>
                <th style="width:180px;">Data</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${proposals}">
                <tr>
                    <td><c:out value="${p.id}"/></td>
                    <td><strong><c:out value="${p.title}"/></strong></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty p.fileContent}">
                                <pre class="txt"><c:out value="${p.fileContent}"/></pre>
                            </c:when>
                            <c:otherwise><span class="muted">Nessun file</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${p.createdBy}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty p.createdAt}"><c:out value="${p.createdAt}"/></c:when>
                            <c:otherwise><span class="muted">n/d</span></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
