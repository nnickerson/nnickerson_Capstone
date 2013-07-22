import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

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
	JScrollPane scrollPane = new JScrollPane();
	int rWidth = 500;
	int rHeight = 500;

	public void init() {
		setupApplet();
	}
	
	public void setupApplet() {
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		double resWidth = resolution.getWidth();
		double resHeight = resolution.getHeight();
		rWidth = (int)resWidth;
		rHeight = (int)resHeight;
		setSize(rWidth-150, rHeight-150);
		addImageLoadMenu();
		addManipulativeTestMenu();
		welcomeJLabel = new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet.");
	    this.add(welcomeJLabel);
	}
	
	public void loadImage(String imageLocation) {
		loadedImage = il.loadImageWithJAI(imageLocation);
		displayJAIimage = new DisplayJAI(loadedImage);
//		scrollPane = new JScrollPane(displayJAIimage);
		imageHolder.add(displayJAIimage);
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
	
	public void defineEyeSize(int centerEyeX, int centerEyeY) {
		JSlider radiusSlider = new JSlider(JSlider.HORIZONTAL);
		radiusSlider.setMinimum(2);
		if(loadedImage.getWidth() >= loadedImage.getHeight()) {
			radiusSlider.setMaximum(loadedImage.getHeight()-1);
		}
		else {
			radiusSlider.setMaximum(loadedImage.getWidth()-1);
		}
		imageHolder.getParent().add(radiusSlider);
		imageHolder.getParent().repaint();
	}
	
	public void grabEyeLocation() {
		imageHolder.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		scrollPane.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int eyeX = scrollPane.getMousePosition().x;
				int eyeY = scrollPane.getMousePosition().y;
				System.out.println("Eye center: " + eyeX + ", " + eyeY);
				scrollPane.setCursor(Cursor.getDefaultCursor());
				System.out.println("Component count for the scrollbar: " + scrollPane.getComponentCount());
				for(Component c : scrollPane.getComponents()) {
					System.out.println(c.getName() + " " + c.getHeight() + " " + c.getWidth());
				}
				defineEyeSize(eyeX, eyeY);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("Mouse was pressed.");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}  });
		System.out.println("created the mouse listener for red eye.");
	}
	
	
	/**
	 * This method was used for manipulating pixels and further transformed into 
	 * trying to fix the red eye problem.
	 */
	public void fixRedEye() {		
//		imageHolder.removeAll();
//		imageHolder.validate();
//		imageHolder.add(new JScrollPane(displayJAIimage));
//		imageHolder.add(welcomeJLabel);
		grabEyeLocation();
		
//		TiledImage myTiledImage = alterPixelsData();
//		loadedImage = myTiledImage.createSnapshot();
//		displayJAIimage = new DisplayJAI(loadedImage);
//		imageHolder.add(new JScrollPane(displayJAIimage));
//		welcomeJLabel.setText("");
//		welcomeJLabel.setVisible(true);
//		welcomeJLabel.setVisible(false);
//		
		this.getContentPane().repaint();
		imageHolder.repaint();
		this.repaint();
		repaint();
	}
	
	public float[] getHSB(int r, int b, int g) {
		float[] hsb = new float[3];
		hsb = Color.RGBtoHSB(r, g, b, hsb);
		return hsb;
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
		
		
		float[] hsb = getHSB(r, g, b);
		float hue = hsb[0];
		float saturation = hsb[1];
		float brightness = hsb[2];
//		if(hue >= .95 && hue <= .95) {
//			if(saturation > .43) {
//				if(brightness > .2) {
//					isRedEyeValue = true;
//				}
//			}
//		}
		
//		if(pixelRedRatio >= redMinRatio) {
//			if(r > redMin && g > greenMin && b > blueMin) {
//				if(b < blueMax) {
//					if((float)g-(float)b < 50) {
//						isRedEyeValue = true;
//					}
//				}
//			}
//		}
		
		
		int averageGrayscale = (r+g+b)/3;
		if(hue >= .95 && hue <= 1.05) {
			if(saturation > .43) {
				if(brightness > .1 && brightness < .9) {
					if(averageGrayscale < 205 && averageGrayscale > 50) {
						if(g-b < 75) {
							if(g < r && b < r) {
								if(pixelRedRatio >= 2.25) {
									isRedEyeValue = true;
								}
							}
						}
					}
				}
			}
		}
		
		return isRedEyeValue;
	}
	
//	public int[] findEye(int x, int y, int amountOfPixels, int[] pixels, int width, int height, int bands) {
//		int[] ps = pixels;
//		int xPlus = x;
//		int yPlus = y;
//		int xNeg = 0;
//		int yNeg = 0;
//		int pixelIndexPlus = 0;
//		int pixelIndexNeg = 0;
//		
//		for(int i = 0; i < amountOfPixels; i++) {
//			if(i < amountOfPixels) {
//				xPlus += i;
//				yPlus += amountOfPixels-i;
//				if(xPlus > width) { xPlus = width; }
//				if(yPlus > height) { yPlus = height; }
//				xNeg = xPlus*(-1);
//				yNeg = yPlus*(-1);
//				if(xNeg < width) { xNeg = 0; }
//				if(yNeg < height) { yNeg = 0; }
//				pixelIndexPlus = yPlus*width*bands+xPlus*bands;
//				pixelIndexNeg = (yPlus+yNeg)*width*bands+(xPlus+xNeg)*bands;
//				pixels[pixelIndexPlus+(0)] = 255;
//				pixels[pixelIndexPlus+(1)] = 255;
//				pixels[pixelIndexPlus+(2)] = 255;
//				pixels[pixelIndexNeg+(0)] = 255;
//				pixels[pixelIndexNeg+(1)] = 255;
//				pixels[pixelIndexNeg+(2)] = 255;
//			}
//			else {
//				xPlus += amountOfPixels-i;
//				yPlus += i;
//				if(xPlus > width) { xPlus = width; }
//				if(yPlus > height) { yPlus = height; }
//				xNeg = xPlus*(-1);
//				yNeg = yPlus*(-1);
//				if(xNeg < width) { xNeg = 0; }
//				if(yNeg < height) { yNeg = 0; }
//				pixelIndexPlus = yPlus*width*bands+xPlus*bands;
//				pixelIndexNeg = (yPlus+yNeg)*width*bands+(xPlus+xNeg)*bands;
//				pixels[pixelIndexPlus+(0)] = 255;
//				pixels[pixelIndexPlus+(1)] = 255;
//				pixels[pixelIndexPlus+(2)] = 255;
//				pixels[pixelIndexNeg+(0)] = 255;
//				pixels[pixelIndexNeg+(1)] = 255;
//				pixels[pixelIndexNeg+(2)] = 255;
//			}
//		}
//		
//		return ps;
//	}
	
	public int[] createBoundingBoxes(int width, int height, int nbands, int[] pixels) {
		List<List<Pixel>> boxedPixels = new ArrayList<List<Pixel>>();
		int[] newPixels = pixels;
		for(Pixel p : redPixels) {
			List<Pixel> pixList = new ArrayList<Pixel>();
			if(!p.isInBoundingBox && !p.alreadyCheckedForStart) {
				p.alreadyCheckedForStart = true;
				p.isInBoundingBox = true;
				for(Pixel pi : redPixels) {
					if(((pi.x-p.x > -5 && pi.x-p.x < 5) && (pi.y-p.y > -5 && pi.y-p.y < 5)) && !pi.isInBoundingBox) {
						pi.isInBoundingBox = true;
						pixList.add(pi);
					}
				}
				if(pixList.size() >= 1) {
					boxedPixels.add(pixList);
//					newPixels = findEye(pixList.get(pixList.size()/2).x, pixList.get(pixList.size()/2).y, pixList.size()*2, newPixels, width, height, nbands);
				}
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
			SquareAnnotation sa;
			if(!(minX-1 < 1) && !(minY-1<1) && !(maxX+1>width) && !(maxY+1>height)) {
				sa = new SquareAnnotation(minX, minY, maxX, maxY, false);
			}
			else {
				sa = new SquareAnnotation(minX-1, minY-1, maxX+1, maxY+1, false);
			}
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
							r = pixels[pixelIndex+band];
						}
						else if(band == 1) {
							g = pixels[pixelIndex+band];
						}
						else {
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
		JMenuItem manipulativeTestOption = new JMenuItem("Fix Red Eye");
	    mainMenu.add(manipulativeTestOption);
	    mainMenuBar.add(mainMenu);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    manipulativeTestOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fixRedEye();
				repaint();
			}
		});
	    //End of listeners//
	    
	    this.getContentPane().repaint();
	    this.repaint();
	}
	
//	public void paint(Graphics g) {
//		// g.drawImage(image, 0, 0, 1360, 768, null);
////		g.drawOval(x, y, width, height)
//	}
}
