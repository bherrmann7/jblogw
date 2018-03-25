package j;

import java.io.*;

public class Article {
    public File file;
    public StringBuffer body = new StringBuffer();
    public String date;
    public String categories;
    public String title;
    public String imageURL;
    public String imageWidth;
    public String imageHeight;
    public String link;

    public Article(String date, String categories, String title, String imageURL, String link,
                   String body) {
        this.date = date;
        this.categories = categories;
        this.title = title;
        this.imageURL = imageURL;
        this.imageWidth = "";
        this.imageHeight = "";
        this.link = link;
        this.body = new StringBuffer(body);
    }

    Article(File file) {
        this.file = file;

        try {
            BufferedReader dis = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));

            String first = dis.readLine();
            if (first.equals("version 2")) {
                date = dis.readLine();
                categories = dis.readLine();
            } else {
                date = first;
                categories = "all";
            }
            title = dis.readLine();
            imageURL = dis.readLine();
            imageWidth = dis.readLine();
            imageHeight = dis.readLine();
            link = dis.readLine();

            String line = null;

            while ((line = dis.readLine()) != null)
                body.append(line + "\n");

            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
            body = new StringBuffer(e.toString());
        }
    }


    public String getBody(Settings s) {
        StringBuffer rbody = new StringBuffer();

        if ((imageURL != null) && (imageURL.trim().length() != 0)) {
            if ((link != null) && (link.trim().length() != 0)) {
                rbody.append("<a href='" + link + "'>");
            }

            rbody.append("<img src='" /*+ s.pubURL+*/ + imageURL + "' width='" + imageWidth
                    + "' height='" + imageHeight + "' align='left'>");

            if ((link != null) && (link.trim().length() != 0)) {
                rbody.append("</a>");
            }
        }

        rbody.append("<p>" + body + "</p>");

        return rbody.toString();
    }

    /**
     * used in the edit[.jsp] article(s) view
     */
    public String toHtml(Settings s) {
        Page page = new Page(s.template, "all");
        return page.substitute(s, this, false, true);
    }

    private String toHtml2(String base) {
        StringBuffer sb = new StringBuffer();

        sb
                .append("<table width='99%' border='0' cellspacing='0' cellpadding='0'>");
        sb.append("              <tr bgcolor='#CCCCCC'> ");
        sb
                .append("                <td align='left' valign='top' bgcolor='#003366'>"
                        + base + "<font color='#FFFFFF'>");
        sb.append("<SPAN CLASS='title'>" + title + "</SPAN></font></td>");
        sb
                .append("                <td align='right' bgcolor='#003366'><SPAN CLASS='date'><font color='#CCCCCC'>"
                        + date + "</font></SPAN></td>");
        sb.append("              </tr>");
        sb.append("              <tr> ");
        sb.append("                <td colspan='2' valign='top'>");

        if ((imageURL != null) && (imageURL.trim().length() != 0)) {
            sb.append("<a href='" + link + "'><img src='" + base + "/"
                    + imageURL + "' width='" + imageWidth + "' height='"
                    + imageHeight + "' align='left'></a>");
        }

        sb.append("<p>" + body + "</p>");

        sb.append("                 </td>");
        sb.append("              </tr>");
        sb.append("            </table>");

        return sb.toString();
    }

    private void toOtherHtml(PrintWriter out) {
        out.println("<table width='100%'>");
        out.println("<tr align='left' valign='top'>");
        out.println(" <td bgcolor='#d0ffd0'>");
        out.println(" <font color='black'>" + date + "</font>");

        out.println(" </td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println(" <td>");
        out.println("<a href='" + link + "'><img src='" + imageURL
                + "' width='" + imageWidth + "' height='" + imageHeight
                + "' align='left'></a>");

        out.println("<p>" + body + "</p>");

        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
    }

    public void delete(javax.servlet.ServletContext sc) {
        file.delete();

        // get rid of image
        String fileName = imageURL;
        int d = fileName.indexOf('/');
        fileName = fileName.substring(d + 1);

        Settings s = new Settings(sc
                .getResourceAsStream("/WEB-INF/settings.prop"));
        new File(s.imgDir, fileName).delete();

        Blog.clearMemoryCache();
    }

    public String getCategories() {
        return categories;
    }

    public boolean isInCategory(String cat) {
        String[] x = categories.split(",");
        for (int i = 0; i < x.length; i++) {
            if (cat.equals(x[i]))
                return true;
        }
        return false;
    }

    public String getId() {
        if (file == null)
            return "none-yet";
        return file.getName();
    }
}