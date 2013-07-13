import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.ImageJAI;
import javax.media.jai.PlanarImage;
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
	PlanarImage loadedImage;
	JMenuBar mainMenuBar;
	JMenu mainMenu;
	JMenuItem loadImageOption;
	JLabel welcomeJLabel;

	public void init() {
		addImageLoadMenu();
		addManipulativeTestMenu();
		welcomeJLabel = new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet.");
	    this.add(welcomeJLabel);
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
		mainMenuBar = new JMenuBar();
	    mainMenu = new JMenu("File");
	    loadImageOption = new JMenuItem("Load Image");
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
				loadedImage = il.loadImageWithJAI(imageLocation);
				displayJAIimage = new DisplayJAI(loadedImage);
				imageHolder.add(new JScrollPane(displayJAIimage));
				welcomeJLabel.setVisible(false);
			}
		});
	    //End of listeners//
	    
	    this.getContentPane().repaint();
	    imageHolder.repaint();
	    this.repaint();
	}
	
	public void manipulativeTest() {
		Raster raster = loadedImage.getData();
		int width = loadedImage.getMaxX();
		int height = loadedImage.getMaxY();
		int numOfBands = loadedImage.getNumBands();
		System.out.println("Width: " + width + "   Height: " + height);
		int[] pixels = raster.getPixels(0, 0, width, height, new int[numOfBands*width*height]);
		int vCount = 1;
		int r = 0, g = 0, b = 0;
		List<Pixel> myPixels = new ArrayList<Pixel>();
		for(int p : pixels) {
			if(vCount >=3) {
//				System.out.print(", " + p);
//				System.out.println("\n");
				myPixels.add(new Pixel(r,g,b));
				vCount = 1;
			}
			else {
//				System.out.print(", " + p);
				if(vCount == 1) {
					r = p;
				}
				else if(vCount == 2) {
					g = p;
				}
				vCount++;
			}
		}
		System.out.println(raster.getPixel(200, 200, new double[40000])[156]);
		System.out.println("DONE");
		
		WritableRaster wr = raster.createCompatibleWritableRaster();
		wr.setPixel(200, 200, raster.getPixels(0, 0, width, height, new double[numOfBands*width*height]));
	}
	
	public void addManipulativeTestMenu() {
		JMenuItem manipulativeTestOption = new JMenuItem("Manipulate Image");
	    mainMenu.add(manipulativeTestOption);
	    mainMenuBar.add(mainMenu);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    manipulativeTestOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				manipulativeTest();
			}
		});
	    //End of listeners//
	    
	    this.getContentPane().repaint();
	    this.repaint();
	}
	
//	public void paint(Graphics g)
//	   {
//	      g.drawImage(image, 0, 0, 1360, 768, null);
//	   } 
}
