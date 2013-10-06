package gui.read;

import gui.GuiFrame;
import gui.threading.BackgroundExecutors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import logic.LibraryManager;
import misc.M;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public class GuiRead5 extends JScrollPane {

	private static final long serialVersionUID = -6774293690900240492L;

//	GuiFrame frame;

	FilenameFilter filter;
//	List<File> files;
	JPanel panel;

	int chapter;

	BackgroundExecutors executors;

	public GuiRead5() {
//		this.frame = frame;
		executors = new BackgroundExecutors();

		filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		};

		panel = new JPanel();
		panel.setBackground(new Color(33, 33, 33));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		final JScrollPane scroll = this;
		scroll.getVerticalScrollBar().setUnitIncrement(70);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent ae) {
				int extent = scroll.getVerticalScrollBar().getModel().getExtent();
				System.out.println("\t" + (scroll.getVerticalScrollBar().getValue() + extent) + " / " + scroll.getVerticalScrollBar().getMaximum());
			}
		});

		// TODO KeyListener: Space -> scroll
		// Toolbar icons: Zoom in/out, Scrollstep +/-, next/previous Page
	}

	public void loadMangaChapter(MangaLibrary library, Manga manga, int chapter, int page) {
		String path = manga.getMangaDirectory(library, chapter);
		File folder = new File(path);
		M.print("path: "+folder.getAbsolutePath());
		
		List<File> files = new ArrayList<File>();
		files.addAll(Arrays.asList(folder.listFiles(filter)));
		Collections.sort(files);
		M.print("size: "+files.size());
		// panel.setPreferredSize(new Dimension(500, 1000));

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			// ImageIcon icon = new ImageIcon(ImageIO.read(file));
			SingleImage image = new SingleImage();
//			image.load(file);
			// frame.getExecuters().loadImageFromFile(image.getImage(), file);
			//executors.loadImageFromFile(image.getImage(), file);
			executors.loadImage(image, file);
			panel.add(image);

		}
//		executors.
		this.setViewportView(panel);
	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		MangaLibrary library = LibraryManager.loadLibrary("config");
		// library.re
		// LibraryManager.saveLibrary(configDirectory, library);
		Manga manga = library.getCollection(MangaCollection.WATCHING).get(1);

		GuiRead5 gui = new GuiRead5();
		gui.loadMangaChapter(library, manga, 1, 1);
		frame.add(gui);

		// Image icon = new Image();
		// icon.loadImage(files.get(0));
		// frame.add(new JScrollPane(icon));

		// frame.pack();
		frame.setSize(500, 500);
		frame.setVisible(true);
		frame.validate();
	}
}