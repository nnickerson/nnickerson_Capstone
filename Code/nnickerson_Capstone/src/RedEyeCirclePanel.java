import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;


public class RedEyeCirclePanel extends JPanel {
	
	int w = 0;
	int h = 0;
	int circleDiameter = 0;
	int ovalX = 0;
	int ovalY = 0;
	
	public void paintComponent(Graphics g) {
		g.drawOval(ovalX, ovalY, circleDiameter, circleDiameter);
	}

	public RedEyeCirclePanel(int width, int height) {
		w = width;
		h = height;
	}

	public RedEyeCirclePanel(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public RedEyeCirclePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public RedEyeCirclePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

}
