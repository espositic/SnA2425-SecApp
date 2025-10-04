<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Registrazione</title></head>
<body>
<h2>Registrazione</h2>
<% String err = (String) request.getAttribute("error"); if (err != null) { %>
<p style="color:red;"><%= err %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/register">
    <label>Email: <input type="email" name="email" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <label>Conferma: <input type="password" name="confirm_password" required></label><br>
    <button type="submit">Registrati</button>
</form>
<p>Hai già un account? <a href="<%= request.getContextPath() %>/login">Login</a></p>
</body></html>
