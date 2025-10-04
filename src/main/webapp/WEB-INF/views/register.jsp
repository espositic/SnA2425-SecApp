<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Registrazione</title></head>
<body>
<h2>Registrati</h2>
<% String err = (String) request.getAttribute("error"); if (err != null) { %>
<p style="color:red;"><%= err %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/register" enctype="multipart/form-data">
    <label>Email: <input type="email" name="email" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <label>Conferma password: <input type="password" name="confirmPassword" required></label><br>
    <label>Foto profilo: <input type="file" name="profilePic" accept=".png,.jpg,.jpeg"></label><br>
    <button type="submit">Registrati</button>
</form>
</body></html>
