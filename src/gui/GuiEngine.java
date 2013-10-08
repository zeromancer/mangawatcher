package gui;

import gui.threading.BackgroundExecutors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import lombok.Getter;
import misc.M;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public @Getter class GuiEngine {

	public @Getter enum Icons {
		// Categories
		ADD("plus2-26.png"),
		MANGA("dossier-26.png"),
		READING("literature-26.png"),
		WATCHING("invisible-26.png"),
		PLANNING("watch-26.png"),
		DROPPED("law-26.png"),
		COMPLETED("ok-26.png"),
		DOWNLOADING("down-26.png"),
		OPTIONS("settings2-26.png"),
		ABOUT("about-26.png"),

		// Reading

		;
		private String path;

		private Icons(String path) {
			this.path = path;
		}

	}

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final BackgroundExecutors executors;

	private final Map<Manga, BufferedImage> covers;
	private final Map<Icons, BufferedImage> icons;

	public GuiEngine(GuiFrame frame) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.executors = frame.getExecutors();

		covers = new HashMap<Manga, BufferedImage>();
		icons = new HashMap<>();
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
			covers.put(manga, image);
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}

	public void load(Icons icon) {
		final String path = "icons/categories/" + icon.getPath();
		try {
			BufferedImage image = ImageIO.read(new File(path));
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
					covers.put(manga, image);
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
