import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.media.jai.ImageJAI;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JLabel;

/**
 * 
 */

/**
 * @author nnickerson
 *
 */
public class Driver extends JApplet {
	Image image;
	public void init() {
	    this.add(new JLabel("Look at me! I'm running!"));
	    loadImageTest();
	}
	
	public void loadImageTest() {
		//Regular image loading//
		ImageLoader il = new ImageLoader();
//		image = il.loadImage(this.getAppletContext());
		//End of regular image loading//
		
		//Loading image with JAI//
//		ImageJAI image = 
		
		//End of loading image with JAI//
	}
	
	public void paint(Graphics g)
	   {
	      g.drawImage(image, 0, 0, 1360, 768, null);
	   } 
}
