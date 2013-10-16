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
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;


public class GuiTabbedPane extends JTabbedPane {
	public GuiTabbedPane() {
		super();
	}

	public GuiTabbedPane(int tabPlacement) {
		super(tabPlacement);
	}

	private Insets getTabInsets() {
		Insets i = UIManager.getInsets("TabbedPane.tabInsets");
		if (i != null) {
			return i;
		} else {
			SynthStyle style = SynthLookAndFeel.getStyle(this, Region.TABBED_PANE_TAB);
			SynthContext context = new SynthContext(this, Region.TABBED_PANE_TAB, style, SynthConstants.ENABLED);
			return style.getInsets(context, null);
		}
	}

	private Insets getTabAreaInsets() {
		Insets i = UIManager.getInsets("TabbedPane.tabAreaInsets");
		if (i != null) {
			return i;
		} else {
			SynthStyle style = SynthLookAndFeel.getStyle(this, Region.TABBED_PANE_TAB_AREA);
			SynthContext context = new SynthContext(this, Region.TABBED_PANE_TAB_AREA, style, SynthConstants.ENABLED);
			return style.getInsets(context, null);
		}
	}

	@Override
	public void doLayout() {
		int tabCount = getTabCount();
		if (tabCount == 0)
			return;
		Insets tabInsets = getTabInsets();
		Insets tabAreaInsets = getTabAreaInsets();
		Insets insets = getInsets();

		int placement = getTabPlacement();

		if (placement == BOTTOM || placement == TOP) {
			int areaWidth = getWidth() - tabAreaInsets.left - tabAreaInsets.right - insets.left - insets.right;
			int tabWidth = 0; // = tabInsets.left + tabInsets.right + 3;
			int gap = 0;

			tabWidth = areaWidth / tabCount;
			gap = areaWidth - (tabWidth * tabCount);
			// "3" is magic number @see BasicTabbedPaneUI#calculateTabWidth
			tabWidth = tabWidth - tabInsets.left - tabInsets.right - 3;
			for (int i = 0; i < tabCount; i++) {
				JLabel l = (JLabel) getTabComponentAt(i);
				if (l == null)
					break;
				l.setPreferredSize(new Dimension(tabWidth + (i < gap ? 1 : 0), l.getPreferredSize().height));
			}

		} else {
			int areaHeight = getHeight() - tabAreaInsets.top - tabAreaInsets.bottom - insets.top - insets.bottom;
			int tabHeight = 0; // = tabInsets.left + tabInsets.right + 3;
			int gap = 0;

			tabHeight = areaHeight / (tabCount);
			gap = areaHeight - (tabHeight * tabCount);
			// "3" is magic number @see BasicTabbedPaneUI#calculateTabWidth
			tabHeight = tabHeight - tabInsets.top - tabInsets.bottom - 3;
			for (int i = 0; i < tabCount; i++) {
				JLabel l = (JLabel) getTabComponentAt(i);
				if (l == null)
					break;
				int width = Math.max(l.getPreferredSize().width,50);
				int height = tabHeight + (i < gap ? 1 : 0);
				l.setPreferredSize(new Dimension(width, height));
			}
		}

		super.doLayout();
	}

	@Override
	public void insertTab(String title, Icon icon, Component component, String tip, int index) {
		super.insertTab(title, icon, component, tip == null ? title : tip, index);
		JLabel label = new JLabel(title, JLabel.CENTER);
		Dimension dim = label.getPreferredSize();
		Insets tabInsets = getTabInsets();
		label.setPreferredSize(new Dimension(0, dim.height + tabInsets.top + tabInsets.bottom));
		setTabComponentAt(index, label);
	}

}
