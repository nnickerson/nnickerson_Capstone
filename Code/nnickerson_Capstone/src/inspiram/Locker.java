package inspiram;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Creates a locker that holds multiple images. Meant for the purposes of grabbing
 * images from the system clipboard and storing them within the program for later use.
 * @author nnickerson
 *
 */
public class Locker extends JMenu {
	
	Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	Image[] storedImages = new Image[5];
	JMenu image0 = new JMenu("Image 0");
	JMenu image1 = new JMenu("Image 1");
	JMenu image2 = new JMenu("Image 2");
	JMenu image3 = new JMenu("Image 3");
	JMenu image4 = new JMenu("Image 4");
	JMenu[] lockerMenuItems = new JMenu[5];
	Image currentImage;

	public Locker() {
		setupLocker();
	}
	
	/**
	 * Sets up the locker for use.
	 */
	public void setupLocker() {
		this.setText("Locker");
		image0.setName("0");
		image1.setName("1");
		image2.setName("2");
		image3.setName("3");
		image4.setName("4");
		lockerMenuItems[0] = image0;
		lockerMenuItems[1] = image1;
		lockerMenuItems[2] = image2;
		lockerMenuItems[3] = image3;
		lockerMenuItems[4] = image4;
		this.add(lockerMenuItems[0]);
		this.add(lockerMenuItems[1]);
		this.add(lockerMenuItems[2]);
		this.add(lockerMenuItems[3]);
		this.add(lockerMenuItems[4]);
		for(int i = 0; i < lockerMenuItems.length; i++) {
			JMenuItem sItem = new JMenuItem("Store Image");
			JMenuItem pItem = new JMenuItem("Paste Image");
			sItem.setName("Store");
			pItem.setName("Paste");
			lockerMenuItems[i].add(sItem);
			pItem.setVisible(false);
			lockerMenuItems[i].add(pItem);
		}
	}

	/**
	 * Grabs the clipboard from the system.
	 */
	public Clipboard getClipBoard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	/**
	 * Adds the image that is currently copied to the locker to the last location
	 */
	public void addCopiedImageToLocker() {
		boolean hadNull = false;
		for(int i = 0; i < storedImages.length; i++) {
			if(storedImages[i] == null) {
				storedImages[i] = getImageFromClipboard();
				Image image = storedImages[i];
				Image shrunkImage = image.getScaledInstance(75, 75, Image.SCALE_FAST);
				ImageIcon icon = new ImageIcon(shrunkImage);
				lockerMenuItems[i].setIcon(icon);
				
				hadNull = true;
				i = storedImages.length;
			}
		}
		
		if(!hadNull) {
			storedImages[4] = getImageFromClipboard();
		}
	}
	
	/**
	 * Adds the image that is currently copied to the locker in the specified location.
	 * The location is zero based.
	 * @param location
	 */
	public void addCopiedImageToLocker(int location) {
		storedImages[location] = getImageFromClipboard();
		Image image = getImageFromClipboard();
		if(image != null) {
			Image shrunkImage = image.getScaledInstance(75, 75, Image.SCALE_FAST);
			ImageIcon icon = new ImageIcon(shrunkImage);
			lockerMenuItems[location].setIcon(icon);
			lockerMenuItems[location].getItem(1).setVisible(true); //Unhides the paste option
		}
	}
	
	/**
	 * Deletes the image at the specified location from the locker
	 * @param location
	 */
	public void deleteImageFromLocker(int location) {
		storedImages[location] = null;
		lockerMenuItems[location].setIcon(null);
		lockerMenuItems[location].getItem(1).setVisible(false); //Unhides the paste option
	}
	
	/**
	 * Grabs the image at a specified location within the locker.
	 * @param location
	 * @return
	 */
	public Image getImageFromLocker(int location) {
		Image imageFromLocker;
		imageFromLocker = storedImages[location];
		return imageFromLocker;
	}
	
	/**
	 * Grabs the image that was copied onto the system clipboard
	 * @return
	 */
	public Image getImageFromClipboard() {
		DataFlavor df = new DataFlavor();
		Image imageFromClipBoard = null;
		try {
			imageFromClipBoard = (Image) systemClipboard.getData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			System.out.println("You need an image copied!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return imageFromClipBoard;
	}
	
	public PlanarImage combineImages(PlanarImage innerImage, PlanarImage outerImage, boolean hasInnerImage) {
		PlanarImage combinedImage = innerImage;
		
		int innerImageWidth = 0;
		int innerImageHeight = 0;
		
		if(hasInnerImage) {
			innerImageWidth = innerImage.getWidth();
			innerImageHeight = innerImage.getHeight();
		}
		
		BufferedImage bi = outerImage.getAsBufferedImage();
		
		boolean imageCanFit = false;
		float scaledX = outerImage.getWidth();
		float scaledY = outerImage.getHeight();
		float scale = 0.0f;
		while(!imageCanFit) {
			if(scaledX <= Toolkit.getDefaultToolkit().getScreenSize().getWidth()*.9) {
				if(scaledY <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()*.9) {
					imageCanFit = true;
				}
				else {
					scaledX = scaledX*.9f;
					scaledY = scaledY*.9f;
					scale += 0.1f;
				}
			}
			else {
				scaledX = scaledX*.9f;
				scaledY = scaledY*.9f;
				scale += 0.1f;
			}
		}
		
		Image image = bi.getScaledInstance((int)scaledX, (int)scaledY, Image.SCALE_SMOOTH);
		
	    ParameterBlock pb2 = new ParameterBlock();
	    pb2.add(image);
	    //The awtImage is an operation
	    PlanarImage scaledOuterImage = (PlanarImage)JAI.create("awtImage", pb2);
	    
	    
	    boolean isOuterWidthBigger = true;
	    boolean isOuterHeightBigger = true;
	    int newImageWidth = scaledOuterImage.getWidth();
		int newImageHeight = scaledOuterImage.getHeight();
		    
	    if(hasInnerImage) {
		    if(scaledOuterImage.getWidth() <= innerImageWidth) {
		    	isOuterWidthBigger = false;
		    }
		    if(scaledOuterImage.getHeight() <= innerImageHeight) {
		    	isOuterHeightBigger = false;
		    }
		    
		    
		    if(!isOuterWidthBigger) {
		    	newImageWidth = innerImageWidth;
		    }
		    if(!isOuterHeightBigger) {
		    	newImageHeight = innerImageWidth;
		    }
	    }
	    
	    BufferedImage myBI = new BufferedImage(newImageWidth, newImageHeight, BufferedImage.TYPE_INT_RGB);
	    Image blankImage = myBI;
	    
	    ParameterBlock pb3 = new ParameterBlock();
	    pb3.add(blankImage);
	    //The awtImage is an operation
	    PlanarImage blankedImage = (PlanarImage)JAI.create("awtImage", pb3);
	    
	    if(hasInnerImage) {
	    	PlanarImage firstImage = addImageToImage(innerImage, blankedImage);
	    	combinedImage = addImageToImage(scaledOuterImage, firstImage);
	    }
	    else {
	    	combinedImage = scaledOuterImage;
	    }
		
		return combinedImage;
	}

	private PlanarImage addImageToImage(PlanarImage innerImage, PlanarImage blankedImage) {
		int width = innerImage.getWidth();
		int height = innerImage.getHeight();
		SampleModel mySampleModel = innerImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = innerImage.getData();
		Raster blankedRaster = innerImage.getData();
		WritableRaster writableRaster = blankedRaster.createCompatibleWritableRaster();
		int[] innerPixels = new int[nbands * width * height];
		int[] blankedPixels = new int[nbands * width * height];
		readableRaster.getPixels(0, 0, width, height, innerPixels);
		blankedRaster.getPixels(0, 0, width, height, blankedPixels);
		
		int xMax = innerImage.getWidth();
		int yMax = innerImage.getHeight();
		
		for (int x = 0; x < xMax; x++) {
			for (int y = 0; y < yMax; y++) {
				int textImagePixelIndex = (int)y * width * nbands + (int)x * nbands;				
				for (int band = 0; band < nbands; band++) {
						blankedPixels[(textImagePixelIndex) + (band)] = innerPixels[(textImagePixelIndex) + (band)];
				}
			}
				
		}
		writableRaster.setPixels(0, 0, width, height, blankedPixels);
		TiledImage ti = new TiledImage(blankedImage, 1, 1);
		ti.setData(writableRaster);
		TiledImage myTiledImage = ti;
		PlanarImage combinedImage = myTiledImage.createSnapshot();
		return combinedImage;
	}
}
