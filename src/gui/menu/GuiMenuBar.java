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

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GuiMenuBar extends JPanel {

	JButton button;

	public GuiMenuBar() {
		// TODO Auto-generated constructor stub
		button = new JButton("test");
		button.setPreferredSize(new Dimension(25, 16));
		add(button);
	}
	
	public void info(int progress,String text){
		
	}

}
