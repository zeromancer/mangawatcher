/*
    MangaWatcher - a manga management program. 
    Copyright (C) 2013 David Siewert

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package gui;

import gui.about.GuiAbout;
import gui.add.GuiMangaAdd;
import gui.collection.GuiMangaCollectionGrid;
import gui.downloading.GuiDownloading;
import gui.full.GuiMangaFull;
import gui.reading.GuiRead;
import gui.threading.BackgroundExecutors;
import gui.tray.GuiTray;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import logic.DiskIOManager;
import logic.MangaLogic;
import lombok.Getter;
import misc.M;
import data.Engine;
import data.Engine.Icons;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;
import data.Options;

public @Getter class GuiFrame extends JFrame {

	// General
	private final BackgroundExecutors executors;
	private final MangaLibrary library;
	private final MangaLogic logic;
	private final Options options;
	private final Engine engine;

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
	private final GuiOptions option;
	private final GuiAbout about;

	private final GuiTray tray;

	// private GuiMenuBar menu;

	public GuiFrame(String config) {

		// frame
		super("Manga Watcher");
		setSize(800, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		// general

		setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 14));

		executors = new BackgroundExecutors();
		library = DiskIOManager.loadLibrary(config);
		options = DiskIOManager.loadOptions(config);
		logic = new MangaLogic(library);
		engine = new Engine(this);
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
		//		tabbed = new GuiTabbedPane(JTabbedPane.LEFT);
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

		downloading = new GuiDownloading(this);
		addTab("Download", Icons.DOWNLOADING, downloading);
		logic.setGui(downloading);

		option = new GuiOptions(this);
		addTab("Options", Icons.OPTIONS, option);

		about = new GuiAbout(this);
		addTab("Info", Icons.ABOUT, about);

		tabbed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//System.out.println("Tab: " + tabbed.getSelectedIndex());
				Component component = tabbed.getSelectedComponent();
				if (component == full)
					((GuiMangaFull) component).update();
				for (MangaCollection collection : MangaCollection.values())
					if (component == collections.get(collection))
						((GuiMangaCollectionGrid) component).update();
				if(component == downloading){
					DefaultCaret caret = (DefaultCaret)downloading.getText().getCaret();
					caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				}
			}
		});

		if(library.getCollection(MangaCollection.WATCHING).size()==0)
			tabbed.setSelectedComponent(add);
		else
			tabbed.setSelectedComponent(collections.get(MangaCollection.WATCHING));

		tray = new GuiTray(this);
	}

	private JLabel addTab(String text, Icons icon, Component component) {
		ImageIcon i = new ImageIcon(engine.getIcons().get(icon));
		JLabel label = new JLabel(text, i, JLabel.CENTER);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		//label.setPreferredSize(new Dimension(label.getPreferredSize().width, getHeight()/10-10));
		//		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//		label.setIconTextGap(10);
		//		label.setPreferredSize(new Dimension(80, 70));
		tabbed.addTab(text, i, component);
		tabbed.setTabComponentAt(tabbed.indexOfComponent(component), label);
		//		tabbed.addTab("<html><body leftmargin=10 topmargin=20 rightmargin=10 bottommargin=20>Tab1</body></html>", i, component);
		//		tabbed.addTab("<html><div style=\"height: "+getHeight()/20+"px\">"+text+"</div></html>", i, component);
		return label;
	}

	public void updateAll() {
		for (GuiMangaCollectionGrid entry : collections.values())
			entry.update();
	}

	public static void main(final String[] args) {

		final String os = (System.getProperty("os.name")).toUpperCase();

		if (os.contains("LINUX")) {
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					if (os.contains("LINUX"))
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
					else if (os.contains("WIN"))
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					else
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
				}
				

//				M.print("args: "+Arrays.toString(args));
				
				String config = null;
				
				if (args.length >= 1) {
					config = args[0];
					File file = new File(config);
					if (Files.exists(Paths.get(file.toURI())) && !file.isDirectory()) {
						M.print("" + config + " is not a directory, please provide a valid config directory");
						System.exit(1);
					}
				}else{

					URL url = ClassLoader.getSystemResource(".");
					
					config = url.toString().substring(5, url.toString().length());
					
					if(config.endsWith("bin/"))
						config = config.substring(0, config.length()-4);
					
					if(config.endsWith("MangaWatcher/"))
						config += "config/";
					
//					M.print("config: "+config);
				}
				JFrame frame = new GuiFrame(config);
				//frame.pack();
				frame.setVisible(true);
			}
		});
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}
}
