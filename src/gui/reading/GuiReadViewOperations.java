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
package gui.reading;

import gui.GuiFrame;
import gui.menu.GuiProgressBar;
import gui.reading.GuiReadView.ReadingState;
import gui.threading.BackgroundExecutors;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import misc.M;
import data.Manga;
import data.MangaLibrary;
import data.Options;

public class GuiReadViewOperations {

	private final GuiFrame frame;
	private final GuiRead gui;
	private final Options options;
	private final GuiReadView view;
	private final GuiProgressBar progress;
	private final BackgroundExecutors executors;
	private final JSlider slider;

	private final MangaLibrary library;

	private final FilenameFilter filter;
	private final Map<Integer, List<BufferedImage>> mapImages;
	private final Map<Integer, List<File>> mapFiles;
	
	public GuiReadViewOperations(GuiFrame frame, GuiRead gui, GuiReadView view) {
			this.frame = frame;
			this.options = frame.getOptions();
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

	protected void backgroundLoading(final Manga manga, final int chapter) {
		final int zoom = options.getReadingZoom();

		if(manga == null || chapter < 1 || chapter > manga.getDownloaded())
			return;
		
		// Clean up if to many chapters loaded
		if(mapImages.size()>10){
			int doNotDelete = view.getChapter();
			List<Integer> delete = new ArrayList<>();
			for(Integer index : mapImages.keySet())
				if(Math.abs(index.intValue() - doNotDelete)>1)
					delete.add(index);
			for(Integer index : delete){
				mapImages.remove(index);
				mapFiles.remove(index);
			}
		}
		
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				final String path = manga.getMangaDirectory(library, chapter);
				final File folder = new File(path);

				final List<BufferedImage> images = new ArrayList<BufferedImage>();
				File[] listed = folder.listFiles(filter);
				if (listed == null){
					view.setState(ReadingState.NOTFOUND);
					return;
				}
				final List<File> files = new ArrayList<File>(Arrays.asList(listed));

				Collections.sort(files);

				for (int i = 0; i < files.size(); i++) {
					final File file = files.get(i);
					// M.print(" loading file: " + file.getName());
					BufferedImage image = loadImage(file);
					if (zoom == 100)
						images.add(image);
					else
						images.add(M.scale(image, 100, zoom));
					progress((i + 1) * 100 / files.size(), "Loading image: " + file.getName());
				}
				backgroundLoadingFinish(chapter, images, files);
			}
		});
	}

	private void backgroundLoadingFinish(final int chapter, final List<BufferedImage> images, final List<File> files) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				defaultProgress();

				mapFiles.put(chapter, files);
				mapImages.put(chapter, images);

				if (view.getState() == ReadingState.LOADING) {
					view.setState(ReadingState.READING);
					slider.setMinimum(0);
					slider.setValue(view.getManga().getPage());
					slider.setMaximum(files.size() - 1);
				}
				view.repaint();
			}
		});
	}


	public void backgroundZooming(final int newZoom) {
		final int oldZoom = options.getReadingZoom();
		int count = 0;
		final Map<Integer, List<BufferedImage>> mapCopy = new HashMap<>();
		for (Entry<Integer, List<BufferedImage>> entry : mapImages.entrySet()) {
			List<BufferedImage> list = new ArrayList<>();
			for(BufferedImage image : entry.getValue()){
				list.add(image);
				count++;
			}
			mapCopy.put(entry.getKey(), list);
		}
		final Map<Integer, List<BufferedImage>> mapNew = new HashMap<>();
		final int totalCount = count;
		executors.runOnFileThread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				for (Entry<Integer, List<BufferedImage>> entry : mapCopy.entrySet()) {
					List<BufferedImage> list = new ArrayList<>();
					for(BufferedImage oldImage : entry.getValue()){
						BufferedImage newimage = M.scale(oldImage, oldZoom, newZoom);
						list.add(newimage);
						count++;
						progress(count*100/totalCount,"Resizing Image "+count+" / "+totalCount);
					}
					mapNew.put(entry.getKey(), list);
				}
				backgroundZoomingFinish(mapNew);
			}
		});
		// TODO Auto-generated method stub
//		M.print("zoom: " + this.zoom + " -> " + zoom);
//		if (this.zoom == zoom)
//			return;
//		state = ReadingState.RESIZING;
//		repaint();
//		List<BufferedImage> images = mapImages.get(chapter);
//		for (int i = 0; i < images.size(); i++) {
//			BufferedImage image = images.get(i);
//			if (image == null)
//				continue;
//			BufferedImage newer = operations.scaleImage(image, this.zoom, zoom);
//			// M.print(" "+image.getWidth()+ " -> "+newer.getWidth());
//			images.set(i, newer);
//		}
//		this.zoom = zoom;
//		state = ReadingState.READING;
	}
	

	public void backgroundZoomingFinish(final Map<Integer, List<BufferedImage>> mapNew) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (Entry<Integer, List<BufferedImage>> entry : mapNew.entrySet()) {
					Integer key = entry.getKey();
					List<BufferedImage> list = entry.getValue();
					for (int i = 0; i < list.size(); i++) {
						mapImages.get(key).set(i, list.get(i));
					}
				}
				view.setState(ReadingState.READING);
				view.repaint();
				defaultProgress();
			}
		});
	}
	
	private void progress(final int percent, final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progress.setValue(percent);
				progress.setText(text);
				progress.repaint();
			}
		});
	}

	protected void defaultProgress() {
		//progress.setValue((GuiReadView.this.chapter) * 100 / manga.getReleased());
		progress.setValue(100);
		progress.setText("" + view.getManga().getName() + " Chapter " + view.getChapter());
		progress.repaint();
	}
	
	


	public static BufferedImage loadImage(File file) {
		// try {
		// Thread.sleep((int) (Math.random() * 100));
		// } catch (InterruptedException e1) {
		// }
		try {
			// M.print("\tloading image: " + file.getName());
			return ImageIO.read(file);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void initializeKeyShortcuts() {

		ActionMap actionMap = view.getActionMap();
		InputMap inputMap = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		AbstractAction scrollUp = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.scroll(-options.getReadingScroll());
			}
		};

		AbstractAction scrollDown = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.scroll(options.getScrollAmount());
			}
		};

		AbstractAction heightUp = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.scroll(-options.getReadingScroll());
			}
		};

		AbstractAction heightDown = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.scroll(view.getHeight());
			}
		};

		AbstractAction pageDown = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.nextPage();
			}
		};

		Map<Integer, AbstractAction> actions = new HashMap<>();
		actions.put(KeyEvent.VK_W, scrollUp);
		actions.put(KeyEvent.VK_S, scrollDown);
		actions.put(KeyEvent.VK_PAGE_UP, heightUp);
		actions.put(KeyEvent.VK_PAGE_DOWN, heightDown);
		actions.put(KeyEvent.VK_SPACE, pageDown);

		for (Entry<Integer, AbstractAction> entry : actions.entrySet()) {
			Integer key = entry.getKey();
			AbstractAction value = entry.getValue();
			inputMap.put(KeyStroke.getKeyStroke(key, 0), "" + key);
			actionMap.put("" + key, value);
		}
	}



}
