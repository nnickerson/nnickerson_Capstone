import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
	}
	
	public void loadImage(String imageLocation) {
		loadedImage = il.loadImageWithJAI(imageLocation);
		displayJAIimage = new DisplayJAI(loadedImage);
		imageHolder.add(new JScrollPane(displayJAIimage));
		welcomeJLabel.setVisible(false);
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
				loadImage(imageLocation);
			}
		});
	    //End of listeners//
	    
	    this.getContentPane().repaint();
	    imageHolder.repaint();
	    this.repaint();
	}
	
	public void manipulativeTest() {		
		imageHolder.removeAll();
		
		TiledImage myTiledImage = alterPixelsData();
		loadedImage = myTiledImage.createSnapshot();
		displayJAIimage = new DisplayJAI(loadedImage);
		imageHolder.add(new JScrollPane(displayJAIimage));
		
		 this.getContentPane().repaint();
		 imageHolder.repaint();
		 this.repaint();
	}
	
	public boolean isRedEyeValues(int r, int g, int b) {
		boolean isRedEyeValue = false;
		float pixelRedRatio = ((float)r/(((float)g+(float)b)/(float)2));
		int redMin = 88;
		int redMax = 255;
		int greenMin = 33;
		int greenMax = 255;
		int blueMin = 33;
		int blueMax = 251;
		
		float redMinRatio = ((float)redMin/(((float)greenMin+(float)blueMin)/(float)2));
		float redMaxRatio = ((float)redMax/(((float)greenMax+(float)blueMax)/(float)2));
		
		if(pixelRedRatio >= redMaxRatio && pixelRedRatio >= redMinRatio) {
			if(r > redMin && g > greenMin && b > blueMin) {
				if(b < blueMax) {
					if((float)g-(float)b < 50) {
						isRedEyeValue = true;
					}
				}
			}
		}
		
		return isRedEyeValue;
	}
	
	
	
	public TiledImage alterPixelsData() {
		int width = loadedImage.getWidth();
		int height = loadedImage.getHeight();
		SampleModel mySampleModel = loadedImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = loadedImage.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands*width*height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		int pixelIndex = 0;
		int r = 0, g = 0, b = 0;
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++)
			{
				pixelIndex = h*width*nbands+w*nbands;
				for(int band=0;band<nbands;band++) {
//					if(h == height/2 && w == height/2) { //Changing pixel near the center of the image.
						if(band == 0) {
//							System.out.println("Value: " + pixels[pixelIndex+band]);
//							pixels[pixelIndex+band] = 255;
//							pixels[(pixelIndex-1)+band] = 255;
							r = pixels[pixelIndex+band];
						}
						else if(band == 1) {
//							System.out.println("Value: " + pixels[pixelIndex+band]);
//							pixels[pixelIndex+band] = 0;
//							pixels[(pixelIndex-1)+band] = 0;
							g = pixels[pixelIndex+band];
						}
						else {
//							System.out.println("Value: " + pixels[pixelIndex+band]);
//							pixels[pixelIndex+band] = 0;
//							pixels[(pixelIndex-1)+band] = 0;
							b = pixels[pixelIndex+band];
							if(isRedEyeValues(r, g, b)) {
								pixels[pixelIndex+(band)] = 0;
								pixels[pixelIndex+(band-1)] = 0;
								pixels[pixelIndex+(band-2)] = 0;
							}
						}
//					}
				}
			}
		}
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(loadedImage,1,1);
		ti.setData(writableRaster);
		return ti;
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
