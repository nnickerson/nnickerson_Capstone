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

public class History extends JMenu {
	
	List<Change> history = new ArrayList<Change>();

	public History() {
		this.setText("History");
		this.setName("History");
	}
	
	public void addChange(Change change) {
		history.add(change);
		this.removeAll();
		for(JMenuItem i : history) {
			this.add(i);
		}
	}
	
	public List<Change> getHistory() {
		return history;
	}

	public void setHistory(List<Change> history) {
		this.history = history;
	}
}
