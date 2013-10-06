package gui.manga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Manga;
import data.MangaLibrary;
import data.Manga.MangaCollection;

public class GuiMangaCollectionGrid extends JPanel {

	private static final long serialVersionUID = 566167950981962725L;
	
	MangaLibrary library;
	MangaCollection collection;

	Map<Manga, JComponent> olderMap;
	Map<Manga, JComponent> newerMap;

	JLabel titel;
	
	JLabel newerLabel;
	JLabel olderLabel;

	JPanel newerPanel;
	JPanel olderPanel;

	public GuiMangaCollectionGrid(MangaLibrary library, MangaCollection collection) {
		this.library = library;
		this.collection = collection;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		newerMap = new HashMap<>();
		olderMap = new HashMap<>();

		titel = new JLabel(collection.getName());
		add(titel);
		
		newerLabel = new JLabel("New");
		add(newerLabel);
		newerPanel = new JPanel();
		add(newerPanel);

		olderLabel = new JLabel("Up to Date:");
		add(olderLabel);
		olderPanel = new JPanel();
		add(olderPanel);

		update();
	}

	public void update() {
		
		// initialize

		List<Manga> newer = new ArrayList<>();
		List<Manga> older = new ArrayList<>();
		for (Manga manga : library.getCollection(collection))
			if (manga.newAvailable())
				newer.add(manga);
			else
				older.add(manga);

		// remove leftover panel entries

		List<Manga> newerRemove = new ArrayList<>(newerMap.keySet());
		List<Manga> olderRemove = new ArrayList<>(olderMap.keySet());
		newerRemove.removeAll(newer);
		olderRemove.removeAll(older);

		for (Manga manga : newerRemove) {
			JComponent component = newerMap.get(manga);
			newerPanel.remove(component);
			newerMap.remove(manga);
		}
		for (Manga manga : olderRemove) {
			JComponent component = olderMap.get(manga);
			olderPanel.remove(component);
			olderMap.remove(manga);
		}

		// add new panel entries

		newer.removeAll(newerMap.keySet());
		older.removeAll(olderMap.keySet());

		if (newer.size() + newerMap.size() > 0) {
			newerLabel.setVisible(true);
			newerPanel.setVisible(true);
			for (Manga manga : newer) {
				JComponent component = new GuiMangaQuickNewest(library, manga);
				newerPanel.add(component);
				newerMap.put(manga, component);
			}
		} else {
			newerLabel.setVisible(false);
			newerPanel.setVisible(false);
		}

		if (older.size() + olderMap.size() > 0) {
			olderLabel.setVisible(true);
			olderPanel.setVisible(true);
			for (Manga manga : older) {
				JComponent component = new GuiMangaQuick(library, manga);
				olderPanel.add(component);
				olderMap.put(manga, component);
			}
		} else {
			olderLabel.setVisible(false);
			olderPanel.setVisible(false);
		}
	}

}
