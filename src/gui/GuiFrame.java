package gui;

import gui.manga.GuiMangaCollectionGrid;
import gui.menu.GuiMenuBar;
import gui.threading.BackgroundExecutors;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
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

	private MangaLibrary library;
	private BackgroundExecutors executers;
	
	
	private Map<MangaCollection, GuiMangaCollectionGrid> collections;

	private JTabbedPane tabbed;
	private GuiMenuBar menu;
	
	
	public GuiFrame(MangaLibrary library) {
		super("Manga Watcher");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		menu = new GuiMenuBar();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			
			System.setProperty("awt.useSystemAAFontSettings","on");
			System.setProperty("swing.aatext", "true");
			
		} catch (Exception e) {
			e.printStackTrace();
			M.print(e.getMessage());
		} 
		
		
		tabbed = new JTabbedPane(JTabbedPane.LEFT);
		getContentPane().add(tabbed);
		
		collections = new HashMap<Manga.MangaCollection, GuiMangaCollectionGrid>();
		for (MangaCollection collection : MangaCollection.values()) {
			GuiMangaCollectionGrid c = new GuiMangaCollectionGrid(library,collection);
			collections.put(collection, c);
			tabbed.addTab(collection.getName(), c);
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

				MangaLibrary library = LibraryManager.loadLibrary("config");
				// MangaAll.try_refresh(library);
				// LibraryManager.saveLibrary(configDirectory, library);
				JFrame frame = new GuiFrame(library);
				frame.pack();

				frame.setVisible(true);
			}
		});
	}
}
