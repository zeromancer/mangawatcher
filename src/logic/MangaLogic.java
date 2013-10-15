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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class MangaLogic {

	private final MangaLibrary library;

	private final Map<MangaSource, MangaLogicGroup> map;

	public MangaLogic(MangaLibrary library) {
		this.library = library;
		map = new HashMap<>();
		for (MangaSource source : MangaSource.values())
			map.put(source, new MangaLogicGroup(library, source));
	}

	public void add(MangaSource source, String name, MangaCollection collection) {

		if (!library.getAvailable().containsKey(source))
			library.getAvailable().put(source, new HashMap<String, String>());

		if (!library.getCollections().containsKey(collection))
			library.getCollections().put(collection, new ArrayList<Manga>());

		map.get(source).getAdd().tryAdd(name, collection);
	}

	public void updateShallow() {
		for (MangaSource source : MangaSource.values())
			updateShallow(source);
	}

	public void updateShallow(MangaSource source) {
		map.get(source).getUpdate().tryUpdateShallow();
	}
	public void updateDeep() {
		for (MangaCollection collection : MangaCollection.values())
			for(int i = 0;i< library.getCollection(collection).size();i++){
				Manga manga = library.getCollection(collection).get(i);
				map.get(manga.getSource()).getUpdate().tryUpdateDeep(manga, manga.getDownloaded()+1);
			}				
	}

	public void updateDeep(Manga manga) {
		updateDeep(manga, manga.getDownloaded() + 1);
	}

	public void updateDeep(Manga manga, int from) {
		MangaSource source = manga.getSource();
		map.get(source).getUpdate().tryUpdateDeep(manga, from);
	}

	public void updateAvailable() {
		for (MangaSource source : MangaSource.values())
			updateAvailable(source);
	}

	public void updateAvailable(MangaSource source) {

		if (!library.getAvailable().containsKey(source))
			library.getAvailable().put(source, new LinkedHashMap<String, String>());

		map.get(source).getAvailable().tryRefresh();

	}

	public void setGui(GuiDownloading gui){
		for (MangaSource source : MangaSource.values())
			map.get(source).setGui(gui);
	}
}
