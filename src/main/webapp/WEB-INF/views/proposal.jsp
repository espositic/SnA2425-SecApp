<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Dettaglio proposta</title></head>
<body>
<h2><%= request.getAttribute("proposal") != null ? ((it.uniba.secapp.model.Proposal)request.getAttribute("proposal")).getTitle() : "" %></h2>
<nav>
    <a href="<%= request.getContextPath() %>/proposals">Indietro</a>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>
<pre><%= (String) request.getAttribute("content") %></pre>
</body></html>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Dettaglio proposta</title></head>
<body>
<h2><%= request.getAttribute("proposal") != null ? ((it.uniba.secapp.model.Proposal)request.getAttribute("proposal")).getTitle() : "" %></h2>
<nav>
    <a href="<%= request.getContextPath() %>/proposals">Indietro</a>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>
<pre><%= (String) request.getAttribute("content") %></pre>
</body></html>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Dettaglio proposta</title></head>
<body>
<h2><%= request.getAttribute("proposal") != null ? ((it.uniba.secapp.model.Proposal)request.getAttribute("proposal")).getTitle() : "" %></h2>
<nav>
    <a href="<%= request.getContextPath() %>/proposals">Indietro</a>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>
<pre><%= (String) request.getAttribute("content") %></pre>
</body></html>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Dettaglio proposta</title></head>
<body>
<h2><%= request.getAttribute("proposal") != null ? ((it.uniba.secapp.model.Proposal)request.getAttribute("proposal")).getTitle() : "" %></h2>
<nav>
    <a href="<%= request.getContextPath() %>/proposals">Indietro</a>
    <form method="post" action="<%= request.getContextPath() %>/logout" style="display:inline;">
        <button type="submit">Logout</button>
    </form>
</nav>
<hr/>
<pre><%= (String) request.getAttribute("content") %></pre>
</body></html>
