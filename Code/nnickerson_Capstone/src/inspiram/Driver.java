package inspiram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.media.jai.widget.DisplayJAI;


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
	BufferedImage redEyeCircleBI;
	Graphics redEyeCircle;
	int redEyeCenterX = 0;
	int redEyeCenterY = 0;
	int redEyeDiameter = 0;
	JSlider radiusSlider;
	JFrame sliderFrame;
	RedEyeCirclePanel recp;
	JFrame dynamicCircle;
	Graphics previousGraphics;
	Frame appletFrame;

	public void init() {
		setupApplet();
	}
	
	public void setupApplet() {
		appletFrame = (Frame)this.getParent().getParent();
		appletFrame.setTitle("Inspiram");
		appletFrame.setIconImage(il.loadLogoAsImage());
		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		double resWidth = resolution.getWidth();
		double resHeight = resolution.getHeight();
		rWidth = (int)resWidth;
		rHeight = (int)resHeight;
		setSize(rWidth-150, rHeight-150);
		addImageLoadMenu();
		addRedEyeMenu();
		welcomeJLabel = new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet.");
	    this.add(welcomeJLabel);
	}
	
	public void loadImage(String imageLocation) {
		loadedImage = il.loadPlanarImageWithJAI(imageLocation);
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
		sliderFrame = new JFrame("Slide the slider to fit over the iris in a red eye.");
		sliderFrame.setLayout(new BorderLayout());
		radiusSlider = new JSlider(JSlider.HORIZONTAL);
		JButton fixRedEyeButton = new JButton("Fix it!");
		sliderFrame.add(fixRedEyeButton, BorderLayout.PAGE_END);
		fixRedEyeButton.setVisible(true);
		fixRedEyeButton.repaint();
		sliderFrame.repaint();
		sliderFrame.add(radiusSlider, BorderLayout.PAGE_START);
		radiusSlider.setMinimum(2);
		radiusSlider.setMaximum((int)((double)(rHeight)*.9));
		if(loadedImage.getWidth() >= loadedImage.getHeight()) {
			radiusSlider.setMaximum(loadedImage.getHeight()-1);
		}
		else {
			radiusSlider.setMaximum(loadedImage.getWidth()-1);
		}
		sliderFrame.setSize(400, 110);
		sliderFrame.setVisible(true);
		sliderFrame.repaint();
		radiusSlider.setBorder(BorderFactory.createBevelBorder(2));
		radiusSlider.setMajorTickSpacing(rHeight/10);
		radiusSlider.setMinorTickSpacing(rHeight/50);
		radiusSlider.setPaintLabels(true);
		radiusSlider.setPaintTicks(true);
		radiusSlider.setPaintTrack(true);
		radiusSlider.setVisible(true);
		radiusSlider.repaint();
		redEyeCircle = displayJAIimage.getGraphics();
		
		eyeLineup();
		
		//Radius Listeners//
		radiusSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				displayJAIimage.paint(previousGraphics);
				eyeLineup();
			}
		});
		
		fixRedEyeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(MouseListener ml : imageHolder.getMouseListeners()) {
					imageHolder.removeMouseListener(ml);
				}
				fixRedEye();
			} 
		});
		//End of radius Listeners//
	}
	
	public void eyeLineup() {
		
		redEyeDiameter = radiusSlider.getValue();
		System.out.println("Red Eye Radius: " + redEyeDiameter);
//	    redEyeCircle.setStroke(new BasicStroke());
		int previousCenterX = redEyeCenterX;
		int previousCenterY = redEyeCenterY;
	    redEyeCircle.drawOval(redEyeCenterX-(redEyeDiameter/2), redEyeCenterY-(redEyeDiameter/2), redEyeDiameter, redEyeDiameter);
//	    redEyeCircle.setPaint(Color.green);
	    redEyeCenterX = previousCenterX;
	    redEyeCenterY = previousCenterY;
	    previousGraphics = redEyeCircle;
	} 
	
	public void paint2DGraphics(Graphics2D g2d) {
		imageHolder.paintComponents(g2d);
		imageHolder.repaint();
	}
	
	public void grabEyeLocation() {
		imageHolder.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		imageHolder.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				redEyeCenterX = imageHolder.getMousePosition().x;
				redEyeCenterY = imageHolder.getMousePosition().y;
				System.out.println("Eye center: " + redEyeCenterX + ", " + redEyeCenterY);
				imageHolder.setCursor(Cursor.getDefaultCursor());
				for(MouseListener ml : imageHolder.getMouseListeners()) {
					imageHolder.removeMouseListener(ml);
				}
				defineEyeSize(redEyeCenterX, redEyeCenterY);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("Mouse was pressed.");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}  });
		System.out.println("created the mouse listener for red eye.");
	}
	
	public TiledImage fixRedEyePixels() {
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
		int yMax = redEyeCenterY+redEyeDiameter;
		int xMax = redEyeCenterX+redEyeDiameter;
		int yMin = redEyeCenterY-redEyeDiameter;
		int xMin = redEyeCenterX-redEyeDiameter;
		
		if(yMax >= imageHolder.getHeight()) {
			yMax = imageHolder.getHeight();
		}
		if(xMax >= imageHolder.getWidth()) {
			xMax = imageHolder.getWidth();
		}
		if(yMin < 0) {
			yMin = 0;
		}
		if(xMin < 0) {
			xMin = 0;
		}
		
		for(int y = yMin;y<yMax;y++) {
			for(int x = xMin;x<xMax;x++)
			{
				pixelIndex = y*width*nbands+x*nbands;
				for(int band=0;band<nbands;band++) {
						if(isRedEyeValues(r, g, b)) {
							redPixels.add(new Pixel(x, y));
							pixels[pixelIndex+(band)] = 0;
						}
				}
			}
		}
//		pixels = createBoundingBoxes(width, height, nbands, pixels);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(loadedImage,1,1);
		ti.setData(writableRaster);
		return ti;
	}
	
	
	/**
	 * This method was used for manipulating pixels and further transformed into 
	 * trying to fix the red eye problem.
	 */
	public void fixRedEye() {
		sliderFrame.dispose();
		
		TiledImage myTiledImage = alterPixelsData();
		loadedImage = myTiledImage.createSnapshot();
		displayJAIimage = new DisplayJAI(loadedImage);
		imageHolder.add(displayJAIimage);
//		welcomeJLabel.setText("");
//		welcomeJLabel.setVisible(true);
//		welcomeJLabel.setVisible(false);
//		
		this.getContentPane().repaint();
		
		this.setSize(this.getWidth()-1, this.getHeight()-1);
		this.setSize(this.getWidth()+1, this.getHeight()+1);
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
//		if(hue >= .65 && hue <= 1.25) {
//			if(saturation > .43) {
//				if(brightness > .05 && brightness < .95) {
//					if(averageGrayscale < 230 && averageGrayscale > 25) {
//						if(g-b < 75) {
//							if(g < r && b < r) {
								if(pixelRedRatio >= 1.67) {
									isRedEyeValue = true;
								}
//							}
//						}
//					}
//				}
//			}
//		}
		
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
					if(((pi.x-p.x > -5 && pi.x-p.x < 5) && (pi.y-p.y > -5 && pi.y-p.y < 5)) && !pi.isInBoundingBox) {
						pi.isInBoundingBox = true;
						pixList.add(pi);
					}
				}
				if(pixList.size() >= 1) {
					boxedPixels.add(pixList);
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
		int y1 = 0;
		int x1 = 0;
		int xMax = width;
		int yMax = height;
		if(redEyeCenterY-(redEyeDiameter/2) >= 0) {
			y1 = redEyeCenterY-(redEyeDiameter/2);
		}
		if(redEyeCenterX-(redEyeDiameter/2) >= 0) {
			x1 = redEyeCenterX-(redEyeDiameter/2);
		}
		if(redEyeCenterX+(redEyeDiameter/2) <= width) {
			xMax = redEyeCenterX+(redEyeDiameter/2);
		}
		if(redEyeCenterY+(redEyeDiameter/2) <= height) {
			yMax = redEyeCenterY+(redEyeDiameter/2);
		}
		for(int y=y1;y<yMax;y++) {
			for(int x=x1;x<xMax;x++)
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
								pixels[pixelIndex+(band)] = 0;
								pixels[pixelIndex+(band-1)] = 10;
								pixels[pixelIndex+(band-2)] = 10;
							}
						}
//					}
				}
			}
		}
//		pixels = createBoundingBoxes(width, height, nbands, pixels);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(loadedImage,1,1);
		ti.setData(writableRaster);
		return ti;
	}
	
	public void addRedEyeMenu() {
		JMenuItem redEyeOption = new JMenuItem("Fix Red Eye");
	    mainMenu.add(redEyeOption);
	    mainMenuBar.add(mainMenu);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    redEyeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				grabEyeLocation();
				repaint();
			}
		});
	    //End of listeners//
	    
	    this.getContentPane().repaint();
	    this.repaint();
	}
	
//	public void paint(Graphics g) {
//		g.drawOval(300, 300, 200, 200);
//	}
}
