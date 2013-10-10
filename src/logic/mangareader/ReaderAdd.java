package logic.mangareader;

import gui.downloading.GuiDownloading;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import logic.interfaces.MangaAdd;
import misc.M;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class ReaderAdd implements MangaAdd{

	private final MangaLibrary library;
	private GuiDownloading gui;
	
	public ReaderAdd(MangaLibrary library) {
		this.library = library;
	}
	
	public void tryAdd(String name, MangaCollection collection){
		try{
			add(name,collection);
		}catch (IOException e){
			e.printStackTrace();
			print("Error: "+e.getMessage());
		}
	}
	
	public void add(String name, MangaCollection collection) throws IOException{
		progress(0,"Adding Manga: "+name);
		Document doc = Jsoup.connect(library.getAvailable(source(), name)).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps.txt"), "UTF-8");
		
		
		Manga manga = new Manga(name,source());
		library.getCollections().get(collection).add(manga);
		
		//Directory
		String manga_directory = manga.getMangaDirectory(library);
		File manga_dir = new File(manga_directory);
		progress(25,"Creating "+name+" Directory: "+manga_dir.getAbsolutePath());
		if(!Files.exists(Paths.get(manga_directory)))
			manga_dir.mkdirs();
		
		// Image
		Elements links2 = doc.select("div[id=mangaimg]");
		Elements links = links2.get(0).select("img[src]");
		for (Element element: links){
			String url = element.attr("src");
			String filename = String.format("%s.jpg",manga.getName());
			progress(50,"Downloading Image: "+filename+" from "+url);
			BufferedImage image = ImageIO.read(new URL(url));
			BufferedImage scaled = M.scale(image, image.getHeight(), 340);
			ImageIO.write(scaled, "jpg",new File(manga_dir.getAbsolutePath(),filename));
		}
		
		// Description
		links = doc.select("div[id=readmangasum]");
		for (Element element: links){
			String description = element.text();
			description = description.replace("Read "+name+" Manga Online ", "");
			progress(75,"Parsing Description: "+description.substring(0, Math.min(30, description.length())));
			manga.setDescription(description);
		}
		
		progress(75,"Successfully added "+name+" to Library Collection "+collection.getName());
		//print(manga.getHomePage(library));
		
		// Content
		//MangaUpdater.downloadChapters(library, manga, 0, Integer.MAX_VALUE);
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
