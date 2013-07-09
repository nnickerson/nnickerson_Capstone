import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

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
		ImageLoader il = new ImageLoader();
		image = il.loadImage(this.getAppletContext());
		
	}
	
	public void paint(Graphics g)
	   {
	      g.drawImage(image, 0, 0, 2360, 1468, null);
	   } 
}
