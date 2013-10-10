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
			for(Manga manga : library.getCollection(collection))
				map.get(manga.getSource()).getUpdate().tryUpdateDeep(manga, manga.getDownloaded()+1);
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
