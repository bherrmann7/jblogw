package j;

import com.sun.image.codec.jpeg.ImageFormatException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NImage {
    public final static NImage NONE = new NImage();

    String imageURL = "";
    String imageOrigURL = "";

    File imgLocalFile;

    private NImage() {

    }

    private File getLocalFile(String path, String name) {
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return new File(dir, name + ".png");
    }

    public NImage(ServletContext sc, String path, InputStream stream, String fileName, boolean keepAndLink) {
        Settings s = new Settings(sc
                .getResourceAsStream("/WEB-INF/settings.prop"));

        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        imgLocalFile = getLocalFile(path, fileName);

        File imgOriginalFile = null;
        if (keepAndLink)
            imgOriginalFile = genOrig(imgLocalFile);


        try {
            storeImage(stream, imgLocalFile, imgOriginalFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageURL = s.imgSubDir + "/" + fileName + ".png";
        imageOrigURL = s.imgSubDir + "/orig-" + fileName + ".png";

    }


    public static File genOrig(File scaledDest) {
        return new File(scaledDest.getParent(), "orig-" + scaledDest.getName());
    }

    public NImage(ServletContext sc, String path, String urlStr, boolean keepAndLink) {
        Settings s = new Settings(sc
                .getResourceAsStream("/WEB-INF/settings.prop"));

        try {
            // if no path entered.
            if ((urlStr == null) || (urlStr.trim().length() == 0)) {
                return;
            }

            int dex = urlStr.lastIndexOf("/");
            int dot = urlStr.lastIndexOf(".");

            if (dot == -1) {
                // Not Image
                return;
            }

            String name = urlStr.substring(dex + 1, dot);

            imgLocalFile = getLocalFile(path, name);

            imageURL = s.imgSubDir + "/" + name + ".png";
            imageOrigURL = s.imgSubDir + "/orig-" + name + ".png";

            // if path is not http... must have already fetched it.
            if (!urlStr.startsWith("http://")) {
                imageURL = urlStr;

                return;
            }

            File imgOriginalFile = null;
            if (keepAndLink)
                imgOriginalFile = genOrig(imgLocalFile);

            storeImage(urlStr, imgLocalFile, imgOriginalFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeImage(Object source, File imgLocalFile, File imgOriginalFile)
            throws ImageFormatException, IOException {

        BufferedImage img = null;
        if (source instanceof String) {
            img = ImageIO.read(new URL(source.toString()));
        } else {
            img = ImageIO.read((InputStream) source);
        }

        if (imgOriginalFile != null) {
            ImageIO.write(img, "png", imgOriginalFile);
        }

        int height = img.getHeight();
        int width = img.getWidth();
        if (height > 120) {
            width = width * 120 / height;
            height = 120;

            BufferedImage scaledImg = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D gScaledImg = scaledImg.createGraphics();
            // Note the use of BILNEAR filtering to enable smooth scaling
            gScaledImg.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            gScaledImg.drawImage(img, 0, 0, width, height, null);

            img = scaledImg;
        }

        /*boolean ok = */
        ImageIO.write(img, "png", imgLocalFile);

    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageOrigURL() {
        return imageOrigURL;
    }
}