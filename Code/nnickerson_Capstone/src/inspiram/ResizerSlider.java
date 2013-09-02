package inspiram;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ResizerSlider extends JFrame {
	
	int originalWidth = 0;
	int originalHeight = 0;
	int maxWidth = 0;
	int maxHeight = 0;
	float aspectRatio = 0;
	int largerNumber = 0;
	int smallerNumber = 0;
	JSlider widthSlider;
	JSlider heightSlider;
	JLabel widthLabel;
	JLabel heightLabel;
	int currentWidth = 0;
	int currentHeight = 0;
	JButton resizeButton;
	JFrame thisFrame = this;
	boolean heightIsLarger = false;
	int percent = 100;
	Inspiram inspiram;

	public ResizerSlider(int currentImageWidth, int currentImageHeight, Inspiram inspiram) {
		this.inspiram = inspiram;
		originalWidth = currentImageWidth;
		originalHeight = currentImageHeight;
		currentWidth = originalWidth;
		currentHeight = originalHeight;
		aspectRatio = determineAspectRatio(originalHeight, originalWidth);
		this.setSize(400, 500);
		this.setLayout(new GridLayout(4,1));
		setHeightIsLarger();
		setupWidthSlider();
		setupHeightSlider();
		setupLabels();
		setupResizeButton();
		this.repaint();
	}
	
	public void setHeightIsLarger() {
		heightIsLarger = (currentHeight>currentWidth)? true : false;
	}
	
	public float determineAspectRatio(int h, int w) {
		float ar = 0;
		
		if(h >= w) {
			largerNumber = h;
			smallerNumber = w;
		}
		else {
			largerNumber = w;
			smallerNumber = h;
		}
		
		ar = largerNumber / smallerNumber;
		
		return ar;
	}

	public void setupWidthSlider() {
//		widthSlider = new JSlider();
////		widthSlider.setValue(originalWidth);
//		this.setTitle("Slide a slider to change the size:");
//		widthSlider = new JSlider(JSlider.HORIZONTAL);
//		this.repaint();
//		this.add(widthSlider);
//		widthSlider.setMinimum(10);
//		widthSlider.setMaximum((int)((double)(Toolkit.getDefaultToolkit().getScreenSize().getWidth())*.9));
//		this.setVisible(true);
//		this.repaint();
//		widthSlider.setBorder(BorderFactory.createBevelBorder(10));
//		widthSlider.setMajorTickSpacing((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/10));
//		widthSlider.setMinorTickSpacing((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/50));
//		widthSlider.setPaintLabels(true);
//		widthSlider.setPaintTicks(true);
//		widthSlider.setPaintTrack(true);
//		widthSlider.setVisible(true);
//		widthSlider.repaint();
//		
//		widthSlider.addChangeListener(new ChangeListener() {
//
//			@Override
//			public void stateChanged(ChangeEvent e) {
////				JSlider tempSlider = (JSlider)e.getSource();
////				int newWidth = tempSlider.getValue();
////				float multiplier = 0.0f;
////				int newHeight = 0;
////				if(newWidth > originalWidth) {
////					multiplier = newWidth/originalWidth;
////					newHeight = (int)(originalHeight/multiplier);
////				}
////				else {
////					multiplier = originalWidth/newWidth;
////					newHeight = (int)(originalHeight*multiplier);
////				}
////				System.out.println(multiplier);
////				widthLabel.setText("Width: " + newWidth);
////				heightLabel.setText("Height: " + newHeight);
//				widthSlider.repaint();
//				heightSlider.repaint();
//				widthLabel.repaint();
//				heightLabel.repaint();
//				thisFrame.repaint();
//				heightSlider.updateUI();
//				widthSlider.updateUI();
//			}
//		});
//		this.add(widthSlider);
		thisFrame.repaint();
	}
	
	public void setupHeightSlider() {
		heightSlider = new JSlider();
		heightSlider.setValue(100);
		this.setTitle("Slide a slider to change the size:");
		heightSlider = new JSlider(JSlider.HORIZONTAL);
		this.repaint();
		this.add(heightSlider);
		heightSlider.setMinimum(1);
		heightSlider.setMaximum((int)(200));
		this.setVisible(true);
		this.repaint();
		heightSlider.setBorder(BorderFactory.createBevelBorder(10));
		heightSlider.setMajorTickSpacing((int) (20));
		heightSlider.setMinorTickSpacing((int) (10));
		heightSlider.setPaintLabels(true);
		heightSlider.setPaintTicks(true);
		heightSlider.setPaintTrack(true);
		heightSlider.setVisible(true);
		heightSlider.repaint();
		
		heightSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider tempSlider = (JSlider)e.getSource();
				int newHeight = originalHeight;
				int newWidth = (int)(originalWidth);
				percent = tempSlider.getValue();
				newWidth = (originalWidth/100)*percent;
				newHeight = (originalHeight/100)*percent;
				widthLabel.setText("Width: " + newWidth);
				heightLabel.setText("Height: " + newHeight);
				currentWidth = newWidth;
				currentHeight = newHeight;
				thisFrame.repaint();
				widthLabel.setVisible(true);
				heightLabel.setVisible(true);
				resizeButton.setVisible(true);
			}
		});
		this.add(heightSlider);
		thisFrame.repaint();
	}
	
	public void setupResizeButton() {
		resizeButton = new JButton("Resize!");
		this.add(resizeButton);
		resizeButton.setVisible(true);
		resizeButton.repaint();
		
		resizeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton tempButton = (JButton)e.getSource();
				Text texter = new Text();
				
				inspiram.layers[inspiram.currentLayer].setLayerImage(texter.getPlanarImageFromImage(inspiram.layers[inspiram.currentLayer].getPlainImage()));
				System.out.println("THE LAYRE IMAGE: " + currentWidth);
				inspiram.layers[inspiram.currentLayer].setLayerImage(Resizer.resizeImage(inspiram.layers[inspiram.currentLayer].getLayerImage(),currentWidth,currentHeight,percent));
				inspiram.layers[inspiram.currentLayer].setSize(currentWidth, currentHeight);
				inspiram.layers[inspiram.currentLayer].setPlainImage();
				inspiram.layers[inspiram.currentLayer].add(inspiram.layers[inspiram.currentLayer].getImageDisplay());
				System.out.println("THE LAYRE IMAGE: " + inspiram.layers[inspiram.currentLayer].getLayerImage().getHeight());
				inspiram.layers[inspiram.currentLayer].repaint();
				inspiram.layers[inspiram.currentLayer].updateUI();
			}
		});
		thisFrame.repaint();
		widthLabel.setVisible(true);
		heightLabel.setVisible(true);
		resizeButton.setVisible(true);
		thisFrame.repaint();
	}
	
	public void setupLabels() {
		widthLabel = new JLabel("Width: " + currentWidth);
		heightLabel = new JLabel("Height: " + currentHeight);
		this.add(widthLabel);
		this.add(heightLabel);
		this.repaint();
	}
}
