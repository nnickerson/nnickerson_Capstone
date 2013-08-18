package inspiram;

import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.sun.media.jai.widget.DisplayJAI;

public class Change extends JMenu {
	
	JMenuItem undoChange = new JMenuItem();
	String changeDescription = "";
	List<PixelHistory> allPixelHistory = new ArrayList<PixelHistory>();

	public Change(String description) {
		changeDescription = description;
		this.setText(description);
		undoChange.setText("Undo");
		this.add(undoChange);
	}
	
	public PlanarImage revertChange(PlanarImage pImage) {
		System.out.println("REVERTING!!!");
		int width = pImage.getWidth();
		int height = pImage.getHeight();
		SampleModel mySampleModel = pImage.getSampleModel();
		int nbands = mySampleModel.getNumBands();
		Raster readableRaster = pImage.getData();
		WritableRaster writableRaster = readableRaster.createCompatibleWritableRaster();
		int[] pixels = new int[nbands * width * height];
		readableRaster.getPixels(0, 0, width, height, pixels);
		int pixelIndex = 0;
		
		for(PixelHistory ph : allPixelHistory) {
			System.out.println("REVERT PIXEL!!!");
			System.out.println("R: " + ph.getPrevR() + "    G: " + ph.getPrevG() + "     B: " + ph.getPrevB() );
			pixelIndex = ph.getY() * width * nbands + ph.getX() * nbands;
			pixels[(pixelIndex) + (ph.R_BAND)] = ph.getPrevR();
			pixels[(pixelIndex) + (ph.G_BAND)] = ph.getPrevG();
			pixels[(pixelIndex) + (ph.B_BAND)] = ph.getPrevB();
		}

		writableRaster.setPixels(0, 0, width, height, pixels);
		TiledImage ti = new TiledImage(pImage, 1, 1);
		ti.setData(writableRaster);
		TiledImage myTiledImage = ti;
		pImage = null;
		pImage = myTiledImage.createSnapshot();
		return pImage;
	}

	public String getChangeDescription() {
		return changeDescription;
	}

	public void setChangeDescription(String changeDescription) {
		this.changeDescription = changeDescription;
	}

	public List<PixelHistory> getAllPixelHistory() {
		return allPixelHistory;
	}

	public void setAllPixelHistory(List<PixelHistory> allPixelHistory) {
		this.allPixelHistory = allPixelHistory;
	}
	
	
}