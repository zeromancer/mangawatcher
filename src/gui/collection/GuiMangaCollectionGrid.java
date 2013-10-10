package gui.collection;

import gui.GuiFrame;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import misc.WrapLayout;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public class GuiMangaCollectionGrid extends JScrollPane {

	private static final long serialVersionUID = 566167950981962725L;
	
	private final JPanel panel;
	
	private final GuiFrame frame;
	private final MangaLibrary library;
	private final MangaCollection collection;

	private final Map<Manga, JComponent> olderMap;
	private final Map<Manga, JComponent> newerMap;

	private final JLabel titel;
	
	private final JLabel newerLabel;
	private final JLabel olderLabel;

	private final JPanel newerPanel;
	private final JPanel olderPanel;

	public GuiMangaCollectionGrid(GuiFrame frame, MangaCollection collection) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.collection = collection;

		
		panel = new JPanel();
		panel.setLayout(new WrapLayout());
//		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		setViewportView(panel);
		//setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		setWheelScrollingEnabled(true);
		
//		panel.setPreferredSize(new Dimension(getWidth(), 99999));
//		panel.setSize(frame.getWidth(), 0);
//		panel.setPreferredSize(new Dimension(700, 0));
		
		newerMap = new HashMap<>();
		olderMap = new HashMap<>();

		titel = new JLabel(collection.getName());
		titel.setFont(frame.getOptions().getTitelFont());
//		titel.setPreferredSize(new Dimension(1000,40));
		titel.setAlignmentX(0.5f);
		titel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(titel);
		
		newerLabel = new JLabel("New:");
		newerLabel.setFont(frame.getOptions().getSubtitelFont());
//		newerLabel.setPreferredSize(new Dimension(getWidth(),titel.getPreferredSize().height));
		panel.add(newerLabel);
		newerPanel = new JPanel(new WrapLayout());
//		newerPanel.setPreferredSize(new Dimension(getWidth(), 0));
		//panel.add(newerPanel);


		List<Manga> newer = new ArrayList<>();
		List<Manga> older = new ArrayList<>();
		for (Manga manga : library.getCollection(collection))
			if (manga.newAvailable())
				newer.add(manga);
			else
				older.add(manga);
		
		for (Manga manga : newer) {
			JComponent component = new GuiMangaCoverButton(frame, manga);
			panel.add(component);
			newerMap.put(manga, component);
		}
		
		
		
		
		olderLabel = new JLabel("Up to Date:");
		olderLabel.setFont(frame.getOptions().getSubtitelFont());
		olderLabel.setPreferredSize(new Dimension(getWidth(),titel.getPreferredSize().height));
		panel.add(olderLabel);
		olderPanel = new JPanel(new WrapLayout());
//		olderPanel = new JPanel();
//		olderPanel.setPreferredSize(new Dimension(getWidth(), 0));
		//panel.add(olderPanel);
		
		
		
		for (Manga manga : older) {
			JComponent component = new GuiMangaCover(frame, manga);
			panel.add(component);
			olderMap.put(manga, component);
		}
		
		
		addComponentListener(new ComponentListener() {
		    public void componentResized(ComponentEvent e) {
				if(getWidth()<=0) return;
				titel.setPreferredSize(new Dimension(getWidth()-2,60));
				newerLabel.setPreferredSize(new Dimension(getWidth()-2,60));
				olderLabel.setPreferredSize(new Dimension(getWidth()-2,60));
		    }

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
				if(getWidth()<=0) return;
				titel.setPreferredSize(new Dimension(getWidth()-2,60));
				newerLabel.setPreferredSize(new Dimension(getWidth()-2,60));
				olderLabel.setPreferredSize(new Dimension(getWidth()-2,60));
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
		
		update();
	}

	public void update() {
		revalidate();
		this.repaint();
		
		if(2==2)return;
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
				JComponent component = new GuiMangaCoverButton(frame, manga);
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
				JComponent component = new GuiMangaCover(frame, manga);
				olderPanel.add(component);
				olderMap.put(manga, component);
			}
		} else {
			olderLabel.setVisible(false);
			olderPanel.setVisible(false);
		}
		

//		this.revalidate();
//		
//		M.print("scroll.s(): " + this.getSize().toString());
//		M.print("panel.s(): " + panel.getSize().toString());
//		M.print("older.s(): " + olderPanel.getSize().toString());
//		
//		M.print("scroll.ps(): " + this.getPreferredSize().toString());
//		M.print("panel.ps(): " + panel.getPreferredSize().toString());
//		M.print("older.ps(): " + olderPanel.getPreferredSize().toString());
		
//		M.print("scroll.getSize(): " + this.getWidth()+" , "+this.getHeight());
//		M.print("panel.getSize(): " + panel.getWidth()+" , "+panel.getHeight());
//		M.print("older.getSize(): " + olderPanel.getWidth()+" , "+olderPanel.getHeight());
		
	}

}



/*




public class GuiMangaCollectionGrid extends JScrollPane {

	private static final long serialVersionUID = 566167950981962725L;
	
	private final JPanel panel;
	
	private final GuiFrame frame;
	private final MangaLibrary library;
	private final MangaCollection collection;

	private final Map<Manga, JComponent> olderMap;
	private final Map<Manga, JComponent> newerMap;

	private final JLabel titel;
	
	private final JLabel newerLabel;
	private final JLabel olderLabel;

	private final JPanel newerPanel;
	private final JPanel olderPanel;

	public GuiMangaCollectionGrid(GuiFrame frame, MangaCollection collection) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.collection = collection;

		
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		setViewportView(panel);
		//setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		setWheelScrollingEnabled(true);
		
//		panel.setPreferredSize(new Dimension(getWidth(), 99999));
//		panel.setSize(frame.getWidth(), 0);
		panel.setPreferredSize(new Dimension(700, 0));
		
		newerMap = new HashMap<>();
		olderMap = new HashMap<>();

		titel = new JLabel(collection.getName());
		titel.setFont(frame.getOptions().getTitelFont());
		panel.add(titel);
		
		newerLabel = new JLabel("New:");
		newerLabel.setFont(frame.getOptions().getSubtitelFont());
		panel.add(newerLabel);
		newerPanel = new JPanel(new WrapLayout());
//		newerPanel.setPreferredSize(new Dimension(getWidth(), 0));
		panel.add(newerPanel);

		olderLabel = new JLabel("Up to Date:");
		olderLabel.setFont(frame.getOptions().getSubtitelFont());
		panel.add(olderLabel);
		olderPanel = new JPanel(new WrapLayout());
//		olderPanel = new JPanel();
//		olderPanel.setPreferredSize(new Dimension(getWidth(), 0));
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
				JComponent component = new GuiMangaCoverButton(frame, manga);
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
				JComponent component = new GuiMangaCover(frame, manga);
				olderPanel.add(component);
				olderMap.put(manga, component);
			}
		} else {
			olderLabel.setVisible(false);
			olderPanel.setVisible(false);
		}
		

//		this.revalidate();
//		
//		M.print("scroll.s(): " + this.getSize().toString());
//		M.print("panel.s(): " + panel.getSize().toString());
//		M.print("older.s(): " + olderPanel.getSize().toString());
//		
//		M.print("scroll.ps(): " + this.getPreferredSize().toString());
//		M.print("panel.ps(): " + panel.getPreferredSize().toString());
//		M.print("older.ps(): " + olderPanel.getPreferredSize().toString());
		
//		M.print("scroll.getSize(): " + this.getWidth()+" , "+this.getHeight());
//		M.print("panel.getSize(): " + panel.getWidth()+" , "+panel.getHeight());
//		M.print("older.getSize(): " + olderPanel.getWidth()+" , "+olderPanel.getHeight());
		
	}

}



*/