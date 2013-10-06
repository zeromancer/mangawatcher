package gui.reading;

import gui.menu.GuiProgressBar;
import gui.threading.BackgroundExecutors;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import lombok.Getter;
import lombok.Setter;
import misc.M;
import data.Manga;
import data.MangaLibrary;

public @Getter @Setter class GuiReadViewBackup extends JPanel implements KeyListener, MouseWheelListener {

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

	private ReadingState state = ReadingState.ERROR;

	private MangaLibrary library;
	private Manga manga;
	private int chapter = 0;
	private int page = 0;

	private int scroll = 0;
	private int scrollAmount = 100;
	private int zoom = 100;

	final private FilenameFilter filter;
	final private Map<Integer, List<BufferedImage>> mapImages;
	final private Map<Integer, List<File>> mapFiles;

	final private GuiRead gui;
	final private JSlider slider;
	final private GuiProgressBar progress;

	final private BackgroundExecutors executors;

	public GuiReadViewBackup(GuiRead gui, GuiProgressBar progress, JSlider slider, BackgroundExecutors executors) {
		this.gui = gui;
		this.progress = progress;
		this.slider = slider;
		this.executors = new BackgroundExecutors();

		setFocusable(true);
		addMouseWheelListener(this);
		addKeyListener(this);

		filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		};

		mapImages = new HashMap<Integer, List<BufferedImage>>();
		mapFiles = new HashMap<Integer, List<File>>();
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
		this.manga = manga;
		this.chapter = chapter;
		this.page = page;

		state = ReadingState.LOADING;

		backgroundLoading(executors, library, manga, chapter);
	}

	public void backgroundLoading(BackgroundExecutors executers, final MangaLibrary library, final Manga manga, final int chapter) {
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

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progress.setValue(100);
						progress.setText("Loading Complete");
						progress.repaint();
						mapFiles.put(chapter, files);
						mapImages.put(chapter, images);
						state = ReadingState.READING;
						slider.setMinimum(0);
						slider.setValue(manga.getPage());
						slider.setMaximum(files.size() - 1);
						repaint();
					}
				});
			}
		};
		executers.runOnFileThread(run);
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
		// M.print("index: " + imageIndex);
		if (!mapImages.containsKey(chapter))
			return;
		List<BufferedImage> images = mapImages.get(chapter);
		BufferedImage image = images.get(page);
		int imageHeight = image.getHeight();

		if (scroll < 0 && page <= 0) {
			// previous chapter

		} else if (scroll < 0 && page > 0) {
			// previous page
			page--;
			slider.setValue(page);
			scroll += imageHeight;
		} else if (imageHeight < scroll && page < images.size() - 1) {
			// next page
			page++;
			slider.setValue(page);
			scroll -= imageHeight;
		} else if (imageHeight < scroll && page >= images.size() - 1) {
			// next chapter

		}

		image = images.get(page);
		drawImage(g, image, scroll);
		// drawImage(g, images.get(imageIndex+1), imageScroll - imageHeight);

		int index = page + 1;
		int screenHeight = scroll + getHeight() - imageHeight;
		// M.print(" i: " + imageIndex + " , scroll: " + imageScroll +
		// " , imgHeight: " + imageHeight);
		// M.print(" getHeight(): " + getHeight() + " , sreenHeight: " +
		// screenHeight);

		while (screenHeight > 0 && index < images.size()) {
			image = images.get(index);
			drawImage(g, image, +screenHeight - getHeight());
			screenHeight -= image.getHeight();
			index++;
		}

		g.drawString((page + 1) + " / " + (images.size()), (getWidth()) / 2, (getHeight() - 1));
	}


	private void drawImage(Graphics g, BufferedImage image, int offset) {
		if (image != null) {
			g.drawImage(image, (getWidth() - image.getWidth()) / 2, 0 - offset, null);
			return;
		}
		g.drawString("Loading image...", getWidth() / 2 - 20, 0 - offset);
	}


	public void scroll(int scroll) {
		this.scroll += scroll;

		List<BufferedImage> images = mapImages.get(chapter);
		BufferedImage image = images.get(page);
		int imageHeight = image.getHeight();
		if (scroll < 0 && page <= 0) {
			// previous chapter

		} else if (scroll < 0 && page > 0) {
			// previous page
			page--;
			slider.setValue(page);
			scroll += imageHeight;
		} else if (imageHeight < scroll && page < images.size() - 1) {
			// next page
			page++;
			slider.setValue(page);
			scroll -= imageHeight;
		} else if (imageHeight < scroll && page >= images.size() - 1) {
			// next chapter

		}

		repaint();
	}

	public void page(int diff) {
		page += diff;
		if (page < 0)
			page = 0;
		if (page >= mapImages.get(chapter).size())
			page = mapImages.get(chapter).size() - 1;
		if (Math.abs(diff) > 1)
			scroll = 0;
		slider.setValue(page);
		// TODO: update page+- buttons
		repaint();
	}

	public void previousPage() {
		page(-1);
	}

	public void nextPage() {
		page(+1);
	}

	public boolean chapter(int diff) {
		int newChapter = chapter + diff;
		if (newChapter > 0 && newChapter < manga.getDownloaded()) {
			state = ReadingState.LOADING;
			backgroundLoading(executors, library, manga, newChapter);
			return true;
		}
		return false;
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
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		// if(e.getKeyChar() == '')
		// M.print("key: "+e.getKeyChar());

		if (e.getKeyChar() == 'q' || e.getKeyCode() == KeyEvent.VK_UP)
			scroll(-scrollAmount);
		else if (e.getKeyChar() == 'a' || e.getKeyCode() == KeyEvent.VK_DOWN)
			scroll(scrollAmount);
		else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP)
			scroll(-getHeight());
		else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
			scroll(getHeight());
		else if (e.getKeyChar() == 'w' || e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			previousPage();
		else if (e.getKeyChar() == 's' || e.getKeyCode() == KeyEvent.VK_SPACE)
			nextPage();
		else if (e.getKeyChar() == 'e')
			previousChapter();
		else if (e.getKeyChar() == 'd')
			nextChapter();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

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

}
