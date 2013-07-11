import java.applet.AppletContext;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.*;

import com.sun.media.jai.widget.DisplayJAI;


public class ImageLoader {
	String fileLocation = "C:/mcShaders2 - Copy.png";
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
	
	public DisplayJAI loadImageWithJAI() {
		ImageJAI ijai;
		BufferedImage bi;
		ParameterBlock pb = new ParameterBlock();
		pb.add(fileLocation);
		PlanarImage loadedImage = JAI.create("fileLoad", pb);
		bi = loadedImage.getAsBufferedImage();
		DisplayJAI jai = new DisplayJAI(loadedImage);
		
		return jai;
	}
}
