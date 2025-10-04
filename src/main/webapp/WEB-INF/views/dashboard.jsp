<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Dashboard</title></head>
<body>
<h2>Dashboard</h2>
<nav>
    <a href="<%= request.getContextPath() %>/proposals">Le mie proposte</a>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>

<h3>Carica proposta (.txt)</h3>
<% String err = (String) request.getAttribute("error"); if (err != null) { %>
<p style="color:red;"><%= err %></p>
<% } %>
<form method="post" action="<%= request.getContextPath() %>/upload" enctype="multipart/form-data">
    <label>Titolo: <input type="text" name="title" required></label><br>
    <label>File .txt: <input type="file" name="proposal" accept=".txt" required></label><br>
    <button type="submit">Invia</button>
</form>
</body></html>
