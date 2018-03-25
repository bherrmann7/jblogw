<% 
	j.Settings s = new j.Settings( getServletContext() );	
%>
<%@page import="j.Version"%>
<head>
<title><%= s.shortName %></title>
</head>
<h2>JBlog Control for <a href='<%=s.pubURL %>'><%=s.pubURL %></a></h2>
version: <%= Version.str() %>
<table valign=middle cellpadding=10>
  <tr><td>
<a href=settings.jsp><img src=i/settings.jpg></a><td> <a href=settings.jsp>Settings</a>
  <tr><td>
<a href=newart.jsp><img src=i/pencil.gif></a> <td><a href=newart.jsp>New article</a>
  <tr><td>
<a href=edit.jsp><img src=i/edit.jpg></a> <td><a href=edit.jsp>Edit article</a>
  <tr><td>
<a href=gen.jsp><img src=i/gears.jpg></a> <td><a href=gen.jsp>Generate new weblog</a> (Useful if you edited the Article files or footer/headers and want to regenerate stuff)
</table>

<p>
