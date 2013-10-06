package logic.mangareader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import logic.LibraryManager;
import misc.M;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.MangaLibrary;
import data.Manga.MangaSource;

public class ReaderAvailable {

	
	public static void tryRefresh(MangaLibrary library){
		try {
			refresh(library);
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
			//TODO -> show error message dialog, report problem
		}
	}
	
	public static void refresh(MangaLibrary library) throws IOException{

		ArrayList<String> tobooList = new ArrayList<>(Arrays.asList(
				"Advanced Search", "Popular Manga", "Manga List",
				"Latest Releases", "Surprise Me!", "Anime", "Anime Downloads",
				"Manga", "Privacy Statements", "Free File Hosting",
				"Watch One Piece", "Anime Online", "Good Anime", "Memes"));
		
//		ArrayList<String>all = new ArrayList<>();
		HashMap<String, String>all = new HashMap<>();
		
		//Document doc = Jsoup.connect("http://www.mangareader.net/alphabetical").userAgent("Mozilla").get();
		Document doc = Jsoup.parse(new File("test-data/mangareader-all.txt"), "UTF-8");
		Elements elements = doc.select("a[href]");
		for(Element element : elements){
			String destination = element.attr("href");
			String text = element.text();
			
			if(text.length()>2 && !tobooList.contains(text)){
				M.print(text+" , "+destination);
				//all.add(text);
				all.put(text, "http://www.mangareader.net"+destination);
			}
		}
		
//		HashSet<String> set = new HashSet<>(library.getAll());
//		set.addAll(all);
//		library.setAll(all);
		library.getAvailable().get(MangaSource.MANGAREADER).clear();
		library.getAvailable().get(MangaSource.MANGAREADER).putAll(all);;
		M.print("all size: "+all.size());
	}
	

	public static void main(String[] args) {
		
		
		String configDirectory = "config";
		MangaLibrary library = LibraryManager.loadLibrary(configDirectory);
		tryRefresh(library);
		LibraryManager.saveLibrary(configDirectory, library);
	}

}
