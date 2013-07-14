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
	
	public void findRedEyeValues() {
		int redMin = 175;
		int redMax = 255;
		int greenMin = 0;
		int greenMax = 75;
		int blueMin = 0;
		int blueMax = 75;
		
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
		for(int h=0;h<height;h++) {
			for(int w=0;w<width;w++)
			{
				pixelIndex = h*width*nbands+w*nbands;
				for(int band=0;band<nbands;band++) {
					if(h == height/2 && w == height/2) {
						if(band == 0) {
							System.out.println("Value: " + pixels[pixelIndex+band]);
							pixels[pixelIndex+band] = 255;
							pixels[(pixelIndex-1)+band] = 255;
						}
						else {
							System.out.println("Value: " + pixels[pixelIndex+band]);
							pixels[pixelIndex+band] = 0;
							pixels[(pixelIndex-1)+band] = 0;
						}
					}
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
