package inspiram;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

public class Resizer {

	public Resizer() {
		
	}
	
	public static PlanarImage resizeImage(PlanarImage originalImage, int newWidth, int newHeight, int scaleFactor) {
		BufferedImage newDimensions = new BufferedImage(newWidth, newHeight,BufferedImage.TYPE_INT_RGB);
		Text texter = new Text();
//		PlanarImage newImage = texter.getPlanarImageFromImage(newDimensions);
//			
//		int[] oldPixels = new int[3*originalImage.getWidth()*originalImage.getHeight()];
//		int[] newPixels = new int[3*newWidth*newHeight];
//		Raster readableRaster = originalImage.getData();
//		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
//		readableRaster.getPixels(0, 0, originalImage.getWidth(), originalImage.getHeight(), oldPixels);
//		
//		Raster newReadRaster = newImage.getData();
//		for(int i = 0; i < oldPixels.length; i++) {
//			newPixels[((i)/100)*scaleFactor] = oldPixels[i];
//		}
//		writableRaster.setPixels(0, 0, newWidth, newHeight, newPixels);
//		TiledImage ti = new TiledImage(newImage,1,1);
//		ti.setData(writableRaster);
//		newImage = ti.createSnapshot();
		System.out.println("RESIZER: " + newWidth);
		Image ni = originalImage.getAsBufferedImage().getScaledInstance(newWidth, newHeight, Image.SCALE_REPLICATE);
		PlanarImage nii = texter.getPlanarImageFromImage(ni);
		return nii;
	}
}
