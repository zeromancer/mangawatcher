package gui.read;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Data;
import misc.M;
import net.miginfocom.swing.MigLayout;
import data.Manga;
import data.MangaLibrary;

public @Data
class GuiRead5Own extends JPanel implements KeyListener, MouseWheelListener {

	public static class ToolBar extends JPanel {

		private static final long serialVersionUID = 5673903268744027432L;

		JToggleButton showZoom;
		JButton save;
		JButton previousChapter;
		JButton previousPage;
		JButton nextChapter;
		JButton nextPage;
		private JSlider zoom;
		private GuiRead5Own gui;

		public ToolBar(final GuiRead5Own gui) {
			this.gui = gui;
			// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setLayout(new MigLayout());

			save = new JButton("Save");
			showZoom = new JToggleButton("Show Zoom");
			previousChapter = new JButton("Chapter--");
			previousPage = new JButton("Page--");
			nextPage = new JButton("Page++");
			nextChapter = new JButton("Chapter++");
			zoom = new JSlider(10, 400, 100);
			zoom.setPaintTicks(true);
			zoom.setMinorTickSpacing(10);
			// zoom.setMajorTickSpacing(100);
			zoom.setSnapToTicks(true);

			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(new Integer(0), new JLabel("10%"));
			labelTable.put(new Integer(100), new JLabel("100%"));
			labelTable.put(new Integer(200), new JLabel("200%"));
			labelTable.put(new Integer(300), new JLabel("300%"));
			labelTable.put(new Integer(400), new JLabel("400%"));
			zoom.setLabelTable(labelTable);
			zoom.setPaintLabels(true);

			add(save);
			add(showZoom);
			add(previousChapter);
			add(previousPage);
			add(nextPage);
			add(nextChapter, "wrap");
			final String zoomAdd = "growx, span 6";
			add(zoom, zoomAdd);

			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					M.print("TODO: manga save");
				}
			});

			// TODO: disable next/previous Page/Chapter, then no next/previous
			// Chapter available

			showZoom.setSelected(true);
			showZoom.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AbstractButton abstractButton = (AbstractButton) e.getSource();
					boolean selected = abstractButton.getModel().isSelected();
					System.out.println("Action - selected=" + selected + "\n");
					if (selected) {
						add(zoom, zoomAdd);
						// setSize(getPreferredSize());
					} else {
						remove(zoom);
						// setSize(getWidth(), getPreferredSize());
						// setSize(getPreferredSize());
					}
					repaint();
				}
			});

			previousChapter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gui.previousChapter();
				}
			});
			previousPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gui.previousPage();
				}
			});
			nextPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gui.nextPage();
				}
			});
			nextChapter.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gui.nextChapter();
				}
			});

			// TODO: load default zoom value from file
			// TODO: Disable zoom while loading images
			zoom.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (zoom.getValueIsAdjusting())
						return;
					int value = zoom.getValue();
					gui.setZoom(value);
				}
			});

		}

		public void previousChapter(boolean enabled) {
			previousChapter.setEnabled(enabled);
		}

		public void nextChapter(boolean enabled) {
			nextChapter.setEnabled(enabled);
		}

	}

	public static class GuiReadPageSlider extends JSlider implements ChangeListener {

		private static final long serialVersionUID = -7905617057335217624L;

		private GuiRead5Own gui;

		public GuiReadPageSlider(GuiRead5Own gui) {
			this.gui = gui;
			this.setPaintTicks(true);
			this.setMinorTickSpacing(1);
			this.setSnapToTicks(true);
			this.setOrientation(SwingConstants.VERTICAL);
			this.setInverted(true);
			this.addChangeListener(this);
		}

		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			int value = (int) source.getValue();
			int index = gui.getImageIndex();

			if (source.getValueIsAdjusting())
				return;

			// M.print("slider: " + value + ", index: " + index);
			if (value != index) {
				gui.setImageIndex(value);
				gui.setImageScroll(0);
				gui.repaint();
				// gui.getManga().setPage(value);
			}
		}
	}

	private static final long serialVersionUID = 5195762488056129448L;
	FilenameFilter filter;

	MangaLibrary library;
	Manga manga;
	int chapter = 0;

	int zoom = 100;
	int scroll = 0;
	int imageIndex = 0;
	int imageScroll = 0;

	// current chapter
	List<File> files = new ArrayList<File>();
	List<BufferedImage> images = new ArrayList<>();

	Map<Integer,List<BufferedImage>> map;

	ToolBar toolbar;
	public GuiReadPageSlider slider;

	public void view(MangaLibrary library, Manga manga, int chapter, int page) {
		if (this.manga.equals(manga) && map.containsKey(chapter)) {
			this.chapter = chapter;
			this.imageIndex = page;
			return;
		} else if (this.manga != manga) {
			map.clear();
		}
		this.manga = manga;
		this.chapter = chapter;
		this.imageIndex = page;

		map.put(chapter, new ArrayList<BufferedImage>());
		List<BufferedImage> images = map.get(chapter);

		String path = manga.getMangaDirectory(library, chapter);
		File folder = new File(path);
		List<File> files = new ArrayList<File>(Arrays.asList(folder.listFiles(filter)));
		Collections.sort(files);

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			images.add(load(file));
		}
		slider.setValue(manga.getPage());
		slider.setMaximum(files.size());
	}

	public GuiRead5Own() {

		map = new HashMap<Integer, List<BufferedImage>>();




		filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		};

		toolbar = new ToolBar(this);
		slider = new GuiReadPageSlider(this);
		slider.setMinimum(0);
		// slider.setPaintTicks(true);
		// slider.setMinorTickSpacing(1);

		setFocusable(true);
		addMouseWheelListener(this);
		addKeyListener(this);


		
		String path = "/home/divakar/Mangas/Naruto/0001";
		File folder = new File(path);
		files.clear();
		files.addAll(Arrays.asList(folder.listFiles(filter)));
		Collections.sort(files);

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			images.add(load(file));
		}

		slider.setValue(1);
		slider.setMaximum(files.size());

		// images.add(load(files.get(0)));

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		M.print("index: " + imageIndex);
		BufferedImage image = images.get(imageIndex);
		int imageHeight = image.getHeight();
		if (imageScroll < 0 && imageIndex > 0) {
			imageIndex--;
			slider.setValue(imageIndex);
			imageScroll += imageHeight;
		} else if (imageHeight < imageScroll && imageIndex < images.size() - 1) {
			imageIndex++;
			slider.setValue(imageIndex);
			imageScroll -= imageHeight;
		}
		
		image = images.get(imageIndex);
		drawImage(g, image, imageScroll);
		// drawImage(g, images.get(imageIndex+1), imageScroll - imageHeight);

		int index = imageIndex + 1;
		int screenHeight = imageScroll + getHeight() - imageHeight;
//		M.print(" i: " + imageIndex + " , scroll: " + imageScroll + " , imgHeight: " + imageHeight);
//		M.print(" getHeight(): " + getHeight() + " , sreenHeight: " + screenHeight);

		while (screenHeight > 0 && index < images.size()) {
			image = images.get(index);
			drawImage(g, image, +screenHeight - getHeight());
			screenHeight -= image.getHeight();
			index++;
		}

		g.drawString(imageIndex + " / " + images.size(), (getWidth()) / 2, (getHeight() - 1));
	}

	private void drawImage(Graphics g, BufferedImage image, int offset) {
		if (image != null) {
			g.drawImage(image, (getWidth() - image.getWidth()) / 2, 0 - offset, null);
			return;
		}
		g.drawString("Loading image...", getWidth() / 2 - 20, 0 - offset);
		// g.setColor(Color.BLACK);
		// g.fillRect(0, 0, getWidth(), getHeight());
		// g.setColor(getBackground());
		// g.fillRect(3, 3, getWidth() - 3, getHeight() - 3);
		// String text = " Loading Image ... ";
		// FontMetrics metrics = g.getFontMetrics();
		// int height = metrics.getHeight();
		// int width = metrics.stringWidth(text);
		// g.setColor(Color.BLACK);
		// g.drawString(text, (getWidth() - width - 4) / 2, (getHeight() -
		// height - 2) / 2);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		// if(e.getKeyChar() == '')
		// M.print("key: "+e.getKeyChar());
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			// M.print("scroll++ : "+scroll);
			scrollMove(100);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			// M.print("scroll-- : "+scroll);
			scrollMove(-100);
		}
	}

	public void scrollMove(int scroll) {
		imageScroll += scroll;
		repaint();
	}

	public void previousChapter() {
		M.print("previousChapter");
	}

	public void previousPage() {
		imageIndex--;
		imageScroll = 0;
		slider.setValue(imageIndex);
		repaint();
	}

	public void nextPage() {
		imageIndex++;
		imageScroll = 0;
		slider.setValue(imageIndex);
		repaint();
	}

	public void nextChapter() {
		M.print("nextChapter");
	}

	public void setZoom(int zoom) {
		M.print("zoom: " + this.zoom + " -> " + zoom);
		if (this.zoom == zoom)
			return;
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

	public BufferedImage scaleImage(BufferedImage image, int zoom) {
		int width = (int) ((float) image.getWidth() * zoom / this.zoom);
		int height = (int) ((float) image.getHeight() * zoom / this.zoom);
		// M.print("  "+image.getWidth()+ " -> "+width);
		Image img = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage newer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		newer.getGraphics().drawImage(img, 0, 0, null);
		return newer;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
			scrollMove(-100);
		} else {
			scrollMove(100);
		}
	}

	public static BufferedImage load(File file) {
		// try {
		// Thread.sleep((int) (Math.random() * 2000));
		// } catch (InterruptedException e1) {
		// }
		try {
			M.print("\tloading image: " + file.getName());
			return ImageIO.read(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// MangaLibrary library = LibraryManager.loadLibrary("config");
		// LibraryManager.saveLibrary(configDirectory, library);
		// Manga manga = library.getCollection(MangaCollection.WATCHING).get(1);
		Container content = frame.getContentPane();
		GuiRead5Own gui = new GuiRead5Own();

		content.setLayout(new BorderLayout());
		content.add(gui);
		Component bar = gui.getToolbar();
		content.add(bar, BorderLayout.NORTH);
		JSlider slider = gui.getSlider();
		content.add(slider, BorderLayout.EAST);
		// frame.pack();
		frame.setSize(600, 800);
		frame.setVisible(true);
		frame.validate();
	}

}
