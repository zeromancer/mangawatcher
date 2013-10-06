package logic.mangareader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import logic.LibraryManager;
import misc.M;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;
import data.Manga.MangaSource;

public class ReaderAdd {

	public static MangaSource source(){
		return MangaSource.MANGAREADER;
	}
	
	public static void tryAdd(MangaLibrary library,String name, MangaCollection collection){
		try{
			add(library,name,collection);
		}catch (IOException e){
			e.printStackTrace();
			M.print("Error: "+e.getMessage());
		}
	}
	
	public static void add(MangaLibrary library,String name, MangaCollection collection) throws IOException{
		
		Document doc = Jsoup.connect(library.getAvailable(source(), name)).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps.txt"), "UTF-8");
		
		
		Manga manga = new Manga(name,source());
		library.getCollections().get(collection).add(manga);
		
		//Directory
		String manga_directory = library.getMangaDirectory()+File.separator+name;
		File manga_dir = new File(manga_directory);
		if(!Files.exists(Paths.get(manga_directory)))
			manga_dir.mkdirs();

		// Image
		Elements links2 = doc.select("div[id=mangaimg]");
		Elements links = links2.get(0).select("img[src]");
		for (Element element: links){
			String url = element.attr("src");
			
			M.print("\t"+url);
			//String extension = url.substring(url.length()-3);
			//String filename = String.format("%s.%s",manga.getName(),extension);
			String filename = String.format("%s.jpg",manga.getName());
			M.print(""+filename);
			BufferedImage image = ImageIO.read(new URL(url));
			//ImageIO.write(image, extension,new File(manga_dir.getAbsolutePath(),filename));			
			ImageIO.write(image, "jpg",new File(manga_dir.getAbsolutePath(),filename));
		}
		
		// Description
		links = doc.select("div[id=readmangasum]");
		for (Element element: links){
			M.print(element.text());
			manga.setDescription(element.text());			
		}
		
		M.print(manga.getHomePage(library));
		// Content
		//MangaUpdater.downloadChapters(library, manga, 0, Integer.MAX_VALUE);
	}
	

	public static void main(String[] args) {
		
		String configDirectory = "config";
		MangaLibrary library = LibraryManager.loadLibrary(configDirectory);
		ReaderAdd.tryAdd(library, "Baby Steps",MangaCollection.WATCHING);
		LibraryManager.saveLibrary(configDirectory, library);
	}
	
}
