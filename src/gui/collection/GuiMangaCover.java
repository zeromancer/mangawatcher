/*
    MangaWatcher - a manga management program. 
    Copyright (C) 2013 David Siewert

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package gui.collection;

import gui.GuiFrame;
import gui.full.GuiMangaFull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JTabbedPane;

import data.Manga;
import data.MangaLibrary;

@SuppressWarnings("unused") 
public class GuiMangaCover extends JButton {

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final Manga manga;

	private BufferedImage image;
	
	public GuiMangaCover(final GuiFrame frame, final Manga manga) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.manga = manga;
		image = frame.getEngine().getCover(manga);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// M.print("" + (String) combo.getSelectedItem());
				JTabbedPane tabbed = frame.getTabbed();
				GuiMangaFull full = frame.getFull();
				tabbed.setSelectedComponent(full);
				full.update(manga);
			}
		});
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
		String text = "Read " + manga.getRead() + " / " + manga.getDownloaded();
		FontMetrics metrics = g.getFontMetrics();
		int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		int x = getWidth() - width - 4;
		int y = getHeight() - height / 2;
		g.setColor(Color.WHITE);
		g.fillRect(x, y-(height-2)+2, width, height-2);
		g.setColor(Color.BLACK);
		g.drawString(text, x, y);
		//g.drawString(getText(), getWidth() / 2 - width / 2, getHeight() - height / 2);// centered
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

}
