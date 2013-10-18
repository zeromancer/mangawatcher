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
package gui.add;

import gui.GuiFrame;
import gui.downloading.GuiDownloading;
import gui.full.GuiMangaFull;
import gui.threading.BackgroundExecutors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import logic.MangaLogic;
import net.miginfocom.swing.MigLayout;
import data.Engine;
import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class GuiMangaAdd extends JPanel {

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final MangaLogic logic;
	private final BackgroundExecutors executors;

	private final JLabel title;
	private final JLabel source;
	private final JComboBox<String> combo;

	private final JLabel collectionLabel;
	private final JComboBox<String> collection;
	private final JLabel checkLabel;
	private final JCheckBox check;
	private final JButton button;
	
	
	

	public GuiMangaAdd(final GuiFrame frame) {
		super(new MigLayout("align center", "0:10%:20%[grow,fill]0:10%:20%", ""));
		//		super(new MigLayout("align center,fillx", "[grow,fill]rel[grow,fill]", "[]1[]"));
		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.frame = frame;
		this.library = frame.getLibrary();
		this.logic = frame.getLogic();
		this.executors = frame.getExecutors();

		title = new JLabel("Add New Manga");
		title.setFont(frame.getOptions().getTitelFont());
		add(title, "wrap");

		source = new JLabel("mangareader.net");
		source.setFont(frame.getOptions().getSubtitelFont());
		add(source, "wrap");

		// combo box
		List<String> all = new ArrayList<>(library.getAvailable(MangaSource.MANGAREADER).keySet());
		Collections.sort(all);
		String[] array = all.toArray(new String[0]);
		combo = new JComboBox<>(array);
		combo.setMaximumRowCount(35);
		combo.setSelectedIndex(-1);
		add(combo, "wrap");

		collectionLabel = new JLabel("Add to Colllection:");
		collectionLabel.setFont(frame.getOptions().getLabelFont());
		add(collectionLabel, "split 2");
		collection = new JComboBox<String>(MangaCollection.strings());
		collection.setSelectedIndex(0);
		add(collection, "wrap");

		
		checkLabel = new JLabel("Start Downloading");
		checkLabel.setFont(frame.getOptions().getLabelFont());
		add(checkLabel,"split 2");
		check = new JCheckBox();
		check.setSelected(true);
		add(check,"wrap");
		
		
		
		// button
		button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				final String name = (String) combo.getSelectedItem();
				final MangaCollection addCollection = MangaCollection.parse((String) collection.getSelectedItem());
				final JTabbedPane tabbed = frame.getTabbed();
				final GuiDownloading down = frame.getDownloading();
				final Engine engine = frame.getEngine();
				final boolean selected = check.isSelected();
				
				if(combo.getSelectedIndex()==-1)
					return;
				
				tabbed.setSelectedComponent(down);
				combo.setSelectedIndex(-1);

				final Manga manga = library.getManga(name);
				if (manga != null) {
					final GuiMangaFull full = frame.getFull();
					frame.getTabbed().setSelectedComponent(full);
					full.update(manga);
					return;
				}
				
				frame.getDownloading().enableButtons(false);

				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						logic.add(MangaSource.MANGAREADER, name, addCollection);
						engine.load(library.getManga(name));
						if(selected)
							down.updateDeep(library.getManga(name));
						library.save(executors);
						frame.getDownloading().enableButtonsInvoked(true);
					}
				});
			}
		});
		add(button);
	}

}
