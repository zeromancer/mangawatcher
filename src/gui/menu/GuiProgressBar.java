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

import gui.GuiFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JProgressBar;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiProgressBar extends JProgressBar {

	private final GuiFrame frame;

	private String text;

	public GuiProgressBar(GuiFrame frame) {
		this.frame = frame;
		text = "Unselected";
		//setStringPainted(true);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.setFont(frame.getOptions().getSubtitelFont());
		String text = getText();
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(Color.BLACK);
		int x = (getWidth() - metrics.stringWidth(text)) / 2;
		int y = metrics.getAscent();
		g.drawString(getText(), x, y);

//		int y = (getHeight() ) / 2 + (+metrics.getDescent() + metrics.getAscent()) / 2;
//		System.out.println(g.getClip());
//		M.print("metrics.getDescent(): " + metrics.getDescent() + " , metrics.getAscent(): " + metrics.getAscent());
//		M.print("metrics.width: " + width + " , metrics.height: " + height);
//		M.print("");
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension((int)d.getWidth(), (int)frame.getOptions().getSubtitelFont().getSize()+3);
//		return super.getPreferredSize();
	}

	//	public void setText(String text){
	//		setString(text);
	//	}

}

// @formatter:off
/*

public @Getter @Setter class GuiProgressBar extends JProgressBar {

	private static final long serialVersionUID = -5807268907142977794L;
	private String text;

	public GuiProgressBar() {
		// TODO Auto-generated constructor stub
		text = "test";
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics();
		// int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		g.setColor(Color.BLACK);
		g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
	}

}


*/