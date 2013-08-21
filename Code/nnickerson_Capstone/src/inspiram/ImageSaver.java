package inspiram;

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
				imageSaver.saveImageAsPNG(inspiram.loadedImage, false, "");
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
	public void saveImageAsPNG(PlanarImage imageToSave, boolean creatingDirectory, String fileNameParam) {
		String fileName = "";
		
		if(!creatingDirectory) {
			fileName = getFileNameFromUser();
		}
		else {
			fileName = fileNameParam;
		}
		
		BufferedImage bi = imageToSave.getAsBufferedImage();
		
		File savingFile = new File(filePath + fileName + ".png");		
		
		try {
			savingFile.createNewFile();
			ImageIO.write(bi, "png", savingFile);
			
		} catch (IOException e) {
			System.out.println("Creating the Inspiram saving directory under the C drive.");
			boolean inspiramSavingDirectory = new File("C:/Inspiram/").mkdirs();
			saveImageAsPNG(imageToSave, true, fileName);
		}
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
