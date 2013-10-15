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
package gui.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.Getter;

public @Getter class BackgroundExecutors {

	ExecutorService networkExecuter;
	ExecutorService fileExecuter;
	ScheduledExecutorService scheduler;
	
	public BackgroundExecutors() {
		networkExecuter = Executors.newSingleThreadExecutor();
		fileExecuter = Executors.newSingleThreadExecutor();
		scheduler = Executors.newScheduledThreadPool(1);
//		networkExecuter.
	}
	
	public void runOnNetworkThread(Runnable run) {
		networkExecuter.execute(run);
	}

	public void runOnFileThread(Runnable run) {
		fileExecuter.execute(run);
	}

	// public void loadImage(final SingleImage image, final File file){
	// Runnable run = new Runnable() {
	// @Override
	// public void run() {
	// image.load(file);
	// }
	// };
	// networkExecuter.execute(run);
	// }
	//
	// public void updateAvailable(final MangaLibrary library){
	// Runnable run = new Runnable() {
	// @Override
	// public void run() {
	// library.updateAvailable();
	// }
	// };
	// networkExecuter.execute(run);
	// }
	// public void updateShallow(final MangaLibrary library){
	// Runnable run = new Runnable() {
	// @Override
	// public void run() {
	// library.updateShallow();
	// }
	// };
	// networkExecuter.execute(run);
	// }
	//
	// public void updateDeep(final MangaLibrary library,final Manga manga){
	// Runnable run = new Runnable() {
	// @Override
	// public void run() {
	// manga.updateDeep(library);
	// }
	// };
	// networkExecuter.execute(run);
	// }

	// public void loadImageFromFile(BufferedImage image, File file) {
	// M.print("start loading image");
	// Runnable load = new LoadImageFromFileRunnable(image, file);
	// fileExecuter.execute(load);
	// }
	//
	// public static class LoadImageFromFileRunnable implements Runnable{
	//
	// BufferedImage image;
	// File file;
	//
	// public LoadImageFromFileRunnable(BufferedImage image, File file) {
	// this.image = image;
	// this.file = file;
	// }
	//
	//
	// @Override
	// public void run() {
	// try {
	// image = ImageIO.read(file);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }
	

}
