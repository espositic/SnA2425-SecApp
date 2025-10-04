<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.uniba.secapp.model.Proposal" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Le mie proposte</title></head>
<body>
<h2>Le mie proposte</h2>
<nav>
    <a href="<%= request.getContextPath() %>/dashboard">Dashboard</a>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>
<%
    List<Proposal> list = (List<Proposal>) request.getAttribute("proposals");
    if (list == null || list.isEmpty()) {
%>
<p>Nessuna proposta.</p>
<% } else { %>
<ul>
    <% for (Proposal p : list) { %>
    <li>
        <a href="<%= request.getContextPath() %>/proposal?id=<%= p.getId() %>">
            <%= p.getTitle() %>
        </a>
    </li>
    <% } %>
</ul>
<% } %>
</body></html>
