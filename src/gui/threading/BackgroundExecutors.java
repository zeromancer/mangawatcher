package gui.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutors {

	ExecutorService networkExecuter;
	ExecutorService fileExecuter;
	
	public BackgroundExecutors() {
		networkExecuter = Executors.newSingleThreadExecutor();
		fileExecuter = Executors.newSingleThreadExecutor();
//		networkExecuter.
	}
	
	public void runOnNetworkThread(Runnable run) {
		networkExecuter.execute(run);
	}

	public void runOnFileThread(Runnable run) {
		networkExecuter.execute(run);
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
