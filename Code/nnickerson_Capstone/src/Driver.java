import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

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
	List<Pixel> redPixels = new ArrayList<Pixel>();
	

	public void init() {
		setSize(500, 500);
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
		welcomeJLabel.setText("");
		welcomeJLabel.setVisible(true);
		welcomeJLabel.setVisible(false);
		
		this.getContentPane().repaint();
		imageHolder.repaint();
		this.repaint();
		repaint();
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
	
	public int[] createBoundingBoxes(int width, int height, int nbands, int[] pixels) {
		List<List<Pixel>> boxedPixels = new ArrayList<List<Pixel>>();
		int[] newPixels = pixels;
		for(Pixel p : redPixels) {
			List<Pixel> pixList = new ArrayList<Pixel>();
			if(!p.isInBoundingBox && !p.alreadyCheckedForStart) {
				p.alreadyCheckedForStart = true;
				p.isInBoundingBox = true;
				for(Pixel pi : redPixels) {
					if((pi.x-p.x > -5 && pi.x-p.x < 5) || (pi.y-p.y > -5 && pi.y-p.y < 5) && !pi.isInBoundingBox) {
						pixList.add(pi);
						pi.isInBoundingBox = true;
					}
				}
				boxedPixels.add(pixList);
			}
		}
		
		
		for(List<Pixel> lp : boxedPixels) {
			System.out.println(lp.toString());
			int minX = width;
			int maxX = 0;
			int minY = height;
			int maxY = 0;
			for(Pixel pl : lp) {
				if(pl.x < minX) {
					minX = pl.x;
				}
				if(pl.x > maxX) {
					maxX = pl.x;
				}
				if(pl.y < minY) {
					minY = pl.y;
				}
				if(pl.y > maxY) {
					maxY = pl.y;
				}
			}
			SquareAnnotation sa = new SquareAnnotation(minX, minY, maxX, maxY, false);
			sa.createBoundingBox(width, nbands, newPixels);
		}
		
		//Need to find the area where the pixels occur
		return newPixels;
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
		for(int y=0;y<height;y++) {
			for(int x=0;x<width;x++)
			{
				pixelIndex = y*width*nbands+x*nbands;
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
								redPixels.add(new Pixel(x, y));
								pixels[pixelIndex+(band)] = 0;
								pixels[pixelIndex+(band-1)] = 0;
								pixels[pixelIndex+(band-2)] = 0;
							}
						}
//					}
				}
			}
		}
		pixels = createBoundingBoxes(width, height, nbands, pixels);
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
				repaint();
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
