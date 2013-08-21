package inspiram;

import java.awt.Color;

import javax.media.jai.PlanarImage;
import javax.swing.JPanel;

import com.sun.media.jai.widget.DisplayJAI;

public class Layer extends DisplayJAI {

	String layerName = "";
	int LayerID = 0;
	PlanarImage layerImage;
	DisplayJAI layerDisplay;
	
	public Layer(String layerName, int layerID)  {
		this.layerName = layerName;
		this.LayerID = layerID;
		setupBlankLayer();
	}
	
	public DisplayJAI getLayerDisplay() {
		return layerDisplay;
	}

	public void setLayerDisplay(DisplayJAI layerDisplay) {
		this.layerDisplay = layerDisplay;
	}

	public PlanarImage getLayerImage() {
		return layerImage;
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
