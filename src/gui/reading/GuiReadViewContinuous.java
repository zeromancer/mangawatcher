package gui.reading;

import gui.menu.GuiProgressBar;
import gui.threading.BackgroundExecutors;

import java.awt.Color;
import java.awt.Font;
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

public @Getter @Setter class GuiReadViewContinuous extends JPanel implements KeyListener, MouseWheelListener {

	private static final long serialVersionUID = 5195762488056129448L;

	private FilenameFilter filterImage;
	private FilenameFilter filterDirectory;

	private MangaLibrary library;
	private Manga manga;
	private int chapter = 0;
	private int page = 0;

	private int scroll = 0;
	private int scrollAmount = 100;
	private int zoom = 100;

	private final Map<Integer, Integer> pages;
	private final Map<String, File> map;
	private final List<String> list;

	private final List<BufferedImage> loaded;
	private final BufferedImage underflow;
	private final BufferedImage loading;
	private final BufferedImage overflow;

	private int index = 0;
	private final int middle = 3;

	private GuiRead gui;
	private JSlider slider;
	private GuiProgressBar progress;
	// private JProgressBar progress;
	// private JLabel info;

	private BackgroundExecutors executors;

	public GuiReadViewContinuous(GuiRead gui) {
		this.gui = gui;
		this.progress = gui.getProgress();
		// this.info = gui.getInfo();

		this.executors = new BackgroundExecutors();

		setFocusable(true);
		addMouseWheelListener(this);
		addKeyListener(this);

		filterImage = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		};
		filterDirectory = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		};

		pages = new HashMap<>();
		map = new HashMap<>();
		list = new ArrayList<>();
		loaded = new ArrayList<>();

		underflow = getInitialImage("No Previous images available");
		loading = getInitialImage("Loading image...");
		overflow = getInitialImage("No Next images available");
	}

	public BufferedImage getInitialImage(String text) {
		BufferedImage image = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		int border = 5;
		g.setColor(getBackground());
		g.fillRect(border, border, image.getWidth() - 2 * border, image.getHeight() - 2 * border);
		g.setFont(new Font("Serif", Font.BOLD, 15));
		g.drawString("Loading image...", image.getWidth() / 2, image.getHeight() / 2);
		g.dispose();
		return image;
	}

	public void view(MangaLibrary library, Manga manga, int chapter, int page) {
		this.manga = manga;
		this.chapter = chapter;
		this.page = page;

		loaded.clear();
		for (int i = 0; i < 7; i++)
			loaded.add(null);

		final String path = manga.getMangaDirectory(library);
		final File folder = new File(path);

		// addFilesRecursive(folder.listFiles(), map);

		// List<BufferedImage> images = new ArrayList<BufferedImage>();
		// List<File> f = new ArrayList<File>();
		String chapterName = null;
		// List<File> chapters = new
		// ArrayList<File>(Arrays.asList(folder.listFiles(filterDirectory)));
		// Collections.sort(chapter);

		File[] chapters = folder.listFiles(filterDirectory);
		Arrays.sort(chapters);

		for (File file : chapters) {

			// M.print("file: " + file.getName() + " , isDir: " +
			// file.isDirectory());
			int directory = Integer.parseInt(file.getName());

			File[] images = file.listFiles(filterImage);
			Arrays.sort(images);

			pages.put(directory, images.length);

			for (int j = 0; j < images.length; j++) {
				File image = images[j];
				String name = image.getName();
				map.put(name, image);
				list.add(name);
				if (directory == chapter && j == page)
					chapterName = name;
			}

		}
		assert chapterName != null;
		M.print("name: " + chapterName);
		M.print("pages: " + pages.toString());
		M.print("size: " + pages.size());

		index = list.indexOf(chapterName);
		// load(index, middle);

		// System.exit(0);
	}


	public void load(final int sourceIndex, final int destinationIndex) {
		final int indexOld = this.index;
		loaded.set(destinationIndex, loading);

		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				final File file = map.get(list.get(sourceIndex));
				final BufferedImage image = load(file);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						int diff = GuiReadViewContinuous.this.index - indexOld;
						int newDestination = destinationIndex - diff;
						if (newDestination >= 0 && newDestination < loaded.size()) {
							loaded.set(newDestination, image);
							M.print("loaded " + list.get(sourceIndex) + " -> " + (destinationIndex - diff));
							repaint();
						}
					}
				});
			}

		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Collections.ro
		//
		int worked = 0;
		int height = 0;
		int i = middle;
		for (i = middle; worked < getHeight() + scroll && i < loaded.size(); i++) {

			// M.print("i: " + i + " , worked: " + worked + " < " + (getHeight()
			// + scroll));

			BufferedImage image = loaded.get(i);

			// drawImage(g, image, -worked + pageScroll);

			if (image == null) {
				int newIndex = index + i - middle;
				if (newIndex >= 0 && newIndex < list.size()) {
					load(newIndex, i);
					drawImage(g, loading, scroll - worked);
					M.print(index + "[" + i + "]" + " image: " + "loading - " + list.get(index + i - middle));
				} else if (newIndex < 0) {
					drawImage(g, underflow, scroll - worked);
					M.print(index + "[" + i + "]" + " image: " + "underflow");
				} else if (newIndex >= list.size()) {
					drawImage(g, overflow, scroll - worked);
					M.print(index + "[" + i + "]" + " image: " + "overflow");
				}
				height = 9999;
			} else {
				drawImage(g, image, -worked + scroll);
				height = image.getHeight();
				M.print(index + "[" + i + "]" + " image: " + list.get(index + i - middle) + " , height: " + height);
			}

			// M.print("   image: " + list.get(index + i - middle) +
			// " , height: " + height);
			worked += height;
		}
		for(;i<loaded.size();i++)
			if(loaded.get(i) == null){
				int newIndex = index + i - middle;
				if (newIndex >= 0 && newIndex < list.size())
					load(newIndex, i);
			}
		M.print("");
	}

	private void drawImage(Graphics g, BufferedImage image, int offset) {
		if (image != null) {
			g.drawImage(image, (getWidth() - image.getWidth()) / 2, 0 - offset, null);
			return;
		}
		g.drawString("Loading image...", getWidth() / 2 - 20, 0 - offset);
	}

	private int getImageHeight(int index) {
		BufferedImage image = loaded.get(index);
		if (image == null)
			return 9999;
		else
			return image.getHeight();

	}

	public void scroll(int scroll) {
		this.scroll += scroll;

		// int imageHeight = image.getHeight();
		// if (imageScroll < 0 && imageIndex > 0) {
		// // previous page
		// imageIndex--;
		// slider.setValue(imageIndex);
		// imageScroll += imageHeight;
		// }

		BufferedImage image = loaded.get(middle);
		
		if (this.scroll < 0 && index >= 0) {
			this.scroll += image.getHeight();
			page(-1);
		} else if (this.scroll > getImageHeight(middle) && index <= list.size()) {
			this.scroll -= image.getHeight();
			page(+1);
		}
		repaint();
	}

	public int getBoundedInt(int min, int value, int max) {
		return Math.max(min, Math.min(value, max));
	}

	public void page(int paging) {
		page += paging;

		if (page < 0 && chapter > 0 && pages.containsKey(chapter - 1)) {
			page = pages.get(chapter) - page;
			chapter--;
			slider.setMaximum(pages.get(chapter));
		} else if (page > pages.get(chapter) && pages.containsKey(chapter + 1)) {
			page = page - pages.get(chapter);
			chapter++;
			slider.setMaximum(pages.get(chapter));
		} else if (page < 0 || page > pages.get(chapter)) {
			page = getBoundedInt(0, page, pages.get(chapter));
			paging = 0;
		}

		if (Math.abs(paging) > 1)
			scroll = 0;
		slider.setValue(page);
		index += paging;

		// rotation:
		// for (int i = 0; i < paging; i++) {
		// for (int j = 0; j < loaded.size(); j++) {
		//
		// }
		// }
		
		 
		if (paging != 0)
		for (int i = 0; i < loaded.size(); i++) {
			int index = i + paging;
			int loadedIndex = index + i - middle; 
			if( 0<=index && index<loaded.size())
				loaded.set(i, loaded.get(index));
			// else if (loadedIndex < 0)
			// loaded.set(i, underflow);
			// else if (loadedIndex >= list.size())
			// loaded.set(i, overflow);
			else
				loaded.set(i, null);
		}

		repaint();
	}

	public void previousChapter() {
		int diff = -page - 1;
		M.print("previousChapter, diff: "+diff);
		page(diff);
	}

	public void previousPage() {
		page(-1);
	}

	public void nextPage() {
		page(+1);
	}

	public void nextChapter() {
		int diff = pages.get(chapter) - page;
		M.print("nextChapter, diff: "+diff);
		page(diff);
	}


	public void setZoom(int zoom) {
		M.print("zoom: " + this.zoom + " -> " + zoom);
		if (this.zoom == zoom)
			return;
		List<BufferedImage> images = loaded;
		for (int i = 0; i < images.size(); i++) {
			BufferedImage image = images.get(i);
			if (image == null)
				continue;
			BufferedImage newer = scaleImage(image, zoom);
			// M.print(" "+image.getWidth()+ " -> "+newer.getWidth());
			images.set(i, newer);
		}
		this.zoom = zoom;
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
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
	public void keyReleased(KeyEvent e) {}

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
	public static BufferedImage load(File file) {
		// try {
		// // Thread.sleep((int) (Math.random() * 1000));
		// Thread.sleep(3000);
		// } catch (InterruptedException e1) {
		// }
		try {
			// M.print("\tloading image: " + file.getName());
			return ImageIO.read(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
