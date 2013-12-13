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
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RedEye {
	
	private int RED_BAND = 0;
	private int GREEN_BAND = 1;
	private int BLUE_BAND = 2;
	public int rValue = 0;
	public int gValue = 0;
	public int bValue = 0;
	JColorChooser colorChooser;

	public RedEye() {
		
	}
	
	public int[] antiAlias(int[] pixels, int y, int width, int nbands, int x, int redEyeDiameter, int circleX, int circleY, Change change) {
		int pixelIndex = (int)y * width * nbands + (int)x * nbands;
		int east3 = (int)y * width * nbands + ((int)(x+3)) * nbands;
		int west3 = (int)y * width * nbands + ((int)(x-3)) * nbands;
		int north3 = ((int)(y-3)) * width * nbands + ((int)(x+0)) * nbands;
		int south3 = ((int)(y+3)) * width * nbands + ((int)(x+0)) * nbands;
		int ne3 = ((int)(y-3)) * width * nbands + ((int)(x+3)) * nbands;
		int nw3 = ((int)(y-3)) * width * nbands + ((int)(x-3)) * nbands;
		int sw3 = ((int)(y+3)) * width * nbands + ((int)(x-3)) * nbands;
		int se3 = ((int)(y+3)) * width * nbands + ((int)(x+3)) * nbands;
		int ne2 = ((int)(y-2)) * width * nbands + ((int)(x+2)) * nbands;
		int nw2 = ((int)(y-2)) * width * nbands + ((int)(x-2)) * nbands;
		int sw2 = ((int)(y+2)) * width * nbands + ((int)(x-2)) * nbands;
		int se2 = ((int)(y+2)) * width * nbands + ((int)(x+2)) * nbands;
		int ne1 = ((int)(y-1)) * width * nbands + ((int)(x+1)) * nbands;
		int nw1 = ((int)(y-1)) * width * nbands + ((int)(x-1)) * nbands;
		int sw1 = ((int)(y+1)) * width * nbands + ((int)(x-1)) * nbands;
		int se1 = ((int)(y+1)) * width * nbands + ((int)(x+1)) * nbands;
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
		
		int currentRE1 = pixels[east1+0];
		int currentGE1 = pixels[east1+1];
		int currentBE1 = pixels[east1+2];
		int currentRE2 = pixels[east2+0];
		int currentGE2 = pixels[east2+1];
		int currentBE2 = pixels[east2+2];
		
		int currentRW1 = pixels[west1+0];
		int currentGW1 = pixels[west1+1];
		int currentBW1 = pixels[west1+2];
		int currentRW2 = pixels[west2+0];
		int currentGW2 = pixels[west2+1];
		int currentBW2 = pixels[west2+2];
		
		int currentRS1 = pixels[south1+0];
		int currentGS1 = pixels[south1+1];
		int currentBS1 = pixels[south1+2];
		int currentRS2 = pixels[south2+0];
		int currentGS2 = pixels[south2+1];
		int currentBS2 = pixels[south2+2];
		
		int currentRN1 = pixels[north1+0];
		int currentGN1 = pixels[north1+1];
		int currentBN1 = pixels[north1+2];
		int currentRN2 = pixels[north2+0];
		int currentGN2 = pixels[north2+1];
		int currentBN2 = pixels[north2+2];
		
		int currentRSE1 = pixels[se1+0];
		int currentGSE1 = pixels[se1+1];
		int currentBSE1 = pixels[se1+2];
		int currentRSE2 = pixels[se2+0];
		int currentGSE2 = pixels[se2+1];
		int currentBSE2 = pixels[se2+2];
		
		int currentRNE1 = pixels[ne1+0];
		int currentGNE1 = pixels[ne1+1];
		int currentBNE1 = pixels[ne1+2];
		int currentRNE2 = pixels[ne2+0];
		int currentGNE2 = pixels[ne2+1];
		int currentBNE2 = pixels[ne2+2];
		
		int currentRSW1 = pixels[sw1+0];
		int currentGSW1 = pixels[sw1+1];
		int currentBSW1 = pixels[sw1+2];
		int currentRSW2 = pixels[sw2+0];
		int currentGSW2 = pixels[sw2+1];
		int currentBSW2 = pixels[sw2+2];
		
		int currentRNW1 = pixels[nw1+0];
		int currentGNW1 = pixels[nw1+1];
		int currentBNW1 = pixels[nw1+2];
		int currentRNW2 = pixels[nw2+0];
		int currentGNW2 = pixels[nw2+1];
		int currentBNW2 = pixels[nw2+2];
		
		PixelHistory east1History = new PixelHistory((int)x+1, y);
		PixelHistory east2History = new PixelHistory((int)x+2, y);
		PixelHistory west1History = new PixelHistory((int)x-1, y);
		PixelHistory west2History = new PixelHistory((int)x-2, y);
		PixelHistory north1History = new PixelHistory((int)x, (int)y-1);
		PixelHistory north2History = new PixelHistory((int)x, (int)y-2);
		PixelHistory south1History = new PixelHistory((int)x, (int)y+1);
		PixelHistory south2History = new PixelHistory((int)x, (int)y+2);
		
		PixelHistory se1History = new PixelHistory((int)x+1, (int)y+1);
		PixelHistory se2History = new PixelHistory((int)x+2, (int)y+2);
		PixelHistory ne1History = new PixelHistory((int)x+1, (int)y-1);
		PixelHistory ne2History = new PixelHistory((int)x+2, (int)y-2);
		PixelHistory sw1History = new PixelHistory((int)x-1, (int)y+1);
		PixelHistory sw2History = new PixelHistory((int)x-2, (int)y+2);
		PixelHistory nw1History = new PixelHistory((int)x-1, (int)y-1);
		PixelHistory nw2History = new PixelHistory((int)x-2, (int)y-2);
		
		//East//
		int east3R = pixels[(east3)+0];
		int east3G = pixels[(east3)+1];
		int east3B = pixels[(east3)+2];
		east1History.setPrevR(currentRE1);
		east1History.setPrevG(currentGE1);
		east1History.setPrevB(currentBE1);
		east2History.setPrevR(currentRE2);
		east2History.setPrevG(currentGE2);
		east2History.setPrevB(currentBE2);
		int differenceR = currentR-east3R;
		int differenceG = currentG-east3G;
		int differenceB = currentB-east3B;
		if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x+1, y)) {
			pixels[(east1)+0] = ((differenceR/3)*2)+east3R;
			pixels[(east1)+1] = ((differenceG/3)*2)+east3G;
			pixels[(east1)+2] = ((differenceB/3)*2)+east3B;
		}
		if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x+2, y)) {
			pixels[(east2)+0] = ((differenceR/3))+east3R;
			pixels[(east2)+1] = ((differenceG/3))+east3G;
			pixels[(east2)+2] = ((differenceB/3))+east3B;
		}
		east1History.setNewR(pixels[(east1)+0]);
		east1History.setNewG(pixels[(east1)+1]);
		east1History.setNewB(pixels[(east1)+2]);
		east2History.setNewR(pixels[(east2)+0]);
		east2History.setNewG(pixels[(east2)+1]);
		east2History.setNewB(pixels[(east2)+2]);
		
		//SouthEast//
		se1History.setPrevR(currentRSE1);
		se1History.setPrevG(currentGSE1);
		se1History.setPrevB(currentBSE1);
		se2History.setPrevR(currentRSE2);
		se2History.setPrevG(currentGSE2);
		se2History.setPrevB(currentBSE2);
				int se3R = pixels[(se3)+0];
				int se3G = pixels[(se3)+1];
				int se3B = pixels[(se3)+2];
				differenceR = currentR-se3R;
				differenceG = currentG-se3G;
				differenceB = currentB-se3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x+1, y+1)) {
					pixels[(se1)+0] = ((differenceR/3)*2)+se3R;
					pixels[(se1)+1] = ((differenceG/3)*2)+se3G;
					pixels[(se1)+2] = ((differenceB/3)*2)+se3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x+2, y+2)) {
					pixels[(se2)+0] = ((differenceR/3))+se3R;
					pixels[(se2)+1] = ((differenceG/3))+se3G;
					pixels[(se2)+2] = ((differenceB/3))+se3B;
				}
				se1History.setNewR(pixels[(se1)+0]);
				se1History.setNewG(pixels[(se1)+1]);
				se1History.setNewB(pixels[(se1)+2]);
				se2History.setNewR(pixels[(se2)+0]);
				se2History.setNewG(pixels[(se2)+1]);
				se2History.setNewB(pixels[(se2)+2]);
				
				
				//NorthEast//
				ne1History.setPrevR(currentRNE1);
				ne1History.setPrevG(currentGNE1);
				ne1History.setPrevB(currentBNE1);
				ne2History.setPrevR(currentRNE2);
				ne2History.setPrevG(currentGNE2);
				ne2History.setPrevB(currentBNE2);
				int ne3R = pixels[(ne3)+0];
				int ne3G = pixels[(ne3)+1];
				int ne3B = pixels[(ne3)+2];
				differenceR = currentR-ne3R;
				differenceG = currentG-ne3G;
				differenceB = currentB-ne3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x+1, y-1)) {
					pixels[(ne1)+0] = ((differenceR/3)*2)+ne3R;
					pixels[(ne1)+1] = ((differenceG/3)*2)+ne3G;
					pixels[(ne1)+2] = ((differenceB/3)*2)+ne3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x+2, y-2)) {
					pixels[(ne2)+0] = ((differenceR/3))+ne3R;
					pixels[(ne2)+1] = ((differenceG/3))+ne3G;
					pixels[(ne2)+2] = ((differenceB/3))+ne3B;
				}
				ne1History.setNewR(pixels[(ne1)+0]);
				ne1History.setNewG(pixels[(ne1)+1]);
				ne1History.setNewB(pixels[(ne1)+2]);
				ne2History.setNewR(pixels[(ne2)+0]);
				ne2History.setNewG(pixels[(ne2)+1]);
				ne2History.setNewB(pixels[(ne2)+2]);
				
				//SouthWest//
				sw1History.setPrevR(currentRSW1);
				sw1History.setPrevG(currentGSW1);
				sw1History.setPrevB(currentBSW1);
				sw2History.setPrevR(currentRSW2);
				sw2History.setPrevG(currentGSW2);
				sw2History.setPrevB(currentBSW2);
				int sw3R = pixels[(sw3)+0];
				int sw3G = pixels[(sw3)+1];
				int sw3B = pixels[(sw3)+2];
				differenceR = currentR-sw3R;
				differenceG = currentG-sw3G;
				differenceB = currentB-sw3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x-1, y+1)) {
					pixels[(sw1)+0] = ((differenceR/3)*2)+sw3R;
					pixels[(sw1)+1] = ((differenceG/3)*2)+sw3G;
					pixels[(sw1)+2] = ((differenceB/3)*2)+sw3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x-2, y+2)) {
					pixels[(sw2)+0] = ((differenceR/3))+sw3R;
					pixels[(sw2)+1] = ((differenceG/3))+sw3G;
					pixels[(sw2)+2] = ((differenceB/3))+sw3B;
				}
				sw1History.setNewR(pixels[(sw1)+0]);
				sw1History.setNewG(pixels[(sw1)+1]);
				sw1History.setNewB(pixels[(sw1)+2]);
				sw2History.setNewR(pixels[(sw2)+0]);
				sw2History.setNewG(pixels[(sw2)+1]);
				sw2History.setNewB(pixels[(sw2)+2]);
				
				//NrthWest//
				nw1History.setPrevR(currentRNW1);
				nw1History.setPrevG(currentGNW1);
				nw1History.setPrevB(currentBNW1);
				nw2History.setPrevR(currentRNW2);
				nw2History.setPrevG(currentGNW2);
				nw2History.setPrevB(currentBNW2);
				int nw3R = pixels[(nw3)+0];
				int nw3G = pixels[(nw3)+1];
				int nw3B = pixels[(nw3)+2];
				differenceR = currentR-nw3R;
				differenceG = currentG-nw3G;
				differenceB = currentB-nw3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x-1, y-1)) {
					pixels[(nw1)+0] = ((differenceR/3)*2)+nw3R;
					pixels[(nw1)+1] = ((differenceG/3)*2)+nw3G;
					pixels[(nw1)+2] = ((differenceB/3)*2)+nw3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x-2, y-2)) {
					pixels[(nw2)+0] = ((differenceR/3))+nw3R;
					pixels[(nw2)+1] = ((differenceG/3))+nw3G;
					pixels[(nw2)+2] = ((differenceB/3))+nw3B;
				}
				nw1History.setNewR(pixels[(nw1)+0]);
				nw1History.setNewG(pixels[(nw1)+1]);
				nw1History.setNewB(pixels[(nw1)+2]);
				nw2History.setNewR(pixels[(nw2)+0]);
				nw2History.setNewG(pixels[(nw2)+1]);
				nw2History.setNewB(pixels[(nw2)+2]);
		
		//West/
				int west3R = pixels[(west3)+0];
				int west3G = pixels[(west3)+1];
				int west3B = pixels[(west3)+2];
				west1History.setPrevR(currentRW1);
				west1History.setPrevG(currentGW1);
				west1History.setPrevB(currentBW1);
				west2History.setPrevR(currentRW2);
				west2History.setPrevG(currentGW2);
				west2History.setPrevB(currentBW2);
				differenceR = currentR-west3R;
				differenceG = currentG-west3G;
				differenceB = currentB-west3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x-1, y)) {
					pixels[(west1)+0] = ((differenceR/3)*2)+west3R;
					pixels[(west1)+1] = ((differenceG/3)*2)+west3G;
					pixels[(west1)+2] = ((differenceB/3)*2)+west3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x-2, y)) {
					pixels[(west2)+0] = ((differenceR/3))+west3R;
					pixels[(west2)+1] = ((differenceG/3))+west3G;
					pixels[(west2)+2] = ((differenceB/3))+west3B;
				}
				west1History.setNewR(pixels[(west1)+0]);
				west1History.setNewG(pixels[(west1)+1]);
				west1History.setNewB(pixels[(west1)+2]);
				west2History.setNewR(pixels[(west2)+0]);
				west2History.setNewG(pixels[(west2)+1]);
				west2History.setNewB(pixels[(west2)+2]);
				
				//South//
				south1History.setPrevR(currentRS1);
				south1History.setPrevG(currentGS1);
				south1History.setPrevB(currentBS1);
				south2History.setPrevR(currentRS2);
				south2History.setPrevG(currentGS2);
				south2History.setPrevB(currentBS2);
				int south3R = pixels[(south3)+0];
				int south3G = pixels[(south3)+1];
				int south3B = pixels[(south3)+2];
				differenceR = currentR-south3R;
				differenceG = currentG-south3G;
				differenceB = currentB-south3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x, y+1)) {
					pixels[(south1)+0] = ((differenceR/3)*2)+south3R;
					pixels[(south1)+1] = ((differenceG/3)*2)+south3G;
					pixels[(south1)+2] = ((differenceB/3)*2)+south3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x, y+2)) {
					pixels[(south2)+0] = ((differenceR/3))+south3R;
					pixels[(south2)+1] = ((differenceG/3))+south3G;
					pixels[(south2)+2] = ((differenceB/3))+south3B;
				}
				south1History.setNewR(pixels[(south1)+0]);
				south1History.setNewG(pixels[(south1)+1]);
				south1History.setNewB(pixels[(south1)+2]);
				south2History.setNewR(pixels[(south2)+0]);
				south2History.setNewG(pixels[(south2)+1]);
				south2History.setNewB(pixels[(south2)+2]);
				
				//North//
				north1History.setPrevR(currentRN1);
				north1History.setPrevG(currentGN1);
				north1History.setPrevB(currentBN1);
				north2History.setPrevR(currentRN2);
				north2History.setPrevG(currentGN2);
				north2History.setPrevB(currentBN2);
				int north3R = pixels[(north3)+0];
				int north3G = pixels[(north3)+1];
				int north3B = pixels[(north3)+2];
				differenceR = currentR-north3R;
				differenceG = currentG-north3G;
				differenceB = currentB-north3B;
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x, y-1)) {
					pixels[(north1)+0] = ((differenceR/3)*2)+north3R;
					pixels[(north1)+1] = ((differenceG/3)*2)+north3G;
					pixels[(north1)+2] = ((differenceB/3)*2)+north3B;
				}
				if(isWithinUserCircle(circleX, circleY, redEyeDiameter, x, y-2)) {
					pixels[(north2)+0] = ((differenceR/3))+north3R;
					pixels[(north2)+1] = ((differenceG/3))+north3G;
					pixels[(north2)+2] = ((differenceB/3))+north3B;
				}
				north1History.setNewR(pixels[(north1)+0]);
				north1History.setNewG(pixels[(north1)+1]);
				north1History.setNewB(pixels[(north1)+2]);
				north2History.setNewR(pixels[(north2)+0]);
				north2History.setNewG(pixels[(north2)+1]);
				north2History.setNewB(pixels[(north2)+2]);
		
		change.getAllPixelHistory().add(east1History);
		change.getAllPixelHistory().add(east2History);
		
		change.getAllPixelHistory().add(west1History);
		change.getAllPixelHistory().add(west2History);
		change.getAllPixelHistory().add(north1History);
		change.getAllPixelHistory().add(north2History);
		change.getAllPixelHistory().add(south1History);
		change.getAllPixelHistory().add(south2History);
		change.getAllPixelHistory().add(se1History);
		change.getAllPixelHistory().add(se2History);
		change.getAllPixelHistory().add(sw1History);
		change.getAllPixelHistory().add(sw2History);
		
		change.getAllPixelHistory().add(ne1History);
		change.getAllPixelHistory().add(ne2History);
		change.getAllPixelHistory().add(nw1History);
		change.getAllPixelHistory().add(nw2History);
		
		return pixels;
	}
	
	public TiledImage fixRedEyePixels(Inspiram inspiram, PlanarImage imageToFix, int centerEyeX, int centerEyeY) {
		int width = imageToFix.getWidth();
		int height = imageToFix.getHeight();
		SampleModel mySampleModel = imageToFix.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = imageToFix.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands*width*height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		Change change = new Change("Red Eye Removal", inspiram.currentLayer);
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
//		System.out.println("#*(UE#@(*UE#DJ Y!: " + y1 + ", " + yMax + "   :::   "  + x1 + ", " + xMax);
		for(int y=y1;y<yMax;y++) {
			for(int x=x1;x<xMax;x++)
			{
				pixelIndex = y*width*nbands+x*nbands;
				r = pixels[pixelIndex+0];
				g = pixels[pixelIndex+1];
				b = pixels[pixelIndex+2];
				if(isWithinUserCircle(centerEyeX, centerEyeY, inspiram.redEyeDiameter, x, y)) {
					if(isRedEyeValues(r, g, b)) {
						PixelHistory pixelHistory = new PixelHistory(x, y);
						pixelHistory.setPrevR(pixels[r]);
						pixelHistory.setPrevG(pixels[g]);
						pixelHistory.setPrevB(pixels[b]);
						pixels[pixelIndex+(0)] = rValue;
						pixels[pixelIndex+(1)] = gValue;
						pixels[pixelIndex+(2)] = bValue;
						pixelHistory.setNewR(pixels[pixelIndex+(0)]);
						pixelHistory.setNewG(pixels[pixelIndex+(1)]);
						pixelHistory.setNewB(pixels[pixelIndex+(2)]);
						change.allPixelHistory.add(pixelHistory);
	//					pixels = -antiAliasRedEye(pixels, pixelIndex, x, y, width, nbands);
						pixels = antiAlias(pixels, (int)y, width, nbands, (int)x, inspiram.redEyeDiameter, centerEyeX, centerEyeY, change);
					}
				}
			}
		}
//		inspiram.inspiramHistory.addChange(change);
		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(imageToFix,1,1);
		ti.setData(writableRaster);
		inspiram.repaintEverything();
		return ti;
	}
	
	public boolean isWithinUserCircle(int centerEyeX, int centerEyeY, int redEyeDiameter, int xCheck, int yCheck) {
		boolean isWithinCircle = false;
		
		int distance = (int) Math.sqrt((xCheck - centerEyeX) * (xCheck - centerEyeX) + (yCheck - centerEyeY) * (yCheck - centerEyeY));
		int radius = redEyeDiameter/2;
		
		if(distance > radius) {
			isWithinCircle = false;
		} 
		else {
			isWithinCircle = true;
		}
		
		return isWithinCircle;
	}
	
	public int[] antiAliasRedEye(int[] pixels, int index, int currentX, int currentY, int currentWidth, int currentBands, int centerCircleX, int centerCircleY, int redEyeDiameter) {
		int currentPixelIndex = currentY*currentWidth*currentBands+currentX*currentBands;
		int northWestPixelIndex = ((currentY)-1)*currentWidth*currentBands+((currentX)-1)*currentBands;
		int northPixelIndex = ((currentY)-1)*currentWidth*currentBands+((currentX))*currentBands;
		int northEastPixelIndex = ((currentY)-1)*currentWidth*currentBands+((currentX)+1)*currentBands;
		int westPixelIndex = ((currentY))*currentWidth*currentBands+((currentX)-1)*currentBands;
		
		//NorthWest Check//
		if(isWithinUserCircle(centerCircleX, centerCircleY, redEyeDiameter, currentX-1, currentY-1)) {
			pixels[northWestPixelIndex+RED_BAND] = medianChange(pixels[currentPixelIndex+RED_BAND], pixels[northWestPixelIndex+RED_BAND]);
			pixels[northWestPixelIndex+GREEN_BAND] = medianChange(pixels[currentPixelIndex+GREEN_BAND], pixels[northWestPixelIndex+GREEN_BAND]);
			pixels[northWestPixelIndex+BLUE_BAND] = medianChange(pixels[currentPixelIndex+BLUE_BAND], pixels[northWestPixelIndex+BLUE_BAND]);
		}
		//End of NorthWest Check//
		
		//North Check//
		if(isWithinUserCircle(centerCircleX, centerCircleY, redEyeDiameter, currentX, currentY-1)) {
			pixels[northPixelIndex+RED_BAND] = medianChange(pixels[currentPixelIndex+RED_BAND], pixels[northPixelIndex+RED_BAND]);
			pixels[northPixelIndex+GREEN_BAND] = medianChange(pixels[currentPixelIndex+GREEN_BAND], pixels[northPixelIndex+GREEN_BAND]);
			pixels[northPixelIndex+BLUE_BAND] = medianChange(pixels[currentPixelIndex+BLUE_BAND], pixels[northPixelIndex+BLUE_BAND]);
		}
		//End of North Check//
		
		//NorthEast Check//
		if(isWithinUserCircle(centerCircleX, centerCircleY, redEyeDiameter, currentX+1, currentY-1)) {
			pixels[northEastPixelIndex+RED_BAND] = medianChange(pixels[currentPixelIndex+RED_BAND], pixels[northEastPixelIndex+RED_BAND]);
			pixels[northEastPixelIndex+GREEN_BAND] = medianChange(pixels[currentPixelIndex+GREEN_BAND], pixels[northEastPixelIndex+GREEN_BAND]);
			pixels[northEastPixelIndex+BLUE_BAND] = medianChange(pixels[currentPixelIndex+BLUE_BAND], pixels[northEastPixelIndex+BLUE_BAND]);
		}
		//End of NorthEast Check//
		
		//West Check//
		if(isWithinUserCircle(centerCircleX, centerCircleY, redEyeDiameter, currentX-1, currentY)) {
			pixels[westPixelIndex+RED_BAND] = medianChange(pixels[currentPixelIndex+RED_BAND], pixels[westPixelIndex+RED_BAND]);
			pixels[westPixelIndex+GREEN_BAND] = medianChange(pixels[currentPixelIndex+GREEN_BAND], pixels[westPixelIndex+GREEN_BAND]);
			pixels[westPixelIndex+BLUE_BAND] = medianChange(pixels[currentPixelIndex+BLUE_BAND], pixels[westPixelIndex+BLUE_BAND]);
		}
		//West of North Check//
		
		for(int i = 0; i < 3; i++) {
			
		}
		return pixels;
	}
	
	public int medianChange(int val1, int val2) {
		int newValue = 0;
		newValue = Math.abs(val1-val2);
		return newValue;
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
				if(brightness > .1 && brightness < .95) {
					if(averageGrayscale < 230 && averageGrayscale > 25) {
//						if(g-b < 75) {
							if(g < r && b < r) {
								if(pixelRedRatio >= 1.55) { //A good value here is 1.67
									isRedEyeValue = true;
								}
							}
//						}
					}
				}
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
	
	public void defineEyeSize(final int centerEyeX, final int centerEyeY, final Inspiram inspiram, PlanarImage imageToFix) {
		inspiram.sliderFrame = new JFrame("Slide the slider to fit over the iris in a red eye.");
		JLabel colorChooserLabel = new JLabel("Choose Red Eye Color:");
		colorChooser = new JColorChooser();
		colorChooserLabel.setSize(500, 500);
		inspiram.sliderFrame.repaint();
		colorChooser.repaint();
		colorChooser.setVisible(true);
//		colorChooser.setSize(inspiram.sliderFrame.getWidth(), inspiram.radiusSlider.getHeight());
		inspiram.sliderFrame.setLayout(new BorderLayout());
		inspiram.sliderFrame.add(colorChooser, BorderLayout.NORTH);
		inspiram.radiusSlider = new JSlider(JSlider.HORIZONTAL);
		JButton fixRedEyeButton = new JButton("Fix it!");
		inspiram.sliderFrame.add(fixRedEyeButton, BorderLayout.SOUTH);
		fixRedEyeButton.setVisible(true);
		fixRedEyeButton.repaint();
		inspiram.sliderFrame.repaint();
		inspiram.sliderFrame.add(inspiram.radiusSlider, BorderLayout.CENTER);
		inspiram.radiusSlider.setMinimum(2);
		inspiram.radiusSlider.setMaximum((int)((double)(inspiram.rHeight)*.9));
		if(imageToFix.getWidth() >= imageToFix.getHeight()) {
			inspiram.radiusSlider.setMaximum(imageToFix.getHeight()-1);
		}
		else {
			inspiram.radiusSlider.setMaximum(imageToFix.getWidth()-1);
		}
		inspiram.sliderFrame.setSize(620, 450);
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
				rValue = colorChooser.getColor().getRed();
				gValue = colorChooser.getColor().getGreen();
				bValue = colorChooser.getColor().getBlue();
				fixRedEye(inspiram, centerEyeX, centerEyeY);
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
		inspiram.redEyeCircle.drawOval(inspiram.redEyeCenterX-((inspiram.redEyeDiameter/2))-2, (inspiram.redEyeCenterY-(inspiram.redEyeDiameter/2))-2, inspiram.redEyeDiameter+2, inspiram.redEyeDiameter+2);
		inspiram.redEyeCircle.drawOval(inspiram.redEyeCenterX-((inspiram.redEyeDiameter/2))-1, (inspiram.redEyeCenterY-(inspiram.redEyeDiameter/2))-2, inspiram.redEyeDiameter+1, inspiram.redEyeDiameter+1);
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
	public void fixRedEye(Inspiram inspiram, int centerEyeX, int centerEyeY) {
		inspiram.sliderFrame.dispose();
		TiledImage myTiledImage = fixRedEyePixels(inspiram, inspiram.layers[inspiram.currentLayer].getLayerImage(), centerEyeX, centerEyeY);
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
