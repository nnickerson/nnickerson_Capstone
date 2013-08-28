package inspiram;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import javax.media.jai.TiledImage;
import javax.swing.JMenuItem;

public class Line {
	
	int lineBeginningX = 0;
	int lineEndingX = 10;
	int lineBeginningY = 0;
	int lineEndingY = 10;
	int lineWidth = 1;
	String lineName = "";
	int lineClicks = 0;

	public Line() {
		
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
	
	/**
	 * Adds a JMenuItem for the line add option.
	 * @param inspiram
	 */
	public void addCreateLineMenu(final Inspiram inspiram) {
	    inspiram.createLineOption = new JMenuItem("Create Line");
	    inspiram.toolsMenu.add(inspiram.createLineOption);
	    inspiram.setJMenuBar(inspiram.mainMenuBar);
	    
	    //Listeners//
	    inspiram.createLineOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getLineLocation(inspiram);
			}
		});
	    //End of listeners//
	    
	    inspiram.getContentPane().repaint();
	    
	    inspiram.repaint();
	}
	
	/**
	 * Grabs the line start location and the line end location from user input.
	 * @param inspiram
	 */
	public void getLineLocation(final Inspiram inspiram) {
		inspiram.layers[inspiram.currentLayer].setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		inspiram.layers[inspiram.currentLayer].addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

		@Override
		public void mousePressed(MouseEvent e) {
				if(lineClicks == 0) {
					System.out.println("100 / -100: " + 100/(-100));
					lineBeginningX = inspiram.layers[inspiram.currentLayer].getMousePosition().x;
					lineBeginningY = inspiram.layers[inspiram.currentLayer].getMousePosition().y;
					lineClicks++;
					System.out.println("Line start click! - (" + lineBeginningX + ", " + lineBeginningY + ")");
				}
				else {
					lineEndingX = inspiram.layers[inspiram.currentLayer].getMousePosition().x;
					lineEndingY = inspiram.layers[inspiram.currentLayer].getMousePosition().y;
					lineClicks = 0;
					inspiram.layers[inspiram.currentLayer].setCursor(Cursor.getDefaultCursor());
					for(MouseListener ml : inspiram.layers[inspiram.currentLayer].getMouseListeners()) {
						inspiram.layers[inspiram.currentLayer].removeMouseListener(ml);
					}
					double yDifference = lineEndingY-lineBeginningY;
					double xDifference = lineEndingX-lineBeginningX;
					double slope = (((yDifference)/(xDifference)));
					System.out.println("Line end click! - (" + lineEndingX + ", " + lineEndingY + ")");
					inspiram.displayTiledImage(drawLine(lineBeginningX, lineBeginningY, lineEndingX, lineEndingY, slope, inspiram));
					System.gc();
					inspiram.newLine();
				}
		}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		});
	}
	
	/**
	 * Draws the line into the PlanarImage.
	 * @param lineBX
	 * @param lineBY
	 * @param lineEX
	 * @param lineEY
	 * @param slope
	 * @param inspiram
	 * @return
	 */
	public TiledImage drawLine(double lineBX, double lineBY, double lineEX, double lineEY, double slope, Inspiram inspiram) {
		System.gc();
		int width = inspiram.layers[inspiram.currentLayer].getWidth();
		int height = inspiram.layers[inspiram.currentLayer].getHeight();
		SampleModel mySampleModel = inspiram.layers[inspiram.currentLayer].getLayerImage().getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = inspiram.layers[inspiram.currentLayer].getLayerImage().getData();
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
		
		Change change = new Change("Line Creation");
		change.undoChange.addActionListener(change.createChangeUndoListener(inspiram));
		System.out.println("SLOPE: " + slope);
//		int minimalWhite = 0;
//		int maximumWhite = 255;
		for (double x = x1+.5; x <= x2+.5; x+=.01) {
			int newInt = (int)x;
			
			double y = (slope * x) + yIntercept;
				pixelIndex = (int)y * width * nbands + (int)x * nbands;
				if(x == lineBX || x == lineEX-1) {
					System.out.println("Coordinates: (" + x + ", " + y + ")");
				}
				PixelHistory pixelHistory = new PixelHistory((int)x, (int)y);
				if(!change.getAllPixelHistory().contains(pixelHistory.x) && !change.getAllPixelHistory().contains(pixelHistory.y)) {
					int prevR = new Integer(pixels[pixelIndex + pixelHistory.R_BAND]);
					int prevG = new Integer(pixels[pixelIndex + pixelHistory.G_BAND]);
					int prevB = new Integer(pixels[pixelIndex + pixelHistory.B_BAND]);
				
					pixelHistory.setPrevR(prevR);
					pixelHistory.setPrevG(prevG);
					pixelHistory.setPrevB(prevB);
				}
//				System.out.println("" + x + "\n" + (0<x) + "\n" + (x<1) + "\n" + (slope<0));
				if(0 <= (x%1) && (x%1) < .01 && slope < 0) {
//					int aaIndexUp = ((int)(y-1)) * width * nbands + ((int)(x)) * nbands;
//					int aaIndexRight = ((int)(y)) * width * nbands + ((int)(x+1)) * nbands;
//					pixels[(aaIndexUp) + (0)] = 87;
//					pixels[(aaIndexUp) + (1)] = 170;
//					pixels[(aaIndexUp) + (2)] = 34;
//					pixels[(aaIndexRight) + (0)] = 87;
//					pixels[(aaIndexRight) + (1)] = 170;
//					pixels[(aaIndexRight) + (2)] = 34;
//					System.out.println("HIT: " + x);
					pixels = antiAlias(pixels, (int)y, width, nbands, (int)x);
				}
//				double antiAliasedValue = (maximumWhite-((maximumWhite-minimalWhite)-((maximumWhite-minimalWhite)*(x%1))));
					pixels[(pixelIndex - 0) + (0)] = 255;
					pixels[(pixelIndex - 0) + (1)] = 255;
					pixels[(pixelIndex - 0) + (2)] = 255;
					pixelHistory.setNewR(255);
					pixelHistory.setNewG(255);
					pixelHistory.setNewB(255);
					if(!change.getAllPixelHistory().contains(pixelHistory.x) && !change.getAllPixelHistory().contains(pixelHistory.y)) {
						change.getAllPixelHistory().add(pixelHistory);
					}
					pixels = antiAlias2(pixels, (int)y, width, nbands, (int)x);
		}
		inspiram.inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(inspiram.layers[inspiram.currentLayer].getLayerImage(), 1, 1);
		ti.setData(writableRaster);
		return ti;
	}
	
	public int[] antiAlias(int[] pixels, int y, int width, int nbands, int x) {
		int pixelIndex = (int)y * width * nbands + (int)x * nbands;
		int east3 = (int)y * width * nbands + ((int)(x+3)) * nbands;
		int west3 = (int)y * width * nbands + ((int)(x-3)) * nbands;
		int north3 = ((int)(y-3)) * width * nbands + ((int)(x+0)) * nbands;
		int south3 = ((int)(y+3)) * width * nbands + ((int)(x+0)) * nbands;
		int east1 = (int)y * width * nbands + ((int)(x+1)) * nbands;
		int west1 = (int)y * width * nbands + ((int)(x-1)) * nbands;
		int north1 = ((int)(y-1)) * width * nbands + ((int)(x+0)) * nbands;
		int south1 = ((int)(y+1)) * width * nbands + ((int)(x+0)) * nbands;
		int east2 = (int)y * width * nbands + ((int)(x+2)) * nbands;
		int west2 = (int)y * width * nbands + ((int)(x-2)) * nbands;
		int north2 = ((int)(y-2)) * width * nbands + ((int)(x+0)) * nbands;
		int south2 = ((int)(y+2)) * width * nbands + ((int)(x+0)) * nbands;
		int currentR = pixels[pixelIndex+0];
		int currentG = pixels[pixelIndex+1];
		int currentB = pixels[pixelIndex+2];
		
		//East//
		int east3R = pixels[(east3)+0];
		int east3G = pixels[(east3)+1];
		int east3B = pixels[(east3)+2];
		int differenceR = currentR-east3R;
		int differenceG = currentG-east3G;
		int differenceB = currentB-east3B;
		pixels[(east1)+0] = ((differenceR/3)*2)+east3R;
		pixels[(east1)+1] = ((differenceG/3)*2)+east3G;
		pixels[(east1)+2] = ((differenceB/3)*2)+east3B;
		pixels[(east2)+0] = ((differenceR/3))+east3R;
		pixels[(east2)+1] = ((differenceG/3))+east3G;
		pixels[(east2)+2] = ((differenceB/3))+east3B;
		
		
		
		//West/
				int west3R = pixels[(west3)+0];
				int west3G = pixels[(west3)+1];
				int west3B = pixels[(west3)+2];
				differenceR = currentR-west3R;
				differenceG = currentG-west3G;
				differenceB = currentB-west3B;
				pixels[(west1)+0] = ((differenceR/3)*2)+west3R;
				pixels[(west1)+1] = ((differenceG/3)*2)+west3G;
				pixels[(west1)+2] = ((differenceB/3)*2)+west3B;
				pixels[(west2)+0] = ((differenceR/3))+west3R;
				pixels[(west2)+1] = ((differenceG/3))+west3G;
				pixels[(west2)+2] = ((differenceB/3))+west3B;
				
				//South//
				int south3R = pixels[(south3)+0];
				int south3G = pixels[(south3)+1];
				int south3B = pixels[(south3)+2];
				differenceR = currentR-south3R;
				differenceG = currentG-south3G;
				differenceB = currentB-south3B;
				pixels[(south1)+0] = ((differenceR/3)*2)+south3R;
				pixels[(south1)+1] = ((differenceG/3)*2)+south3G;
				pixels[(south1)+2] = ((differenceB/3)*2)+south3B;
				pixels[(south2)+0] = ((differenceR/3))+south3R;
				pixels[(south2)+1] = ((differenceG/3))+south3G;
				pixels[(south2)+2] = ((differenceB/3))+south3B;
				
				//North//
				int north3R = pixels[(north3)+0];
				int north3G = pixels[(north3)+1];
				int north3B = pixels[(north3)+2];
				differenceR = currentR-north3R;
				differenceG = currentG-north3G;
				differenceB = currentB-north3B;
				pixels[(north1)+0] = ((differenceR/3)*2)+north3R;
				pixels[(north1)+1] = ((differenceG/3)*2)+north3G;
				pixels[(north1)+2] = ((differenceB/3)*2)+north3B;
				pixels[(north2)+0] = ((differenceR/3))+north3R;
				pixels[(north2)+1] = ((differenceG/3))+north3G;
				pixels[(north2)+2] = ((differenceB/3))+north3B;
		
		
		return pixels;
	}
	
	public int[] antiAlias2(int[] pixels, int y, int width, int nbands, int x) {
		int pixelIndex = (int)y * width * nbands + (int)x * nbands;
		int east2 = (int)y * width * nbands + ((int)(x+2)) * nbands;
		int west2 = (int)y * width * nbands + ((int)(x-2)) * nbands;
		int north2 = ((int)(y-2)) * width * nbands + ((int)(x+0)) * nbands;
		int south2 = ((int)(y+2)) * width * nbands + ((int)(x+0)) * nbands;
		int east1 = (int)y * width * nbands + ((int)(x+1)) * nbands;
		int west1 = (int)y * width * nbands + ((int)(x-1)) * nbands;
		int north1 = ((int)(y-1)) * width * nbands + ((int)(x+0)) * nbands;
		int south1 = ((int)(y+1)) * width * nbands + ((int)(x+0)) * nbands;
		int currentR = pixels[pixelIndex+0];
		int currentG = pixels[pixelIndex+1];
		int currentB = pixels[pixelIndex+2];
		
		//East//
		int east2R = pixels[(east2)+0];
		int east2G = pixels[(east2)+1];
		int east2B = pixels[(east2)+2];
		int differenceR = currentR-east2R;
		int differenceG = currentG-east2G;
		int differenceB = currentB-east2B;
		pixels[(east1)+0] = ((differenceR/3))+east2R;
		pixels[(east1)+1] = ((differenceG/3))+east2G;
		pixels[(east1)+2] = ((differenceB/3))+east2B;
		
		
		
		//West/
				int west2R = pixels[(west2)+0];
				int west2G = pixels[(west2)+1];
				int west2B = pixels[(west2)+2];
				differenceR = currentR-west2R;
				differenceG = currentG-west2G;
				differenceB = currentB-west2B;
				pixels[(west1)+0] = ((differenceR/3))+west2R;
				pixels[(west1)+1] = ((differenceG/3))+west2G;
				pixels[(west1)+2] = ((differenceB/3))+west2B;
				
				//South//
				int south2R = pixels[(south2)+0];
				int south2G = pixels[(south2)+1];
				int south2B = pixels[(south2)+2];
				differenceR = currentR-south2R;
				differenceG = currentG-south2G;
				differenceB = currentB-south2B;
				pixels[(south1)+0] = ((differenceR/3))+south2R;
				pixels[(south1)+1] = ((differenceG/3))+south2G;
				pixels[(south1)+2] = ((differenceB/3))+south2B;
				
				//North//
				int north2R = pixels[(north2)+0];
				int north2G = pixels[(north2)+1];
				int north2B = pixels[(north2)+2];
				differenceR = currentR-north2R;
				differenceG = currentG-north2G;
				differenceB = currentB-north2B;
				pixels[(north1)+0] = ((differenceR/3))+north2R;
				pixels[(north1)+1] = ((differenceG/3))+north2G;
				pixels[(north1)+2] = ((differenceB/3))+north2B;
		
		
		return pixels;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
}
