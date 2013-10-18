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
package gui.reading;

import gui.GuiFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import data.Engine;
import data.Engine.Icons;
import data.Options;

public class GuiReadToolBar extends JToolBar {

	private static final long serialVersionUID = 5673903268744027432L;

	private final GuiFrame frame;
	private final Options options;
	
	private JSlider zoom;
	private JSlider scroll;
	private GuiRead gui;
	private GuiReadView view;

	private JToggleButton showZoom;
	private JToggleButton showScroll;
	private JButton resync;
	private JButton previousChapter;
	private JButton previousPage;
	private JButton nextChapter;
	private JButton nextPage;

	public GuiReadToolBar(GuiFrame frame, GuiRead gui, GuiReadView view) {
		this.frame = frame;
		this.options = frame.getOptions();
		this.gui = gui;
		this.view = view;
		constructGuiOptions();
	}

	public void constructGuiOptions() {
		setLayout(new MigLayout("","0![grow]0!","0![]0![]0!"));
//		LayoutManager layout = new GridLayout();
//		setLayout(layout);
		
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setBorder(BorderFactory.createEmptyBorder());
//		resync = new JButton("Reload");
//		showZoom = new JToggleButton("Show Zoom");
//		showScroll = new JToggleButton("Show Scroll");
//		previousChapter = new JButton("Chapter--");
//		previousPage = new JButton("Page--");
//		nextPage = new JButton("Page++");
//		nextChapter = new JButton("Chapter++");
		
		add(gui.getProgress(),"span 7, grow, shrink, h 30, wrap");
		
		Engine engine = frame.getEngine();
		
		resync = new JButton(new ImageIcon(engine.getIcon(Icons.REFRESH)));
//		resync.setMargin(new Insets(0, 0, 0, 0));
		showZoom = new JToggleButton(new ImageIcon(engine.getIcon(Icons.ZOOM)));
		showScroll = new JToggleButton(new ImageIcon(engine.getIcon(Icons.SCROLL)));
		previousChapter = new JButton(new ImageIcon(engine.getIcon(Icons.DOUBLELEFT)));
		previousPage = new JButton(new ImageIcon(engine.getIcon(Icons.LEFT)));
		nextPage = new JButton(new ImageIcon(engine.getIcon(Icons.RIGHT)));
		nextChapter = new JButton(new ImageIcon(engine.getIcon(Icons.DOUBLERIGHT)));
		
		resync.setFocusable(false);
		showZoom.setFocusable(false);
		showScroll.setFocusable(false);
		previousChapter.setFocusable(false);
		previousPage.setFocusable(false);
		nextPage.setFocusable(false);
		nextChapter.setFocusable(false);
		
		zoom = new JSlider(10, 400, options.getReadingZoom());
		zoom.setPaintTicks(true);
		zoom.setMinorTickSpacing(10);
		zoom.setSnapToTicks(true);
		
		scroll = new JSlider(1, 500, options.getReadingScroll());
		scroll.setPaintTicks(true);
		scroll.setMinorTickSpacing(10);
		scroll.setSnapToTicks(true);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("10%"));
		labelTable.put(new Integer(100), new JLabel("100%"));
		labelTable.put(new Integer(200), new JLabel("200%"));
		labelTable.put(new Integer(300), new JLabel("300%"));
		labelTable.put(new Integer(400), new JLabel("400%"));
		zoom.setLabelTable(labelTable);
		zoom.setPaintLabels(true);
		
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("10 px"));
		labelTable.put(new Integer(100), new JLabel("100 px"));
		labelTable.put(new Integer(200), new JLabel("200 px"));
		labelTable.put(new Integer(300), new JLabel("300 px"));
		labelTable.put(new Integer(400), new JLabel("400 px"));
		labelTable.put(new Integer(500), new JLabel("400 px"));
		scroll.setLabelTable(labelTable);
		scroll.setPaintLabels(true);

		add(resync,"growx");
		add(showZoom,"growx");
		add(showScroll,"growx");
		add(previousChapter,"growx");
		add(previousPage,"growx");
		add(nextPage,"growx");
		add(nextChapter, "growx, wrap");
		final String sliderOptions = "growx, span";
		// add(zoom, zoomAdd);

		resync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(view.getManga()== null)
					return;
				view.getMapImages().clear();
				view.getMapFiles().clear();
				view.load();
			}
		});

		showZoom.setSelected(false);
		showZoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if (selected)
					add(zoom, sliderOptions);
				else
					remove(zoom);
				revalidate();
				repaint();
			}
		});


		showScroll.setSelected(false);
		showScroll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				if (selected)
					add(scroll, sliderOptions);
				else
					remove(scroll);
				revalidate();
				repaint();
			}
		});

		previousChapter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.previousChapter();
			}
		});
		previousPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.previousPage();
			}
		});
		nextPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.nextPage();
			}
		});
		nextChapter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.nextChapter();
			}
		});

		zoom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (zoom.getValueIsAdjusting())
					return;
				int value = zoom.getValue();
				view.setZoom(value);
			}
		});
		scroll.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (scroll.getValueIsAdjusting())
					return;
				int value = scroll.getValue();
				options.setReadingScroll(value);
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

