package inspiram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;

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
	Layer[] layers = new Layer[0];
	JMenu layersMenu;
	JMenuItem addLayerOption;
	JMenuItem deleteLayerOption;
	Container container = this.getContentPane();
	JPanel layersHolder = new JPanel();
	JPanel layersPanel;
	int currentLayer = 0;
	

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
		imageHolder.setBackground(Color.gray);
		addLayersMenu();
		addLayersPanel();
		addLayersHolder();
		redEye.addRedEyeMenu(inspiramClass);
		line.addCreateLineMenu(inspiramClass);
		addBezierCurveDemos();
		addTextOption();
		addResizeOption();
		inspiramLocker.addPasteOption(this);
		imageSaver.addSaveOption(inspiramClass);
		inspiramLocker.addInspiramLocker(this);
		mainMenuBar.add(inspiramHistory);
		welcomeJLabel = new JLabel("Click File > Load Image > Choose a png, not tested with other formats yet.");
	    this.add(welcomeJLabel);
	}
	
	public void addResizeOption() {
		JMenuItem resizeOption = new JMenuItem("Resize Image On Layer");
		editMenu.add(resizeOption);
		
	    resizeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getNewImageSize();				
			}
		});
	    
	    getContentPane().repaint();	    
	    repaint();
	}
	
	public void getNewImageSize() {
		ResizerSlider rs = new ResizerSlider(layers[currentLayer].getWidth(), layers[currentLayer].getHeight(), this);
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
	    setJMenuBar(mainMenuBar);
	    
	    //Listeners//
	    loadImageOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(null);
				String imageLocation = fileChooser.getSelectedFile().getAbsolutePath();
				System.out.println(imageLocation);
				il.loadImage(inspiramClass, imageLocation);
			}
		});
	    //End of listeners//
	    
	    getContentPane().repaint();	    
	    repaint();
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
	
	public TiledImage createText(int textX, int textY, PlanarImage myTextImage, String text) {
		int width = myTextImage.getWidth();
		int height = myTextImage.getHeight();
		SampleModel mySampleModel = myTextImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = myTextImage.getData();
		Raster actualRaster = layers[currentLayer].getLayerImage().getData();
		WritableRaster writableRaster = actualRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands * width * height];
		int[] actualPixels = new int[nbands * width * height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		actualRaster.getPixels(0, 0, width, height, actualPixels);
		Graphics graphic = layers[currentLayer].getGraphics();
		graphic.setColor(Color.BLACK);
		graphic.drawString(text, textX, textY);
		int xMax = myTextImage.getWidth();
		int yMax = myTextImage.getHeight();
		
		Change change = new Change("Text Creation", currentLayer);
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
					int actualPixelIndex = ((int)y) * layers[currentLayer].getLayerImage().getWidth() * nbands + ((int)x) * nbands;
					pixelHistory.setPrevR(actualPixels[textImagePixelIndex + pixelHistory.R_BAND]);
					pixelHistory.setPrevG(actualPixels[textImagePixelIndex + pixelHistory.G_BAND]);
					pixelHistory.setPrevB(actualPixels[textImagePixelIndex + pixelHistory.B_BAND]);
					pixelHistory.setNewR(0);
					pixelHistory.setNewG(0);
					pixelHistory.setNewB(0);
					change.getAllPixelHistory().add(pixelHistory);
					for (int band = 0; band < nbands; band++) {
						actualPixels[actualPixelIndex + band] = 0;
					}
				}
				else {
					int actualPixelIndex = ((int)y) * layers[currentLayer].getLayerImage().getWidth() * nbands + ((int)x) * nbands;
					for (int band = 0; band < nbands; band++) {
						actualPixels[actualPixelIndex + band] = actualPixels[actualPixelIndex + band];
					}
				}
			}
				
		}
		inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, actualPixels);
		TiledImage ti = new TiledImage(layers[currentLayer].getLayerImage(), 1, 1);
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
		layers[currentLayer].setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		layers[currentLayer].addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
					int textX = layers[currentLayer].getMousePosition().x;
					int textY = layers[currentLayer].getMousePosition().y;
					layers[currentLayer].setCursor(Cursor.getDefaultCursor());
					for(MouseListener ml : layers[currentLayer].getMouseListeners()) {
						layers[currentLayer].removeMouseListener(ml);
					}
					String text = JOptionPane.showInputDialog("Please enter the text you want.");
					Text texter = new Text();
					PlanarImage myTextImage = texter.getPlanarImageFromImage(texter.putTextOnPlanarImage(layers[currentLayer].getLayerImage(), textX, textY, text));
					displayTiledImage(createText(textX, textY, myTextImage, text));
					texter = null;
					System.gc();
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
//		linearBezierOption = new JMenuItem("DEMO: Linear Bezier Curve");
		quadraticBezierOption = new JMenuItem("DEMO: Quadratic Bezier Curve");
//		highOrderBezierOption = new JMenuItem("DEMO: High Bezier Curve");
//		linearBezierOption.setName("linearbezier");
		quadraticBezierOption.setName("quadraticBezier");
//		highOrderBezierOption.setName("highOrderBezier");
//	    toolsMenu.add(linearBezierOption);
	    toolsMenu.add(quadraticBezierOption);
//	    toolsMenu.add(highOrderBezierOption);
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
					bcd.performQuadraticCurve(inspiramClass);
				}
				else if(itemChosen.getName().equalsIgnoreCase("highOrderBezier")) {
					bcd.performHighOrderCurve();
				}
			}
		};
	    //End of listeners//
		
//		linearBezierOption.addActionListener(bezierListener);
		quadraticBezierOption.addActionListener(bezierListener);
//		highOrderBezierOption.addActionListener(bezierListener);
	    
	    this.getContentPane().repaint();
	    
	    this.repaint();
	}	

	/**
	 * Takes a TiledImage and turns it into a PlanarImage to be displayed onto a a DisplayJAI. 
	 * The DisplayJAI is put onto the applet container.
	 * @param myTiledImage
	 */
	public void displayTiledImage(TiledImage myTiledImage) {
		layers[currentLayer].setLayerImage(myTiledImage.createSnapshot());
		layers[currentLayer].setPlainImage();
		layers[currentLayer].add(layers[currentLayer].getImageDisplay());
		layers[currentLayer].setLayerImage(null);
//		removeOldComponents();
//		layers[currentLayer].set(layers[currentLayer].getLayerImage());
		this.getContentPane().repaint();
		
		this.setSize(this.getWidth()-1, this.getHeight()-1);
		this.setSize(this.getWidth()+1, this.getHeight()+1);
		repaintEverything();
	}
	
	public void createRedEye() {
		redEye = null;
		System.gc();
		redEye = new RedEye();
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
	
	public void paintSelectedLayer() {
		layersPanel.getComponent(currentLayer).setBackground(Color.WHITE);
	}
	
	public void paint2DGraphics(Graphics2D g2d) {
		imageHolder.paintComponents(g2d);
	}
	
	public void addLayersMenu() {
		layersMenu = new JMenu("Layers");
		addLayerOption = new JMenuItem("Add Layer");
		deleteLayerOption = new JMenuItem("Delete Layer");
		addLayerOption.setName("addLayer");
		deleteLayerOption.setName("deleteLayer");
		ActionListener layersOptionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem menuItem = (JMenuItem)e.getSource();
				if(menuItem.getName().equalsIgnoreCase("addLayer")) {
					addLayer();
				}
				else {
					deleteLayer();
				}
 			}
		};
		addLayerOption.addActionListener(layersOptionListener);
		deleteLayerOption.addActionListener(layersOptionListener);
		layersMenu.add(addLayerOption);
		layersMenu.add(deleteLayerOption);
		mainMenuBar.add(layersMenu);
	}
	
	public void addLayersHolder() {
		layersHolder.setBackground(Color.GRAY);
//		container.add(layersHolder, BorderLayout.WEST);
	}
	
	public void newLine() {
		line = null;
		System.gc();
		line = new Line();
	}
	
	public void addLayersPanel() {
		layersPanel = new JPanel();
		layersPanel.setSize(rWidth,rHeight);
		layersPanel.setVisible(true);
		layersPanel.setBackground(Color.black);
		this.setLayout(new BorderLayout());
		layersPanel.setLayout(new BoxLayout(layersPanel, BoxLayout.Y_AXIS));
		this.add(layersPanel, BorderLayout.EAST);
//		this.add(layersHolder);
		repaintEverything();	
	}
	
	public void displayLayersOnPanel() {
		layersPanel.removeAll();
		this.add(layersPanel, BorderLayout.EAST);
		//Setting up button listeners//
		ActionListener layerButtonsListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton layerButton = (JButton)e.getSource();
				JPanel layerPanel = (JPanel)layerButton.getParent();
				JLabel layerNameLabel = (JLabel)layerPanel.getComponent(0);
				System.out.println("LAYERLABEL: " + layerNameLabel.getName());
				if(layerButton.getText().contains("up")) {
					moveLayerUp(Integer.parseInt(layerPanel.getName()));
					System.out.println("Layer movement: " + layerNameLabel.getName());
				}
				else if(layerButton.getText().contains("down")) {
					moveLayerDown(Integer.parseInt(layerPanel.getName()));
					System.out.println("Layer movement: " + layerNameLabel.getName());
				}
			}
		};
		//Ending button listeners setup//
		
		for(int i = 0; i < layers.length; i++) {
			JPanel newLayerPanel = new JPanel();
			JButton upButton = new JButton();
			JButton downButton = new JButton();
			upButton.setText("Move " + layers[i].getLayerName() + " up");
			downButton.setText("Move " + layers[i].getLayerName() + " down");
			upButton.setName(layers[i].getLayerName() + " up");
			downButton.setName(layers[i].getLayerName() + " down");
			upButton.addActionListener(layerButtonsListener);
			downButton.addActionListener(layerButtonsListener);
			newLayerPanel.setLayout(new BorderLayout());
			System.out.println(layers[i].getLayerName());
			JLabel layerLabel = new JLabel(layers[i].getLayerName());
			layerLabel.setName(layers[i].getLayerName());
			layerLabel.setText(layers[i].getLayerName());
			newLayerPanel.setName("" + i);
			newLayerPanel.add(layerLabel, BorderLayout.EAST);
			newLayerPanel.add(upButton, BorderLayout.NORTH);
			newLayerPanel.add(downButton, BorderLayout.SOUTH);
			newLayerPanel.setBackground(Color.LIGHT_GRAY);
			newLayerPanel.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {
					JPanel layerPanel = (JPanel)e.getSource();
					currentLayer = (Integer.parseInt(layerPanel.getName()));
					System.out.println("Current Layer: " + currentLayer);
					for(Component c : layersPanel.getComponents()) {
						c.setBackground(Color.LIGHT_GRAY);
					}
					layerPanel.setBackground(Color.WHITE);
				}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}
			});
			layersPanel.add(newLayerPanel);
		}
		System.out.println("PANELS INSTEAD OF BUTTON");
		correctLayerIdentities();
		repaintEverything();
	}
	
	public void addLayer() {
		layersPanel.removeAll();
		Layer[] tempLayers = new Layer[layers.length];
		for(int i = 0; i < layers.length; i++) {
			System.out.println(layers[i].getLayerName() + " LAYER");
			tempLayers[i] = layers[i];
		}
		layers = new Layer[tempLayers.length+1];
		for(int i = 0; i < tempLayers.length; i++) {
			layers[i] = tempLayers[i];
		}
		System.out.println("Added a new layer");
		Layer newLayer = new Layer("Layer " + (layers.length-1), layers.length-1);
		newLayer.setVisible(true);
		currentLayer = layers.length-1;
		layers[layers.length-1] = newLayer;
		layers[0].setBackground(Color.green);
		try {
			layers[0].setOpaque(false);
//			layers[0].add(new JLabel(new ImageIcon(ImageIO.read(new File("images.jpg")))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(layers.length > 1) {
			layers[1].setBackground(Color.red);
			layers[1].setOpaque(false);
			try {
//				layers[1].add(new JLabel(new ImageIcon(ImageIO.read(new File("Untitled.jpg")))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		correctLayerIdentities();
		displayAllLayers();
		displayLayersOnPanel();
		correctLayerIdentities();
		layersPanel.getComponent(layersPanel.getComponentCount()-1).setBackground(Color.WHITE);
		repaintEverything();
	}
	
	public void displayAllLayers() {
		layersHolder.removeAll();
		container.removeAll();
//		layersPanel.removeAll();
		for(Layer layer : layers) {
			System.out.println("Adding layer back to holder!");
			layer.setOpaque(false);
			this.add(layer);
			welcomeJLabel.setVisible(false);
		}
		container.add(layersHolder); //Limits the area for each layer
		correctLayerIdentities();
		displayLayersOnPanel();
		correctLayerIdentities();
		repaintEverything();
	}
	
	public PlanarImage tiledImageToPlanarImage(TiledImage tiledImage) {
		return tiledImage.createSnapshot();
	}
	
	public void moveLayerDown(int movingLayersID) {
		int layerPositionMoving = 0;
		int layerPositionBelow = 0;
		Layer tempLayer;
		for(int i = 0; i < layers.length; i++) {
			if(layers[i].getLayerID() == movingLayersID) {
				if(i != layers.length-1) {
					layerPositionMoving = i;
					layerPositionBelow = i+1;
					currentLayer = i;
					i = layers.length;
				}
			}
		}
		tempLayer = layers[layerPositionMoving];
		layers[layerPositionMoving] = layers[layerPositionBelow];
		layers[layerPositionBelow] = tempLayer;
		displayLayersOnPanel();
		displayAllLayers();
		correctLayerIdentities();
		paintSelectedLayer();
		repaintEverything();
	}
	
	public void moveLayerUp(int movingLayersID) {
		
		int layerPositionMoving = 0;
		int layerPositionAbove = 0;
		Layer tempLayer;
		for(int i = 0; i < layers.length; i++) {
			if(layers[i].getLayerID() == movingLayersID) {
				if(i != 0) {
					layerPositionMoving = i;
					layerPositionAbove = i-1;
					currentLayer = i;
					i = layers.length;
				}
			}
		}
		tempLayer = layers[layerPositionMoving];
		layers[layerPositionMoving] = layers[layerPositionAbove];
		layers[layerPositionAbove] = tempLayer;
		displayLayersOnPanel();
		displayAllLayers();
		correctLayerIdentities();
		paintSelectedLayer();
		repaintEverything();
	}
	
	public void deleteLayer() {
		layersHolder.removeAll();
		layersHolder.repaint();
		layersHolder.updateUI();
		if(layers.length > 0) {
			Layer[] tempLayers = new Layer[layers.length-1];
			int tempLayerIndex = 0;
			for(int i = 0; i < layers.length; i++) {
				if(i == currentLayer) {
					//Do nothing
				}
				else {
					System.out.println("Color: " + layers[i].getBackground());
					tempLayers[tempLayerIndex] = layers[i];
					tempLayerIndex++;
				}
			}
			layers = new Layer[tempLayers.length];
			System.out.println("Now for the new layers..");
			for(int i = 0; i < layers.length; i++) {
				layers[i] = tempLayers[i];
				System.out.println("Color: " + layers[i].getBackground());
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "There are no layers to delete!");
		}
		
		displayAllLayers();
		displayLayersOnPanel();
		correctLayerIdentities();
		repaintEverything();
	}
	
	public void repaintEverything() {
		repaint();
		this.repaint();
		container.repaint();
		layersHolder.repaint();
		layersPanel.repaint();
		layersHolder.updateUI();
		layersPanel.updateUI();
		for(int i = 0; i < layersPanel.getComponentCount(); i++) {
			layersPanel.getComponent(i).repaint();
		}
	}
	
	public void correctLayerIdentities() {
		for(int i = 0; i < layersPanel.getComponentCount(); i++) {
			JPanel layerToChange = null;
			layerToChange = (JPanel)layersPanel.getComponent(i);
			layerToChange.setName("" + i);
			JButton upButton = (JButton)layerToChange.getComponent(1);
			upButton.setText("Layer up");
			upButton.setName("Layer up");
			
			JButton downButton = (JButton)layerToChange.getComponent(2);
			downButton.setText("Layer down");
			downButton.setName("Layer down");
		}
		for(int i = 0; i < layers.length; i++) {
			Layer layerToChange = null;
			layerToChange = layers[i];
			layerToChange.setLayerID(i);
			layers[i] = layerToChange;
		}
	}
	
//	public void paint(Graphics g) {
//		g.drawOval(300, 300, 200, 200);
//	}
}
