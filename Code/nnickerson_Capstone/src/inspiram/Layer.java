package inspiram;

import java.awt.Color;

import javax.swing.JPanel;

public class Layer extends JPanel {

	String layerName = "";
	int LayerID = 0;
	
	public Layer(String layerName, int layerID)  {
		this.layerName = layerName;
		this.LayerID = layerID;
		setupBlankLayer();
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
