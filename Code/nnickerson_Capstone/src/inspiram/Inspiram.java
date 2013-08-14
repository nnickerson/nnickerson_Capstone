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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	double lineClicks = 0.0;
	double lineBeginningX = 0.0;
	double lineBeginningY = 0.0;
	double lineEndingX = 0.0;
	double lineEndingY = 0.0;
	double lineWidth = 1.0;
	JApplet thisApplet = this;
	boolean vPressed = false;
	boolean ctrlPressed = false;
	Locker inspiramLocker = new Locker();
	History inspiramHistory = new History();

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
		addCreateLineMenu();
		addBezierCurveDemos();
		addTextOption();
		addPasteOption();
		addSaveOption();
		addInspiramLocker();
		mainMenuBar.add(inspiramHistory);
		welcomeJLabel = new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet.");
	    this.add(welcomeJLabel);
	}
	
	public void pasteImageFromClipboard() {
		Locker myLocker = new Locker();
		Text noText = new Text();
		Image copiedImage = myLocker.getImageFromClipboard();
		
		if(copiedImage != null) {
			PlanarImage copiedPlanarImage = noText.getPlanarImageFromImage(copiedImage);
			
			loadedImage = myLocker.combineImages(loadedImage, copiedPlanarImage, loadedImage != null);
			
			displayJAIimage = null;
			removeOldComponents();
			displayJAIimage = new DisplayJAI(loadedImage);
			imageHolder.add(displayJAIimage);
	
			this.getContentPane().repaint();
			this.setSize(this.getWidth() - 1, this.getHeight() - 1);
			this.setSize(this.getWidth() + 1, this.getHeight() + 1);
			imageHolder.repaint();
			this.repaint();
			repaint();
		}
		else {
			System.out.println("The flavor on the clipboard was not an image!");
		}
	}
	
	public void addSaveOption() {
		saveOption = new JMenuItem("Save Image");
		fileMenu.add(saveOption);
		this.setJMenuBar(mainMenuBar);
		
		//Listeners//
	    ActionListener saveListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageSaver imageSaver = new ImageSaver();
				imageSaver.saveImageAsPNG(loadedImage, false, "");
			}
		};
		
		saveOption.addActionListener(saveListener);
	    //End of listeners//
	}
	
	public void addPasteOption() {
		pasteOption = new JMenuItem("Paste");
	    editMenu.add(pasteOption);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    ActionListener pasteListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pasteImageFromClipboard();				
			}
		};
	    //End of listeners//
		
		pasteOption.addActionListener(pasteListener);
		
		thisApplet.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("KEY PRESSED!: " + e.getKeyCode());
				
				if(e.getKeyCode() == KeyEvent.VK_V) {
					vPressed = true;
					System.out.println("FOUND V KEY");
				}
				if(e.getKeyCode() == 17) {
					ctrlPressed = true;
					System.out.println("FOUND CONTROL KEY");
				}
				if(ctrlPressed && vPressed) {
					pasteImageFromClipboard();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_V) {
					vPressed = false;
				}
				if(e.getKeyCode() == KeyEvent.CTRL_DOWN_MASK) {
					ctrlPressed = false;
				}
			}
			
		});
		thisApplet.setFocusable(true);
	    
	    this.getContentPane().repaint();
	    
	    this.repaint();
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
	
	public void createText(int textX, int textY, PlanarImage myTextImage) {
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
		change.undoChange.addActionListener(createChangeUndoListener());
		
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
		TiledImage myTiledImage = ti;
		loadedImage = null;
		loadedImage = myTiledImage.createSnapshot();
		displayJAIimage = null;
		removeOldComponents();
		displayJAIimage = new DisplayJAI(loadedImage);
		imageHolder.add(displayJAIimage);
		
		writableRaster = null;
		actualPixels = null;
		readableRaster = null;
		actualRaster = null;

		this.getContentPane().repaint();
		this.setSize(this.getWidth() - 1, this.getHeight() - 1);
		this.setSize(this.getWidth() + 1, this.getHeight() + 1);
		imageHolder.repaint();
		this.repaint();
		repaint();
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
					createText(textX, textY, myTextImage);
			}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
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
			
		}
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
	
	public ActionListener createChangeUndoListener() {
		ActionListener changeUndoListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem chosenMenuItem = (JMenuItem) e.getSource();
				
					System.out.println("Undoing image from history!");
					JPopupMenu popupMenu = (JPopupMenu)chosenMenuItem.getParent();
					Change parentMenu = (Change)popupMenu.getInvoker();
					System.out.println("Undoing image from history!");
						
						loadedImage = parentMenu.revertChange(loadedImage);
						
						displayJAIimage = null;
						removeOldComponents();
						displayJAIimage = new DisplayJAI(loadedImage);
						imageHolder.add(displayJAIimage);
	
	
						thisApplet.getContentPane().repaint();
						thisApplet.setSize(thisApplet.getWidth() - 1, thisApplet.getHeight() - 1);
						thisApplet.setSize(thisApplet.getWidth() + 1, thisApplet.getHeight() + 1);
						imageHolder.repaint();
						thisApplet.repaint();
						repaint();
			}
		};
		return changeUndoListener;
	}
	
	public void addInspiramLocker() {
		mainMenuBar.add(inspiramLocker);
		ActionListener lockerListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem chosenMenuItem = (JMenuItem) e.getSource();
				String chosenItemName = chosenMenuItem.getName();
				if(chosenItemName.equalsIgnoreCase("Store")) {
					System.out.println("Storing image in locker!");
					JPopupMenu popupMenu = (JPopupMenu)chosenMenuItem.getParent();
					JMenu parentMenu = (JMenu)popupMenu.getInvoker();
					int chosenImageNumber = Integer.parseInt(parentMenu.getName());
					inspiramLocker.addCopiedImageToLocker(chosenImageNumber);
				}
				else {
					System.out.println("Pasting image from locker!");
					JPopupMenu popupMenu = (JPopupMenu)chosenMenuItem.getParent();
					JMenu parentMenu = (JMenu)popupMenu.getInvoker();
					int chosenImageNumber = Integer.parseInt(parentMenu.getName());
					Text noText = new Text();
					Image copiedImage = inspiramLocker.getImageFromLocker(chosenImageNumber);
					System.out.println("Pasting image from Locker!");
					if(copiedImage != null) {
						PlanarImage copiedPlanarImage = noText.getPlanarImageFromImage(copiedImage);
						
						loadedImage = inspiramLocker.combineImages(loadedImage, copiedPlanarImage, loadedImage != null);
						
						displayJAIimage = null;
						removeOldComponents();
						displayJAIimage = new DisplayJAI(loadedImage);
						imageHolder.add(displayJAIimage);
	
	
						thisApplet.getContentPane().repaint();
						thisApplet.setSize(thisApplet.getWidth() - 1, thisApplet.getHeight() - 1);
						thisApplet.setSize(thisApplet.getWidth() + 1, thisApplet.getHeight() + 1);
						imageHolder.repaint();
						thisApplet.repaint();
						repaint();
					}
					else {
						System.out.println("The flavor on the clipboard was not an image!");
					}
				}
			}
		};
		
		System.out.println("Images held in locker: " + inspiramLocker.getItemCount());
		
		for(int i = 0; i < inspiramLocker.getItemCount(); i++) {
			JMenu pickedMenuItem = (JMenu)inspiramLocker.getItem(i);
			JMenuItem pasteStoredImageOption = (JMenuItem)pickedMenuItem.getItem(0);
			JMenuItem storeImageOption = (JMenuItem)pickedMenuItem.getItem(1);
			pasteStoredImageOption.addActionListener(lockerListener);
			storeImageOption.addActionListener(lockerListener);
		}
	}
	
	public void loadImage(String imageLocation) {
		loadedImage = il.loadPlanarImageWithJAI(imageLocation);
		displayJAIimage = null;
		removeOldComponents();
		displayJAIimage = new DisplayJAI(loadedImage);
//		scrollPane = new JScrollPane(displayJAIimage);
		imageHolder.add(displayJAIimage);
		this.setSize(this.getWidth() - 1, this.getHeight() - 1);
		this.setSize(this.getWidth() + 1, this.getHeight() + 1);
		imageHolder.repaint();
		this.repaint();
		this.getContentPane().repaint();
		welcomeJLabel.setVisible(false);
	}
	
	public void addCreateLineMenu() {
	    createLineOption = new JMenuItem("Create Line");
	    toolsMenu.add(createLineOption);
	    this.setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    createLineOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getLineLocation();
			}
		});
	    //End of listeners//
	    
	    this.getContentPane().repaint();
	    
	    this.repaint();
	}
	
	public void getLineLocation() {
		imageHolder.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		imageHolder.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(lineClicks == 0) {
					System.out.println("100 / -100: " + 100/(-100));
					lineBeginningX = imageHolder.getMousePosition().x;
					lineBeginningY = imageHolder.getMousePosition().y;
					lineClicks++;
					System.out.println("Line start click! - (" + lineBeginningX + ", " + lineBeginningY + ")");
				}
				else {
					lineEndingX = imageHolder.getMousePosition().x;
					lineEndingY = imageHolder.getMousePosition().y;
					lineClicks = 0;
					imageHolder.setCursor(Cursor.getDefaultCursor());
					for(MouseListener ml : imageHolder.getMouseListeners()) {
						imageHolder.removeMouseListener(ml);
					}
					double yDifference = lineEndingY-lineBeginningY;
					double xDifference = lineEndingX-lineBeginningX;
					double slope = (((yDifference)/(xDifference)));
					System.out.println("Line end click! - (" + lineEndingX + ", " + lineEndingY + ")");
					drawLine(lineBeginningX, lineBeginningY, lineEndingX, lineEndingY, slope);
				}
			}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
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
			
		}
		});
	}
	
	/**
	 * Based from y = mx + b. Calculates the y-intercept from a given point.
	 * @param x
	 * @param y
	 * @param slope
	 * @return
	 */
	public double findYIntercept(double x, double y, double slope) {
		double yIntercept = 0;
		double slopeAndX = slope*x;
		double b = y-slopeAndX;
		yIntercept = b;
		return yIntercept;
	}
	
	public void drawLine(double lineBX, double lineBY, double lineEX, double lineEY, double slope) {
		int width = loadedImage.getWidth();
		int height = loadedImage.getHeight();
		SampleModel mySampleModel = loadedImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = loadedImage.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands * width * height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		double yIntercept = findYIntercept(lineEX, lineEY, slope);
		System.out.println("Y-Intercept of 1st: " + findYIntercept(lineBX, lineBY, slope) + "     Y-Intercept of 2nd: " + findYIntercept(lineEX, lineEY, slope));
		int pixelIndex = 0;
		
		double x1 = lineBX;
		double x2 = lineEX;
		double y1 = lineBY;
		double y2 = lineEY;
		if(lineBX >= lineEX) {
			x1 = lineEX;
			x2 = lineBX;
			y1 = lineEY;
			y2 = lineBY;
		}
		
		double repeatingXs = Math.abs(slope);
		double yPlus = 0;
		
		
		for (double x = x1; x <= x2; x+=.01) {
			double y = (slope * x) + yIntercept;
				pixelIndex = (int)y * width * nbands + (int)x * nbands;
				if(x == lineBX || x == lineEX-1) {
					System.out.println("Coordinates: (" + x + ", " + y + ")");
				}
				for (int band = 0; band < nbands; band++) {
					pixels[(pixelIndex - 0) + (band)] = 255;
					pixels[(pixelIndex - 1) + (band)] = 255;
					pixels[(pixelIndex - 2) + (band)] = 255;
					pixels[(pixelIndex - 3) + (band)] = 255;
					pixels[(pixelIndex + 1) + (band)] = 255;
					pixels[(pixelIndex + 2) + (band)] = 255;
					pixels[(pixelIndex + 3) + (band)] = 255;
				}
		}
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(loadedImage, 1, 1);
		ti.setData(writableRaster);
		TiledImage myTiledImage = ti;
		loadedImage = null;
		loadedImage = myTiledImage.createSnapshot();
		displayJAIimage = null;
		removeOldComponents();
		displayJAIimage = new DisplayJAI(loadedImage);
		imageHolder.add(displayJAIimage);
		writableRaster = null;
		readableRaster = null;

		this.getContentPane().repaint();

		this.setSize(this.getWidth() - 1, this.getHeight() - 1);
		this.setSize(this.getWidth() + 1, this.getHeight() + 1);
		imageHolder.repaint();
		this.repaint();
		repaint();

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
	
	public void addImageLoadMenu() {
		mainMenuBar = new JMenuBar();
	    fileMenu = new JMenu("File");
	    editMenu = new JMenu("Edit");
	    toolsMenu = new JMenu("Tools");
	    loadImageOption = new JMenuItem("Load Image");
	    fileMenu.add(loadImageOption);
	    mainMenuBar.add(fileMenu);
	    mainMenuBar.add(editMenu);
	    mainMenuBar.add(toolsMenu);
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
//		System.out.println("Red Eye Radius: " + redEyeDiameter);
//	    redEyeCircle.setStroke(new BasicStroke());
		int previousCenterX = redEyeCenterX;
		int previousCenterY = redEyeCenterY;
	    redEyeCircle.drawOval(redEyeCenterX-(redEyeDiameter/2), redEyeCenterY-(redEyeDiameter/2), redEyeDiameter, redEyeDiameter);
//	    redEyeCircle.setPaint(Color.green);
	    redEyeCenterX = previousCenterX;
	    redEyeCenterY = previousCenterY;
	    previousGraphics = redEyeCircle;
	} 
	
	public void removeOldComponents() {
		for(int i = 0; i < imageHolder.getComponentCount();i++) {
			imageHolder.remove(i);
		}
	}
	
	public void paint2DGraphics(Graphics2D g2d) {
		imageHolder.paintComponents(g2d);
	}
	
	public void grabEyeLocation() {
		imageHolder.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		imageHolder.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				redEyeCenterX = imageHolder.getMousePosition().x;
				redEyeCenterY = imageHolder.getMousePosition().y;
//				System.out.println("Eye center: " + redEyeCenterX + ", " + redEyeCenterY);
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
		loadedImage = null;
		return ti;
	}
	
	
	/**
	 * This method was used for manipulating pixels and further transformed into 
	 * trying to fix the red eye problem.
	 */
	public void fixRedEye() {
		sliderFrame.dispose();
		
		TiledImage myTiledImage = alterPixelsData();
		displayTiledImage(myTiledImage);
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
//			System.out.println(lp.toString());
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
		loadedImage = null;
		return ti;
	}
	
	public void addRedEyeMenu() {
		JMenuItem redEyeOption = new JMenuItem("Fix Red Eye");
	    toolsMenu.add(redEyeOption);
	    mainMenuBar.add(toolsMenu);
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
	}
	
//	public void paint(Graphics g) {
//		g.drawOval(300, 300, 200, 200);
//	}
}
