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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import misc.WrapLayout;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public class GuiMangaCollectionGrid extends JScrollPane {

	private static final long serialVersionUID = 566167950981962725L;
	
	private final JPanel panel;
	
	private final GuiFrame frame;
	private final MangaLibrary library;
	private final MangaCollection collection;

	private final JLabel titel;
	private final JLabel newerLabel;
	private final JLabel olderLabel;

	private int olderSize = 0;
	private int newerSize = 0;
	
	public GuiMangaCollectionGrid(GuiFrame frame, MangaCollection collection) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.collection = collection;

		panel = new JPanel();
		panel.setLayout(new WrapLayout());
		
		setViewportView(panel);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		setWheelScrollingEnabled(true);
		
		titel = new JLabel(collection.getName()+" Collection");
		titel.setFont(frame.getOptions().getTitelFont());
		titel.setAlignmentX(0.5f);
		titel.setHorizontalAlignment(SwingConstants.CENTER);
		titel.setBackground(Color.green);
		panel.add(titel);
		
		newerLabel = new JLabel("New:");
		newerLabel.setFont(frame.getOptions().getSubtitelFont());
		//panel.add(newerLabel);

		olderLabel = new JLabel("Up to Date:");
		olderLabel.setFont(frame.getOptions().getSubtitelFont());
		olderLabel.setPreferredSize(new Dimension(getWidth(),titel.getPreferredSize().height));
		//panel.add(olderLabel);
		
		addComponentListener(new ComponentListener() {
		    public void componentResized(ComponentEvent e) {
				if(getWidth()<=0) return;
				titel.setPreferredSize(new Dimension(getWidth()-2,60));
				newerLabel.setPreferredSize(new Dimension(getWidth()-2,60));
				olderLabel.setPreferredSize(new Dimension(getWidth()-2,60));
		    }
			@Override
			public void componentMoved(ComponentEvent e) {}
			@Override
			public void componentShown(ComponentEvent e) {
				if(getWidth()<=0) return;
				titel.setPreferredSize(new Dimension(getWidth()-2,60));
				newerLabel.setPreferredSize(new Dimension(getWidth()-2,60));
				olderLabel.setPreferredSize(new Dimension(getWidth()-2,60));
			}
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		
		update();
	}

	public void update() {
		
		//Update button text
		Component[] components = panel.getComponents();
		for(Component component : components)
			if(component instanceof GuiMangaCoverButton)
				((GuiMangaCoverButton)component).update();
		
		List<Manga> newer = new ArrayList<>();
		List<Manga> older = new ArrayList<>();
		for (Manga manga : library.getCollection(collection))
			if (manga.newAvailable())
				newer.add(manga);
			else
				older.add(manga);

		//Check if no inter-collection movements
		if(newerSize == newer.size() && olderSize == older.size())
			return;
		
		//M.print("Rebuilding: "+newer.size()+" , "+older.size());
		
		//Reset
		newerSize = newer.size();
		olderSize = older.size();
		panel.removeAll();
		
		//Add
		
		panel.add(titel);
		
		if(newerSize != 0)
			panel.add(newerLabel);
		
		for (Manga manga : newer) {
			JComponent component = new GuiMangaCoverButton(frame, manga);
			panel.add(component);
		}
		
		if(olderSize != 0)
			panel.add(olderLabel);
		
		for (Manga manga : older) {
			JComponent component = new GuiMangaCover(frame, manga);
			panel.add(component);
		}
		
	}

}
