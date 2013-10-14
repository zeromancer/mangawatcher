package data;

import gui.threading.BackgroundExecutors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import logic.DiskIOManager;
import logic.MangaLogic;
import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;

import data.Manga.MangaCollection;
import data.Manga.MangaSource;

public @Getter @Setter class MangaLibrary {

	// Configs
	@Expose private String mangaDirectory;
	private String configDirectory;

	// Mangas
	@Expose private Map<MangaCollection, List<Manga>> collections;
	private Map<MangaSource, Map<String, String>> available;


	public MangaLibrary(String configDirectory) {

		String os = (System.getProperty("os.name")).toUpperCase();
		if (os.contains("WIN"))
			mangaDirectory = "C://Mangas/";
		else
			mangaDirectory = System.getProperty("user.home") + File.separator + "Mangas";

		this.configDirectory = new File(configDirectory).getAbsolutePath();

		collections = new LinkedHashMap<>();
		for (MangaCollection collection : MangaCollection.values())
			collections.put(collection, new ArrayList<Manga>());
		
		available = new LinkedHashMap<>();
		for (MangaSource source : MangaSource.values())
			available.put(source, new HashMap<String, String>());

		MangaLogic logic = new MangaLogic(this);
		logic.updateAvailable();

	}
	
	public MangaLibrary(String mangaDirectory, String configDirectory, Map<MangaCollection, List<Manga>> collections,
			Map<MangaSource, Map<String, String>> available) {
		assert mangaDirectory != null;
		assert configDirectory != null;
		assert collections != null;
		assert available != null;
		this.mangaDirectory = mangaDirectory;
		this.configDirectory = configDirectory;
		this.collections = collections;
		this.available = available;
	}


	
	public List<Manga> getCollection(MangaCollection collection) {
		assert collections.containsKey(collection) : collection + " , " + collections.toString();
		return collections.get(collection);
	}

	public Map<String, String> getAvailable(MangaSource source) {
		assert available.containsKey(source);
		return available.get(source);
	}

	public String getAvailable(MangaSource source, String name) {
		assert available.get(source).containsKey(name);
		return available.get(source).get(name);
	}


	public void save() {
		DiskIOManager.saveLibrary(MangaLibrary.this);
	}
	public void saveAvailable() {
		DiskIOManager.saveLibraryAvailable(MangaLibrary.this);
	}

	public void save(BackgroundExecutors executors) {
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				save();
			}
		});
	}
	public void saveAvailable(BackgroundExecutors executors) {
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				saveAvailable();
			}
		});
	}
	

	public Manga getManga(String name) {
		for (MangaCollection collection : MangaCollection.values())
			for (Manga manga : getCollection(collection))
				if (manga.getName().equals(name))
					return manga;
		return null;
	}

	
	public int newAvailable(){
		int amount = 0;
		for(Manga manga : collections.get(MangaCollection.WATCHING))
			if(manga.newAvailable())
				amount++;
		return amount;
	}



}
/*

	public MangaLibrary(String configDirectory) {

		File configDir = new File(configDirectory);
		if (!Files.exists(Paths.get(configDir.toURI())))
			configDir.mkdirs();

		File configMangaDirectory = new File(configDirectory + File.separator + "mangas.json");
		File configMangaCollections = new File(configDirectory + File.separator + "collections.json");
		File configMangaAvailable = new File(configDirectory + File.separator + "available.json");

		if (!Files.exists(Paths.get(configMangaDirectory.toURI())) || !Files.exists(Paths.get(configMangaCollections.toURI()))
				|| !Files.exists(Paths.get(configMangaAvailable.toURI()))) {
			init(configDirectory);
			return;
		}

		try {
			JsonReader reader;
			Gson gson = new Gson();

			reader = new JsonReader(new FileReader(configMangaDirectory));
			mangaDirectory = gson.fromJson(reader, String.class);
			assert mangaDirectory != null;

			reader = new JsonReader(new FileReader(configMangaCollections));
			collections = gson.fromJson(reader, LinkedHashMap.class);
			assert collections.size() >= 0;

			reader = new JsonReader(new FileReader(configMangaAvailable));
			available = gson.fromJson(reader, LinkedHashMap.class);
			assert available.size() >= 1;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}

	public void saveLibrary(){
		try {
			File configDir = new File(configDirectory);
			if(!Files.exists(Paths.get(configDir.toURI())))
				configDir.mkdirs();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			String mangaDirectory = gson.toJson(this.mangaDirectory);
			String collections = gson.toJson(this.collections);
			String available = gson.toJson(this.available);
			
			saveLibraryPart(mangaDirectory, configDirectory, "mangas.json");
			saveLibraryPart(collections, configDirectory, "collections.json");
			saveLibraryPart(available, configDirectory, "available.json");
			
		} catch (IOException e) {
			e.printStackTrace();
			M.print(e.getMessage());
		}
	}
	
	private static void saveLibraryPart(String output, String configDirectory, String filename) throws IOException{
		File file = new File(configDirectory+File.separator+filename);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(output);
        writer.close();
	}
	
	







*/