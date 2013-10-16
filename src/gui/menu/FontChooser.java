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
package gui.menu;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import lombok.Getter;
import lombok.Setter;
import misc.WrapLayout;

public @Getter @Setter class FontChooser extends JPanel {

	private final JComboBox<String> box;
	private final JSlider slider;
	private final FontChooserRenderer renderer;

	private Font selectedFont;

	public FontChooser(Font startingFont) {

		
		final int displayFontSize = 16;

		Font setFont = new Font(startingFont.getFamily(), Font.PLAIN, displayFontSize);

		setLayout(new WrapLayout());

		selectedFont = new JLabel().getFont();
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = environment.getAvailableFontFamilyNames();
		box = new JComboBox<String>(fonts);
		box.setSelectedItem(0);
		renderer = new FontChooserRenderer(displayFontSize);
		box.setRenderer(renderer);
		box.setSelectedItem(setFont.getFamily());
		box.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final String fontName = box.getSelectedItem().toString();
					box.setFont(new Font(fontName, Font.PLAIN, displayFontSize));
					selectedFont = new Font(fontName, Font.PLAIN, slider.getValue());
				}
			}
		});
		
		box.setFont(setFont);
		box.setSelectedItem(0);
		box.getEditor().selectAll();
		add(box);

		slider = new JSlider(6, 60, 16);
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				final String fontName = box.getSelectedItem().toString();
				selectedFont = new Font(fontName, Font.PLAIN, slider.getValue());
			}
		});
		add(slider);

//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		//		setPreferredSize(new Dimension(400, 60));
//		setLocation(200, 105);
//		pack();
//
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				box.setPopupVisible(true);
//				box.setPopupVisible(false);
//			}
//		});
//		setVisible(true);
	}

	private class FontChooserRenderer extends BasicComboBoxRenderer {

		private final int size;

		private FontChooserRenderer(int size) {
			this.size = size;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus) {
			super.getListCellRendererComponent(list, value, index, selected, focus);
			final String font = (String) value;
			setFont(new Font(font, Font.PLAIN, size));
			return this;
		}
	}
	
	
//	public static void main(String arg[]) {
//
//		System.setProperty("awt.useSystemAAFontSettings", "on");
//		System.setProperty("swing.aatext", "true");
//
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				FontChooser systemFontDisplayer = new FontChooser();
//			}
//		});
//	}

}