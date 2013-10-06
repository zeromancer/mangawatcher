package gui.reading;

import gui.menu.GuiProgressBar;
import gui.threading.BackgroundExecutors;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;
import misc.M;
import data.Manga;
import data.MangaLibrary;

 public @Getter @Setter class GuiReadView extends JPanel implements MouseWheelListener {

	public enum ReadingState {
		READING(""),
		LOADING("Loading images..."),
		RESIZING("Resizing images..."),
		ERROR("Error...");

		String message;

		private ReadingState(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}

	private static final long serialVersionUID = 5195762488056129448L;

	private MangaLibrary library;
	private Manga manga;
	private int chapter = 0;
	private int page = 0;

	private int scroll = 0;
	private int scrollAmount = 100;
	private int zoom = 100;

	private ReadingState state = ReadingState.ERROR;

	final private FilenameFilter filter;
	final private Map<Integer, List<BufferedImage>> mapImages;
	final private Map<Integer, List<File>> mapFiles;

	final private GuiRead gui;
	final private JSlider slider;
	final private GuiProgressBar progress;

	final private BackgroundExecutors executors;

	public GuiReadView(GuiRead gui, GuiProgressBar progress, JSlider slider, BackgroundExecutors executors) {
		this.gui = gui;
		this.progress = progress;
		this.slider = slider;
		this.executors = new BackgroundExecutors();

		setFocusable(true);
		addMouseWheelListener(this);
		// addKeyListener(this);

		filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		};

		mapImages = new HashMap<Integer, List<BufferedImage>>();
		mapFiles = new HashMap<Integer, List<File>>();

		initializeKeyShortcuts();

	}

	public void view(MangaLibrary library, Manga manga, int chapter, int page) {
		if (this.manga != null && this.manga.equals(manga) && mapImages.containsKey(chapter)) {
			this.chapter = chapter;
			this.page = page;
			return;
		} else if (this.manga != manga) {
			mapImages.clear();
			mapFiles.clear();
		}
		this.library = library;
		this.manga = manga;
		this.chapter = chapter;
		this.page = page;

		state = ReadingState.LOADING;

		backgroundLoading(manga, chapter);
		backgroundLoading(manga, chapter - 1);
		backgroundLoading(manga, chapter + 1);
	}

	public void backgroundLoading(final Manga manga, final int chapter) {
		Runnable run = new Runnable() {
			@Override
			public void run() {
				final String path = manga.getMangaDirectory(library, chapter);
				final File folder = new File(path);

				final List<BufferedImage> images = new ArrayList<BufferedImage>();
				final List<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles(filter)));

				Collections.sort(files);

				for (int i = 0; i < files.size(); i++) {
					final File file = files.get(i);
					// M.print(" loading file: " + file.getName());
					images.add(loadImage(file));
					progress((i + 1) * 100 / files.size(), "Loading image: " + file.getName());
				}

				backgroundLoadingFinish(chapter, images, files);
			}
		};
		executors.runOnFileThread(run);
	}

	private void backgroundLoadingFinish(final int chapter, final List<BufferedImage> images, final List<File> files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// progress.setValue(100);
				// progress.setText("Loading Complete");
				progress.setValue((GuiReadView.this.chapter) * 100 / manga.getReleased());
				progress.setText("Reading " + GuiReadView.this.manga.getName() + " Chapter " + GuiReadView.this.chapter);
				progress.repaint();

				mapFiles.put(chapter, files);
				mapImages.put(chapter, images);

				if (state == ReadingState.LOADING) {
					state = ReadingState.READING;
					slider.setMinimum(0);
					slider.setValue(manga.getPage());
					slider.setMaximum(files.size() - 1);
				}
				repaint();
			}
		});
	}

	private void progress(final int percent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progress.setValue(percent);
				progress.setText(text);
				repaint();
			}
		});
	}

	// Threading:
	// loading, resizing

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (state == ReadingState.READING)
			paintReading(g);
		else
			paintMessage(g, state.getMessage());
	}

	private void paintMessage(Graphics g, String message) {
		g.drawString(message, (getWidth()) / 2, (getHeight()) / 2);
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
		if (state != ReadingState.READING)
			return;

		M.print("scroll: " + scroll);
		int imageHeight = mapImages.get(chapter).get(page).getHeight();

		if (scroll < 0)
			while (scroll < 0 && page(-1)) {
				M.print(" <" + scroll + " + " + imageHeight + " = " + (scroll + imageHeight));
				scroll += imageHeight;
				if (mapImages.containsKey(chapter) && page < mapImages.get(chapter).size())
					imageHeight = mapImages.get(chapter).get(page).getHeight();
				else
					scroll = 0;
			}
		else
			while (scroll > imageHeight && page(+1)) {
				M.print(" >" + scroll + " - " + imageHeight + " = " + (scroll - imageHeight));
				scroll -= imageHeight;
				if (mapImages.containsKey(chapter) && page < mapImages.get(chapter).size())
					imageHeight = mapImages.get(chapter).get(page).getHeight();
				else
					scroll = 0;
			}

		M.print(" scroll: " + scroll);
		repaint();
	}

	public boolean page(int diff) {
		int newPage = page + diff;
		int oldChapter = chapter;
		M.print("newPage: " + newPage);
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

		M.print(" page: " + page);

		// if (Math.abs(diff) > 1)
		// scroll = 0;

		slider.setValue(page);
		slider.repaint();
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

		if (newChapter <= 0 || newChapter > manga.getDownloaded())
			return false;

		M.print("newChapter: " + newChapter);

		if (mapImages.containsKey(newChapter)) {
			slider.setMaximum(mapImages.get(newChapter).size());
		} else {
			state = ReadingState.LOADING;
			backgroundLoading(manga, newChapter);
		}


		this.chapter = newChapter;
		repaint();

		// preloading
		newChapter++;
		if (newChapter <= 0 || newChapter > manga.getDownloaded())
			return true;
		if (!mapImages.containsKey(newChapter))
			backgroundLoading(manga, newChapter);

		return true;
	}

	public void previousChapter() {
		M.print("previousChapter");
		chapter(-1);
	}

	public void nextChapter() {
		M.print("nextChapter");
		chapter(+1);
	}

	public void setZoom(int zoom) {
		M.print("zoom: " + this.zoom + " -> " + zoom);
		if (this.zoom == zoom)
			return;
		state = ReadingState.RESIZING;
		repaint();
		List<BufferedImage> images = mapImages.get(chapter);
		for (int i = 0; i < images.size(); i++) {
			BufferedImage image = images.get(i);
			if (image == null)
				continue;
			BufferedImage newer = scaleImage(image, zoom);
			// M.print(" "+image.getWidth()+ " -> "+newer.getWidth());
			images.set(i, newer);
		}
		this.zoom = zoom;
		state = ReadingState.READING;
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
			scroll(-scrollAmount);
		} else {
			scroll(scrollAmount);
		}
	}

	public BufferedImage scaleImage(BufferedImage image, int zoom) {
		int width = (int) ((float) image.getWidth() * zoom / this.zoom);
		int height = (int) ((float) image.getHeight() * zoom / this.zoom);
		// M.print("  "+image.getWidth()+ " -> "+width);
		Image img = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage newer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		newer.getGraphics().drawImage(img, 0, 0, null);
		return newer;
	}

	public static BufferedImage loadImage(File file) {
		try {
			Thread.sleep((int) (Math.random() * 100));
		} catch (InterruptedException e1) {
		}
		try {
			// M.print("\tloading image: " + file.getName());
			return ImageIO.read(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void initializeKeyShortcuts() {

		ActionMap actionMap = getActionMap();
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		AbstractAction scrollUp = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scroll(-scrollAmount);
			}
		};

		AbstractAction scrollDown = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scroll(scrollAmount);
			}
		};

		AbstractAction heightUp = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scroll(-getHeight());
			}
		};

		AbstractAction heightDown = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scroll(getHeight());
			}
		};

		AbstractAction pageDown = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextPage();
			}
		};

		Map<Integer, AbstractAction> actions = new HashMap<>();
		actions.put(KeyEvent.VK_W, scrollUp);
		actions.put(KeyEvent.VK_S, scrollDown);
		actions.put(KeyEvent.VK_PAGE_UP, heightUp);
		actions.put(KeyEvent.VK_PAGE_DOWN, heightDown);
		actions.put(KeyEvent.VK_SPACE, pageDown);

		for (Entry<Integer, AbstractAction> entry : actions.entrySet()) {
			Integer key = entry.getKey();
			AbstractAction value = entry.getValue();
			inputMap.put(KeyStroke.getKeyStroke(key, 0), "" + key);
			actionMap.put("" + key, value);
		}
	}

	@SuppressWarnings("resource")
	public static void fileCopy(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			// inChannel.transferTo(0, inChannel.size(), outChannel); //
			// original -- apparently has trouble copying large files on Windows

			// magic number for Windows, 64Mb - 32Kb)
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = 0;
			while (position < size) {
				position += inChannel.transferTo(position, maxCount, outChannel);
			}
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}

}
