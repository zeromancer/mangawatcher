package gui.tray;

import gui.GuiFrame;
import gui.reading.GuiRead;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import misc.M;
import data.Engine;
import data.Engine.Icons;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public class GuiTray {

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final Engine engine;

	private boolean supported;

	private SystemTray tray;
	private PopupMenu popup;
	private MenuItem item;
	private TrayIcon trayIcon;

	private Image noNewerAvailableIcon;
	private Image newerAvailableIcon;

	private int oldAvailable;

	public GuiTray(final GuiFrame frame) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.engine = frame.getEngine();

		supported = SystemTray.isSupported();
		if (!supported)
			return;

		tray = SystemTray.getSystemTray();
		popup = new PopupMenu();
		item = new MenuItem("A MenuItem");
		popup.add(item);
		
		BufferedImage image;
		image = engine.getIcon(Icons.LOGOEMPTY);
		noNewerAvailableIcon = M.scale(image, image.getHeight(), 32);
		image = engine.getIcon(Icons.LOGO);
		newerAvailableIcon = M.scale(image, image.getHeight(), 32);
		
		trayIcon = new TrayIcon(noNewerAvailableIcon, "The Tip Text", popup);
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println("Can't add to tray");
		}
		
		oldAvailable = -1;
		
		update();
	}

	public void update() {
		if (!supported)
			return;
		int newAvailable = library.newAvailable();
		//M.print("oldAvailable: " + oldAvailable + " , newAvailable: " + newAvailable);
		if (newAvailable != oldAvailable) {
			oldAvailable = newAvailable;

			popup.removeAll();

			item = new MenuItem("Open MangaWatcher");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(true);					
				}
			});
			popup.add(item);
			
			if (newAvailable == 0)
				trayIcon.setImage(noNewerAvailableIcon);
			else {
				trayIcon.setImage(newerAvailableIcon);

				popup.addSeparator();
				
				for (final Manga manga : library.getCollection(MangaCollection.WATCHING))
					if (manga.newAvailable()) {
						//M.print("manga: " + manga);
						item = new MenuItem("Read " + manga.getName() + " " + (manga.getRead() + 1));
						item.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								frame.setVisible(true);
								GuiRead read = frame.getRead();
								read.view(manga, manga.getRead() + 1, 0);
								frame.getTabbed().setSelectedComponent(read);
							}
						});
						popup.add(item);
						
					}
				
			}
			popup.addSeparator();
			item = new MenuItem("Quit");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			popup.add(item);
			

		}
	}

	//	private Image getImage(boolean newAvailable) {
	//		if(newAvailable)
	//			
	//		return null;
	//	}
}
