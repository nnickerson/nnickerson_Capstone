package inspiram;

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
		System.out.println("Performing the linear curve!");
		
		int width = loadedImage.getWidth();
		int height = loadedImage.getHeight();
		SampleModel mySampleModel = loadedImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = loadedImage.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands*width*height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		int pixelIndex = 0;
		Random r = new Random();
		int randomXMin = r.nextInt(50)+10;
		int randomYMin = r.nextInt(50)+10;
		int randomXMax = r.nextInt(200)+150;
		int randomYMax = r.nextInt(200)+150;
		int slope = (randomYMax-randomYMin)/(randomXMax-randomXMin);
		int totalIncrements = 100;
		int bezierIncrements = ((randomXMax-randomXMin)/totalIncrements);
		int currentIncrement = 1;
		for(int y=randomYMin;y<randomYMax;y++) {
			for(int x=randomXMin;x<randomXMax;x++)
			{
				System.out.println("SLOPE: " + (randomYMin+(y+(slope*(x-randomXMin)))) + "    Y: " + y);
				if((randomYMin+(y+(slope*(x-randomXMin)))) == y) {
					pixelIndex = y*width*nbands+x*nbands;
					for(int band=0;band<nbands;band++) {
						if((bezierIncrements*x) == x) {
							System.out.println("Increment " + currentIncrement + " at: (" + x + ", " + y);
							currentIncrement++;
						}
						pixels[(pixelIndex-0)+(band)] = 255;
						pixels[(pixelIndex-1)+(band)] = 255;
						pixels[(pixelIndex-2)+(band)] = 255;
						pixels[(pixelIndex-3)+(band)] = 255;
						pixels[(pixelIndex-4)+(band)] = 255;
						pixels[(pixelIndex-5)+(band)] = 255;
						pixels[(pixelIndex-6)+(band)] = 255;
						pixels[(pixelIndex-7)+(band)] = 255;
						pixels[(pixelIndex-8)+(band)] = 255;
						pixels[(pixelIndex-9)+(band)] = 255;
						writableRaster.setPixels(0, 0, width, height, pixels);
						TiledImage ti = new TiledImage(loadedImage,1,1);
						ti.setData(writableRaster);
						loadedImage = ti.createSnapshot();
						DisplayJAI displayJAIimage = new DisplayJAI(loadedImage);
						currentApplet.getContentPane().add(displayJAIimage);
						
						currentApplet.getContentPane().repaint();
						
						currentApplet.setSize(currentApplet.getWidth()-1, currentApplet.getHeight()-1);
						currentApplet.setSize(currentApplet.getWidth()+1, currentApplet.getHeight()+1);
						currentApplet.getContentPane().repaint();
						currentApplet.repaint();
					}
				}
			}
		}
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(loadedImage,1,1);
		ti.setData(writableRaster);
		TiledImage myTiledImage = ti;
		return ti;
	}
	
	public void performQuadraticCurve() {
		System.out.println("Performing the quadratic curve!");
	}
	
	public void performHighOrderCurve() {
		System.out.println("Performing the high order curve!");
	}
}
