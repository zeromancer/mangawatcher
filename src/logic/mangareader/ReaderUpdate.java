package logic.mangareader;

import gui.downloading.GuiDownloading;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	private GuiDownloading gui;
	
	public ReaderUpdate(MangaLibrary library) {
		this.library = library;
	}


	public void tryUpdateShallow() {
		try {
			updateShallow();
		} catch (IOException e) {
			e.printStackTrace();
			progress(100,"Error: " + e.getMessage());
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

		for (Element e : links) {
			for (Manga manga : mangas)
				if (e.text().startsWith(manga.getName())) {
					print("manga=" + manga.toString());
					int released = Integer.parseInt(e.text().replace(manga.getName() + " ", ""));
					manga.setReleased(released);
					print("new release=" + released);
					int downloaded = manga.getDownloaded();
					if (downloaded < released) {
						try {
							downloadChapters(manga, downloaded + 1, released);
							manga.setDownloaded(downloaded);
						} catch (IOException error) {
							error.printStackTrace();
							M.print("Error: " + error.getMessage());

						}
					}
				}
		}
	}

	public void tryUpdateDeep(Manga manga, int from, int to) {
		try {
			downloadChapters(manga, from, to);
		} catch (IOException e) {
			e.printStackTrace();
			print("Error: " + e.getMessage());
		}
	}

	public void downloadChapters(Manga manga, int from, int to) throws IOException {
		print("downloading " + manga.getName() + " chapters: " + from + " -> " + to);

		HashSet<Integer> downloaded = new HashSet<>();
		int max = to == Integer.MAX_VALUE ? from : to;

		Document doc = Jsoup.connect(manga.getHomePage(library)).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps.txt"), "UTF-8");
		Elements elements = doc.select("a[href]");
		for (Element element : elements) {
			String text = element.text();
			String link = element.attr("href");

			if (text.startsWith(manga.getName())) {
				int release = Integer.parseInt(text.replace(manga.getName() + " ", ""));
				max = Math.max(max, release);

				if (release >= from && !downloaded.contains(release)) {
					print("" + manga.getName() + " " + release + " -> " + link);
					downloaded.add(release);
					downloadChapterPages( manga, release, "http://www.mangareader.net" + link);
				}

			}

		}

		generateMangaHTML(manga, from, max);
	}

	public void downloadChapterPages(Manga manga, int chapter, String chapterLink) throws IOException {
		String name = manga.getName();
		String filename = library.getMangaDirectory() + File.separator + name + File.separator + String.format("%04d", chapter);
		File destination = new File(filename);
		if (!Files.exists(Paths.get(filename)))
			destination.mkdirs();

		ArrayList<String> images = new ArrayList<>();

		Document doc = Jsoup.connect(chapterLink).userAgent("Mozilla").get();
		//Document doc = Jsoup.parse(new File("test-data/mangareader-baby-steps-10.txt"), "UTF-8");
		Elements elements = doc.select("option");

		for (Element element : elements) {
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

			print("\t" + url);
			String extension = url.substring(url.length() - 3);
			String filename = String.format("%s_%04d_%03d.%s", manga.getName(), chapter, page, extension);
			print("" + filename);
			BufferedImage image = ImageIO.read(new URL(url));
			ImageIO.write(image, extension, new File(destination.getAbsolutePath(), filename));
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
		print("Saving html index file to: "+file.getAbsolutePath());
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(string);
		writer.close();
	}

	public void generateMangaHTML(Manga manga, int from, int to) {

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
