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
import gui.threading.BackgroundExecutors;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import logic.interfaces.MangaUpdate;
import lombok.Setter;
import misc.M;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public @Setter class ReaderUpdate implements MangaUpdate {

	private final MangaLibrary library;
	private BackgroundExecutors executors;
	
	private GuiDownloading gui;
	private boolean spacing = false;
	
	public ReaderUpdate(MangaLibrary library) {
		this.library = library;
	}


	public void tryUpdateShallow() {
		progressStart(M.getTimeHHMMSS()+" Updating from latest releases");
		print("Updating mangas from latest mangareader.net releases");
		try {
			updateShallow();
			print("Successfully updated mangas from latest mangareader.net releases");
			progressEnd("Successfully updated latest releases");
		} catch (IOException e) {
			M.exception(e);
			print("Error: " + e.getMessage());
			print("Failed to update mangas from latest mangareader.net releases");
			progressEnd("Failed to update from latest releases");
		}
	}

	public void updateShallow() throws IOException {
		
		Document doc = Jsoup.connect("http://www.mangareader.net/latest").userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-releases.txt"), "UTF-8");
		Elements links = doc.select("a[class=chaptersrec]"); // a with href

		List<Manga> mangas = new ArrayList<Manga>();
		//		for(MangaCollection collection : MangaCollection.values())
		MangaCollection[] collections = { MangaCollection.WATCHING, MangaCollection.PLANNING };
		for (MangaCollection collection : collections)
			for (Manga manga : library.getCollection(collection))
				if (manga.getSource() == source())
					mangas.add(manga);

		for (Element element : links) {
			for (Manga manga : mangas) {
				if (element.text().startsWith(manga.getName()))
					downloadChapters(manga, manga.getDownloaded() + 1);
			}
		}
	}

	public void tryUpdateDeep(Manga manga, int from) {
		progressStart(M.getTimeHHMMSS()+" Updating manga "+manga.getName());
		print("Updating manga "+manga.getName());
		try {
			downloadChapters(manga, from);
			print("Successfully updated Manga "+manga.getName());
			progressEnd("Successfully updated "+manga.getName());
		} catch (IOException e) {
			e.printStackTrace();
			print("Error: " + e.getMessage());
			progressEnd("Failed to update manga "+manga.getName());
			progressEnd("Failed to update "+manga.getName());
		}
		
	}

	public void downloadChapters(final Manga manga, final int from) throws IOException {
		assert manga != null;
		HashSet<Integer> downloaded = new HashSet<>();
		
		Document doc = Jsoup.connect(manga.getHomePage(library)).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps.txt"), "UTF-8");
		Elements elements = doc.select("a[href]");
		
		for(int i = elements.size()-1; i>=0; i--){
			Element element = elements.get(i);
			String text = element.text();
			if(!text.startsWith(manga.getName()))
				elements.remove(i);
		}
		
		Collections.sort(elements, new Comparator<Element>() {
			@Override
			public int compare(Element e1, Element e2) {
				int release1 = Integer.parseInt(e1.text().replace(manga.getName() + " ", ""));
				int release2 = Integer.parseInt(e2.text().replace(manga.getName() + " ", ""));
				return release1 - release2;
			}
		});
		
		for (Element element : elements) {
			String text = element.text();
			String link = "http://www.mangareader.net" + element.attr("href");
			
			int newRelease = Integer.parseInt(text.replace(manga.getName() + " ", ""));
			int oldRelease = manga.getReleased();
			if(newRelease > oldRelease)
				manga.setReleased(newRelease);
			
			if(newRelease >= from && !downloaded.contains(newRelease)){
				print("" + manga.getName() + " " + newRelease + " -> " + link);
				
				downloaded.add(newRelease);
				downloadChapterPages( manga, newRelease, link);
				
				if(newRelease>manga.getDownloaded())
					manga.setDownloaded(newRelease);
				
				if(downloaded.size()%3 == 2){
					print("Downloaded 2 or more chapters:");
					print("Saving library to "+library.getConfigDirectory());
					if(executors!=null)
						library.save(executors);
					else
						library.save();
				}
			}
		}

		generateMangaHTML(manga, from, manga.getDownloaded());
	}

	public void downloadChapterPages(Manga manga, int chapter, String chapterLink) throws IOException {
		
		progress("Downloading "+manga.getName()+" "+chapter);
		
		String filename = manga.getMangaDirectory(library, chapter);
		File destination = new File(filename);
		if (!Files.exists(Paths.get(filename)))
			destination.mkdirs();

		ArrayList<String> images = new ArrayList<>();

		Document doc = Jsoup.connect(chapterLink).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps-10.txt"), "UTF-8");
		Elements elements = doc.select("option");

		for(int i = 0; i < elements.size();i++){
			progress((i+1)*100/elements.size());
			
			Element element = elements.get(i);
			int page = Integer.parseInt(element.text());
			String link = element.attr("value");
			String image = downloadChapterPageImage(manga, chapter, page, "http://www.mangareader.net" + link, destination);
			images.add(image);
		}
		if (!images.isEmpty())
			generateChapterHTML(manga, chapter, destination, images);

	}

	public String downloadChapterPageImage(Manga manga, int chapter, int page, String pageLink, File destination) throws IOException {
		Document doc = Jsoup.connect(pageLink).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps-10.txt"), "UTF-8");
		Elements elements = doc.select("img[id=img]");
		for (Element element : elements) {
			String url = element.attr("src");
			String extension = url.substring(url.length() - 3);
			String filename = String.format("%s_%04d_%03d.%s", manga.getName(), chapter, page, extension);
			print("Downloading image: "+filename+"  from: "+url);
			try {
				BufferedImage image = ImageIO.read(new URL(url));
				if(image!=null)
					ImageIO.write(image, extension, new File(destination.getAbsolutePath(), filename));	
			} catch (IOException e) {
				M.exception(e);
				print("Error: " + e.getMessage());
				M.print("Error: " + e.getMessage());
			}
			return filename;
		}
		return ".";
	}

	public void generateChapterHTML(Manga manga, int chapter, File destination, ArrayList<String> list) throws IOException {
		String name = manga.getName();
		// start
		String string = "";
		string += "<!DOCTYPE>\n";
		string += "<html><head><title>" + name + " " + chapter + "</title></head>\n";
		string += "<body>";
		string += "<table border=\"0\" align=\"center\">\n";
		string += "<tr><th><h1>" + name + " " + chapter + "</h1></th></tr>\n";
		// images
		for (String image : list) {
			string += "<tr><th>";
			string += "<img src=\"" + image + "\"/>";
			string += "</th></tr>\n";
		}
		// next page	
		string += "<tr><th><h2>";
		string += "<a href=\"" + ".." + File.separator + String.format("%04d", chapter + 1);
		string += File.separator + String.format("%s_%04d.html", name, chapter + 1) + "\">";
		string += "next chapter" + "</a>";
		string += "</h2></th></tr>\n";
		// end
		string += "</table>";
		string += "</body>\n";
		// save
		String filename = String.format("%s_%04d.html", name, chapter);
		File file = new File(destination.getAbsolutePath(), filename);
		print("Generating chapter html file in: "+file.getAbsolutePath());
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(string);
		writer.close();
	}

	public void generateMangaHTML(Manga manga, int from, int to) {

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
	public void progress(String text) {
		if(gui != null)
			gui.progressInvoked(text);
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
		this.executors = gui.getExecutors();
	}
	
}
