package data;

import java.util.List;
import java.util.Map;

import logic.LibraryManager;
import logic.MangaLogic;
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
	
	public void updateShallow(){
		for (MangaSource source : MangaSource.values())
			MangaLogic.updateShallow(this, source);
	}
	public void updateAvailable(){
		for (MangaSource source : MangaSource.values())
			MangaLogic.updateAvailable(this, source);
	}
	public void add(MangaSource source, String name, MangaCollection collection){
		MangaLogic.add(this, source, name,collection);
	}
	public void save(){
		LibraryManager.saveLibrary(configDirectory, this);
	}
	
}
