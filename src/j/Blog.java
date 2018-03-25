package j;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Blog {
    private Settings s;

    java.awt.Image awtImage;

    public Blog(ServletContext sc) {
        s = new Settings(sc.getResourceAsStream("/WEB-INF/settings.prop"));
    }

    public Blog(InputStream is) {
        s = new Settings(is);
    }

    public void updateStory(File file, String date, String category,
                            String title, NImage image, String link, String body) {
        // A+datestring.art
        String unique = file.getName().substring(1);
        unique = unique.substring(0, unique.length() - 4);

        writeStory(file, unique, date, category, title, image, link, body);
    }

    public void newStory(String date, String category, String title,
                         NImage image, String link, String body) {
        // add story... unique name is time
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyyMMdd-HHmmss");
        String unique = sdf.format(new java.util.Date());

        File newArticle = new File(s.artDir, "A" + unique + ".art");

        writeStory(newArticle, unique, date, category, title, image, link, body);
    }

    private void writeStory(File file, String unique, String date,
                            String category, String title, NImage image, String link,
                            String body) {
        if (awtImage != null) {
            awtImage.flush();
            awtImage = null;
        }

        // install program...
        s.dataDir.mkdirs();
        s.artDir.mkdirs();
        s.imgDir.mkdirs();
        s.templatesDir.mkdirs();

        // move image to right location
        File imageFile = null;

        if ((image.imgLocalFile != null) && image.imgLocalFile.exists()) {
            File uniqPubFile = genUniqPubFile(s, unique, image.imgLocalFile);
            copyFile(image.imgLocalFile, uniqPubFile);
            imageFile = uniqPubFile;
            image.imageURL = s.imgSubDir + "/" + uniqPubFile.getName();

            File originalFile = NImage.genOrig(image.imgLocalFile);
            if (originalFile.exists()) {
                uniqPubFile = genUniqPubFile(s, unique, originalFile);
                copyFile(originalFile, uniqPubFile);
                String imageOrigURL = s.imgSubDir + "/" + uniqPubFile.getName();
                if (link.equals(image.imageOrigURL)) {
                    link = imageOrigURL;
                }
            }
        }


        File newArticle = new File(s.artDir, "A" + unique + ".art");

        String cleanCatgories = scrubCategories(category);

        try {
            PrintWriter out = new PrintWriter(new FileWriter(newArticle));
            out.println("version 2");
            out.println(date);
            out.println(cleanCatgories);
            out.println(title);
            out.println(image.getImageURL());
            out.println(getWidth(imageFile));
            out.println(getHeight(imageFile));
            out.println(link);
            out.println(body);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearMemoryCache();

        genBlog();
    }

    private File genUniqPubFile(Settings s, String unique, File imgLocalFile) {
        // if already uniqued, then dont add extra junk into it.
        String name = imgLocalFile.getName();
        while (name.matches("I\\d{8}-\\d{6}-.*"))
            name = name.substring(14 + 3);
        String imgName = "I" + unique + "-" + name;
        return new File(s.imgDir, imgName);
    }

    public static String scrubCategories(String category) {
        ArrayList al = new ArrayList();
        String[] x = ("all," + category).split(",");
        for (int i = 0; i < x.length; i++) {
            String cat = x[i].trim();
            if (!al.contains(cat))
                al.add(cat);
        }
        Collections.sort(al);
        StringBuffer clean = new StringBuffer();
        for (int i = 0; i < al.size(); i++) {
            if (i != 0)
                clean.append(",");
            clean.append(al.get(i));
        }
        return clean.toString();
    }

    public String genSample(Article story) {
        StringBuffer sb = new StringBuffer();
        Page page = new Page(s.template, ALL);

        // header
        sb.append(page.header);

        Article sample = new j.Article("Thursday, 07 September 2006", story.categories,
                "Sample Second article ", "", "http://slashdot.org",
                "a sample article to fill out look of preview");

        boolean imagePublishedAlready = new File(s.pubDir, story.imageURL).exists();

        sb.append(page.substitute(s, story, false, imagePublishedAlready));
        sb.append(page.substitute(s, sample, false, false));

        sb.append(page.footer);

        return sb.toString();
    }

    public static String ALL = "all";

    public void genBlog() {

        String cats[] = getCategories();
        for (int i = 0; i < cats.length; i++) {
            String cat = cats[i];
            ArrayList articles = getArticles(cat);

            int max = articles.size();

            if (max > s.articlesOnMainPage) {
                max = s.articlesOnMainPage;
            }

            Article[] newArticles = getArticles(articles, 0, max);
            Article[] oldArticles = getArticles(articles, max, articles.size());

            String pageName = cat;
            if (cat.equals(ALL)) {
                pageName = "index";
            }

            genBlog(new File(s.pubDir, pageName + ".html"), s.template,
                    newArticles, cat);
            genBlog(new File(s.pubDir, "older" + pageName + ".html"),
                    s.oldTemplate, oldArticles, cat);

            genRSS(new File(s.pubDir, cat + ".rss"), new File(s.templatesDir,
                    "feed.rss"), newArticles, cat);
        }
    }

    private Article[] getArticles(ArrayList articles, int start, int endExclusive) {
        Article[] arry = new Article[endExclusive - start];
        int j = 0;
        for (int i = start; i < endExclusive; i++) {
            arry[j++] = (Article) articles.get(i);
        }
        return arry;
    }

    //private static ArrayList<Article> al = null;

    public ArrayList getArticles(String cat) {
//        if (al == null) {
        ArrayList<Article> al = new ArrayList();
        File[] files = s.artDir.listFiles();

        if (files == null) {
            return al;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().startsWith("A")) {
                al.add(new Article(files[i]));
            }
        }
        try {
            File orderFile = new File(s.artDir, "order");
            if (orderFile.exists()) {
                List<String> articleOrder = Files.
                        readAllLines(orderFile.toPath())
                        .stream()
                        .map(s -> {
                            int d = s.indexOf("|");
                            return s.substring(0, d);
                        })
                        .collect(Collectors.toList());
                if (articleOrder.size() != al.size()) {
                    throw new RuntimeException(
                            String.format("Order size %d does not match files size %d",
                                    articleOrder.size(), al.size()));
                }
                List<Article> arts = articleOrder.stream()
                        .map(ao -> al.stream().filter(a -> a.file.getName().equals(ao)).findFirst().get())
                        .collect(Collectors.toList());
                al.clear();
                al.addAll(arts);

            } else {
                Collections.sort(al, new reverse());
            }
            saveOrder(al);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

//        }

        if (cat.equals(ALL)) {
            return al;
        }

        ArrayList subset = new ArrayList();

        for (int i = 0; i < al.size(); i++) {
            Article a = (Article) al.get(i);
            if (a.isInCategory(cat))
                subset.add(al.get(i));
        }

        return subset;
    }

    private void saveOrder(List<Article> al) {
        try {
            File orderFile = new File(s.artDir, "order");
            PrintWriter pw = new PrintWriter(orderFile);
            for (Article article : al) {
                pw.println(article.file.getName() + "|" + article.date + "|" + article.title);
            }
            pw.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    void genBlog(File outFile, File templatePage, Article[] arts, String cat) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outFile));

            Page page = new Page(templatePage, cat);

            // header
            out.println(page.header);

            // handle no articles
            if (arts.length == 0) {
                out.println("<p>No news is good news.</p>");
            }

            for (int i = 0; i < arts.length; i++)
                out.println(page.substitute(s, arts[i], false, false));

            // footer
            out.println(page.footer);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void genRSS(File outFile, File templatePage, Article[] arts, String cat) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(outFile));

            Page page = new Page(templatePage, cat);

            // header
            out.println(page.header);

            for (int i = 0; i < arts.length; i++)
                out.println(page.substitute(s, arts[i], true, true));

            // footer
            out.println(page.footer);

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int getWidth(File imgFile) {
        if ((imgFile == null) || !imgFile.exists()) {
            return -1;
        }

        if (awtImage == null) {
            load(imgFile);
        }

        return awtImage.getWidth(null);
    }

    int getHeight(File imgFile) {
        if ((imgFile == null) || !imgFile.exists()) {
            return -1;
        }

        if (awtImage == null) {
            load(imgFile);
        }

        return awtImage.getHeight(null);
    }

    private void load(File imgFile) {
        System.out.println("Trying to load this image: " + imgFile.toString());

        try {
            awtImage = ImageIO.read(imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
//		awtImage = java.awt.Toolkit.getDefaultToolkit().createImage(
//				imgFile.toString());
//
//		AllBits ab = new AllBits(awtImage, "something");
//		ab.waitForBits();
//

    }

    /*
     * public void main(String args[]){ genBlog(); }
     */
    public void copyFile(File in, File out) {
        try {
            FileInputStream fis = new FileInputStream(in);
            FileOutputStream fos = new FileOutputStream(out);
            byte[] buf = new byte[1024];
            int i = 0;

            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }

            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class reverse implements java.util.Comparator {
        public int compare(Object o1, Object o2) {
            return ((Article) o2).file.getName().compareTo(
                    ((Article) o1).file.getName());
        }
    }

    public String[] getCategories() {
        ArrayList al = getArticles(ALL);
        ArrayList categories = new ArrayList();
        for (int i = 0; i < al.size(); i++) {
            String[] c = ((Article) al.get(i)).getCategories().split(",");
            for (int ac = 0; ac < c.length; ac++) {
                if (!categories.contains(c[ac].trim()))
                    categories.add(c[ac].trim());
            }
        }
        if (categories.size() == 0)
            return new String[]{"all"};
        return (String[]) categories.toArray(new String[categories.size()]);
    }

    public static void clearMemoryCache() {
        //al = null;
    }

}