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
package logic;

import gui.downloading.GuiDownloading;
import logic.interfaces.MangaAdd;
import logic.interfaces.MangaAvailable;
import logic.interfaces.MangaUpdate;
import logic.mangareader.ReaderAdd;
import logic.mangareader.ReaderAvailable;
import logic.mangareader.ReaderUpdate;
import lombok.Getter;
import data.Manga.MangaSource;
import data.MangaLibrary;

public @Getter class MangaLogicGroup {

	private MangaAdd add;
	private MangaAvailable available;
	private MangaUpdate update;
	
	public MangaLogicGroup(MangaLibrary library, MangaSource source) {
		assert library != null;
		switch (source) {
		case MANGAREADER:
			add = new ReaderAdd(library);
			available = new ReaderAvailable(library);
			update = new ReaderUpdate(library);
			break;
		default:
			assert false;
			break;
		}
	}
	
	public void setGui(GuiDownloading gui){
		available.setGui(gui);
		add.setGui(gui);
		update.setGui(gui);
	}

}
