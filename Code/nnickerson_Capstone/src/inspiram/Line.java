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
	
	public void getLineLocation(final Inspiram inspiram) {
		inspiram.layersHolder.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
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
		
		for (double x = x1; x <= x2; x+=.01) {
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
					pixels[(pixelIndex - 0) + (0)] = 255;
					pixels[(pixelIndex - 0) + (1)] = 255;
					pixels[(pixelIndex - 0) + (2)] = 255;
					pixelHistory.setNewR(255);
					pixelHistory.setNewG(255);
					pixelHistory.setNewB(255);
					if(!change.getAllPixelHistory().contains(pixelHistory.x) && !change.getAllPixelHistory().contains(pixelHistory.y)) {
						change.getAllPixelHistory().add(pixelHistory);
					}
		}
		inspiram.inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(inspiram.layers[inspiram.currentLayer].getLayerImage(), 1, 1);
		ti.setData(writableRaster);
		return ti;
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
