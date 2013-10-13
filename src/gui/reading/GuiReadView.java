package gui.reading;

import gui.GuiFrame;
import gui.menu.GuiProgressBar;
import gui.threading.BackgroundExecutors;

import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JSlider;

import lombok.Getter;
import lombok.Setter;
import data.Manga;
import data.MangaLibrary;
import data.Options;

public @Getter @Setter class GuiReadView extends JPanel implements MouseWheelListener {

	public enum ReadingState {
		READING(""),
		LOADING("Loading images..."),
		RESIZING("Resizing images..."),
		NOTFOUND("No Images found."),
		ERROR("Please select a manga");

		String message;

		private ReadingState(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}

	private final GuiFrame frame;
	private final GuiRead gui;
	private final Options options;
	private final GuiProgressBar progress;
	private final BackgroundExecutors executors;
	private final JSlider slider;
	private final GuiReadViewOperations operations;

	private final MangaLibrary library;
	private Manga manga;
	private int chapter = 0;
	private int page = 0;

	private int scroll = 0;
//	private int scrollAmount = 100;
//	private int zoom = 100;

	private ReadingState state = ReadingState.ERROR;

	private final Map<Integer, List<BufferedImage>> mapImages;
	private final Map<Integer, List<File>> mapFiles;

	public GuiReadView(GuiFrame frame, GuiRead gui) {
		this.frame = frame;
		this.gui = gui;
		this.options = frame.getOptions();
		this.progress = gui.getProgress();
		this.executors = frame.getExecutors();
		this.slider = gui.getSlider();
		this.library = frame.getLibrary();
		
		setFocusable(true);
		addMouseWheelListener(this);

		mapImages = new HashMap<Integer, List<BufferedImage>>();
		mapFiles = new HashMap<Integer, List<File>>();

		operations = new GuiReadViewOperations(frame, gui, this);
		operations.initializeKeyShortcuts();

//		this.zoom = options.getReadingZoom();
//		this.scrollAmount = frame.getOptions().getReadingScroll();
		
	}

	public void view(Manga manga, int chapter, int page) {
		if (this.manga != null && this.manga.equals(manga) && mapImages.containsKey(chapter)) {
			this.chapter = chapter;
			this.page = page;
			return;
		} else if (this.manga != manga) {
			mapImages.clear();
			mapFiles.clear();
		}
		this.manga = manga;
		this.chapter = chapter;
		this.page = page;

		load();
	}
	
	public void load(){
		state = ReadingState.LOADING;
		operations.backgroundLoading(manga, chapter);
		operations.backgroundLoading(manga, chapter - 1);
		operations.backgroundLoading(manga, chapter + 1);
		repaint();
	}

	// Threading:
	// loading, resizing

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (state == ReadingState.READING)
			paintReading(g);
		else
			paintNonreading(g, state.getMessage());
	}

	private void paintNonreading(Graphics g, String message) {
		g.setFont(frame.getOptions().getSubtitelFont());
		g.drawString(message, (getWidth() - g.getFontMetrics().stringWidth(message)) / 2, (getHeight()) / 2);
	}

	private void paintReading(Graphics g) {

		if (!mapImages.containsKey(chapter) || page >= mapImages.get(chapter).size())
			return;

		List<BufferedImage> images = mapImages.get(chapter);

		int index = page;
		int remaining = scroll + getHeight();
		while (remaining > 0 && index < images.size()) {

			BufferedImage image = images.get(index);
			assert image != null;
			// if (image != null) {
			g.drawImage(image, (getWidth() - image.getWidth()) / 2, getHeight() - remaining, null);
			// } else {
			// g.drawString("Loading image...", getWidth() / 2 - 20, 0 -
			// remaining);
			// return;
			// }

			remaining -= image.getHeight();
			index++;
		}
		g.drawString((page + 1) + " / " + (images.size()), (getWidth()) / 2, (getHeight() - 1));
	}

	public void scroll(int scrollAmount) {
		this.scroll += scrollAmount;
		if (state != ReadingState.READING || !mapImages.containsKey(chapter))
			return;

		// M.print("scroll: " + scroll);
		int imageHeight = mapImages.get(chapter).get(page).getHeight();

		if (scroll < 0)
			while (scroll < 0 && page(-1)) {
				//M.print(" <" + scroll + " + " + imageHeight + " = " + (scroll + imageHeight));
				if (mapImages.containsKey(chapter) && page < mapImages.get(chapter).size())
					imageHeight = mapImages.get(chapter).get(page).getHeight();
				else
					scroll = 0;
				scroll += imageHeight;
			}
		else
			while (scroll > imageHeight && page(+1)) {
				//M.print(" >" + scroll + " - " + imageHeight + " = " + (scroll - imageHeight));
				scroll -= imageHeight;
				if (mapImages.containsKey(chapter) && page < mapImages.get(chapter).size())
					imageHeight = mapImages.get(chapter).get(page).getHeight();
				else
					scroll = 0;
			}

		// M.print(" scroll: " + scroll);
		repaint();
	}

	public boolean page(int diff) {
		if (manga == null || state != ReadingState.READING)
			return false;
		int newPage = page + diff;
		int oldChapter = chapter;
		// M.print("newPage: " + newPage);
		if (newPage < 0) {
			if (chapter(-1)) {
				if (mapImages.containsKey(chapter))
					page = mapImages.get(chapter).size() + newPage;
				else
					page = 0;
			} else {
				return false;
			}

		} else if (newPage >= mapImages.get(chapter).size()) {
			if (chapter(+1)) {
				if (mapImages.containsKey(chapter))
					page = newPage - mapImages.get(chapter - 1).size();
				else
					page = 0;
			} else {
				return false;
			}

		} else {
			page = newPage;
		}

		// M.print(" page: " + page);

		// if (Math.abs(diff) > 1)
		// scroll = 0;

		slider.setValue(page);
		slider.repaint();
		manga.setPage(page);
		repaint();
		return true;
	}

	public void previousPage() {
		page(-1);
	}

	public void nextPage() {
		page(+1);
	}

	public boolean chapter(int diff) {
		int newChapter = chapter + diff;

		if (manga == null || newChapter <= 0 || newChapter > manga.getDownloaded())
			return false;

		// M.print("newChapter: " + newChapter);

		if (mapImages.containsKey(newChapter)) {
			slider.setMaximum(mapImages.get(newChapter).size());
		} else {
			state = ReadingState.LOADING;
			operations.backgroundLoading(manga, newChapter);
		}

		this.chapter = newChapter;
		repaint();

		operations.defaultProgress();

		manga.setRead(chapter);
		library.save(executors);

		// preloading
		if (!mapImages.containsKey(newChapter-1))
			operations.backgroundLoading(manga, newChapter-1);
		if (!mapImages.containsKey(newChapter+1))
			operations.backgroundLoading(manga, newChapter+1);

		return true;
	}

	public void previousChapter() {
		chapter(-1);
	}

	public void nextChapter() {
		chapter(+1);
	}

	public void setZoom(int zoom) {
		if(this.options.getReadingZoom() == zoom)
			return;
		state = ReadingState.RESIZING;
		operations.backgroundZooming(zoom);
//		this.zoom = zoom;
		options.setReadingZoom(zoom);
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
			scroll(-options.getReadingScroll());
		} else {
			scroll(options.getReadingScroll());
		}
	}

}
