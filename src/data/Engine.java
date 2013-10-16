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

package data;

import gui.GuiFrame;
import gui.threading.BackgroundExecutors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import lombok.Getter;
import misc.M;
import data.Manga.MangaCollection;

public @Getter class Engine {

	public @Getter enum Icons {
		// Categories
		ADD("categories/plus2-26.png"),
		MANGA("categories/dossier-26.png"),
		READING("categories/literature-26.png"),
		WATCHING("categories/invisible-26.png"),
		PLANNING("categories/watch-26.png"),
		DROPPED("categories/law-26.png"),
		COMPLETED("categories/ok-26.png"),
		DOWNLOADING("categories/down-26.png"),
		OPTIONS("categories/settings2-26.png"),
		ABOUT("categories/about-26.png"),

		// Reading
		REFRESH("reading/available_updates-26.png"),
		LEFT("reading/arrow-left-26.png"),
		RIGHT("reading/arrow-26.png"),
		DOUBLELEFT("reading/rewind-26.png"),
		DOUBLERIGHT("reading/fast_forward-26.png"),
		ZOOM("reading/search-26.png"),
		SCROLL("reading/line_width-26.png"),
		
		// Logo
//		LOGO("MangaWatcher.png"),
		LOGO("logo/MangaWatcher.png"),
		LOGOEMPTY("logo/MangaWatcherEmpty.png"),

		;
		private String path;

		private Icons(String path) {
			this.path = path;
		}

	}

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final BackgroundExecutors executors;

	private final Map<String, BufferedImage> covers;
	private final Map<Icons, BufferedImage> icons;

	public Engine(GuiFrame frame) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.executors = frame.getExecutors();

		covers = new HashMap<String, BufferedImage>();
		icons = new HashMap<>();
	}

	public void setLookAndFeel() {

		try {
			//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//			 UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//			 UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			//			 UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//			 UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

			String os = (System.getProperty("os.name")).toUpperCase();
			if (os.contains("LINUX"))
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			else if (os.contains("WIN"))
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			else
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
			M.exception(e);
		}

	}

	public BufferedImage getCover(Manga manga) {
		assert covers.containsKey(manga.getName()) : covers.toString();
		return covers.get(manga.getName());
	}

	public BufferedImage getIcon(Icons icon) {
		assert icons.containsKey(icon);
		return icons.get(icon);
	}

	public void loadAll() {
		for (MangaCollection collection : MangaCollection.values())
			for (Manga manga : library.getCollection(collection))
				load(manga);
		for (Icons icon : Icons.values())
			load(icon);
	}

	public void load(Manga manga) {
		final String path = manga.getMangaImagePath(library);
		try {
			BufferedImage image = ImageIO.read(new File(path));
			assert image != null;
			covers.put(manga.getName(), image);
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}

	public void load(Icons icon) {
		URL url = null;
		if(url == null)
			url = ClassLoader.getSystemResource(icon.getPath());
		if(url == null)
			url = ClassLoader.getSystemResource("../"+icon.getPath());
		if(url == null)
			url = ClassLoader.getSystemResource("../"+"icons/"+icon.getPath());
		try {
			BufferedImage image = ImageIO.read(url);
//			BufferedImage image = ImageIO.read(new File(path));
			icons.put(icon, image);
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}

	public void loadAllBackground() {
		for (MangaCollection collection : MangaCollection.values())
			for (Manga manga : library.getCollection(collection))
				loadBackground(manga);
		for (Icons icon : Icons.values())
			loadBackground(icon);
	}

	public void loadBackground(final Manga manga) {
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				final String path = manga.getMangaDirectory(library) + "/" + manga.getName() + ".jpg";

				try {
					BufferedImage image = ImageIO.read(new File(path));
					covers.put(manga.getName(), image);
				} catch (IOException e) {
					e.printStackTrace();
					M.print(e.getMessage());
				}

			}
		});
	}

	public void loadBackground(final Icons icon) {
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				final String path = "icons/categories/" + icon.getPath();

				try {
					BufferedImage image = ImageIO.read(new File(path));
					icons.put(icon, image);
				} catch (IOException e) {
					e.printStackTrace();
					M.print(e.getMessage());
				}

			}
		});
	}

	//	private void loadFinish(final Manga manga){
	//		
	//	}

}
