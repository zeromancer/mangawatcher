package logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import misc.M;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class LibraryManager {

	public static MangaLibrary loadLibrary(String configDirectory){
		
		File configDir = new File(configDirectory);
		if(!Files.exists(Paths.get(configDir.toURI())))
			configDir.mkdirs();
		
		File configFile = new File(configDirectory+File.separator+"config.json");
		
		if(!Files.exists(Paths.get(configFile.toURI())))
			return initLibrary(configDirectory);
		
		try {
			JsonReader reader = new JsonReader(new FileReader(configFile));
			Gson gson = new Gson();
			MangaLibrary library = gson.fromJson(reader, MangaLibrary.class);
			assert library != null;
			return library;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			M.print(e.getMessage());
			return null;
		}
	}

	public static void saveLibrary(String configDirectory, MangaLibrary library){
		try {
			File configDir = new File(configDirectory);
			if(!Files.exists(Paths.get(configDir.toURI())))
				configDir.mkdirs();
			
			assert library != null;
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String output = gson.toJson(library);
			
			File file = new File(configDirectory+File.separator+"config.json");
			//M.print(file.getAbsolutePath());
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	        writer.write(output);
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}
	
	public static MangaLibrary initLibrary(String configDirectory){
		MangaLibrary library = new MangaLibrary();
		
		//mangaDirectory
		String os = (System.getProperty("os.name")).toUpperCase();
		String mangaDirectory = null;
		if(os.contains("WIN"))
			mangaDirectory = "C://Mangas/";
		else
			mangaDirectory = System.getProperty("user.home")+File.separator+"Mangas";
		library.setMangaDirectory(mangaDirectory);
		
		//configDirectory
		library.setConfigDirectory(new File(configDirectory).getAbsolutePath());
		
		//maps
		Map<MangaCollection, List<Manga>> collections = new LinkedHashMap<>();
//		Map<MangaSource, Map<String, Manga>> current = new EnumMap<>(MangaSource.class);
		Map<MangaSource, Map<String, String>> available = new LinkedHashMap<>();
		
		for(MangaCollection collection : MangaCollection.values())
			collections.put(collection, new ArrayList<Manga>());
		
//		for(MangaSource source: MangaSource.values())
//			current.put(source, new HashMap<String,Manga>());

		for(MangaSource source: MangaSource.values())
			available.put(source, new HashMap<String,String>());
		
		library.setCollections(collections);
//		library.setCurrent(current);
		library.setAvailable(available);
		
		MangaLogic logic = new MangaLogic(library);
		logic.updateAvailable();
		
		return library;
	}
	
	public static void main(String[] args) {
		String configDirectory = "config";
		MangaLibrary library = loadLibrary(configDirectory);
		saveLibrary(configDirectory, library);
	}
	
}
/*


////			XStream x = new XStream(new StaxDriver());
//			XStream x = new XStream();
//			return (MangaLibrary)x.fromXML(configFile);

//			XStream x = new XStream(new StaxDriver());
//			XStream x = new XStream();
//			String output = x.toXML(library);




//				Yaml yaml = new Yaml();
//				return yaml.loadAs(new FileInputStream(configFile), MangaLibrary.class);
//				//return (MangaLibrary)yaml.load(new FileInputStream(configFile));

//			DumperOptions options = new DumperOptions();
//			options.setDefaultFlowStyle(FlowStyle.BLOCK);
//			Yaml yaml = new Yaml(options);
//			Yaml yaml = new Yaml();
//			String output = yaml.dump(library);



*/