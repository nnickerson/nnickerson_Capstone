import java.applet.AppletContext;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.*;

import com.sun.media.jai.widget.DisplayJAI;


public class ImageLoader {
//	String fileLocation = "C:/mcShaders2 - Copy.png";
//	public Image loadImage(AppletContext context) {
//		AppletContext appletContext = context;
//		Image image = null;
//		try {
//			File i = new File(fileLocation);
//			image = ImageIO.read(i);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return image;
//	}
	
	public PlanarImage loadImageWithJAI(String fileLocation) {
		ParameterBlock pb = new ParameterBlock();
		pb.add(fileLocation);
		PlanarImage loadedImage = JAI.create("fileLoad", pb);
		
		BufferedImage bi = loadedImage.getAsBufferedImage();
		
		boolean imageCanFit = false;
		float scaledX = loadedImage.getWidth();
		float scaledY = loadedImage.getHeight();
		float scale = 0.0f;
		while(!imageCanFit) {
			if(scaledX <= Toolkit.getDefaultToolkit().getScreenSize().getWidth()*.9) {
				if(scaledY <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()*.9) {
					imageCanFit = true;
				}
				else {
					scaledX = scaledX*.9f;
					scaledY = scaledY*.9f;
					scale += 0.1f;
				}
			}
			else {
				scaledX = scaledX*.9f;
				scaledY = scaledY*.9f;
				scale += 0.1f;
			}
		}
		
		Image image = bi.getScaledInstance((int)scaledX, (int)scaledY, Image.SCALE_SMOOTH);
		
	    ParameterBlock pb2 = new ParameterBlock();
	    pb2.add(image);
	    //The awtImage is an operation
	    PlanarImage im = (PlanarImage)JAI.create("awtImage", pb2);
		return im;
	}
}
