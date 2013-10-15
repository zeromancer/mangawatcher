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
package logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import misc.M;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import data.Manga.MangaSource;
import data.MangaLibrary;

public class LibraryManager {

	public static MangaLibrary loadLibrary(String configDirectory){
		
		File configDir = new File(configDirectory);
		if(!Files.exists(Paths.get(configDir.toURI())))
			configDir.mkdirs();
		
		File configLibrary = new File(configDirectory+File.separator+"library.json");
		File configAvailable = new File(configDirectory+File.separator+"available.json");
		
		if(	!Files.exists(Paths.get(configLibrary.toURI())) ||
			!Files.exists(Paths.get(configAvailable.toURI()))){
			MangaLibrary library = new MangaLibrary(configDirectory);
			library.save();
			library.saveAvailable();
			return library;
		}
		
		try {
			JsonReader reader;
			Gson gson = new Gson();
			
			reader = new JsonReader(new FileReader(configLibrary));
			MangaLibrary library = gson.fromJson(reader, MangaLibrary.class);
			
			reader = new JsonReader(new FileReader(configAvailable));
			Map<String, Map<String, String>> availableParsed = gson.fromJson(reader, LinkedHashMap.class);
			Map<MangaSource, Map<String, String>> available = new LinkedHashMap<>();
			for(String source : availableParsed.keySet())
				available.put(MangaSource.valueOf(source), availableParsed.get(source));

			library.setConfigDirectory(configDirectory);
			library.setAvailable(available);
			
			assert library != null;
			return library;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			M.print(e.getMessage());
			return null;
		}
	}

	
	private enum SavePart{
		COLLECTIONS,
		AVAILABLE
	}

	public static void saveLibrary(MangaLibrary library){
		saveLibrary(library, SavePart.COLLECTIONS);
	}
	public static void saveLibraryAvailable(MangaLibrary library){
		saveLibrary(library, SavePart.AVAILABLE);
	}
	public static void saveLibrary(MangaLibrary library,SavePart part){
		try {
			String configDirectory = library.getConfigDirectory();
			File configDir = new File(configDirectory);
			if(!Files.exists(Paths.get(configDir.toURI())))
				configDir.mkdirs();
			
			assert library != null;
			Gson gson = new GsonBuilder().setPrettyPrinting().setExclusionStrategies(new ExclusionStrategy() {
				public boolean shouldSkipClass(Class<?> clazz) {
					return false;
				}
				public boolean shouldSkipField(FieldAttributes f) {
					return (f.getName().equals("configDirectory"))||
				            (f.getName().equals("available"));
				}
			}).create();
			
			String generatedLibrary = gson.toJson(library,library.getClass());
			String generatedAvailable = gson.toJson(library.getAvailable(),library.getAvailable().getClass());
			
			if(part == SavePart.COLLECTIONS)
				saveLibraryPart(generatedLibrary, configDirectory, "library.json");
			if(part == SavePart.AVAILABLE)
				saveLibraryPart(generatedAvailable, configDirectory, "available.json");
			
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
	
//	public static void main(String[] args) {
//		String configDirectory = "config";
//		MangaLibrary library = loadLibrary(configDirectory);
//		saveLibrary(library);
//	}
	
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