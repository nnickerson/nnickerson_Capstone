import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.media.jai.ImageJAI;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * 
 */

/**
 * @author nnickerson
 *
 */
public class Driver extends JApplet {
	Image image;
	DisplayJAI displayJAIimage;
	ImageLoader il = new ImageLoader();
	Container imageHolder = this.getContentPane();

	public void init() {
		addImageLoadMenu();
	    this.add(new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet."));
	    loadImageTest();
	    
	}
	
	public void loadImageTest() {
		//Regular image loading//
//		image = il.loadImage(this.getAppletContext());
		//End of regular image loading//
		
		//Loading image with JAI//
//		ImageJAI image = 
//		RenderedImage renderedJAI;
//		displayJAIimage = il.loadImageWithJAI();
//		Container imageHolder = this.getContentPane();
//		imageHolder.add(new JScrollPane(displayJAIimage));
		//End of loading image with JAI//
	}
	
	public void addImageLoadMenu() {
		JMenuBar mainMenuBar = new JMenuBar();
	    JMenu mainMenu = new JMenu("File");
	    JMenuItem loadImageOption = new JMenuItem("Load Image");
	    mainMenu.add(loadImageOption);
	    mainMenuBar.add(mainMenu);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    loadImageOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(null);
				String imageLocation = fileChooser.getSelectedFile().getAbsolutePath();
				System.out.println(imageLocation);
				displayJAIimage = il.loadImageWithJAI(imageLocation);
				
				imageHolder.add(new JScrollPane(displayJAIimage));
			}
		});
	    //End of listeners//
	    
	    this.repaint();
	}
	
//	public void paint(Graphics g)
//	   {
//	      g.drawImage(image, 0, 0, 1360, 768, null);
//	   } 
}
