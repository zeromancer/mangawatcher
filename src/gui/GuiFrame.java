package gui;

import gui.about.GuiAbout;
import gui.downloading.GuiDownloading;
import gui.manga.GuiMangaAdd;
import gui.manga.GuiMangaCollectionGrid;
import gui.manga.GuiMangaFull;
import gui.reading.GuiRead;
import gui.threading.BackgroundExecutors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import logic.LibraryManager;
import lombok.Getter;
import lombok.Setter;
import misc.M;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public @Getter @Setter class GuiFrame extends JFrame {

	// General
	private final MangaLibrary library;
	private final BackgroundExecutors executors;

	// Header
	// private final GuiMenuBar menubar;
	// private final JMenuBar menubar;
	// private final GuiProgressBar progress;

	// Content
	private final JTabbedPane tabbed;
	private final GuiDownloading downloading;
	private final GuiMangaAdd add;
	private final Map<Manga, GuiMangaFull> all;
	private final Map<MangaCollection, GuiMangaCollectionGrid> collections;
	private final GuiRead read;

	private final GuiOptions options;
	private final GuiAbout about;

	private JLabel mangaTab;

	// private GuiMenuBar menu;
	
	private String getIconPath(MangaCollection collection) {
		if (collection == MangaCollection.WATCHING)
			return "icons/categories/invisible-26.png";
		else if (collection == MangaCollection.PLANNING)
			return "icons/categories/watch-26.png";
		else if (collection == MangaCollection.DROPPED)
			return "icons/categories/law-26.png";
		else if (collection == MangaCollection.COMPLETED)
			return "icons/categories/ok-26.png";
		else
			return null;
	}
	
	public GuiFrame() {
		// frame
		super("Manga Watcher");
		setSize(800, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLookAndFeel();
		getContentPane().setLayout(new BorderLayout());
		// general
		library = LibraryManager.loadLibrary("config");
		executors = new BackgroundExecutors();

		// header
		// menubar = new GuiMenuBar();
		// menubar = new JMenuBar();
		// setJMenuBar(menubar);
		// getContentPane().add(menubar, BorderLayout.NORTH);
		// progress = new GuiProgressBar();
		// progress.setMaximumSize(new Dimension(getWidth(), 20));
		// getContentPane().add(progress, BorderLayout.SOUTH);
		// JMenuItem item = new JMenuItem("test");
		// menubar.add(item);
		// item.add(progress);
		// progress.setValue(50);
		// menubar.add(progress);

		options = new GuiOptions();

		ImageIcon icon;
		
		// content
		tabbed = new JTabbedPane(JTabbedPane.LEFT);
		tabbed.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(tabbed, BorderLayout.CENTER);

		add = new GuiMangaAdd(this);
		icon = new ImageIcon("icons/categories/plus2-26.png");
		addTab("Add", icon, add);


		all = new HashMap<>();
		for (MangaCollection collection : MangaCollection.values())
			for (Manga manga : library.getCollection(collection)) {
				GuiMangaFull full = new GuiMangaFull(this, manga);
				all.put(manga, full);
			}

		collections = new HashMap<Manga.MangaCollection, GuiMangaCollectionGrid>();
		for (MangaCollection collection : MangaCollection.values()) {
			GuiMangaCollectionGrid c = new GuiMangaCollectionGrid(this, collection);
			collections.put(collection, c);
//			JScrollPane scroll = new JScrollPane(c);
//			scroll.getVerticalScrollBar().setUnitIncrement(getOptions().getScrollAmount());
//			scroll.setWheelScrollingEnabled(true);
			icon = new ImageIcon(getIconPath(collection));
//			addTab(collection.getName(), icon, scroll);
			addTab(collection.getName(), icon, c);
		}
		
		if(library.getCollection(MangaCollection.WATCHING).size()!=0){
			Manga manga = library.getCollection(MangaCollection.WATCHING).get(0);
			GuiMangaFull full = all.get(manga);
			icon = new ImageIcon("icons/categories/dossier-26.png");
			mangaTab = addTab("Manga", icon, full);
			tabbed.setSelectedComponent(full);
		}

		read = new GuiRead(this);
		icon = new ImageIcon("icons/categories/literature-26.png");
		addTab("Reading", icon, read);
		//		tabbed.setEnabledAt(tabbed.indexOfComponent(read), false);
		
		downloading = new GuiDownloading();
		icon = new ImageIcon("icons/categories/down-26.png");
		addTab("Downloading", icon, downloading);

		icon = new ImageIcon("icons/categories/settings2-26.png");
		addTab("Options", icon, options);

		about = new GuiAbout();
		icon = new ImageIcon("icons/categories/about-26.png");
		addTab("About", icon, about);
		
		
	}
	
	private JLabel addTab(String text, ImageIcon icon, Component component) {
		JLabel label = new JLabel(text, icon, JLabel.CENTER);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		tabbed.addTab(text, icon, component);
		tabbed.setTabComponentAt(tabbed.indexOfComponent(component), label);
		return label;
	}

	public void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");

		} catch (Exception e) {
			e.printStackTrace();
			M.print(e.getMessage());
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
				JFrame frame = new GuiFrame();
				//frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
