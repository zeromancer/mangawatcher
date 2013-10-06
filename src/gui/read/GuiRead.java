package gui.read;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import misc.M;

public class GuiRead extends JScrollPane {

	private static final long serialVersionUID = 4951037525535388323L;

	public class ImageBuffer extends JPanel {

		private static final long serialVersionUID = 1152742277827616450L;
		BufferedImage image = null;

		public ImageBuffer() {

		}

		public void loadImage(File file) {
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null)
				g.drawImage(image, 0, 0, null);
		}
		@Override
		public Dimension getPreferredSize() {
			if(image==null)
				return new Dimension(200, 500);
			else
				return new Dimension(image.getWidth(), image.getHeight());
		}
	}

	public GuiRead() {

		setMinimumSize(new Dimension(400, 400));
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		this.add(panel);
		
		JButton button = new JButton("Read Next Chapter " + (3000 + 1));
		panel.add(button);
		
		String path = "/home/divakar/Mangas/Naruto/0001";
		File folder = new File(path);
		List<File> files = new ArrayList<File>();
		files.addAll(Arrays.asList(folder.listFiles()));
		Collections.sort(files);

		int current = 0;
		int middle = 5;

		List<ImageBuffer> buffer = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ImageBuffer image = new ImageBuffer();
			buffer.add(image);
			panel.add(image);
			// String imagePath = String.format("%s%04d", path,current);
			image.loadImage(files.get(current + i));
		}

		// JScrollPane scroll = new JScrollPane();
		

		M.print("roots: " + files.toString());

		

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Test Manga Add");
				// frame.setSize(300, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// String configDirectory = "config";
				// MangaLibrary library =
				// LibraryManager.loadLibrary(configDirectory);
				// MangaAll.try_refresh(library);
				// LibraryManager.saveLibrary(configDirectory, library);
				// Manga manga =
				// library.getCollection(MangaCollection.WATCHING).get(0);
				// Component component = new GuiRead(library, manga);
				Component component = new GuiRead();
				frame.add(component);
				frame.pack();

				frame.setVisible(true);
			}
		});
	}
}
