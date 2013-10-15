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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiProgressMenuItem extends JMenuItem {

	private static final long serialVersionUID = -5807268907142977794L;
	private String text;

	private JProgressBar progress;

	public void setMaximum(int maximum) {
		progress.setMaximum(maximum);
	}

	public void setMinimum(int minimum) {
		progress.setMinimum(minimum);
	}

	public void setValue(int value) {
		progress.setValue(value);
	}

	public int getValue() {
		return progress.getValue();
	}

	public GuiProgressMenuItem() {
		// TODO Auto-generated constructor stub
		text = "test";

		progress = new JProgressBar() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				FontMetrics metrics = g.getFontMetrics();
				// int height = metrics.getHeight();
				int width = metrics.stringWidth(text);
				g.setColor(Color.BLACK);
				g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
			}
		};
	}

	// protected void paintComponent(Graphics g) {
	// super.paintComponent(g);
	// g.setColor(Color.BLACK);
	// FontMetrics metrics = g.getFontMetrics();
	// // int height = metrics.getHeight();
	// int width = metrics.stringWidth(text);
	// g.setColor(Color.BLACK);
	// g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
	// }

}