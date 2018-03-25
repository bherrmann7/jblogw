<%@ page language="java"%>
<%@ page import="j.*"%>
<%!public String emit(Blog b, String[] currentCategory) {
		String[] c = b.getCategories();
		List cc = Arrays.asList(currentCategory);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < c.length; i++) {
			boolean isAll = c[i].equals("all");
			sb.append("<input name='catCBs' type='checkbox' value='" + c[i]
					+ "' ");
			if (cc.contains(c[i]) || isAll)
				sb.append("checked ");
			if (isAll)
				sb.append("disabled ");
			sb.append(">" + c[i] + " ");
		}

		return sb.toString();
	}

	public String makeCategory(String[] catCBs, String catBox) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < catCBs.length; i++) {
			sb.append(catCBs[i]);
			sb.append(",");
		}
		sb.append(catBox);
		return Blog.scrubCategories(sb.toString());
	}%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%
	String cancel = request.getParameter("Cancel");
	String date = request.getParameter("date");
	String title = request.getParameter("title");
	String[] catCBs = request.getParameterValues("catCBs");
	String catBox = request.getParameter("catBox");
	String link = request.getParameter("link");
	String iconURL = request.getParameter("iconURL");
	String body = request.getParameter("body");
	String finish = request.getParameter("Finish");
	String artno = (String) request.getParameter("artno");

	InputStream uploadedFileStream = null;
	String uploadedName = null;
	
	boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	if (isMultipart) {

		//Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		//   Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		//   Parse the request
		List /* FileItem */items = upload.parseRequest(request);

	
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
		String name = item.getFieldName();
		String value = item.getString();
		System.out.println(" name:"+name);
		if (name.equals("Cancel"))
			cancel = value;
		if (name.equals("date"))
			date = value;
		if (name.equals("title"))
			title = value;
		if (name.equals("catCBs"))
			catCBs = new String[] { value };
		if (name.equals("catBox"))
			catBox = value;
		if (name.equals("link"))
			link = value;
		if (name.equals("iconURL"))
			iconURL = value;
		if (name.equals("body"))
			body = value;
		if (name.equals("Finish"))
			finish = value;
		if (name.equals("artno"))
			artno = value;
			} else {
				if ( item.getSize()>0){
					uploadedFileStream = item.getInputStream();
					uploadedName = item.getName();
				}
		
			}
		}
	}

	if (cancel != null) {
		response.sendRedirect("index.jsp");
		return;
	}

	if (title == null)
		title = "";
	if (catCBs == null) {
		catCBs = new String[0];
	}
	if (catBox == null)
		catBox = "";
	String category = makeCategory(catCBs, catBox);

	if (date == null) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
		"EEEE, dd MMMM yyyy");
		date = sdf.format(new java.util.Date());
	}
	NImage img;
	if (iconURL == null || iconURL.length() == 0) {
		img = NImage.NONE;
	} else {		
		img = new NImage(getServletContext(), application
		.getRealPath("aimg"), iconURL, true);
	}
	if ( uploadedFileStream!=null ){
		img = new NImage(getServletContext(), application
				.getRealPath("aimg"), uploadedFileStream, uploadedName, true);		
	}
	
	if (link == null || link.trim().length()==0){
		link="";
		if ( img != NImage.NONE  ){
			link = img.getImageOrigURL();			
		}
	}
	
	if (body == null) {
		body = "message body [[about image link here]]... more message...";
	}

	Blog b = new Blog(getServletContext());
	if (finish != null) {
		if (artno == null) {
			b.newStory(date, category, title, img, link, body);
		} else {
			int update = Integer.parseInt(artno);
			Article article = (Article) b.getArticles(b.ALL)
			.get(update);
			b.updateStory(article.file, date, category, title, img,
			link, body);
		}
		Settings s = new Settings(getServletContext());

		int last = request.getRequestURL().toString().lastIndexOf('/');
		String controlPanel = request.getRequestURL().toString()
		.substring(0, last);

		response.sendRedirect(s.pubURL + "?"
		+ System.currentTimeMillis() + "&back="
		+ java.net.URLEncoder.encode(controlPanel, "UTF-8"));
		return;
	}

	Article article = (Article) request.getAttribute("article");
	if (article != null) {
		date = article.date;
		title = article.title;
		Settings s = new Settings(getServletContext());
		img = new NImage(getServletContext(), application
		.getRealPath("aimg"), s.pubURL + "/" + article.imageURL,false);
		link = article.link;
		body = article.body.toString();
		catCBs = article.categories.split(",");
	}
%>

<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.FileItemFactory"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.io.File"%>
<%@page import="java.io.InputStream"%>
<title>Submit Story</title>
<h2>Submit Story</h2>
<p><a href="index.jsp">JBlog Control</a> &gt; New article</p>
<p></p>
<form action="newart.jsp" method="post" ENCTYPE="multipart/form-data">
<%
if (request.getAttribute("artno") != null) {
%> <input type="hidden" name="artno"
	value='<%=request.getAttribute("artno")%>'> <%
 } else if (artno != null) {
 %> <input type="hidden" name="artno" value='<%=artno%>'> <%
 }
 %>

<table>
	<tr>
		<td>Date:</td>
		<td><input type="text" name="date" size="30" value="<%= date %>">
		</td>
	</tr>
	<tr>
		<td>Category:</td>
		<td><%=emit(b, catCBs)%> <br>
		other categories (csv)<input name="catBox" type="text" size="30"
			value="<%= catBox %>"></td>
	</tr>
	<tr>
		<td>Title:</td>
		<td><input type="text" name="title" size="60"
			value="<%= title %>"></td>
	</tr>
	<tr>
		<td>Image URL:</td>
		<td><input type="text" name="iconURL" size="80"
			value="<%=img.getImageURL()%>"></td>
	</tr>
	<tr>
		<td>Image File:</td>
		<td><INPUT NAME="userfile1" TYPE="file" size="60"></td>
	</tr>

	<tr>
		<td>Image Link:</td>
		<td><input type="text" name="link" size="80" value="<%=link%>">
		</td>
	</tr>
</table>
<p>
<textarea name="body" cols="110" rows="10">
<%=body%>
</textarea></p>
<p><input type="submit" name="Cancel" value="Cancel">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="submit" name="Update"
	value="Update"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input
	type="submit" name="Finish" value="Finish">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
<hr>

<%=new Blog(getServletContext()).genSample(new j.Article(
							date, category, title, img.getImageURL(), link,
							body))%> <input type="submit" name="Finish" value="Finish"></form>
