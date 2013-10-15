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
package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jmc.swing.JFontChooser;
import logic.DiskIOManager;
import lombok.Getter;
import lombok.Setter;
import misc.M;
import net.miginfocom.swing.MigLayout;
import data.Options;

public @Getter @Setter class GuiOptions extends JPanel {

	//	private int checkInterval; // in minutes
	//	private int scrollAmount;
	//
	//	private Font buttonFont;
	//	private Font titelFont;
	//	private Font subtitelFont;
	//	private Font labelFont;
	//	private Font textFont;
	//	
	//	private Color readingBackgroundColor;

	private final GuiFrame frame;
	private final Options options;

	private final JFontChooser chooser;

	private boolean changed = false;

	private final String layoutTitle = "span 2, align center";
	private final String layoutLabel = "align right";
	private final String layoutComponent = "align left, gaptop 30";

	private final String layoutComponent1 = "split 2";
	private final String layoutComponent2 = "";

	public GuiOptions(final GuiFrame frame) {
		this.frame = frame;
		this.options = frame.getOptions();

		//		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setLayout(new MigLayout("wrap 2", "5%::20%[grow,shrink]20::50[grow,shrink]5%::20%", ""));

		JLabel title = new JLabel("Options");
		title.setFont(options.getTitelFont());
		this.add(title, layoutTitle);

		chooser = new JFontChooser();
		//		frame.getContentPane().add(fontCh, BorderLayout.CENTER);
		frame.setVisible(true);

		JTextArea area = new JTextArea("Please note: Font and General Mouse Scroll Amount changes will apply only after program restart");
		area.setFont(options.getTextFont());
		area.setEditable(false);
		this.add(area, layoutTitle);

		final JLabel titleFontsLabel = createLeft("Title Font");
		final JButton titleFonts = createRight(options.getTitelFont());
		titleFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setSelectedFont(options.getTitelFont());
				int response = chooser.showDialog(frame, true);
				if (response == JFontChooser.ACCEPT_OPTION) {
					options.setTitelFont(chooser.getSelectedFont());
					titleFonts.setText(options.getTitelFont().getName());
					titleFonts.setFont(options.getTitelFont());
					changed();
				}
			}
		});

		final JLabel subtitleFontsLabel = createLeft("Subtitle Font");
		final JButton subtitleFonts = createRight(options.getSubtitelFont());
		subtitleFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setSelectedFont(options.getSubtitelFont());
				int response = chooser.showDialog(frame, true);
				if (response == JFontChooser.ACCEPT_OPTION) {
					options.setSubtitelFont(chooser.getSelectedFont());
					subtitleFonts.setText(options.getSubtitelFont().getName());
					subtitleFonts.setFont(options.getSubtitelFont());
					changed();
				}
			}
		});

		final JLabel labelFontsLabel = createLeft("Label Font");
		final JButton labelFonts = createRight(options.getLabelFont());
		labelFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setSelectedFont(options.getLabelFont());
				int response = chooser.showDialog(frame, true);
				if (response == JFontChooser.ACCEPT_OPTION) {
					options.setLabelFont(chooser.getSelectedFont());
					labelFonts.setText(options.getLabelFont().getName());
					labelFonts.setFont(options.getLabelFont());
					changed();
				}
			}
		});

		final JLabel buttonFontsLabel = createLeft("Button Font");
		final JButton buttonFonts = createRight(options.getButtonFont());
		buttonFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setSelectedFont(options.getButtonFont());
				int response = chooser.showDialog(frame, true);
				if (response == JFontChooser.ACCEPT_OPTION) {
					options.setButtonFont(chooser.getSelectedFont());
					buttonFonts.setText(options.getButtonFont().getName());
					buttonFonts.setFont(options.getButtonFont());
					changed();
				}
			}
		});

		final JLabel textFontsLabel = createLeft("Text Font");
		final JButton textFonts = createRight(options.getTextFont());
		textFonts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setSelectedFont(options.getTextFont());
				int response = chooser.showDialog(frame, true);
				if (response == JFontChooser.ACCEPT_OPTION) {
					options.setTextFont(chooser.getSelectedFont());
					textFonts.setText(options.getTextFont().getName());
					textFonts.setFont(options.getTextFont());
					changed();
				}
			}
		});

		JLabel recheckLabel = createLeft("Downloading Recheck Interval");
		JSlider recheckSlider = new JSlider(5, 100, Math.max(options.getCheckInterval(), 5));
		final JLabel recheckAmount = new JLabel("every " + options.getCheckInterval() + " min");
		recheckAmount.setFont(options.getLabelFont());
		recheckSlider.setPaintTicks(false);
		recheckSlider.setSnapToTicks(true);
		recheckSlider.setMinorTickSpacing(1);
		recheckSlider.setMajorTickSpacing(10);
		recheckSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if (source.getValueIsAdjusting()) return;
				if (options.getCheckInterval() != source.getValue()) {
					options.setCheckInterval(source.getValue());
					recheckAmount.setText("every " + source.getValue() + " min");
					changed();
				}
			}
		});
		this.add(recheckSlider, layoutComponent1);
		this.add(recheckAmount, layoutComponent2);

		JLabel scrollLabel = createLeft("General Mouse Scroll Amount");
		JSlider scrollSlider = new JSlider(5, 300, M.getBounded(1, options.getScrollAmount(), 300));
		final JLabel scrollAmount = new JLabel("" + options.getScrollAmount() + " px");
		scrollAmount.setFont(options.getLabelFont());
		scrollSlider.setPaintTicks(false);
		scrollSlider.setSnapToTicks(true);
		scrollSlider.setMinorTickSpacing(1);
		scrollSlider.setMajorTickSpacing(50);
		scrollSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if (source.getValueIsAdjusting()) return;
				if (options.getScrollAmount() != source.getValue()) {
					options.setScrollAmount(source.getValue());
					scrollAmount.setText("" + source.getValue() + " px");
					changed();
				}
			}
		});
		this.add(scrollSlider, layoutComponent1);
		this.add(scrollAmount, layoutComponent2);

		JLabel readingZoomLabel = createLeft("Reading Zoom");
		JSlider readingZoomSlider = new JSlider(10, 400, M.getBounded(10, options.getReadingZoom(), 400));
		final JLabel readingZoomAmount = new JLabel("" + options.getCheckInterval() + " %");
		readingZoomAmount.setFont(options.getLabelFont());
		readingZoomSlider.setPaintTicks(false);
		readingZoomSlider.setSnapToTicks(true);
		readingZoomSlider.setMinorTickSpacing(1);
		readingZoomSlider.setMajorTickSpacing(100);
		readingZoomSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if (source.getValueIsAdjusting()) return;
				if (options.getReadingZoom() != source.getValue()) {
					options.setReadingZoom(source.getValue());
					readingZoomAmount.setText("" + source.getValue() + " %");
					changed();
				}
			}
		});
		this.add(readingZoomSlider, layoutComponent1);
		this.add(readingZoomAmount, layoutComponent2);

		JLabel readingScrollLabel = createLeft("Reading Mouse Scroll");
		JSlider readingScrollSlider = new JSlider(1, 500, M.getBounded(1, options.getReadingScroll(), 500));
		final JLabel readingScrollAmount = new JLabel("" + options.getCheckInterval() + " px");
		readingScrollAmount.setFont(options.getLabelFont());
		readingScrollSlider.setPaintTicks(false);
		readingScrollSlider.setSnapToTicks(true);
		readingScrollSlider.setMinorTickSpacing(1);
		readingScrollSlider.setMajorTickSpacing(100);
		readingScrollSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if (source.getValueIsAdjusting()) return;
				if (options.getReadingScroll() != source.getValue()) {
					options.setReadingScroll(source.getValue());
					readingScrollAmount.setText("" + source.getValue() + " px");
					changed();
				}
			}
		});
		this.add(readingScrollSlider, layoutComponent1);
		this.add(readingScrollAmount, layoutComponent2);

		JButton save = new JButton("Save Changes");
		save.setFont(options.getButtonFont());
		save.setPreferredSize(new Dimension(getWidth() - 20, 40));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveChanges();
			}
		});
		this.add(save, layoutTitle);

	}

	private JLabel createLeft(String text) {
		JLabel label = new JLabel(text);
		label.setFont(options.getLabelFont());
		this.add(label, layoutLabel);
		return label;
	}

	private JButton createRight(Font font) {
		JButton button = new JButton(font.getName());
		button.setFont(font);
		this.add(button, layoutComponent);
		return button;
	}

	public void changed() {
		changed = true;
	}

	public void applyChanges() {

	}

	public void saveChanges() {
		if (changed) {
			frame.getExecutors().runOnFileThread(new Runnable() {
				public void run() {
					DiskIOManager.saveOptions(options, frame.getLibrary().getConfigDirectory());
				}
			});
			changed = false;
		}
	}

	//	public GuiOptions(GuiFrame frame) {
	//		
	//	}

}
