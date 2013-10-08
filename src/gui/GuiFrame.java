package gui;

import gui.GuiEngine.Icons;
import gui.about.GuiAbout;
import gui.add.GuiMangaAdd;
import gui.collection.GuiMangaCollectionGrid;
import gui.downloading.GuiDownloading;
import gui.full.GuiMangaFull;
import gui.reading.GuiRead;
import gui.threading.BackgroundExecutors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logic.LibraryManager;
import lombok.Getter;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public @Getter class GuiFrame extends JFrame {

	// General
	private final MangaLibrary library;
	private final BackgroundExecutors executors;
	private final GuiEngine engine;
	
	// Header
	// private final GuiMenuBar menubar;
	// private final JMenuBar menubar;
	// private final GuiProgressBar progress;

	// Content
	private final JTabbedPane tabbed;
	private final GuiDownloading downloading;
	private final GuiMangaAdd add;
	private final GuiMangaFull full;
	private final Map<MangaCollection, GuiMangaCollectionGrid> collections;
	private final GuiRead read;

	private final GuiOptions options;
	private final GuiAbout about;

	private JLabel mangaTab;

	// private GuiMenuBar menu;
	
	
	public GuiFrame() {
		// frame
		super("Manga Watcher");
		setSize(800, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		// general
		library = LibraryManager.loadLibrary("config");
		executors = new BackgroundExecutors();
		options = new GuiOptions();
		engine = new GuiEngine(this);
//		engine.setLookAndFeel();
		engine.loadAll();
		

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


		// content
		tabbed = new JTabbedPane(JTabbedPane.LEFT);
		tabbed.setBorder(BorderFactory.createEmptyBorder());
		tabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		getContentPane().add(tabbed, BorderLayout.CENTER);

		add = new GuiMangaAdd(this);
		addTab("Add", Icons.ADD, add);


		full = new GuiMangaFull(this);
		addTab("Manga", Icons.MANGA, full);
//		tabbed.setEnabledAt(tabbed.indexOfComponent(full), false);
//		tabbed.setSelectedComponent(full);
		
		collections = new HashMap<Manga.MangaCollection, GuiMangaCollectionGrid>();
		for (MangaCollection collection : MangaCollection.values()) {
			GuiMangaCollectionGrid c = new GuiMangaCollectionGrid(this, collection);
			collections.put(collection, c);
			addTab(collection.getName(), Icons.valueOf(collection.toString()), c);
		}
		
		read = new GuiRead(this);
		addTab("Reading", Icons.READING, read);
		//		tabbed.setEnabledAt(tabbed.indexOfComponent(read), false);
		tabbed.setSelectedComponent(read);
		
		downloading = new GuiDownloading();
		addTab("Download", Icons.DOWNLOADING, downloading);

		addTab("Options", Icons.OPTIONS, options);

		about = new GuiAbout();
		addTab("About", Icons.ABOUT, about);
		

		tabbed.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            //System.out.println("Tab: " + tabbed.getSelectedIndex());
	            Component component = tabbed.getSelectedComponent();
	            if(component == full)
	            	((GuiMangaFull)component).update();
	            for(MangaCollection collection : MangaCollection.values())
	            if(component == collections.get(collection))
	            	((GuiMangaCollectionGrid)component).update();
	        }
	    });
	}
	
	

	private JLabel addTab(String text, Icons icon, Component component) {
		ImageIcon i = new ImageIcon(engine.getIcons().get(icon));
		JLabel label = new JLabel(text, i, JLabel.CENTER);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
//		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		label.setIconTextGap(10);
		label.setPreferredSize(new Dimension(80, 70));
		tabbed.addTab(text, i, component);
		tabbed.setTabComponentAt(tabbed.indexOfComponent(component), label);
//		tabbed.addTab("<html><body leftmargin=10 topmargin=20 rightmargin=10 bottommargin=20>Tab1</body></html>", i, component);
//		tabbed.addTab("<html><div style=\"height: "+getHeight()/20+"px\">"+text+"</div></html>", i, component);
		return label;
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
