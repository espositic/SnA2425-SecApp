<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Login</title></head>
<body>
<h2>Login</h2>
<% if (request.getParameter("registered") != null) { %>
<p style="color:green;">Registrazione completata. Ora puoi accedere.</p>
<% } %>
<% if (request.getParameter("logout") != null) { %>
<p style="color:green;">Logout effettuato.</p>
<% } %>
<% String err = (String) request.getAttribute("error"); if (err != null) { %>
<p style="color:red;"><%= err %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/login">
    <label>Email: <input type="email" name="email" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <button type="submit">Entra</button>
</form>
<p>Non hai un account? <a href="<%= request.getContextPath() %>/register">Registrati</a></p>
</body></html>
