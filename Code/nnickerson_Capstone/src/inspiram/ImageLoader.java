package inspiram;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.*;

/**
 * 
 * @author nnickerson
 *
 */

public class ImageLoader {
	
	String fileLocation = "InspiramLogo.png";
	
	public Image loadLogoAsImage() {
		Image image = null;
		try {
			File i = new File(fileLocation);
			image = ImageIO.read(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	public PlanarImage loadPlanarImageWithJAI(String fileLocation) {
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

	public void loadImage(Inspiram inspiram, String imageLocation) {
		if(inspiram.layers.length == 0) {
			inspiram.addLayer();
		}
			PlanarImage newImage = loadPlanarImageWithJAI(imageLocation);
			inspiram.displayJAIimage = null;
//			inspiram.removeOldComponents();
		//		scrollPane = new JScrollPane(displayJAIimage);
			inspiram.layers[inspiram.currentLayer].setSize(newImage.getWidth(), newImage.getHeight());
			inspiram.layers[inspiram.currentLayer].setLayerImage(newImage);
			inspiram.layers[inspiram.currentLayer].setPlainImage();
			inspiram.layers[inspiram.currentLayer].add(inspiram.layers[inspiram.currentLayer].getImageDisplay());
//			inspiram.layers[inspiram.currentLayer].setLayerImage(null);
			inspiram.repaintEverything();
//			inspiram.layers[inspiram.currentLayer].set(inspiram.layers[inspiram.currentLayer].getLayerImage());
			inspiram.displayAllLayers();
			inspiram.displayLayersOnPanel();
			System.out.println("Set the current layer to the new image!!!!!!1");
			inspiram.setSize(inspiram.getWidth() - 1, inspiram.getHeight() - 1);
			inspiram.setSize(inspiram.getWidth() + 1, inspiram.getHeight() + 1);
			inspiram.welcomeJLabel.setVisible(false);
			inspiram.paintSelectedLayer();
			inspiram.repaintEverything();
	}
}
