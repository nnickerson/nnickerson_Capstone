package inspiram;

public class Line {
	
	int startX = 0;
	int startY = 0;
	int endX = 0;
	int endY = 0;
	int lineWidth = 1;
	String lineName = "";
	double slope = 0.0;

	public Line(int startX, int startY, int endX, int endY, int lineWidth) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.lineWidth = lineWidth;
	}
	
	public void calculateSlope() {
		slope = ((endY-startY)/(endX-startX));
	}
	
	public double getSlope() {
		return ((endY-startY)/(endX-startX));
	}
	
	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
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
