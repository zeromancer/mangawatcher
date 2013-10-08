package gui.reading;

import gui.GuiFrame;
import gui.menu.GuiProgressBar;
import gui.reading.GuiReadView.ReadingState;
import gui.threading.BackgroundExecutors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;

import javax.swing.JSlider;

import data.MangaLibrary;

public class GuiReadViewOperations {


	private final GuiFrame frame;
	private final GuiRead gui;
	private final GuiReadView view;
	private final GuiProgressBar progress;
	private final BackgroundExecutors executors;
	private final JSlider slider;

	private final MangaLibrary library;

	private ReadingState state = ReadingState.ERROR;

	final private FilenameFilter filter;
	final private Map<Integer, List<BufferedImage>> mapImages;
	final private Map<Integer, List<File>> mapFiles;
	
	
	public GuiReadViewOperations(GuiFrame frame, GuiRead gui, GuiReadView view) {
			this.frame = frame;
			this.gui = gui;
			this.view = view;
			this.progress = gui.getProgress();
			this.executors = frame.getExecutors();
			this.slider = gui.getSlider();
			this.library = frame.getLibrary();
			

			filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jpg");
				}
			};
			mapImages = view.getMapImages();
			mapFiles = view.getMapFiles();
			
	}

}
