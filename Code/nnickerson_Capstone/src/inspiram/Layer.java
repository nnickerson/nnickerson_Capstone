package inspiram;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sun.media.jai.widget.DisplayJAI;

public class Layer extends DisplayJAI {

	String layerName = "";
	int LayerID = 0;
	private PlanarImage layerImage;
	private DisplayJAI layerDisplay;
	private Image plainImage;
	private JLabel imageDisplay;
	
	public Layer(String layerName, int layerID)  {
		this.layerName = layerName;
		this.LayerID = layerID;
		setupBlankLayer();
	}
	
	
	
	public Image getPlainImage() {
		return plainImage;
	}

	public JLabel getImageDisplay() {
		return imageDisplay;
	}



	public void setImageDisplay(JLabel imageDisplay) {
		this.imageDisplay = imageDisplay;
	}



	public void setPlainImage() {
		plainImage = layerImage.getAsBufferedImage();
		this.removeAll();
		imageDisplay = new JLabel();
		imageDisplay.setIcon(new ImageIcon(plainImage));
		imageDisplay.setSize(layerImage.getWidth(), layerImage.getHeight());
		layerImage = null;
	}
	
	public DisplayJAI getLayerDisplay() {
		return layerDisplay;
	}

	public void setLayerDisplay(DisplayJAI layerDisplay) {
		this.layerDisplay = layerDisplay;
	}

	public PlanarImage getLayerImage() {
		Text t = new Text();
		return t.getPlanarImageFromImage(this.plainImage);
	}

	public void setLayerImage(PlanarImage layerImage) {
		this.layerImage = layerImage;
	}

	
	public void setupBlankLayer() {
		this.setBackground(Color.blue);
		this.setSize(200,200);
		this.setVisible(true);
	}
	

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public int getLayerID() {
		return LayerID;
	}

	public void setLayerID(int layerID) {
		LayerID = layerID;
	}

	
}
