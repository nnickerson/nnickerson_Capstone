package inspiram;

public class PixelHistory {
	
	int x = 0;
	int y = 0;
	int prevR = 0;
	int prevG = 0;
	int prevB = 0;
	int newR = 0;
	int newG = 0;
	int newB = 0;
	final int R_BAND = 0;
	final int G_BAND = 1;
	final int B_BAND = 2;

	public PixelHistory(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getPrevR() {
		return prevR;
	}

	public void setPrevR(int prevR) {
		this.prevR = prevR;
	}

	public int getPrevG() {
		return prevG;
	}

	public void setPrevG(int prevG) {
		this.prevG = prevG;
	}

	public int getPrevB() {
		return prevB;
	}

	public void setPrevB(int prevB) {
		this.prevB = prevB;
	}

	public int getNewR() {
		return newR;
	}

	public void setNewR(int newR) {
		this.newR = newR;
	}

	public int getNewG() {
		return newG;
	}

	public void setNewG(int newG) {
		this.newG = newG;
	}

	public int getNewB() {
		return newB;
	}

	public void setNewB(int newB) {
		this.newB = newB;
	}
}
