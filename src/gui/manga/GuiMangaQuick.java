package gui.manga;

import gui.GuiFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JTabbedPane;

import data.Manga;
import data.MangaLibrary;

public class GuiMangaQuick extends JButton {

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final Manga manga;

	private BufferedImage image;
	

	public GuiMangaQuick(final GuiFrame frame, final Manga manga) {
		this.frame = frame;
		this.library = frame.getLibrary();
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
				JTabbedPane tabbed = frame.getTabbed();
				GuiMangaFull full = frame.getFull();
				tabbed.setSelectedComponent(full);
				full.update(manga);
				
				// System.exit(0);
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

}
