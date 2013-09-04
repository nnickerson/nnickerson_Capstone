package inspiram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.sun.media.jai.widget.DisplayJAI;

public class Change extends JMenu {
	
	JMenuItem undoChange = new JMenuItem();
	String changeDescription = "";
	List<PixelHistory> allPixelHistory = new ArrayList<PixelHistory>();
	Change thisChange = this;

	public Change(String description) {
		changeDescription = description;
		this.setText(description);
		undoChange.setText("Undo");
		this.add(undoChange);
	}
	
	public ActionListener createChangeUndoListener(final Inspiram inspiram) {
		ActionListener changeUndoListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem chosenMenuItem = (JMenuItem) e.getSource();
				
					System.out.println("Undoing image from history!");
					JPopupMenu popupMenu = (JPopupMenu)chosenMenuItem.getParent();
					Change parentMenu = (Change)popupMenu.getInvoker();
					System.out.println("Undoing image from history!");
						
						inspiram.layers[inspiram.currentLayer].setLayerImage(parentMenu.revertChange(inspiram.layers[inspiram.currentLayer].getLayerImage()));
						inspiram.layers[inspiram.currentLayer].setPlainImage();
						inspiram.layers[inspiram.currentLayer].add(inspiram.layers[inspiram.currentLayer].getImageDisplay());
						inspiram.repaintEverything();
						thisChange.removeAll();
			}
		};
		return changeUndoListener;
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
		
		for(int i = allPixelHistory.size()-1; i >= 0; i--) {
			System.out.println("REVERT PIXEL!!!");
			System.out.println("R: " + allPixelHistory.get(i).getPrevR() + "    G: " + allPixelHistory.get(i).getPrevG() + "     B: " + allPixelHistory.get(i).getPrevB() );
			pixelIndex = allPixelHistory.get(i).getY() * width * nbands + allPixelHistory.get(i).getX() * nbands;
			pixels[(pixelIndex) + (allPixelHistory.get(i).R_BAND)] = allPixelHistory.get(i).getPrevR();
			pixels[(pixelIndex) + (allPixelHistory.get(i).G_BAND)] = allPixelHistory.get(i).getPrevG();
			pixels[(pixelIndex) + (allPixelHistory.get(i).B_BAND)] = allPixelHistory.get(i).getPrevB();
		}
		
		History.removeChange(this);
		
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
