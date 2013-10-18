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
package data;

import java.awt.Font;

import lombok.Data;

public @Data class Options {


	private Font buttonFont;
	private Font titelFont;
	private Font subtitelFont;
	private Font labelFont;
	private Font textFont;

	private int checkInterval; // in minutes
	private int scrollAmount;
	
	private int readingZoom;
	private int readingScroll;
	
	private String lastImageCopyLocation;
	
	public Options() {
	}
	
	public void init(){
		titelFont = new Font("Sans", Font.BOLD, 48);
		subtitelFont = new Font("Sans", Font.BOLD, 28);
		labelFont = new Font("Sans", Font.PLAIN, 16);
		buttonFont = new Font("Sans", Font.PLAIN, 16);
		textFont = new Font("Sans", Font.PLAIN, 12);

		checkInterval = 10;
		scrollAmount = 50;
		
		readingZoom = 100;
		readingScroll = 100;
	}
	
	
}
