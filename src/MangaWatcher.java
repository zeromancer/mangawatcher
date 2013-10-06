import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import data.Manga;
import data.Manga.MangaSource;


public class MangaWatcher {

	HashMap<String, Manga> mangas;
	
	public MangaWatcher() {
		mangas = new HashMap<>();

		Manga m1 = new Manga("Naruto",MangaSource.MANGAREADER);
		mangas.put(m1.getName(),m1);
		
		Manga m2 = new Manga("Bleach",MangaSource.MANGAREADER);
		mangas.put(m2.getName(),m2);
		
		Manga m3 = new Manga("Baby Steps",MangaSource.MANGAREADER);
		mangas.put(m3.getName(),m3);
	}

	public void load_config(){
		
	}
	
	public void save_config(){
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		Yaml yaml = new Yaml(options);
		String output = yaml.dump(mangas);
//		File file = new File(destination.getAbsolutePath(),filename);
//		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//        writer.write(output);
//        writer.close();
	}
	
	public void try_check(){
		try{
			check();
		}catch (IOException error){
			error.printStackTrace();
			print("Error: "+error.getMessage());
		}
	}
	
	public void check() throws IOException{
		File input = new File("test-data/mangareader-releases.txt");
		Document doc = Jsoup.parse(input, "UTF-8");
		Elements links = doc.select("a[class=chaptersrec]"); // a with href
		
		for (Element e: links){
			for (Manga manga : mangas.values())
				if(e.text().startsWith(manga.getName())){
					print("manga="+manga.toString());
					int released = Integer.parseInt(e.text().replace(manga.getName()+" ", ""));
					manga.setReleased(released);
					print("new release="+released);
					int downloaded = manga.getDownloaded(); 
					if( downloaded < released){
						try{
							downloadChapters(manga,downloaded+1,released);
							manga.setDownloaded(downloaded);
						}catch (IOException error){
							error.printStackTrace();
							print("Error: "+error.getMessage());
							
						}
					}
				}
		}
		
		
		//Elements pngs = doc.select("img[src$=.png]");
		// img with src ending .png
		//Element masthead = doc.select("div.masthead").first();
	}
	
	public boolean downloadChapters(Manga manga,int from, int to) throws IOException{
		print("downloading "+manga.getName()+" chapters: "+from+" -> "+to);
		//Document doc = Jsoup.connect("").userAgent("Mozilla").get();
		HashSet<Integer> downloaded = new HashSet<>();
		Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps.txt"), "UTF-8");
		Elements elements = doc.select("a[href]");
		for(Element element : elements){
			String text = element.text();
			String link = element.attr("href");
			
//			String release_str =text.replace(manga.getName()+" ", ""); 
//			int release = release_str.equals("")? 0 : Integer.parseInt(release_str);
			
//			
			
			if(text.startsWith(manga.getName())){
				int release = Integer.parseInt(text.replace(manga.getName()+" ", ""));				
				

				if(release!=190)
					continue;
				
				if(release >=from && !downloaded.contains(release)){
					print(""+manga.getName()+" "+release+" -> "+link);
					downloaded.add(release);
					downloadChapterPages(manga,release,"http://www.mangareader.net"+link);
				}
				
			}
			
		}
		return true;
	}
	

	public void downloadChapterPages(Manga manga,int chapter,String chapterLink) throws IOException{
		String name = manga.getName();
		String filename = "Mangas"+File.separator+name+File.separator+String.format("%04d", chapter);
		File destination = new File(filename);
		if(!Files.exists(Paths.get(filename)))
			destination.mkdirs();
		
		ArrayList<String> images = new ArrayList<>();
		
		//Document doc = Jsoup.connect(chapterLink).userAgent("Mozilla").get();
		Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps-10.txt"), "UTF-8");
		Elements elements = doc.select("option");
		
		for(Element element : elements){
			//print(""+element.text());
			int page = Integer.parseInt(element.text());
			//print(""+element.attr("value"));
			
//			if(page!=1)
//				continue;
			
			String link = element.attr("value");
			String image = downloadChapterPageImage(manga, chapter, page, "http://www.mangareader.net"+link,destination);
			images.add(image);
		}
		if(!images.isEmpty())
			generateHTML(manga, chapter, destination, images);
		
	}

	public String downloadChapterPageImage(Manga manga, int chapter, int page, String pageLink,File destination) throws IOException{
		Document doc = Jsoup.connect(pageLink).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps-10.txt"), "UTF-8");
		Elements elements = doc.select("img[id=img]");
		for(Element element : elements){
			String url = element.attr("src");
			
			print("\t"+url);
			String extension = url.substring(url.length()-3);
			String filename = String.format("%s_%04d_%03d.%s",manga.getName(),chapter,page,extension);
			print(""+filename);
			BufferedImage image = ImageIO.read(new URL(url));
			ImageIO.write(image, extension,new File(destination.getAbsolutePath(),filename));
			return filename;
		}
		return ".";
	}
	
	public void generateHTML(Manga manga,int chapter,File destination, ArrayList<String>list) throws IOException{
		String name = manga.getName();
		// start
		String string = "";
		string += "<!DOCTYPE>\n";
		string += "<html><head><title>"+name+" "+chapter +"</title></head>\n";
		string += "<body>";
		string += "<table border=\"0\" align=\"center\">\n";
		string += "<tr><th><h1>"+name+" "+chapter +"</h1></th></tr>\n";
		// images
		for (String image : list) {
			string += "<tr><th>";
			string += "<img src=\""+image+"\"/>";
			string += "</th></tr>\n";
		}
		// next page	
		string += "<tr><th><h2>";
		string += "<a href=\""+".."+File.separator+String.format("%04d",chapter+1 );
		string += File.separator+String.format("%s_%04d.html",name,chapter+1)+"\">";
		string += "next chapter"+"</a>";
		string += "</h2></th></tr>\n";
		// end
		string += "</table>";
		string += "</body>\n";
		// save
		String filename = String.format("%s_%04d.html",name,chapter);
		File file = new File(destination.getAbsolutePath(),filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(string);
        writer.close();
	}

	
	
	public void start(){
		load_config();
		try_check();
		save_config();
	}
	
	
	public static void main(String[] args) {
		
		
		MangaWatcher manga = new MangaWatcher();
		manga.start();
		
	}
	
	public static void print(String text){
		System.out.println(text);
	}
}





/*




	public void openboxLaunch(){
		// mark as read
		// launch browser
	}
	
	public void openboxMenu(){

		String output = "";
		output += "<openbox_pipe_menu>";
		output += "<separator label=\"Mangareader.net\" />";

		for(Manga manga : mangas.values()){
			if(manga.getRead()<manga.getDownloaded()){
				output += "<item label=\""+manga.getName()+" "+manga.getRead()+" -> "+manga.getDownloaded()+"\">";
				output += " <action name=\"Execute\"><command>";
				output += "  execute this with openboxLaunch ";
				output += "	</command></action></item>";				
			}
		}
		
		output += "<item label=\"No new Manga available at the moment\"></item>";
		output += "<separator label=\" Do something else?\" />";
		output += "<item label=\" Edit Manga List\">";
		output += "	<action name=\"Execute\"><command>";
		output += "		geany $mangaList";
		output += "	</command></action></item>";
		
		output += "<item label=\" Edit Manga Script\">";
		output += "	<action name=\"Execute\"><command>";
		output += "		geany $0";
		output += "	</command></action></item>";
		output += "</openbox_pipe_menu>";	
	}


*/