import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;


public class SquareAnnotation {
	
	private int topLeftX = 0;
	private int topLeftY = 0;
	private int bottomRightX = 0;
	private int bottomRightY = 0;
	private boolean isEye = false;

	public SquareAnnotation(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY, boolean isEye) {
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
		this.isEye = isEye;
	}
	
	public int[] createBoundingBox(int width, int nbands, int[] pixels) {
		int x = topLeftX;
		while(x < bottomRightX) {
			int pixelIndex = topLeftY*width*nbands+x*nbands;
			pixels[pixelIndex+0] = 50;
			pixels[pixelIndex+1] = 200;
			pixels[pixelIndex+2] = 25;
			x++;
		}
		int x1 = topLeftX;
		while(x1 < bottomRightX) {
			int pixelIndex = bottomRightY*width*nbands+x1*nbands;
			pixels[pixelIndex+0] = 50;
			pixels[pixelIndex+1] = 200;
			pixels[pixelIndex+2] = 25;
			x1++;
		}
		int y = topLeftY;
		while(y < bottomRightY) {
			int pixelIndex = y*width*nbands+topLeftX*nbands;
			pixels[pixelIndex+0] = 50;
			pixels[pixelIndex+1] = 200;
			pixels[pixelIndex+2] = 25;
			y++;
		}
		int y1 = topLeftY;
		while(y1 < bottomRightY) {
			int pixelIndex = y1*width*nbands+bottomRightX*nbands;
			pixels[pixelIndex+0] = 50;
			pixels[pixelIndex+1] = 200;
			pixels[pixelIndex+2] = 25;
			y1++;
		}
		
		return pixels;
	}
}
