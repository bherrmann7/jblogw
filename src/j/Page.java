package j;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Page {
    static final String foreach = "{$ foreach $}";
    static final String next = "{$ next $}";
    String header;
    String footer;
    private String template;
    String category;

    Page(File tfile, String category) {
        header = "<p>Header";
        footer = "<p>Header";
        this.category = category;

        try {
            FileInputStream fis = new FileInputStream(tfile);
            byte[] b = new byte[fis.available()];
            fis.read(b);

            String x = new String(b);

            int foreachLoc = x.indexOf(foreach);

            if (foreachLoc == -1) {
                error(foreach + " tag not found");

                return;
            }

            int nextLoc = x.indexOf(next);

            if (nextLoc == -1) {
                error(next + " tag not found");

                return;
            }

            header = x.substring(0, foreachLoc);
            String feedname = category + ".rss";
            header = replaceAll(header, "{$ feedname $}", feedname);
            header = replaceAll(header, "{$ category $}", category);

            template = x.substring(foreachLoc + foreach.length(), nextLoc);

            footer = x.substring(nextLoc + next.length());
            footer = replaceAll(footer, "{$ category $}", category);
            String older = "older" + category + ".html";
            if (category.equals(Blog.ALL)) {
                older = "olderindex.html";
            }
            footer = replaceAll(footer, "{$ olderPage $}", older);

        } catch (Exception e) {
            error("Exception: " + e);
            e.printStackTrace();
        }
    }

    void error(String errmsg) {
        header = "<h2>" + errmsg + "</h2>";
        template = " ";
        footer = " ";
    }

    // date
    // headline (or title)
    // body
    String substitute(Settings s, Article art, boolean rss, boolean imagePublishedAlready) {
        String body = art.getBody(s);
        if (imagePublishedAlready) {
            body = body.replaceAll("<img src='aimg/", "<img src='" + s.pubURL + "aimg/");
        }
        if (rss) {
            body = body.replaceAll("&", "&amp;");
            body = body.replaceAll("<", "&lt;");
            body = body.replaceAll(">", "&gt;");
        }
        String nvalue = "";
        nvalue = replaceAll(template, "{$ body $}", hackBody(body, art.link));
        nvalue = replaceAll(nvalue, "{$ headline $}", art.title);
        nvalue = replaceAll(nvalue, "{$ title $}", art.title);
        nvalue = replaceAll(nvalue, "{$ date $}", art.date);
        nvalue = replaceAll(nvalue, "{$ rssdate $}", rfc822Date(art.date));
        nvalue = replaceAll(nvalue, "{$ id $}", art.getId());
        nvalue = replaceAll(nvalue, "{$ categories $}", createCategoriesString(art, category));
        return nvalue;
    }

    private String hackBody(String body, String link) {
        return body.replaceAll("\\[\\[(.*)\\]\\]", "<a href='" + link + "'>$1</a>");
    }


    public static SimpleDateFormat RFC822DATEFORMAT
            = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);

    java.text.SimpleDateFormat artDateFormat = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy");

    private String rfc822Date(String date) {
        try {
            Date rdate = artDateFormat.parse(date);
            return RFC822DATEFORMAT.format(rdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "date issue";
        }
    }


    private String createCategoriesString(Article art, String activeCategory) {
        String[] c = art.getCategories().split(",");
        StringBuffer sb = new StringBuffer();
        if (c.length == 1)
            sb.append("Category: ");
        else
            sb.append("Categories: ");
        for (int i = 0; i < c.length; i++) {
            if (i != 0)
                sb.append(", ");
            if (c[i].equals(activeCategory))
                sb.append(c[i]);
            else {
                String page = c[i];
                if (page.equals("all"))
                    page = "index";
                try {
                    sb.append("<a href='" + URLEncoder.encode(page, "UTF-8") + ".html'>");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append(c[i]);
                sb.append("<a>");
            }

        }
        return sb.toString();
    }

    String replaceAll(String src, String match, String replace) {
        int loc = -1;

        while ((loc = src.indexOf(match)) != -1) {
            src = src.substring(0, loc) + replace
                    + src.substring(loc + match.length());
        }

        return src;
    }

}