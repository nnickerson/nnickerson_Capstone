package inspiram;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
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
		
		Graphics g = inspiram.layers[inspiram.currentLayer].getGraphics();
		g.setColor(Color.WHITE);
		g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		
		double repeatingXs = Math.abs(slope);
		double yPlus = 0;
		
		Change change = new Change("Line Creation", inspiram.currentLayer);
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
				pixelHistory.setPrevR(pixels[(pixelIndex - 0) + (0)]);
				pixelHistory.setPrevG(pixels[(pixelIndex - 0) + (1)]);
				pixelHistory.setPrevB(pixels[(pixelIndex - 0) + (2)]);
				if((0 <= (x%1) && (x%1) < .01 && slope < 0) || (0 <= (x%1) && (x%1) < .01 && slope > 0)) {
					pixels = antiAlias(pixels, (int)y, width, nbands, (int)x, change, inspiram.currentLayer);
//					System.out.println("I reached ANTI-ALIAS1 MUhahahahHAHA");
				}
				//use to set previous values here
					pixels[(pixelIndex - 0) + (0)] = 255;
					pixels[(pixelIndex - 0) + (1)] = 255;
					pixels[(pixelIndex - 0) + (2)] = 255;
					pixelHistory.setNewR(255);
					pixelHistory.setNewG(255);
					pixelHistory.setNewB(255);
					if(!change.getAllPixelHistory().contains(pixelHistory.x) && !change.getAllPixelHistory().contains(pixelHistory.y)) {
						change.getAllPixelHistory().add(pixelHistory);
					}
					if((0 <= (x%1) && (x%1) < .01 && slope < 0) || (0 <= (x%1) && (x%1) < .01 && slope > 0)) {
						pixels = antiAlias2(pixels, (int)y, width, nbands, (int)x, change, inspiram.currentLayer);
						System.out.println("I reached ANTI-ALIAS2 MUhahahahHAHA");
					}
		}
		inspiram.inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(inspiram.layers[inspiram.currentLayer].getLayerImage(), 1, 1);
		ti.setData(writableRaster);
		return ti;
	}
	
	public int[] antiAlias(int[] pixels, int y, int width, int nbands, int x, Change change, int currentLayer) {
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
		PixelHistory east1History = new PixelHistory((int)x+1, (int)y);
		PixelHistory east2History = new PixelHistory((int)x+2, (int)y);
		int east3R = pixels[(east3)+0];
		int east3G = pixels[(east3)+1];
		int east3B = pixels[(east3)+2];
		int differenceR = currentR-east3R;
		int differenceG = currentG-east3G;
		int differenceB = currentB-east3B;
		east1History.setPrevR(currentR);
		east1History.setPrevG(currentG);
		east1History.setPrevB(currentB);
		int newRed = ((differenceR/3)*2)+east3R;
		int newGreen = ((differenceG/3)*2)+east3G;
		int newBlue = ((differenceB/3)*2)+east3B;
		pixels[(east1)+0] = newRed;
		pixels[(east1)+1] = newGreen;
		pixels[(east1)+2] = newBlue;
		east1History.setNewR(newRed);
		east1History.setNewG(newGreen);
		east1History.setNewB(newBlue);
		east2History.setPrevR(currentR);
		east2History.setPrevG(currentG);
		east2History.setPrevB(currentB);
		int newRed2 = ((differenceR/3))+east3R;
		int newGreen2 = ((differenceG/3))+east3G;
		int newBlue2 = ((differenceB/3))+east3B;
		pixels[(east2)+0] = newRed2;
		pixels[(east2)+1] = newGreen2;
		pixels[(east2)+2] = newBlue2;
		east2History.setNewR(newRed2);
		east2History.setNewG(newGreen2);
		east2History.setNewB(newBlue2);
		
		
		
		//West/
		PixelHistory west1History = new PixelHistory((int)x-1, (int)y);
		PixelHistory west2History = new PixelHistory((int)x-2, (int)y);
				int west3R = pixels[(west3)+0];
				int west3G = pixels[(west3)+1];
				int west3B = pixels[(west3)+2];
				differenceR = currentR-west3R;
				differenceG = currentG-west3G;
				differenceB = currentB-west3B;
				west1History.setPrevR(currentR);
				west1History.setPrevG(currentG);
				west1History.setPrevB(currentB);
				pixels[(west1)+0] = ((differenceR/3)*2)+west3R;
				pixels[(west1)+1] = ((differenceG/3)*2)+west3G;
				pixels[(west1)+2] = ((differenceB/3)*2)+west3B;
				west1History.setNewR(pixels[(west1)+0]);
				west1History.setNewG(pixels[(west1)+1]);
				west1History.setNewB(pixels[(west1)+2]);
				west2History.setPrevR(currentR);
				west2History.setPrevG(currentG);
				west2History.setPrevB(currentB);
				pixels[(west2)+0] = ((differenceR/3))+west3R;
				pixels[(west2)+1] = ((differenceG/3))+west3G;
				pixels[(west2)+2] = ((differenceB/3))+west3B;
				west2History.setNewR(pixels[(west2)+0]);
				west2History.setNewG(pixels[(west2)+1]);
				west2History.setNewB(pixels[(west2)+2]);
				
				//South//
				PixelHistory south1History = new PixelHistory((int)x, (int)y+1);
				PixelHistory south2History = new PixelHistory((int)x, (int)y+2);
				int south3R = pixels[(south3)+0];
				int south3G = pixels[(south3)+1];
				int south3B = pixels[(south3)+2];
				differenceR = currentR-south3R;
				differenceG = currentG-south3G;
				differenceB = currentB-south3B;
				south1History.setPrevR(currentR);
				south1History.setPrevG(currentG);
				south1History.setPrevB(currentB);
				pixels[(south1)+0] = ((differenceR/3)*2)+south3R;
				pixels[(south1)+1] = ((differenceG/3)*2)+south3G;
				pixels[(south1)+2] = ((differenceB/3)*2)+south3B;
				south1History.setNewR(pixels[(south1)+0]);
				south1History.setNewG(pixels[(south1)+1]);
				south1History.setNewB(pixels[(south1)+2]);
				south2History.setPrevR(currentR);
				south2History.setPrevG(currentG);
				south2History.setPrevB(currentB);
				pixels[(south2)+0] = ((differenceR/3))+south3R;
				pixels[(south2)+1] = ((differenceG/3))+south3G;
				pixels[(south2)+2] = ((differenceB/3))+south3B;
				south2History.setNewR(pixels[(south2)+0]);
				south2History.setNewG(pixels[(south2)+1]);
				south2History.setNewB(pixels[(south2)+2]);
				
				//North//
				PixelHistory north1History = new PixelHistory((int)x, (int)y-1);
				PixelHistory north2History = new PixelHistory((int)x, (int)y-2);
				int north3R = pixels[(north3)+0];
				int north3G = pixels[(north3)+1];
				int north3B = pixels[(north3)+2];
				differenceR = currentR-north3R;
				differenceG = currentG-north3G;
				differenceB = currentB-north3B;
				north1History.setPrevR(currentR);
				north1History.setPrevG(currentG);
				north1History.setPrevB(currentB);
				pixels[(north1)+0] = ((differenceR/3)*2)+north3R;
				pixels[(north1)+1] = ((differenceG/3)*2)+north3G;
				pixels[(north1)+2] = ((differenceB/3)*2)+north3B;
				north1History.setNewR(pixels[(north1)+0]);
				north1History.setNewG(pixels[(north1)+1]);
				north1History.setNewB(pixels[(north1)+2]);
				north2History.setPrevR(currentR);
				north2History.setPrevG(currentG);
				north2History.setPrevB(currentB);
				pixels[(north2)+0] = ((differenceR/3))+north3R;
				pixels[(north2)+1] = ((differenceG/3))+north3G;
				pixels[(north2)+2] = ((differenceB/3))+north3B;
				north2History.setNewR(pixels[(north2)+0]);
				north2History.setNewG(pixels[(north2)+1]);
				north2History.setNewB(pixels[(north2)+2]);
		
				if(!change.getAllPixelHistory().contains(east1History.x) && !change.getAllPixelHistory().contains(east1History.y)) {
					change.getAllPixelHistory().add(east1History);
				}
				if(!change.getAllPixelHistory().contains(east2History.x) && !change.getAllPixelHistory().contains(east2History.y)) {
					change.getAllPixelHistory().add(east2History);
				}
				if(!change.getAllPixelHistory().contains(west1History.x) && !change.getAllPixelHistory().contains(west1History.y)) {
					change.getAllPixelHistory().add(west1History);
				}
				if(!change.getAllPixelHistory().contains(west2History.x) && !change.getAllPixelHistory().contains(west2History.y)) {
					change.getAllPixelHistory().add(west2History);
				}
				if(!change.getAllPixelHistory().contains(north1History.x) && !change.getAllPixelHistory().contains(north1History.y)) {
					change.getAllPixelHistory().add(north1History);
				}
				if(!change.getAllPixelHistory().contains(north2History.x) && !change.getAllPixelHistory().contains(north2History.y)) {
					change.getAllPixelHistory().add(north2History);
				}
				if(!change.getAllPixelHistory().contains(south1History.x) && !change.getAllPixelHistory().contains(south1History.y)) {
					change.getAllPixelHistory().add(south1History);
				}
				if(!change.getAllPixelHistory().contains(south2History.x) && !change.getAllPixelHistory().contains(south2History.y)) {
					change.getAllPixelHistory().add(south2History);
				}
		return pixels;
	}
	
	public int[] antiAlias2(int[] pixels, int y, int width, int nbands, int x, Change change, int currentLayer) {
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
		PixelHistory east1History = new PixelHistory((int)x+1, y);
		int east2R = pixels[(east2)+0];
		int east2G = pixels[(east2)+1];
		int east2B = pixels[(east2)+2];
		int differenceR = currentR-east2R;
		int differenceG = currentG-east2G;
		int differenceB = currentB-east2B;
		east1History.setPrevR(currentR); //east1History.setPrevR(pixels[(east1)+0]);
		east1History.setPrevG(currentG); //east1History.setPrevG(pixels[(east1)+1]);
		east1History.setPrevB(currentB); //east1History.setPrevB(pixels[(east1)+2]);
		pixels[(east1)+0] = ((differenceR/3))+east2R;
		pixels[(east1)+1] = ((differenceG/3))+east2G;
		pixels[(east1)+2] = ((differenceB/3))+east2B;
		east1History.setNewR(pixels[(east1)+0]);
		east1History.setNewG(pixels[(east1)+1]);
		east1History.setNewB(pixels[(east1)+2]);
		
		
		
		//West/
		PixelHistory west1History = new PixelHistory((int)x-1, y);
				int west2R = pixels[(west2)+0];
				int west2G = pixels[(west2)+1];
				int west2B = pixels[(west2)+2];
				differenceR = currentR-west2R;
				differenceG = currentG-west2G;
				differenceB = currentB-west2B;
				west1History.setPrevR(currentR);
				west1History.setPrevG(currentG);
				west1History.setPrevB(currentB);
				pixels[(west1)+0] = ((differenceR/3))+west2R;
				pixels[(west1)+1] = ((differenceG/3))+west2G;
				pixels[(west1)+2] = ((differenceB/3))+west2B;
				west1History.setNewR(pixels[(west1)+0]);
				west1History.setNewG(pixels[(west1)+1]);
				west1History.setNewB(pixels[(west1)+2]);
				
				//South//
				PixelHistory south1History = new PixelHistory((int)x, y+1);
				int south2R = pixels[(south2)+0];
				int south2G = pixels[(south2)+1];
				int south2B = pixels[(south2)+2];
				differenceR = currentR-south2R;
				differenceG = currentG-south2G;
				differenceB = currentB-south2B;
				south1History.setPrevR(currentR);
				south1History.setPrevG(currentG);
				south1History.setPrevB(currentB);
				pixels[(south1)+0] = ((differenceR/3))+south2R;
				pixels[(south1)+1] = ((differenceG/3))+south2G;
				pixels[(south1)+2] = ((differenceB/3))+south2B;
				south1History.setNewR(pixels[(south1)+0]);
				south1History.setNewG(pixels[(south1)+1]);
				south1History.setNewB(pixels[(south1)+2]);
				
				//North//
				PixelHistory north1History = new PixelHistory((int)x, y-1);
				int north2R = pixels[(north2)+0];
				int north2G = pixels[(north2)+1];
				int north2B = pixels[(north2)+2];
				differenceR = currentR-north2R;
				differenceG = currentG-north2G;
				differenceB = currentB-north2B;
				north1History.setPrevR(currentR);
				north1History.setPrevG(currentG);
				north1History.setPrevB(currentB);
				pixels[(north1)+0] = ((differenceR/3))+north2R;
				pixels[(north1)+1] = ((differenceG/3))+north2G;
				pixels[(north1)+2] = ((differenceB/3))+north2B;
				north1History.setNewR(pixels[(north1)+0]);
				north1History.setNewG(pixels[(north1)+1]);
				north1History.setNewB(pixels[(north1)+2]);
		
		
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
