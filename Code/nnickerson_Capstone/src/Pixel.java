/**
 * 
 */

/**
 * @author nnickerson
 *
 */
public class Pixel {
	
	int r = 0;
	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public int getB() {
		return b;
	}

	public void setB(int b) {
		this.b = b;
	}

	int g = 0;
	int b = 0;
	int x = 0;
	int y = 0;
	boolean isInBoundingBox = false;
	boolean alreadyCheckedForStart = false;

	/**
	 * 
	 */
	public Pixel(int x, int y) {
//		this.r = r;
//		this.g = g;
//		this.b = b;
		this.x = x;
		this.y = y;
	}
	
	
}
