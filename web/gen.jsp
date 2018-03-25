<%@ page language="java" %><%@ page import="j.*" %><%

  Blog b = new Blog( getServletContext() );
  b.genBlog();

  Settings s = new Settings( getServletContext() );
  
  int last = request.getRequestURL().toString().lastIndexOf('/');
  String controlPanel = request.getRequestURL().toString().substring(0,last);

  response.sendRedirect( s.pubURL + "?" + System.currentTimeMillis() +"&back="+
		  java.net.URLEncoder.encode(controlPanel, "UTF-8"));
 

%>
