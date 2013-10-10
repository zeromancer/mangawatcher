package logic.mangareader;

import gui.downloading.GuiDownloading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import logic.interfaces.MangaAvailable;
import lombok.Setter;
import misc.M;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Manga.MangaSource;
import data.MangaLibrary;

public @Setter class ReaderAvailable implements MangaAvailable {

	private final MangaLibrary library;
	private GuiDownloading gui;
	
	public ReaderAvailable(MangaLibrary library) {
		this.library = library;
	}

	public void tryRefresh(){
		try {
			refresh();
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}
	
	public void refresh() throws IOException{

		ArrayList<String> tobooList = new ArrayList<>(Arrays.asList(
				"Advanced Search", "Popular Manga", "Manga List",
				"Latest Releases", "Surprise Me!", "Anime", "Anime Downloads",
				"Manga", "Privacy Statements", "Free File Hosting",
				"Watch One Piece", "Anime Online", "Good Anime", "Memes"));
		
//		ArrayList<String>all = new ArrayList<>();
		HashMap<String, String>all = new HashMap<>();
		
		Document doc = Jsoup.connect("http://www.mangareader.net/alphabetical").userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-all.txt"), "UTF-8");
		Elements elements = doc.select("a[href]");
		for(Element element : elements){
			String destination = element.attr("href");
			String text = element.text();
			
			if(text.length()>2 && !tobooList.contains(text)){
				print(text+" , "+destination);
				//all.add(text);
				all.put(text, "http://www.mangareader.net"+destination);
			}
		}
		
//		HashSet<String> set = new HashSet<>(library.getAll());
//		set.addAll(all);
//		library.setAll(all);
		library.getAvailable().get(MangaSource.MANGAREADER).clear();
		library.getAvailable().get(MangaSource.MANGAREADER).putAll(all);;
		print("all size: "+all.size());
	}

	private void progress(int progress, String text){
		if(gui == null){
			M.print(text);
		}else{
			gui.progress(source(), progress, text);
		}
	}	
	private void print(String text){
		if(gui == null){
			M.print(text);
		}else{
			gui.text(source(), text);
		}
	}

	public static MangaSource source(){
		return MangaSource.MANGAREADER;
	}

	public void setGui(GuiDownloading gui) {
		this.gui = gui;
	}
}
