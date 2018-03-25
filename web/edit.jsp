<%@ page language="java" %><%@ page import="j.*" %><%

Blog b = new Blog( getServletContext() );
Article[] art = (Article[]) b.getArticles(b.ALL).toArray(new Article[0]);

if ( request.getParameter("edit") != null ){
    int edit = Integer.parseInt(  request.getParameter("article") );
    Article a = art[edit];
    request.setAttribute("article",a);
    request.setAttribute("artno", ""+edit);

    request.getRequestDispatcher("newart.jsp").include(request,response);
    return;
}

if ( request.getParameter("delete") != null ){
    int del = Integer.parseInt(  request.getParameter("article") );
    art[del].delete(getServletContext());

    Blog.clearMemoryCache();
    b.genBlog();

    art = (Article[]) b.getArticles(b.ALL).toArray(new Article[0]);
}

Settings s = new Settings( getServletContext() );

%>
<h2>Blog Editing</h2>
<p>
<a href=index.jsp>JBlog Control</a> &gt; Edit Article
<p>
<p>
  link to <a href=<%=s.pubURL%>>public blog site</a>
<p>
<%
for (int i=0;i<art.length;i++){
%>
   <%=art[i].toHtml(s)%>
   <form>
   <input type=hidden name=article value=<%=i%>>
   <input type=submit name=delete value=delete>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
   <input type=submit name=edit value=edit>
   </form>
<p>
<%
}
%>
