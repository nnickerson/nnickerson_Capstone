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

import com.sun.media.jai.widget.DisplayJAI;

public class Locker {
	
	Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	public Locker() {
		
	}

	/**
	 * Grabs the clipboard from the system.
	 */
	public Clipboard getClipBoard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	
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
