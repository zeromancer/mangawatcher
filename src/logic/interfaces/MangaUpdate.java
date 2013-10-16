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
package logic.interfaces;

import gui.downloading.GuiDownloading;

import java.io.IOException;

import data.Manga;

public interface MangaUpdate {

	public void tryUpdateShallow();

	public void updateShallow() throws IOException;

	public void tryUpdateDeep(Manga manga, int from);

	public void setGui(GuiDownloading gui);
}
