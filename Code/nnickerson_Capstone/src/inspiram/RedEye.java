package inspiram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RedEye {

	public RedEye() {
		
	}
	
	public TiledImage fixRedEyePixels(Inspiram inspiram, PlanarImage imageToFix) {
		int width = imageToFix.getWidth();
		int height = imageToFix.getHeight();
		SampleModel mySampleModel = imageToFix.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = imageToFix.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands*width*height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		int pixelIndex = 0;
		int r = 0, g = 0, b = 0;
		int y1 = 0;
		int x1 = 0;
		int xMax = width;
		int yMax = height;
		if(inspiram.redEyeCenterY-(inspiram.redEyeDiameter/2) >= 0) {
			y1 = inspiram.redEyeCenterY-(inspiram.redEyeDiameter/2);
		}
		if(inspiram.redEyeCenterX-(inspiram.redEyeDiameter/2) >= 0) {
			x1 = inspiram.redEyeCenterX-(inspiram.redEyeDiameter/2);
		}
		if(inspiram.redEyeCenterX+(inspiram.redEyeDiameter/2) <= width) {
			xMax = inspiram.redEyeCenterX+(inspiram.redEyeDiameter/2);
		}
		if(inspiram.redEyeCenterY+(inspiram.redEyeDiameter/2) <= height) {
			yMax = inspiram.redEyeCenterY+(inspiram.redEyeDiameter/2);
		}
		for(int y=y1;y<yMax;y++) {
			for(int x=x1;x<xMax;x++)
			{
				pixelIndex = y*width*nbands+x*nbands;
				r = pixels[pixelIndex+0];
				g = pixels[pixelIndex+1];
				b = pixels[pixelIndex+2];
				if(isRedEyeValues(r, g, b)) {
					pixels[pixelIndex+(0)] = 0;
					pixels[pixelIndex+(1)] = 10;
					pixels[pixelIndex+(2)] = 10;
				}
			}
		}
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(imageToFix,1,1);
		ti.setData(writableRaster);
		return ti;
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
	
	public void grabEyeLocation(final Inspiram inspiram) {
		inspiram.layers[inspiram.currentLayer].setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		MouseListener redEyeListener = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {
				inspiram.redEyeCenterX = inspiram.layers[inspiram.currentLayer].getMousePosition().x;
				inspiram.redEyeCenterY = inspiram.layers[inspiram.currentLayer].getMousePosition().y;
				System.out.println("Eye center: " + inspiram.redEyeCenterX + ", " + inspiram.redEyeCenterY);
				inspiram.layers[inspiram.currentLayer].setCursor(Cursor.getDefaultCursor());
				for(MouseListener ml : inspiram.layers[inspiram.currentLayer].getMouseListeners()) {
					inspiram.layers[inspiram.currentLayer].removeMouseListener(ml);
				}
				defineEyeSize(inspiram.redEyeCenterX, inspiram.redEyeCenterY, inspiram, inspiram.layers[inspiram.currentLayer].getLayerImage());
			}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}  
			};
			inspiram.layers[inspiram.currentLayer].addMouseListener(redEyeListener);
		System.out.println("created the mouse listener for red eye.");
	}
	
	public void defineEyeSize(int centerEyeX, int centerEyeY, final Inspiram inspiram, PlanarImage imageToFix) {
		inspiram.sliderFrame = new JFrame("Slide the slider to fit over the iris in a red eye.");
		inspiram.sliderFrame.setLayout(new BorderLayout());
		inspiram.radiusSlider = new JSlider(JSlider.HORIZONTAL);
		JButton fixRedEyeButton = new JButton("Fix it!");
		inspiram.sliderFrame.add(fixRedEyeButton, BorderLayout.PAGE_END);
		fixRedEyeButton.setVisible(true);
		fixRedEyeButton.repaint();
		inspiram.sliderFrame.repaint();
		inspiram.sliderFrame.add(inspiram.radiusSlider, BorderLayout.PAGE_START);
		inspiram.radiusSlider.setMinimum(2);
		inspiram.radiusSlider.setMaximum((int)((double)(inspiram.rHeight)*.9));
		if(imageToFix.getWidth() >= imageToFix.getHeight()) {
			inspiram.radiusSlider.setMaximum(imageToFix.getHeight()-1);
		}
		else {
			inspiram.radiusSlider.setMaximum(imageToFix.getWidth()-1);
		}
		inspiram.sliderFrame.setSize(400, 110);
		inspiram.sliderFrame.setVisible(true);
		inspiram.sliderFrame.repaint();
		inspiram.radiusSlider.setBorder(BorderFactory.createBevelBorder(2));
		inspiram.radiusSlider.setMajorTickSpacing(inspiram.rHeight/10);
		inspiram.radiusSlider.setMinorTickSpacing(inspiram.rHeight/50);
		inspiram.radiusSlider.setPaintLabels(true);
		inspiram.radiusSlider.setPaintTicks(true);
		inspiram.radiusSlider.setPaintTrack(true);
		inspiram.radiusSlider.setVisible(true);
		inspiram.radiusSlider.repaint();
		inspiram.redEyeCircle = inspiram.layers[inspiram.currentLayer].getGraphics();
		
		eyeLineup(inspiram);
		
		//Radius Listeners//
		inspiram.radiusSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				inspiram.layers[inspiram.currentLayer].paint(inspiram.previousGraphics);
				eyeLineup(inspiram);
			}
		});
		
		fixRedEyeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(MouseListener ml : inspiram.layersHolder.getMouseListeners()) {
					inspiram.layersHolder.removeMouseListener(ml);
				}
				fixRedEye(inspiram);
			} 
		});
		//End of radius Listeners//
	}
	
	public void eyeLineup(Inspiram inspiram) {
		inspiram.redEyeDiameter = inspiram.radiusSlider.getValue();
//		System.out.println("Red Eye Radius: " + redEyeDiameter);
//	    redEyeCircle.setStroke(new BasicStroke());
		int previousCenterX = inspiram.redEyeCenterX;
		int previousCenterY = inspiram.redEyeCenterY;
	    inspiram.redEyeCircle.drawOval(inspiram.redEyeCenterX-(inspiram.redEyeDiameter/2), inspiram.redEyeCenterY-(inspiram.redEyeDiameter/2), inspiram.redEyeDiameter, inspiram.redEyeDiameter);
//	    redEyeCircle.setPaint(Color.green);
	    inspiram.redEyeCenterX = previousCenterX;
	    inspiram.redEyeCenterY = previousCenterY;
	    inspiram.previousGraphics = inspiram.redEyeCircle;
	} 
	
	public void addRedEyeMenu(final Inspiram inspiram) {
		JMenuItem redEyeOption = new JMenuItem("Fix Red Eye");
	    inspiram.toolsMenu.add(redEyeOption);
	    inspiram.mainMenuBar.add(inspiram.toolsMenu);
	    inspiram.setJMenuBar(inspiram.mainMenuBar);
	    
	    //Listeners//
	    redEyeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				grabEyeLocation(inspiram);
				inspiram.repaint();
			}
		});
	    //End of listeners//
	}
	
	/**
	 * This method was used for manipulating pixels and further transformed into 
	 * trying to fix the red eye problem.
	 */
	public void fixRedEye(Inspiram inspiram) {
		inspiram.sliderFrame.dispose();
		TiledImage myTiledImage = fixRedEyePixels(inspiram, inspiram.layers[inspiram.currentLayer].getLayerImage());
		inspiram.layers[inspiram.currentLayer].setLayerImage(inspiram.tiledImageToPlanarImage(myTiledImage));
//		inspiram.layers[inspiram.currentLayer].set(inspiram.layers[inspiram.currentLayer].getLayerImage());
		inspiram.layers[inspiram.currentLayer].setPlainImage();
		inspiram.layers[inspiram.currentLayer].add(inspiram.layers[inspiram.currentLayer].getImageDisplay());
		inspiram.layers[inspiram.currentLayer].setLayerImage(null);
	}
	
	public float[] getHSB(int r, int b, int g) {
		float[] hsb = new float[3];
		hsb = Color.RGBtoHSB(r, g, b, hsb);
		return hsb;
	}
}
