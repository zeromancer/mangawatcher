package data;

import gui.threading.BackgroundExecutors;

import java.util.List;
import java.util.Map;

import logic.LibraryManager;
import lombok.Data;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;

public @Data class MangaLibrary {

	//Configs
	String mangaDirectory;
	String configDirectory;

	//Collections
	Map<MangaCollection, List<Manga>> collections;
	
	//Mangas
	//EnumMap<MangaSource, HashMap<String, Manga>> current;
	Map<MangaSource, Map<String, String>> available;
	
	
//	public Manga getManga(MangaSource source, String name){
//		assert current.get(source).containsKey(name);
//		return current.get(source).get(name);
//	}

	public List<Manga> getCollection(MangaCollection collection){
		assert collections.containsKey(collection);
		return collections.get(collection);
	}
	
	public Map<String, String> getAvailable(MangaSource source) {
		assert available.containsKey(source);
		return available.get(source);
	}

	public String getAvailable(MangaSource source, String name){
		assert available.get(source).containsKey(name);
		return available.get(source).get(name);
	}
	
	public void save(BackgroundExecutors executors){
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				LibraryManager.saveLibrary(configDirectory, MangaLibrary.this);
			}
		});
		
	}
	
	public Manga getManga(String name){
		for(MangaCollection collection : MangaCollection.values())
			for(Manga manga : getCollection(collection))
				if(manga.getName().equals(name))
					return manga;
		return null;
	}
	
}
