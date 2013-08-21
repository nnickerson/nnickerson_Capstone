package inspiram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Text {

	public Text() {
		
	}
	
	public Image putTextOnPlanarImage(PlanarImage pi, int textX, int textY, String text) {		
		BufferedImage bi = new BufferedImage(pi.getWidth(), pi.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D biGraphics = bi.createGraphics();
		biGraphics.setColor(Color.yellow);
		biGraphics.drawString(text, textX, textY);
		Image textImage = bi;
		System.out.println("PUTTING TEXT ONTO IMAGE");
		return textImage;
	}
	
	public PlanarImage getPlanarImageFromImage(Image anyImage) {
		ParameterBlock pb = new ParameterBlock();
		pb.add(anyImage);
		PlanarImage myPlanarImage;
		myPlanarImage = (PlanarImage)JAI.create("awtImage", pb);
		return myPlanarImage;
	}
}
