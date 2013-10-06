package gui.manga;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import logic.LibraryManager;
import logic.mangareader.ReaderAvailable;
import data.Manga;
import data.MangaLibrary;
import data.Manga.MangaCollection;

public class GuiMangaQuick extends JButton {

	MangaLibrary library;
	BufferedImage image = null;
	Manga manga;
	
	public GuiMangaQuick(MangaLibrary library, Manga manga) {
		this.manga = manga;
		String name = manga.getName();

		String path = library.getMangaDirectory() + File.separator + name + File.separator + name + ".jpg";

		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// if(image!=null){
		// JLabel picLabel = new JLabel(new ImageIcon(image));
		// add(picLabel);
		// }
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// M.print("" + (String) combo.getSelectedItem());
				System.exit(0);
			}
		});
		//this.setText("Read " + manga.getRead() + " / " + manga.getReleased());
		// Manga manga = library.getWatching().get(name);
		// this.setText(manga.getRead()+" / "+manga.getReleased());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if (image != null)
			g.drawImage(image, 0, 0, null);
		// Font font = Font.decode("Free Sans 15");
		// g.setFont(font);
		String text = "Read " + manga.getRead() + " / " + manga.getReleased();
		FontMetrics metrics = g.getFontMetrics();
		int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		g.setColor(Color.BLACK);
		g.drawString(text, getWidth() - width - 4, getHeight() - height / 2);
		//g.drawString(getText(), getWidth() / 2 - width / 2, getHeight() - height / 2);// centered
	}

	@Override
	public Dimension getPreferredSize() {
		// int height = metrics.getHeight();
		return new Dimension(image.getWidth(), image.getHeight());
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Test Manga Add");
				// frame.setSize(300, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				String configDirectory = "config";
				MangaLibrary library = LibraryManager.loadLibrary(configDirectory);
				// MangaAll.try_refresh(library);
				// LibraryManager.saveLibrary(configDirectory, library);
				Manga manga = library.getCollection(MangaCollection.WATCHING).get(0);
				GuiMangaQuick component = new GuiMangaQuick(library, manga);
				frame.add(component);
				frame.pack();

				frame.setVisible(true);
			}
		});
	}
}
