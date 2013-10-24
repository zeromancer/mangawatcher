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
package gui.full;

import gui.GuiFrame;
import gui.reading.GuiRead;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;
public class GuiMangaFull extends JScrollPane {

	private final GuiFrame frame;
	private final MangaLibrary library;
	private Manga manga;

	// Description
	private final JLabel title;
	private JLabel icon;
	private final JTextArea description;
	private final JScrollPane scroll;

	// Chapters
	private final List<JGradientButton> buttons;
	private final JPanel grid;

	// Options
	private final JLabel options;
	private final JToggleButton show;
	
	private final JComboBox<String> collection;
	private final JLabel collectionLabel;
	
	private final JButton sync;
	private final JLabel syncLabel;
	
	private final JButton redownload;
	private final JLabel redownloadLabel;
	
	private final JSpinner setDownload;
	private final JLabel setDownloadLabel;

	private final JSpinner setRead;
	private final JLabel setReadLabel;
	
	private final JButton remove;
	private final JLabel removeLabel;

	private final JButton save;
	private final JLabel saveLabel;
	
	private boolean changeCollection;

	public GuiMangaFull(final GuiFrame frame) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.manga = null;
		this.changeCollection = true;
		// Content
		JPanel panel = new JPanel();
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		setViewportView(panel);

		// Layout
		panel.setLayout(new MigLayout("", "5:50:200[align right,grow,200::500][align left,grow,100::400,shrink]5:50:200", "[top][top]"));
		String layoutCenter = "growx, span 2, wrap";
		String layoutLeft = "gapleft 40, align left";
		String layoutRight = "width 200!, wrap";

		// Title
		title = new JLabel("Unselected");
		title.setFont(frame.getOptions().getTitelFont());
		panel.add(title, "align center, span 2, growx, shrink, wrap");

		// Image
		icon = new JLabel();
		panel.add(icon, "");

		// Description
		description = new JTextArea("Please select a manga");
		description.setColumns(20);
		description.setLineWrap(true);
		description.setFont(frame.getOptions().getLabelFont());
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		scroll = new JScrollPane(description);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scroll, "grow, shrink, wrap");

		// Sync
		syncLabel = new JLabel("Recheck:");
		syncLabel.setFont(frame.getOptions().getLabelFont());
//		panel.add(syncLabel, optionsAddLabel);
		sync = new JButton("Check for updates now");
		sync.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getTabbed().setSelectedComponent(frame.getDownloading());
				frame.getDownloading().updateDeep(manga);;
			}
		});
		panel.add(sync, layoutCenter);
		
		
		// Chapters Title
		JLabel label = new JLabel("Chapters");
		label.setFont(frame.getOptions().getSubtitelFont());
		panel.add(label, layoutCenter);
		grid = new JPanel(new GridLayout(0, 10));
		grid.setBorder(BorderFactory.createEmptyBorder());
		panel.add(grid, "align center, growx, shrink, span 2, wrap");
		buttons = new ArrayList<>();

		// Options
		options = new JLabel("Options");
		options.setFont(frame.getOptions().getSubtitelFont());
		panel.add(options, "align left");

		show = new JToggleButton("Show");
		show.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if (selected)
					abstractButton.setText("Hide");
				else
					abstractButton.setText("Show");
				
				setOptionVisibility(selected);
				
				revalidate();
				repaint();
			}
		});
		panel.add(show, layoutRight+", gaptop 7");
		
		// Change Collection
		collectionLabel = new JLabel("Change Colllection:");
		collectionLabel.setFont(frame.getOptions().getLabelFont());
		panel.add(collectionLabel, layoutLeft);
		collection = new JComboBox<String>(MangaCollection.strings());
		collection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!changeCollection) return;
				if(manga==null) return;
				String selection = (String) collection.getSelectedItem();
				MangaCollection newCollection = MangaCollection.parse(selection);
				manga.changeCollection(library, newCollection);
				library.save(frame.getExecutors());
			}
		});
		panel.add(collection, layoutRight);


		// Redownload
		redownloadLabel = new JLabel("Redownload:");
		redownloadLabel.setFont(frame.getOptions().getLabelFont());
		panel.add(redownloadLabel, layoutLeft);
		redownload = new JButton("All Chapters");
		redownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				manga.setDownloaded(0);
				frame.getTabbed().setSelectedComponent(frame.getDownloading());
				frame.getDownloading().getDeep().doClick();
			}
		});
		panel.add(redownload, layoutRight);

		// Set Downloaded
		setDownloadLabel = new JLabel("Set Downloaded:");
		setDownloadLabel.setFont(frame.getOptions().getLabelFont());
		panel.add(setDownloadLabel,layoutLeft);
		SpinnerModel model = new SpinnerNumberModel(0, 0, 9999, 1);
		setDownload = new JSpinner(model);
		setDownload.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner)e.getSource();
				//M.print(""+source.getValue());
				int downloaded = (Integer)source.getValue();
				manga.setDownloaded(downloaded);
				for (int i = buttons.size() - 1; i >= 0; i--) {
					grid.remove(i);
					buttons.remove(i);
				}
				addButtons();
//				updateButtons();
//				addButtons();
//				deleteButtons();
				GuiMangaFull.this.revalidate();
				GuiMangaFull.this.repaint();
				frame.getTray().update();
			}
		});
		panel.add(setDownload,layoutRight);

		// Set Downloaded
		setReadLabel = new JLabel("Set Read:");
		setReadLabel.setFont(frame.getOptions().getLabelFont());
		panel.add(setReadLabel,layoutLeft);
		SpinnerModel model2 = new SpinnerNumberModel(0, 0, 9999, 1);
		setRead = new JSpinner(model2);
		setRead.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner)e.getSource();
				//M.print(""+source.getValue());
				int read = (Integer)source.getValue();
				manga.setRead(read);
				for (int i = buttons.size() - 1; i >= 0; i--) {
					grid.remove(i);
					buttons.remove(i);
				}
				addButtons();
//				updateButtons();
//				addButtons();
//				deleteButtons();
				GuiMangaFull.this.revalidate();
				GuiMangaFull.this.repaint();
				frame.getTray().update();
			}
		});
		panel.add(setRead,layoutRight);
		
		
		// Remove
		removeLabel = new JLabel("Remove:");
		removeLabel.setFont(frame.getOptions().getLabelFont());
		panel.add(removeLabel, layoutLeft);
		remove = new JButton("This Manga");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MangaCollection collection = manga.getCollection();
				library.getCollection(collection).remove(manga);
				title.setText("Unselected");
				icon = new JLabel();
				description.setText("Please select a Manga");
				GuiMangaFull.this.manga = null;
				if(show.isSelected())
					show.doClick();
				show.setEnabled(false);
			}
		});
		panel.add(remove, layoutRight);
		

		// Save
		saveLabel = new JLabel("Save Changes:");
		saveLabel.setFont(frame.getOptions().getLabelFont());
		panel.add(saveLabel, layoutLeft);
		save = new JButton("To Disk");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				library.save(frame.getExecutors());
			}
		});
		panel.add(save, layoutRight);
		
		
		setOptionVisibility(false);
		show.setEnabled(false);
		
	}
	
	private void setOptionVisibility(boolean selected){
		collection.setVisible(selected);
		collectionLabel.setVisible(selected);
		
//		sync.setVisible(selected);
//		syncLabel.setVisible(selected);
		
		redownload.setVisible(selected);
		redownloadLabel.setVisible(selected);
		
		setDownload.setVisible(selected);
		setDownloadLabel.setVisible(selected);

		setRead.setVisible(selected);
		setReadLabel.setVisible(selected);
		
		remove.setVisible(selected);
		removeLabel.setVisible(selected);
		
		save.setVisible(selected);
		saveLabel.setVisible(selected);
	}

	public void update() {
		if(manga ==null)
			return;
		show.setEnabled(true);
		updateMinor();
		revalidate();
		repaint();
	}
	
	public void update(Manga manga) {

		if (this.manga == null || this.manga != manga) {
			this.manga = manga;
			updateMajor();
		} else {
			updateMinor();
		}

		show.setEnabled(true);
		revalidate();
		repaint();
	}

	private void updateMajor() {
		//		M.print("updating Major: "+manga.getName());
		title.setText(manga.getName());
		icon.setIcon(new ImageIcon(frame.getEngine().getCover(manga)));
		description.setText(manga.getDescription());

//		String selection = (String) collection.getSelectedItem();
//		M.print("updating Major: "+selection+" , ordinal: "+manga.getCollection().ordinal()+" col: "+manga.getCollection());
		changeCollection = false;
		collection.setSelectedIndex(manga.getCollection().ordinal());
		changeCollection = true;
		setDownload.setValue(manga.getDownloaded());
		setRead.setValue(manga.getRead());
		updateButtons();
		addButtons();
		deleteButtons();
	}

	private void updateMinor() {
		//		M.print("updating Minor: "+manga.getName());
		setDownload.setValue(manga.getDownloaded());
		setRead.setValue(manga.getRead());
		updateButtons();
		addButtons();
	}

	private void updateButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setRead(i < manga.getRead());
		}
	}

	private void addButtons() {
		for (int i = Math.max(buttons.size(),1); i <= manga.getDownloaded(); i++) {
			JGradientButton button = new JGradientButton("" + i, i <= manga.getRead());
			final int j = i;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					// M.print("" + (String) combo.getSelectedItem());
					GuiRead read = frame.getRead();
					JTabbedPane tabbed = frame.getTabbed();
					read.view(manga, j, manga.getPage());
					tabbed.setSelectedComponent(read);
					tabbed.setEnabledAt(tabbed.indexOfComponent(read), true);
				}
			});
			grid.add(button);
			buttons.add(button);
		}
	}

	private void deleteButtons() {
		//for (int i = manga.getDownloaded()+1; i < buttons.size(); i++) {
		for (int i = buttons.size() - 1; i > manga.getDownloaded(); i--) {
			// JGradientButton button = buttons.get(i);
			grid.remove(i);
			buttons.remove(i);
		}
	}

	private static final @Getter @Setter class JGradientButton extends JButton {

		boolean read = false;
		Color colorRead = Color.GREEN;
		Color colorUnread = Color.YELLOW;
		Color color2 = Color.GRAY.darker();

		private JGradientButton(String text, boolean read) {
			super(text);
			this.read = read;
			setContentAreaFilled(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Color color = read ? colorRead : colorUnread;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(new GradientPaint(new Point(0, 0), color, new Point(0, getHeight()*2), color2));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.drawString(getText(), getWidth() / 2, getHeight());
			g2.dispose();
			super.paintComponent(g);
		}

	}
}
