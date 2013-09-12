package inspiram;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Random;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JApplet;

import com.sun.media.jai.widget.DisplayJAI;

public class BezierCurveDemo {
	
	JApplet currentApplet = null;
	int lineClicks = 0;
	int p0X = 0;
	int p0Y = 0;
	int p2X = 0;
	int p2Y = 0;
	int p1X = 0;
	int p1Y = 0;

	public BezierCurveDemo(JApplet applet) {
		currentApplet = applet;
	}

	/**
	 * Performs a demo of a linear bezier curve. This curve is used to set up higher curves such as the 
	 * quadratic bezier curve or the higher order curve.
	 * @param loadedImage
	 * @return
	 */
	public TiledImage performLinearCurve(PlanarImage loadedImage) {
		TiledImage ti = null;
		return ti;
	}
	
	public void performQuadraticCurve(final Inspiram inspiram) {
		
		inspiram.layers[inspiram.currentLayer].setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		inspiram.layers[inspiram.currentLayer].addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(lineClicks == 0) {
					System.out.println("100 / -100: " + 100/(-100));
					p0X = inspiram.layers[inspiram.currentLayer].getMousePosition().x;
					p0Y = inspiram.layers[inspiram.currentLayer].getMousePosition().y;
					lineClicks++;
					System.out.println("Line start click! - (" + p0X + ", " + p0Y + ")");
				}
				else if(lineClicks == 1) {
					p1X = inspiram.layers[inspiram.currentLayer].getMousePosition().x;
					p1Y = inspiram.layers[inspiram.currentLayer].getMousePosition().y;
					lineClicks++;
					System.out.println("Line Center click! - (" + p0X + ", " + p0Y + ")");
				}
				else {
					p2X = inspiram.layers[inspiram.currentLayer].getMousePosition().x;
					p2Y = inspiram.layers[inspiram.currentLayer].getMousePosition().y;
					lineClicks = 0;
					inspiram.layers[inspiram.currentLayer].setCursor(Cursor.getDefaultCursor());
					for(MouseListener ml : inspiram.layers[inspiram.currentLayer].getMouseListeners()) {
						inspiram.layers[inspiram.currentLayer].removeMouseListener(ml);
					}
					double yDifference1st = p1Y-p0Y;
					double xDifference1st = p1X-p0X;
					double slope1st = (((yDifference1st)/(xDifference1st)));
					double yDifference2st = p2Y-p1Y;
					double xDifference2st = p2X-p1X;
					double slope2nd = (((yDifference2st)/(xDifference2st)));
					System.out.println("Line end click! - (" + p2X + ", " + p2Y + ")");
					inspiram.displayTiledImage(makeQuadCurve(p0X, p0Y, p2X, p2Y, slope1st, slope2nd, inspiram));
					System.gc();
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
	
	public double findYIntercept(double x, double y, double slope) {
		double yIntercept = 0;
		double slopeAndX = slope*x;
		double b = y-slopeAndX;
		yIntercept = b;
		return yIntercept;
	}
		
	public TiledImage makeQuadCurve(int lineBeginningX, int lineBeginningY, int lineEndingX, int lineEndingY, double slope1st, double slope2nd, Inspiram inspiram) {
		int width = inspiram.layers[inspiram.currentLayer].getWidth();
		int height = inspiram.layers[inspiram.currentLayer].getHeight();
		SampleModel mySampleModel = inspiram.layers[inspiram.currentLayer].getLayerImage().getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = inspiram.layers[inspiram.currentLayer].getLayerImage().getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands * width * height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		double yIntercept1st = findYIntercept(p1X, p1Y, slope1st);
		double yIntercept2nd = findYIntercept(p2X, p2Y, slope2nd);
		System.out.println("Y-Intercept of 1st: " + findYIntercept(p0X, p0Y, slope1st) + "     Y-Intercept of 2nd: " + findYIntercept(p1X, p1Y, slope1st));
		int pixelIndexQ = 0;
		
		double x1first = p0X;
		double x2first = p1X;
		double y1first = p0Y;
		double y2first = p1Y;
		double x1second = p1X;
		double x2second = p2X;
		double y1second = p1Y;
		double y2second = p2Y;
		if(p0X >= p1X) {
			x1first = p1X;
			x2first = p0X;
			y1first = p1Y;
			y2first = p0Y;
		}
		if(p1X >= p2X) {
			x1second = p2X;
			x2second = p1X;
			y1second = p2Y;
			y2second = p1Y;
		}
		
		double repeatingXs1st = Math.abs(slope1st);
		double yPlus1st = 0;
		double repeatingXs2nd = Math.abs(slope2nd);
		double yPlus2nd = 0;
		
		Change change = new Change("QUAD Bezier Creation", inspiram.currentLayer);
		change.undoChange.addActionListener(change.createChangeUndoListener(inspiram));
		System.out.println("SLOPE: " + slope1st);
		for (double dub = .01; dub < 1.0; dub+=.001) {
			
			//P's//
			double x1st = (x1first+((x2first-x1first)*dub)); //X coordinate for 1st line
			double x2nd = (x1second+((x2second-x1second)*dub)); //X coordinate for 2nd line
			double y1st = (slope1st * (x1first+((x2first-x1first)*dub)) + yIntercept1st); //Y coordinate for 1st line
			double y2nd = (slope2nd * (x1second+((x2second-x1second)*dub)) + yIntercept2nd); //Y coordinate for 2nd line.
			//End of P's//
			
			//Q's//
			double yDifferenceQ = y2nd-y1st;
			double xDifferenceQ = x2nd-x1st;
			double slopeQ = (((yDifferenceQ)/(xDifferenceQ)));
			double yInterceptQ = findYIntercept(x2nd, y2nd, slopeQ);
			double x1Q = x1st;
			double x2Q = x2nd;
			double y1Q = y1st;
			double y2Q = y2nd;
			if(x1st >= x2nd) {
				x1Q = x2nd;
				x2Q = x1st;
				y1Q = y2nd;
				y2Q = y1st;
			}
			double repeatingXsQ = Math.abs(slopeQ);
			double yPlusQ = 0;
			
			double xQ = (x1Q+((x2Q-x1Q)*dub)); //X coordinate for q line
			double yQ = (slopeQ * (x1Q+((x2Q-x1Q)*dub)) + yInterceptQ); //Y coordinate for q line
			//End of Q's//
			
			
				pixelIndexQ = (int)yQ * width * nbands + (int)xQ * nbands;
				
				PixelHistory pixelHistory = new PixelHistory((int)xQ, (int)yQ);
				if(!change.getAllPixelHistory().contains(pixelHistory.x) && !change.getAllPixelHistory().contains(pixelHistory.y)) {
					pixelHistory.setPrevR(pixels[(pixelIndexQ - 0) + (0)]);
					pixelHistory.setPrevG(pixels[(pixelIndexQ - 0) + (1)]);
					pixelHistory.setPrevB(pixels[(pixelIndexQ - 0) + (2)]);
					pixels[(pixelIndexQ - 0) + (0)] = 255;
					pixels[(pixelIndexQ - 0) + (1)] = 255;
					pixels[(pixelIndexQ - 0) + (2)] = 255;
					pixelHistory.setNewR(255);
					pixelHistory.setNewG(255);
					pixelHistory.setNewB(255);
					if(!change.getAllPixelHistory().contains(pixelHistory.x) && !change.getAllPixelHistory().contains(pixelHistory.y)) {
						change.getAllPixelHistory().add(pixelHistory);
					}
				}
					
		}
		inspiram.inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(inspiram.layers[inspiram.currentLayer].getLayerImage(), 1, 1);
		ti.setData(writableRaster);
		return ti;
	}
	
	public void performHighOrderCurve() {
		System.out.println("Performing the high order curve!");
	}
}
