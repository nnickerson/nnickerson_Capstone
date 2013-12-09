package inspiram;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class ImageSaver {
	
	String filePath = "C:/Inspiram/";

	public ImageSaver() {
		
	}
	
	public void addSaveOption(final Inspiram inspiram) {
		inspiram.saveOption = new JMenuItem("Save Image");
		inspiram.fileMenu.add(inspiram.saveOption);
		inspiram.setJMenuBar(inspiram.mainMenuBar);
		
		//Listeners//
	    ActionListener saveListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageSaver imageSaver = new ImageSaver();
				imageSaver.saveImageAsPNG(inspiram, false, "");
			}
		};
		
		inspiram.saveOption.addActionListener(saveListener);
	    //End of listeners//
	}
	
	/**
	 * Saves an image to the C:/Inspiram/ directory as a png file.
	 * Always put the creatingDirectory boolean to false!
	 * Leave the fileName as "" unless you put creatingDirectory to true for some reason.
	 * @param imageToSave
	 */
	public void saveImageAsPNG(Inspiram ins, boolean creatingDirectory, String fileNameParam) {
		String fileName = "";
		
		if(!creatingDirectory) {
			fileName = getFileNameFromUser();
		}
		else {
			fileName = fileNameParam;
		}
		
		Image imageToSave = mergeAllLayers(ins);
		
		BufferedImage bi = ins.inspiramLocker.imageToBufferedImage(imageToSave);
		
		File savingFile = new File(filePath + fileName + ".png");		
		
		try {
			savingFile.createNewFile();
			ImageIO.write(bi, "png", savingFile);
			
		} catch (IOException e) {
			System.out.println("Creating the Inspiram saving directory under the C drive.");
			boolean inspiramSavingDirectory = new File("C:/Inspiram/").mkdirs();
			saveImageAsPNG(ins, true, fileName);
		}
	}
	
	public Image mergeAllLayers(Inspiram ins) {
		Image finalImage = null;
		int startingNum = ins.layers.length-1;
		if(ins.layers.length%2 == 1) {
			startingNum--;
//			finalImage = ins.inspiramLocker.combineImages(ins.layers[ins.layers.length-1].getLayerImage(), ins.layers[ins.layers.length-1].getLayerImage(), true);
			finalImage = ins.inspiramLocker.add2Images(ins.layers[ins.layers.length-1].getPlainImage(), ins.layers[ins.layers.length-1].getPlainImage());
		}
		for(int i = startingNum; i >= 0; i--) {
			if(finalImage == null) {
				finalImage = ins.layers[startingNum].getPlainImage();
			}
//			finalImage = ins.inspiramLocker.combineImages(finalPlanarImage, ins.layers[i].getLayerImage(), true);
			finalImage = ins.inspiramLocker.add2Images(finalImage, ins.layers[i].getPlainImage());
			
		}
		return finalImage;
	}
	
	/**
	 * Grabs user input from a JOptionPane.
	 * @return
	 */
	public String getFileNameFromUser() {
		String fileName = JOptionPane.showInputDialog("Please enter a name for the image without the extension.");
		
		return fileName;
	}
}
