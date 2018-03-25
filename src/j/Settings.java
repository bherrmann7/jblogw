package j;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    Properties prop = new Properties();
    public String pubURL; // accessed via JSP page
    public File pubDir;
    public File pubIndex;
    public File pubOldIndex;
    public int articlesOnMainPage;
    public File dataDir;
    public File imgDir;
    public File artDir;
    public File templatesDir;
    public File template;
    public File oldTemplate;
    final String imgSubDir = "aimg";

    public String shortName;

    public Settings(ServletContext sc) {
        this(sc.getResourceAsStream("/WEB-INF/settings.prop"));
    }

    public Settings(InputStream is) {
        try {
            prop.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        pubURL = get("publicURL");
        pubDir = getFile("publishToDir");
        dataDir = getFile("dataDir");
        articlesOnMainPage = getInt("articlesOnMainPage");

        deriveValues();
    }

    String get(String key) {
        if (prop.get(key) == null) {
            return "NOT SET";
        }

        return (String) prop.get(key);
    }

    int getInt(String key) {
        if (prop.get(key) == null) {
            return -1;
        }

        return Integer.parseInt(get(key));
    }

    File getFile(String key) {
        if (get(key) == null) {
            return new File("NOT SET");
        }

        return new File(get(key));
    }

    public void save() {
        pubURL = get("publicURL");
        pubDir = getFile("publishToDir");
        dataDir = getFile("dataDir");
        articlesOnMainPage = getInt("articlesOnMainPage");

        prop.put("publicURL", pubURL);
        prop.put("publishToDir", pubDir.toString());
        prop.put("dataURL", dataDir.toString());
        prop.put("articlesOnMainPage", "" + articlesOnMainPage);

        try {
            prop.store(new FileOutputStream("settings.prop"), "header");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deriveValues() {
        // web dir
        pubIndex = new File(pubDir, "index.html");
        pubOldIndex = new File(pubDir, "olderstuff.html");
        imgDir = new File(pubDir, imgSubDir);

        // data dir stuff
        artDir = new File(dataDir, "articles");
        templatesDir = new File(dataDir, "templates");
        template = new File(templatesDir, pubIndex.getName());
        oldTemplate = new File(templatesDir, pubOldIndex.getName());

        // get rid of last slash
        String almostPub = pubURL.toString().substring(0, pubURL.toString().length() - 1);
        shortName = almostPub.substring(almostPub.lastIndexOf('/') + 1);
    }
}