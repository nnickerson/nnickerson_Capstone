package layers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class DemoApplet extends JApplet {
	
	JApplet layersApplet = this;
	JMenuBar layersMenuBar;
	JMenu layersMenu;
	JMenuItem addLayerOption;
	JMenuItem deleteLayerOption;
	Layer[] layers = new Layer[0];
	Container container = this.getContentPane();
	JPanel layersHolder = new JPanel();
	JPanel layersPanel;

	public DemoApplet() throws HeadlessException {
		
	}
	
	public void init() {
		setupApplet();
	}
	
	public void setupApplet() {
		Frame layersFrame = (Frame)this.getParent().getParent();
		layersFrame.setSize(600, 600);
		layersMenuBar = new JMenuBar();
		layersApplet.setJMenuBar(layersMenuBar);
		addLayersMenu();
		addLayersPanel();
		addLayersHolder();
	}
	
	public void addLayersMenu() {
		layersMenu = new JMenu("Layers");
		addLayerOption = new JMenuItem("Add Layer");
		deleteLayerOption = new JMenuItem("Delete Layer");
		addLayerOption.setName("addLayer");
		deleteLayerOption.setName("deleteLayer");
		ActionListener layersOptionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem menuItem = (JMenuItem)e.getSource();
				if(menuItem.getName().equalsIgnoreCase("addLayer")) {
					addLayer();
				}
				else {
					deleteLayer();
				}
 			}
		};
		addLayerOption.addActionListener(layersOptionListener);
		deleteLayerOption.addActionListener(layersOptionListener);
		layersMenu.add(addLayerOption);
		layersMenu.add(deleteLayerOption);
		layersMenuBar.add(layersMenu);
	}
	
	public void addLayersHolder() {
		layersHolder.setBackground(Color.GRAY);
		container.add(layersHolder);
	}
	
	public void addLayersPanel() {
		layersPanel = new JPanel();
		layersPanel.setSize(300,300);
		layersPanel.setVisible(true);
		layersPanel.setBackground(Color.black);
		container.setLayout(new BorderLayout());
		layersPanel.setLayout(new BoxLayout(layersPanel, BoxLayout.Y_AXIS));
		container.add(layersPanel, BorderLayout.EAST);
		layersPanel.repaint();
		layersPanel.updateUI();
		layersHolder.repaint();
		container.repaint();
		this.repaint();		
	}
	
	public void displayLayersOnPanel() {
		layersPanel.removeAll();
		//Setting up button//
		ActionListener layerButtonListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton layerButton = (JButton)e.getSource();
				System.out.println(layerButton.getName());
			}
		};
		//Ending button setup//
		
		for(Layer layer : layers) {
			JButton newLayerButton = new JButton(layer.getLayerName());
			newLayerButton.addActionListener(layerButtonListener);
			System.out.println(layer.getLayerName());
			newLayerButton.setName(layer.getLayerName());
			newLayerButton.setText(layer.getLayerName());
			layersPanel.add(newLayerButton);
		}
		layersPanel.repaint();
		layersPanel.updateUI();
		layersHolder.repaint();
		this.repaint();
	}
	
	public void addLayer() {
		Layer[] tempLayers = new Layer[layers.length];
		for(int i = 0; i < layers.length; i++) {
			System.out.println(layers[i].getLayerName() + " LAYER");
			tempLayers[i] = layers[i];
		}
		layers = new Layer[tempLayers.length+1];
		for(int i = 0; i < tempLayers.length; i++) {
			layers[i] = tempLayers[i];
		}
		System.out.println("Added a new layer");
		Layer newLayer = new Layer("Layer " + (layers.length-1), layers.length-1);
		newLayer.setVisible(true);
		layers[layers.length-1] = newLayer;
		layers[0].setBackground(Color.green);
		try {
			layers[0].removeAll();
			layers[0].setOpaque(false);
			layers[0].add(new JLabel(new ImageIcon(ImageIO.read(new File("images.jpg")))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(layers.length > 1) {
			layers[1].removeAll();
			layers[1].setBackground(Color.red);
			layers[1].setOpaque(false);
			try {
				layers[1].add(new JLabel(new ImageIcon(ImageIO.read(new File("Untitled.jpg")))));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		displayAllLayers();
		displayLayersOnPanel();
	}
	
	public void displayAllLayers() {
		layersHolder.removeAll();
		for(Layer layer : layers) {
			System.out.println("Adding layer back to holder!");
			layersHolder.setLayout(null);
			layer.setOpaque(false);
			layersHolder.add(layer);
		}
		layersHolder.repaint();
		container.repaint();
		this.repaint();
	}
	
	public void deleteLayer() {
		
	}
}
