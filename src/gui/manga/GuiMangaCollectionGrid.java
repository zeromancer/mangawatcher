package gui.manga;

import gui.GuiFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public class GuiMangaCollectionGrid extends JScrollPane {

	private static final long serialVersionUID = 566167950981962725L;
	
	GuiFrame frame;
	MangaLibrary library;
	MangaCollection collection;

	Map<Manga, JComponent> olderMap;
	Map<Manga, JComponent> newerMap;

	JLabel titel;
	
	JLabel newerLabel;
	JLabel olderLabel;

	JPanel newerPanel;
	JPanel olderPanel;

	public GuiMangaCollectionGrid(GuiFrame frame, MangaCollection collection) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.collection = collection;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		setViewportView(panel);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		setWheelScrollingEnabled(true);

		newerMap = new HashMap<>();
		olderMap = new HashMap<>();

		titel = new JLabel(collection.getName());
		titel.setFont(frame.getOptions().getTitelFont());
		panel.add(titel);
		
		newerLabel = new JLabel("New:");
		newerLabel.setFont(frame.getOptions().getSubtitelFont());
		panel.add(newerLabel);
		newerPanel = new JPanel();
		panel.add(newerPanel);

		olderLabel = new JLabel("Up to Date:");
		olderLabel.setFont(frame.getOptions().getSubtitelFont());
		panel.add(olderLabel);
		olderPanel = new JPanel();
		panel.add(olderPanel);

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
				JComponent component = new GuiMangaQuickNewest(frame, manga);
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
				JComponent component = new GuiMangaQuick(frame, manga);
				olderPanel.add(component);
				olderMap.put(manga, component);
			}
		} else {
			olderLabel.setVisible(false);
			olderPanel.setVisible(false);
		}
	}

}
