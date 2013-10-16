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
		progressStartIndeterminate("Refreshing mangareader.net list");
		try {
			refresh();
			print("Successfully refreshed available mangareader.net manga list");
			progressEnd("Successfully refreshed manga list");
		} catch (IOException e) {
			e.printStackTrace();
			print("Error: "+e.getMessage());
			print("Failed to refresh available mangareader.net manga list");
			progressEnd("Failed to refresh manga list");
		}
	}
	
	public void refresh() throws IOException{
		ArrayList<String> tobooList = new ArrayList<>(Arrays.asList(
				"Home","Contact Us",
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
				print(text+"  ,  "+"www.mangareader.net"+destination);
				all.put(text, "http://www.mangareader.net"+destination);
			}
		}
		library.getAvailable().get(MangaSource.MANGAREADER).clear();
		library.getAvailable().get(MangaSource.MANGAREADER).putAll(all);
		print("Number of Mangas in Database: "+all.size());
	}


	public void progressStart(String text) {
		if(gui == null)
			M.print(text);
		else{
			gui.textInvoked(text);
			gui.progressStartInvoked(text);
		}
	}

	public void progressStartIndeterminate(String text) {
		if(gui == null)
			M.print(text);
		else{
			gui.textInvoked(text);
			gui.progressStartIndeterminateInvoked(text);
		}
	}

	public void progress(int percent, String text) {
		if(gui == null)
			M.print(text);
		else
			gui.progressInvoked(percent,text);
	}
	public void progress(int percent) {
		if(gui != null)
			gui.progressInvoked(percent);
	}

	public void progressEnd(String text) {
		text +="\n";
		if(gui == null)
			M.print(text);
		else
			gui.progressEndInvoked(text);
	}
	
	public void print(String text) {
		text = "    "+text;
		if(gui == null)
			M.print(text);
		else
			gui.textInvoked(text);
	}

	public static MangaSource source(){
		return MangaSource.MANGAREADER;
	}

	public void setGui(GuiDownloading gui) {
		this.gui = gui;
	}
}
