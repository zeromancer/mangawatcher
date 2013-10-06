package gui;

import gui.manga.GuiMangaCollectionGrid;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic.LibraryManager;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

@SuppressWarnings("serial") public class GuiOverview extends JPanel {

	MangaLibrary library;
	Map<MangaCollection, GuiMangaCollectionGrid> collections;

	public GuiOverview(MangaLibrary library) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		collections = new HashMap<Manga.MangaCollection, GuiMangaCollectionGrid>();
		
		for (MangaCollection collection : MangaCollection.values()) {
			GuiMangaCollectionGrid c = new GuiMangaCollectionGrid(library,collection);
			collections.put(collection, c);
			add(c);
		}
	}
	
	public void updateAll(){
		for(GuiMangaCollectionGrid entry: collections.values())
			entry.update();
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Test Manga Add");
				// frame.setSize(300, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				MangaLibrary library = LibraryManager.loadLibrary("config");
				// MangaAll.try_refresh(library);
				// LibraryManager.saveLibrary(configDirectory, library);
				Manga manga = library.getCollection(MangaCollection.WATCHING).get(0);
				JComponent component = new GuiOverview(library);
				frame.add(component);
				frame.pack();

				frame.setVisible(true);
			}
		});
	}
}
/*


	MangaLibrary library;
	Map<MangaCollection, JPanel> collections;

	public GuiOverview(MangaLibrary library) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		collections = new HashMap<Manga.MangaCollection, JPanel>();
		for (MangaCollection collection : MangaCollection.values()) {

			JLabel label = new JLabel(collection.getName());
			this.add(label);

			JPanel panel = new JPanel();
			collections.put(collection, panel);

			List<Manga> newer = new ArrayList<>();
			List<Manga> older = new ArrayList<>();
			for (Manga manga : library.getCollection(collection))
				if (manga.newAvailable())
					newer.add(manga);
				else
					older.add(manga);

			if (newer.isEmpty() && older.isEmpty()) {
				continue;
			}

		}
	}

*/
