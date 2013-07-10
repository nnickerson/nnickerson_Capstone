import java.applet.AppletContext;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.*;


public class ImageLoader {
	String fileLocation = "C:/mcShaders2 - Copy.png";
	public Image loadImage(AppletContext context) {
		AppletContext appletContext = context;
		Image image = null;
		try {
			File i = new File(fileLocation);
			image = ImageIO.read(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
//	public ImageJAI loadImageWithJAI() {
//		ImageJAI ijai;
//		BufferedImage bi;
//		ParameterBlock pb = new ParameterBlock();
//		pb.add(fileLocation);
//		RenderedOp loadedImage = JAI.create("fileLoad", pb);
//		bi = loadedImage.getAsBufferedImage();
//		return ijai;
//	}
}
