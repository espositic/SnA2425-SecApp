<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Registrati — Sicurezza nelle Applicazioni</title>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="card" style="max-width:620px;">
    <h2 style="margin:0 0 12px 0;">Crea un account</h2>
    <p class="muted" style="margin-top:6px;">Inserisci i tuoi dati. La foto profilo deve essere .png o .jpg (max 2MB).</p>

    <!-- Messaggi -->
    <c:if test="${not empty param.err}">
        <p style="color:#b00020;"><c:out value="${param.err}"/></p>
    </c:if>
    <c:if test="${not empty param.ok}">
        <p style="color:#0a7c2f;"><c:out value="${param.ok}"/></p>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/register"
          enctype="multipart/form-data"
          style="margin-top:10px;"
          autocomplete="on">
        <div class="form-row">
            <label>Email</label>
            <input class="input"
                   type="email"
                   name="email"
                   required
                   autocomplete="email"
                   spellcheck="false"
                   maxlength="255"
                   autofocus>
        </div>

        <div class="form-row">
            <label>Password <span class="muted">(min 8, 1 maiuscola, 1 minuscola, 1 numero)</span></label>
            <input class="input"
                   type="password"
                   name="password"
                   required
                   autocomplete="new-password"
                   minlength="8"
                   pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}"
                   title="Almeno 8 caratteri, con 1 maiuscola, 1 minuscola e 1 numero">
        </div>

        <div class="form-row">
            <label>Conferma password</label>
            <input class="input"
                   type="password"
                   name="confirm"
                   required
                   autocomplete="new-password">
        </div>

        <div class="form-row">
            <label>Foto profilo (.png / .jpg)</label>
            <input class="input"
                   type="file"
                   name="image"
                   accept=".png,.jpg,.jpeg,image/png,image/jpeg"
                   required>
            <small class="muted">Verrà verificato il tipo reale con Tika.</small>
        </div>

        <div style="display:flex; gap:10px; align-items:center; margin-top:6px;">
            <button class="btn" type="submit">Registrati</button>
            <a class="btn secondary" href="${pageContext.request.contextPath}/login.jsp">Hai già un account? Accedi</a>
        </div>
    </form>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
</body>
</html>
