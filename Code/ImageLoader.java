import java.applet.AppletContext;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;


public class ImageLoader {
	public Image loadImage(AppletContext context) {
		AppletContext appletContext = context;
		Image image = null;
		try {
			File i = new File("C:/mcShaders2 - Copy.png");
			image = ImageIO.read(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
}
