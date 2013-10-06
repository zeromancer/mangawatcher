package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import logic.mangafox.FoxAdd;
import logic.mangafox.FoxAvailable;
import logic.mangafox.FoxUpdate;
import logic.mangahere.HereAdd;
import logic.mangahere.HereAvailable;
import logic.mangahere.HereUpdate;
import logic.mangareader.ReaderAdd;
import logic.mangareader.ReaderAvailable;
import logic.mangareader.ReaderUpdate;
import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class MangaLogic {

	public static void add(MangaLibrary library, MangaSource source, String name,MangaCollection collection){

		if(!library.getAvailable().containsKey(source))
			library.getAvailable().put(source, new HashMap<String,String>());
		
		if(!library.getCollections().containsKey(collection))
			library.getCollections().put(collection, new ArrayList<Manga>());
		
		switch (source) {
		case MANGAREADER:
			ReaderAdd.tryAdd(library, name,collection);
			break;
		case MANGAFOX:
			FoxAdd.tryAdd(library, name,collection);
			break;
		case MANGAHERE:
			HereAdd.tryAdd(library,name,collection);
			break;
		default:
			assert false;
			break;
		}
	}
	
	public static void updateShallow(MangaLibrary library, MangaSource source){
		switch (source) {
		case MANGAREADER:
			ReaderUpdate.tryUpdate(library);
			break;
		case MANGAFOX:
			FoxUpdate.tryUpdate(library);
			break;
		case MANGAHERE:
			HereUpdate.tryUpdate(library);
			break;
		default:
			assert false;
			break;
		}
	}

	public static void updateDeep(MangaLibrary library, MangaSource source,Manga manga,int from,int to){
		switch (source) {
		case MANGAREADER:
			ReaderUpdate.tryDownloadChapters(library, manga, from,to);
			break;
		case MANGAFOX:
			FoxUpdate.tryDownloadChapters(library, manga, from,to);
			break;
		case MANGAHERE:
			HereUpdate.tryDownloadChapters(library, manga, from,to);
			break;
		default:
			assert false;
			break;
		}
	}
	
	public static void updateAvailable(MangaLibrary library, MangaSource source){
		
		if(!library.getAvailable().containsKey(source))
			library.getAvailable().put(source, new LinkedHashMap<String,String>());
		
		switch (source) {
		case MANGAREADER:
			ReaderAvailable.tryRefresh(library);
			break;
		case MANGAFOX:
			FoxAvailable.tryRefresh(library);
			break;
		case MANGAHERE:
			HereAvailable.tryRefresh(library);
			break;
		default:
			assert false;
			break;
		}

	}
	
}
