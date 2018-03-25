//package test;
//
//import j.NImage;
//
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.net.URL;
//
//import javax.imageio.ImageIO;
//import javax.servlet.ServletContext;
//
//import junit.framework.TestCase;
//
//public class NImageTest extends TestCase {
//
//	public void xtestNImage() {
//		File imgLocalFile = new File("/tmp/imgtest");
//		if(!imgLocalFile.exists())
//			assertTrue(imgLocalFile.mkdir());
//		ServletContext sc = new MockServletContext();
//		NImage ni = new NImage(sc,"/tmp/imgtest", "http://images.slashdot.org/topics/topicxmas.gif", false);
//
//		assertEquals("aimg/topicxmas.jpg", ni.getImageURL());
//
//		File out = new File("/tmp/imgtest/topicxmas.jpg");
//		assertTrue(out.exists());
//		assertTrue(out.length()>2000);
//
//		assertTrue(out.delete());
//		assertTrue(out.getParentFile().delete());
//	}
//
//	public void testNImage2() throws Exception {
//		URL url = new URL("http://www.kt.uni-rostock.de/awt/images/zahnrad1.gif");
//		//URL url = new URL("http://ly.lygo.com/ly/wired/shared/images/cs4/logo28_wirednews.gif");
//
//		BufferedImage img = ImageIO.read(url);
//
////		int height = img.getHeight();
////		int width = img.getWidth();
//
//		boolean ok = ImageIO.write(img, "png", new File("/tmp/heyho.png"));
//
//	}
//
//
//}
