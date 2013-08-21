package inspiram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

//import layers.Layer;

public class LayersMenu extends JMenu {

	JMenuItem addLayerOption;
	JMenuItem deleteLayerOption;
	
	public LayersMenu() {
		
	}
	
	public void addLayersMenu(JMenuBar sameJMenuBar) {
		this.setText("Layers");
		addLayerOption = new JMenuItem("Add Layer");
		deleteLayerOption = new JMenuItem("Delete Layer");
		addLayerOption.setName("addLayer");
		deleteLayerOption.setName("deleteLayer");
		ActionListener layersOptionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem menuItem = (JMenuItem)e.getSource();
				if(menuItem.getName().equalsIgnoreCase("addLayer")) {
//					addLayer();
				}
				else {
//					deleteLayer();
				}
 			}
		};
		addLayerOption.addActionListener(layersOptionListener);
		deleteLayerOption.addActionListener(layersOptionListener);
		this.add(addLayerOption);
		this.add(deleteLayerOption);
	}
	
//	public void addLayer() {
//		Layer[] tempLayers = new Layer[layers.length];
//		for(int i = 0; i < layers.length; i++) {
//			System.out.println(layers[i].getLayerName() + " LAYER");
//			tempLayers[i] = layers[i];
//		}
//		layers = new Layer[tempLayers.length+1];
//		for(int i = 0; i < tempLayers.length; i++) {
//			layers[i] = tempLayers[i];
//		}
//		System.out.println("Added a new layer");
//		Layer newLayer = new Layer("Layer " + (layers.length-1), layers.length-1);
//		newLayer.setVisible(true);
//		layers[layers.length-1] = newLayer;
//		layers[0].setBackground(Color.green);
//		try {
//			layers[0].removeAll();
//			layers[0].setOpaque(false);
//			layers[0].add(new JLabel(new ImageIcon(ImageIO.read(new File("images.jpg")))));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		if(layers.length > 1) {
//			layers[1].removeAll();
//			layers[1].setBackground(Color.red);
//			layers[1].setOpaque(false);
//			try {
//				layers[1].add(new JLabel(new ImageIcon(ImageIO.read(new File("Untitled.jpg")))));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		displayAllLayers();
//		displayLayersOnPanel();
//	}
}
