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
import gui.reading.GuiRead;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import data.Manga;
import data.MangaLibrary;

@SuppressWarnings("unused") 
public class GuiMangaCoverButton extends JPanel {
	
	private final GuiFrame frame;
	private final MangaLibrary library;
	private final Manga manga;

	private GuiMangaCover image;
	private final JButton button;

	public GuiMangaCoverButton(final GuiFrame frame, final Manga manga) {
		super(new BorderLayout());
		this.frame = frame;
		this.library = frame.getLibrary();
		this.manga = manga;

		//		setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		image = new GuiMangaCover(frame, manga);
		add(image);
		button = new JButton();
		add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// M.print("" + (String) combo.getSelectedItem());
				GuiRead read = frame.getRead();
				JTabbedPane tabbed = frame.getTabbed();
				read.view(manga, manga.getRead() + 1, manga.getPage());
				tabbed.setSelectedComponent(read);
				tabbed.setEnabledAt(tabbed.indexOfComponent(read), true);
			}
		});

		update();
	}

	public void update() {
		if (manga.newAvailable()) {
			button.setVisible(true);
			button.setText("Read " + (manga.getRead() + 1));
		} else
			button.setVisible(false);
	}

}
