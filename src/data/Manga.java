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
package data;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class Manga {

	public enum MangaSource {

		MANGAREADER("http://www.mangareader.net"),
//		MANGAFOX("http://mangafox.me"),
//		MANGAHERE("http://www.mangahere.com")
		;

		private String link;

		private MangaSource(String link) {
			this.link = link;
		}

		public String getLink() {
			return link;
		}

	}

	public @Getter enum MangaCollection {
		WATCHING("Watching"),
		PLANNING("Planning"),
		DROPPED("Dropped"),
		COMPLETED("Completed");

		public String name;

		private MangaCollection(String print) {
			this.name = print;
		}

		public static String[] strings(){
			MangaCollection[] collections = MangaCollection.values();
			String[] strings = new String[collections.length];
			for (int i = 0; i < strings.length; i++) {
				strings[i] = collections[i].getName();
			}
			return strings;
		}
		public static MangaCollection parse(String name){
			for(MangaCollection collection : MangaCollection.values())
				if(collection.name.equals(name))
					return collection;
			return null;
		}
	}

	public Manga(String name, MangaSource source) {
		super();
		this.name = name;
		this.source = source;
		this.collection = MangaCollection.WATCHING;
		this.read = 0;
		this.downloaded = 0;
		this.released = 0;
		this.description = "";
//		this.author = "";
//		this.artist = "";
//		this.releaseYear = "";
	}

	private String name;
	private MangaSource source;
	private MangaCollection collection;
	private int read;
	private int page;
	private int downloaded;
	private int released;
	private String description;
//	private String author;
//	private String artist;
//	private String releaseYear;

	public void changeCollection(MangaLibrary library, MangaCollection newCollection) {
		library.getCollections().get(collection).remove(this);
		this.collection = newCollection;
		library.getCollections().get(newCollection).add(this);
	}

	public String getHomePage(MangaLibrary library) {
		return library.getAvailable(source, name);
	}

	public boolean newAvailable() {
		return read < downloaded;
	}

	public String getMangaDirectory(MangaLibrary library) {
		assert library != null;
		return library.getMangaDirectory() + File.separator + name;
	}

	public String getMangaDirectory(MangaLibrary library, int chapter) {
		assert library != null;
		return library.getMangaDirectory() + File.separator + name + File.separator + String.format("%04d", chapter);
	}

	public String getMangaImagePath(MangaLibrary library) {
		assert library != null;
		return library.getMangaDirectory() + File.separator + name + File.separator + name + ".jpg";
	}

	@Override
	public String toString() {
//		return name+" ("+collection.name+")";
		return name;
	}
	
	
}
