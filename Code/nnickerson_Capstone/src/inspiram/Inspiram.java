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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.media.jai.widget.DisplayJAI;


/**
 * @author nnickerson
 *
 */
public class Inspiram extends JApplet {
	Image image;
	DisplayJAI displayJAIimage;
	ImageLoader il = new ImageLoader();
	Container imageHolder = this.getContentPane();
	PlanarImage loadedImage;
	JMenuBar mainMenuBar;
	JMenu fileMenu;
	JMenu editMenu;
	JMenu toolsMenu;
	JMenuItem loadImageOption;
	JMenuItem createLineOption;
	JMenuItem linearBezierOption;
	JMenuItem quadraticBezierOption;
	JMenuItem highOrderBezierOption;
	JMenuItem pasteOption;
	JMenuItem textOption;
	JMenuItem saveOption;
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
	JApplet thisApplet = this;
	boolean vPressed = false;
	boolean ctrlPressed = false;
	Locker inspiramLocker = new Locker();
	History inspiramHistory = new History();
	Inspiram inspiramClass = this;
	RedEye redEye = new RedEye();
	ImageSaver imageSaver = new ImageSaver();
	Line line = new Line();

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
		il.addImageLoadMenu(this);
		redEye.addRedEyeMenu(inspiramClass);
		line.addCreateLineMenu(inspiramClass);
		addBezierCurveDemos();
		addTextOption();
		inspiramLocker.addPasteOption(this);
		imageSaver.addSaveOption(inspiramClass);
		inspiramLocker.addInspiramLocker(this);
		mainMenuBar.add(inspiramHistory);
		welcomeJLabel = new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet.");
	    this.add(welcomeJLabel);
	    imageHolder.setBackground(Color.gray);
	}
	
	public void addTextOption() {
		textOption = new JMenuItem("Text");
	    toolsMenu.add(textOption);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    ActionListener textListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseTextLocation();				
			}
		};
	    //End of listeners//
		
		textOption.addActionListener(textListener);
	    
	    this.getContentPane().repaint();
	    
	    this.repaint();
	}
	
	public TiledImage createText(int textX, int textY, PlanarImage myTextImage) {
		int width = myTextImage.getWidth();
		int height = myTextImage.getHeight();
		SampleModel mySampleModel = myTextImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = myTextImage.getData();
		Raster actualRaster = loadedImage.getData();
		WritableRaster writableRaster = actualRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands * width * height];
		int[] actualPixels = new int[nbands * width * height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		actualRaster.getPixels(0, 0, width, height, actualPixels);
		
		int xMax = myTextImage.getWidth();
		int yMax = myTextImage.getHeight();
		
		Change change = new Change("Text Creation");
		change.undoChange.addActionListener(change.createChangeUndoListener(inspiramClass));
		
		for (int x = 0; x < xMax; x++) {
			for (int y = 0; y < yMax; y++) {
				int textImagePixelIndex = (int)y * width * nbands + (int)x * nbands;
				int r = 0;
				int g = 0;
				int b = 0;
				
				PixelHistory pixelHistory = new PixelHistory(x, y);
				
				for (int band = 0; band < nbands; band++) {
					if(band == 0) {
						r = pixels[(textImagePixelIndex) + (band)];
					}
					else if(band == 1) {
						g = pixels[(textImagePixelIndex) + (band)];
					}
					else {
						b = pixels[(textImagePixelIndex) + (band)];
					}
				}
				
				if(!isPixelBlack(r, g, b)) {
					int actualPixelIndex = ((int)y) * loadedImage.getWidth() * nbands + ((int)x) * nbands;
					pixelHistory.setPrevR(pixels[textImagePixelIndex + pixelHistory.R_BAND]);
					pixelHistory.setPrevG(pixels[textImagePixelIndex + pixelHistory.G_BAND]);
					pixelHistory.setPrevB(pixels[textImagePixelIndex + pixelHistory.B_BAND]);
					pixelHistory.setNewR(0);
					pixelHistory.setNewG(0);
					pixelHistory.setNewB(0);
					change.getAllPixelHistory().add(pixelHistory);
					for (int band = 0; band < nbands; band++) {
						actualPixels[actualPixelIndex + band] = 0;
					}
				}
				else {
					int actualPixelIndex = ((int)y) * loadedImage.getWidth() * nbands + ((int)x) * nbands;
					for (int band = 0; band < nbands; band++) {
						actualPixels[actualPixelIndex + band] = actualPixels[actualPixelIndex + band];
					}
				}
			}
				
		}
		inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, actualPixels);
		TiledImage ti = new TiledImage(loadedImage, 1, 1);
		ti.setData(writableRaster);
		return ti;
	}
	
		
	public boolean isPixelBlack(int r, int g, int b) {
		boolean pixelIsBlack = false;
		if(r == 0 && g == 0 && b == 0) {
			pixelIsBlack = true;
		}
		return pixelIsBlack;
	}
	
	public void chooseTextLocation() {
		imageHolder.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		imageHolder.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
					int textX = imageHolder.getMousePosition().x;
					int textY = imageHolder.getMousePosition().y;
					imageHolder.setCursor(Cursor.getDefaultCursor());
					for(MouseListener ml : imageHolder.getMouseListeners()) {
						imageHolder.removeMouseListener(ml);
					}
					String text = JOptionPane.showInputDialog("Please enter the text you want.");
					Text texter = new Text();
					PlanarImage myTextImage = texter.getPlanarImageFromImage(texter.putTextOnPlanarImage(loadedImage, textX, textY, text));
					displayTiledImage(createText(textX, textY, myTextImage));
			}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		});
	}
	
	public void addBezierCurveDemos() {
		linearBezierOption = new JMenuItem("DEMO: Linear Bezier Curve");
		quadraticBezierOption = new JMenuItem("DEMO: Quadratic Bezier Curve");
		highOrderBezierOption = new JMenuItem("DEMO: High Bezier Curve");
		linearBezierOption.setName("linearbezier");
		quadraticBezierOption.setName("quadraticBezier");
		highOrderBezierOption.setName("highOrderBezier");
	    toolsMenu.add(linearBezierOption);
	    toolsMenu.add(quadraticBezierOption);
	    toolsMenu.add(highOrderBezierOption);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    ActionListener bezierListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem itemChosen = (JMenuItem)e.getSource();
				BezierCurveDemo bcd = new BezierCurveDemo(thisApplet);
				if(itemChosen.getName().equalsIgnoreCase("linearBezier")) {
					displayTiledImage(bcd.performLinearCurve(loadedImage));
				}
				else if(itemChosen.getName().equalsIgnoreCase("quadraticBezier")) {
					bcd.performQuadraticCurve();
				}
				else if(itemChosen.getName().equalsIgnoreCase("highOrderBezier")) {
					bcd.performHighOrderCurve();
				}
			}
		};
	    //End of listeners//
		
		linearBezierOption.addActionListener(bezierListener);
		quadraticBezierOption.addActionListener(bezierListener);
		highOrderBezierOption.addActionListener(bezierListener);
	    
	    this.getContentPane().repaint();
	    
	    this.repaint();
	}	

	/**
	 * Takes a TiledImage and turns it into a PlanarImage to be displayed onto a a DisplayJAI. 
	 * The DisplayJAI is put onto the applet container.
	 * @param myTiledImage
	 */
	public void displayTiledImage(TiledImage myTiledImage) {
		loadedImage = null;
		loadedImage = myTiledImage.createSnapshot();
		displayJAIimage = null;
		removeOldComponents();
		displayJAIimage = new DisplayJAI(loadedImage);
		imageHolder.add(displayJAIimage);
		
		this.getContentPane().repaint();
		
		this.setSize(this.getWidth()-1, this.getHeight()-1);
		this.setSize(this.getWidth()+1, this.getHeight()+1);
		imageHolder.repaint();
		this.repaint();
		repaint();
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
	
	public void removeOldComponents() {
		for(int i = 0; i < imageHolder.getComponentCount();i++) {
			imageHolder.remove(i);
		}
	}
	
	public void paint2DGraphics(Graphics2D g2d) {
		imageHolder.paintComponents(g2d);
	}
	
//	public void paint(Graphics g) {
//		g.drawOval(300, 300, 200, 200);
//	}
}
