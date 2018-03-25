<%@ page language="java" %><%@ page import="j.*" %>
<%
 Settings s = new Settings( getServletContext() );
%>
<H2>Settings</h2>
<p>
<a href=index.jsp>JBlog Control</a> &gt; Settings
<p>
Changing these settings updates the, "/WEB-INF/settings.prop" file which maps on the filesystem to <%= getServletContext().getRealPath("/WEB-INF/settings.prop") %>
<p>
<form action='settingsProc.jsp'>
<div style="border:2px dotted black">
<table cellpadding='5' >
  <tr><td>Public URL of blog<td><input name="pubURL" size="50" type="text" value="<%=s.pubURL%>">
  <tr bgcolor='#EEFFEE'><td style="background:#EEFFEE;">Publish directory <br>(where blog files get written)<td style="background:#EEFFEE;"><input style="background:#EEFFEE;" name="pubDir" size="50" type="text" value="<%=s.pubDir%>">
  <tr><td>Where data files are kept<td><input name="dataDir" size="50" type="text" value="<%=s.dataDir%>">
  <tr bgcolor='#EEFFEE'><td>Number of articles on first page<td><input style="background:#EEFFEE;" name="articlesOnMainPage" type="text" size="2" value="<%=s.articlesOnMainPage%>">
  <tr><td><input type="submit" value="Change">
</table>      
</div>
</form>
  <p>

  These settings are a automatically dervived from the above settings.
<p>
<table>
<tr><td>pubIndex<td><%=s.pubIndex%><td>from pubDir
<tr><td>pubIndex<td><%=s.pubIndex%><td>from pubDir
<tr><td>pubOldIndex<td><%=s.pubOldIndex%><td>from pubDir
<tr><td>imgDir<td><%=s.imgDir%><td>from pubDir

<tr><td>artDir<td><%=s.artDir%><td>from dataDir
<tr><td>templatesDir<td><%=s.templatesDir%><td>from dataDir
<tr><td>template<td><%=s.template%><td>from templatesDir
<tr><td>oldTemplate<td><%=s.oldTemplate%><td>from templatesDir

</table>

