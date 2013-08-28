package inspiram;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

public class Resizer {

	public Resizer() {
		
	}
	
	public static PlanarImage resizeImage(PlanarImage originalImage, int newWidth, int newHeight, int scaleFactor) {
		PlanarImage resizedImage = null;	
		int[] oldPixels = new int[3*originalImage.getWidth()*originalImage.getHeight()];
		int[] newPixels = new int[3*newWidth*newHeight];
		Raster readableRaster = originalImage.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		readableRaster.getPixels(0, 0, originalImage.getWidth(), originalImage.getHeight(), oldPixels);
		
		for(int i = 0; i < oldPixels.length; i++) {
			newPixels[((i)/100)*scaleFactor] = oldPixels[i];
		}
		
		writableRaster.setPixels(0, 0, newWidth, newHeight, newPixels);
		TiledImage ti = new TiledImage(originalImage,1,1);
		ti.setData(writableRaster);
		resizedImage = ti.createSnapshot();
		return resizedImage;
	}
}
