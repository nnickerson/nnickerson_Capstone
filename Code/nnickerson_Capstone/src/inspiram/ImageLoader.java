package inspiram;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.jai.*;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.sun.media.jai.widget.DisplayJAI;

/**
 * 
 * @author nnickerson
 *
 */

public class ImageLoader {
	
	String fileLocation = "InspiramLogo.png";
	
	public Image loadLogoAsImage() {
		Image image = null;
		try {
			File i = new File(fileLocation);
			image = ImageIO.read(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}
	
	public PlanarImage loadPlanarImageWithJAI(String fileLocation) {
		ParameterBlock pb = new ParameterBlock();
		pb.add(fileLocation);
		PlanarImage loadedImage = JAI.create("fileLoad", pb);
		
		BufferedImage bi = loadedImage.getAsBufferedImage();
		
		boolean imageCanFit = false;
		float scaledX = loadedImage.getWidth();
		float scaledY = loadedImage.getHeight();
		float scale = 0.0f;
		while(!imageCanFit) {
			if(scaledX <= Toolkit.getDefaultToolkit().getScreenSize().getWidth()*.9) {
				if(scaledY <= Toolkit.getDefaultToolkit().getScreenSize().getHeight()*.9) {
					imageCanFit = true;
				}
				else {
					scaledX = scaledX*.9f;
					scaledY = scaledY*.9f;
					scale += 0.1f;
				}
			}
			else {
				scaledX = scaledX*.9f;
				scaledY = scaledY*.9f;
				scale += 0.1f;
			}
		}
		
		Image image = bi.getScaledInstance((int)scaledX, (int)scaledY, Image.SCALE_SMOOTH);
		
	    ParameterBlock pb2 = new ParameterBlock();
	    pb2.add(image);
	    //The awtImage is an operation
	    PlanarImage im = (PlanarImage)JAI.create("awtImage", pb2);
		return im;
	}

	public void loadImage(Inspiram inspiram, String imageLocation) {
		inspiram.loadedImage = loadPlanarImageWithJAI(imageLocation);
		inspiram.displayJAIimage = null;
		inspiram.removeOldComponents();
		inspiram.displayJAIimage = new DisplayJAI(inspiram.loadedImage);
	//		scrollPane = new JScrollPane(displayJAIimage);
		inspiram.imageHolder.add(inspiram.displayJAIimage);
		inspiram.setSize(inspiram.getWidth() - 1, inspiram.getHeight() - 1);
		inspiram.setSize(inspiram.getWidth() + 1, inspiram.getHeight() + 1);
		inspiram.imageHolder.repaint();
		inspiram.repaint();
		inspiram.getContentPane().repaint();
		inspiram.welcomeJLabel.setVisible(false);
	}

	public void addImageLoadMenu(final Inspiram inspiram) {
		inspiram.mainMenuBar = new JMenuBar();
	    inspiram.fileMenu = new JMenu("File");
	    inspiram.editMenu = new JMenu("Edit");
	    inspiram.toolsMenu = new JMenu("Tools");
	    inspiram.loadImageOption = new JMenuItem("Load Image");
	    inspiram.fileMenu.add(inspiram.loadImageOption);
	    inspiram.mainMenuBar.add(inspiram.fileMenu);
	    inspiram.mainMenuBar.add(inspiram.editMenu);
	    inspiram.mainMenuBar.add(inspiram.toolsMenu);
	    inspiram.setJMenuBar(inspiram.mainMenuBar);
	    
	    //Listeners//
	    inspiram.loadImageOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(null);
				String imageLocation = fileChooser.getSelectedFile().getAbsolutePath();
				System.out.println(imageLocation);
				loadImage(inspiram.inspiramClass, imageLocation);
			}
		});
	    //End of listeners//
	    
	    inspiram.getContentPane().repaint();
	    
	    inspiram.repaint();
	}
}
