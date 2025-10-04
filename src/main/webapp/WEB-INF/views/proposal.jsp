<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Dettaglio proposta</title></head>
<body>
<h2><c:out value="${proposal.title}"/></h2>
<nav>
    <a href="<c:url value='/proposals'/>">Indietro</a>
    <form method="post" action="<c:url value='/logout'/>" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>
<pre><c:out value="${content}"/></pre>
</body></html>
