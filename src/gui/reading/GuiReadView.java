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
import gui.threading.BackgroundExecutors;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import logic.DiskIOManager;
import lombok.Getter;
import lombok.Setter;
import misc.M;
import data.Manga;
import data.MangaLibrary;
import data.Options;

public @Getter @Setter class GuiReadView extends JPanel implements MouseWheelListener,MouseListener {

	public enum ReadingState {
		READING(""),
		LOADING("Loading images..."),
		RESIZING("Resizing images..."),
		NOTFOUND("No images found."),
		ERROR("Please select a manga");

		String message;

		private ReadingState(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

	}

	private final GuiFrame frame;
	private final GuiRead gui;
	private final Options options;
	private final GuiProgressBar progress;
	private final BackgroundExecutors executors;
	private final JSlider slider;
	private final GuiReadViewOperations operations;

	private final MangaLibrary library;
	private Manga manga;
	private int chapter = 0;
	private int page = 0;

	private int scroll = 0;
//	private int scrollAmount = 100;
//	private int zoom = 100;

	private ReadingState state = ReadingState.ERROR;

	private final Map<Integer, List<BufferedImage>> mapImages;
	private final Map<Integer, List<File>> mapFiles;

	private JPopupMenu menu;
	private JMenuItem newLocation;
	private JMenuItem oldLocation;
	private JFileChooser chooser;
	
	
	public GuiReadView(final GuiFrame frame, GuiRead gui) {
		this.frame = frame;
		this.gui = gui;
		this.options = frame.getOptions();
		this.progress = gui.getProgress();
		this.executors = frame.getExecutors();
		this.slider = gui.getSlider();
		this.library = frame.getLibrary();
		
		setFocusable(true);
		addMouseWheelListener(this);
		addMouseListener(this);

		mapImages = new HashMap<Integer, List<BufferedImage>>();
		mapFiles = new HashMap<Integer, List<File>>();

		operations = new GuiReadViewOperations(frame, gui, this);
		operations.initializeKeyShortcuts();

//		this.zoom = options.getReadingZoom();
//		this.scrollAmount = frame.getOptions().getReadingScroll();
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		final ActionListener oldLocationActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = new File(options.getLastImageCopyLocation());
				File source = mapFiles.get(chapter).get(page);
				if(file.isDirectory()){
					try {
						M.copy(source, new File(file, source.getName()));
					} catch (IOException e1) {
						M.exception(e1);
					}
				}else{
					try {
						M.copy(source, new File(file.getParent(), source.getName()));
					} catch (IOException e1) {
						M.exception(e1);
					}
				}
			}
		};
		
		menu = new JPopupMenu();
		newLocation = new JMenuItem("Copy to...");
		newLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = chooser.showOpenDialog(frame);
				if(result == JFileChooser.APPROVE_OPTION){
					File file = chooser.getSelectedFile();
					File source = mapFiles.get(chapter).get(page);
					if(file.isDirectory()){
						try {
							M.copy(source, new File(file, source.getName()));
						} catch (IOException e1) {
							M.exception(e1);
						}
					}else{
						try {
							M.copy(source, file);
						} catch (IOException e1) {
							M.exception(e1);
						}
					}
					options.setLastImageCopyLocation(file.getAbsolutePath());
					if(oldLocation == null || oldLocation.getName() != file.getAbsolutePath()){
						oldLocation = new JMenuItem(file.getAbsolutePath());
						oldLocation.addActionListener(oldLocationActionListener);
						menu.add(oldLocation);
						executors.runOnFileThread(new Runnable() {
							public void run() {
								DiskIOManager.saveOptions(getOptions(), library.getConfigDirectory());
							}
						});
						
					}
				}
			}
		});
		menu.add(newLocation);
		
		if(options.getLastImageCopyLocation() != null){
			oldLocation = new JMenuItem(options.getLastImageCopyLocation());
			oldLocation.addActionListener(oldLocationActionListener);
			menu.add(oldLocation);
		}
	}

	public void view(Manga manga, int chapter, int page) {
		if (this.manga != null && this.manga.equals(manga) && mapImages.containsKey(chapter)) {
			this.chapter = chapter;
			this.page = page;
			repaint();
			return;
		} else if (this.manga != manga) {
			mapImages.clear();
			mapFiles.clear();
		}
		this.manga = manga;
		this.chapter = chapter;
		this.page = page;

		load();
	}
	
	public void load(){
		state = ReadingState.LOADING;
		operations.backgroundLoading(manga, chapter);
		operations.backgroundLoading(manga, chapter - 1);
		operations.backgroundLoading(manga, chapter + 1);
		repaint();
	}

	// Threading:
	// loading, resizing

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (state == ReadingState.READING)
			paintReading(g);
		else
			paintNonreading(g, state.getMessage());
	}

	private void paintNonreading(Graphics g, String message) {
		g.setFont(frame.getOptions().getSubtitelFont());
		g.drawString(message, (getWidth() - g.getFontMetrics().stringWidth(message)) / 2, (getHeight()) / 2);
	}

	private void paintReading(Graphics g) {

		if (!mapImages.containsKey(chapter) || page >= mapImages.get(chapter).size())
			return;

		List<BufferedImage> images = mapImages.get(chapter);

		int index = page;
		int remaining = scroll + getHeight();
		while (remaining > 0 && index < images.size()) {

			BufferedImage image = images.get(index);
			assert image != null;
			// if (image != null) {
			g.drawImage(image, (getWidth() - image.getWidth()) / 2, getHeight() - remaining, null);
			// } else {
			// g.drawString("Loading image...", getWidth() / 2 - 20, 0 -
			// remaining);
			// return;
			// }

			remaining -= image.getHeight();
			index++;
		}
		g.drawString((page + 1) + " / " + (images.size()), (getWidth()) / 2, (getHeight() - 1));
	}

	public void scroll(int scrollAmount) {
		this.scroll += scrollAmount;
		if (state != ReadingState.READING || !mapImages.containsKey(chapter) || page >= mapImages.get(chapter).size())
			return;
		
		// M.print("scroll: " + scroll);
		int imageHeight = mapImages.get(chapter).get(page).getHeight();

		if (scroll < 0)
			while (scroll < 0 && page(-1)) {
				//M.print(" <" + scroll + " + " + imageHeight + " = " + (scroll + imageHeight));
				if (mapImages.containsKey(chapter) && page < mapImages.get(chapter).size())
					imageHeight = mapImages.get(chapter).get(page).getHeight();
				else
					scroll = 0;
				scroll += imageHeight;
			}
		else
			while (scroll > imageHeight && page(+1)) {
				//M.print(" >" + scroll + " - " + imageHeight + " = " + (scroll - imageHeight));
				scroll -= imageHeight;
				if (mapImages.containsKey(chapter) && page < mapImages.get(chapter).size())
					imageHeight = mapImages.get(chapter).get(page).getHeight();
				else
					scroll = 0;
			}

		// M.print(" scroll: " + scroll);
		repaint();
	}

	public boolean page(int diff) {
		if (manga == null || state != ReadingState.READING)
			return false;
		int newPage = page + diff;
		int oldChapter = chapter;
		// M.print("newPage: " + newPage);
		if (newPage < 0) {
			if (chapter(-1)) {
				if (mapImages.containsKey(chapter))
					page = mapImages.get(chapter).size() + newPage;
				else
					page = 0;
			} else {
				return false;
			}

		} else if (newPage >= mapImages.get(chapter).size()) {
			if (chapter(+1)) {
				if (mapImages.containsKey(chapter))
					page = newPage - mapImages.get(chapter - 1).size();
				else
					page = 0;
			} else {
				return false;
			}

		} else {
			page = newPage;
		}

		// M.print(" page: " + page);

		// if (Math.abs(diff) > 1)
		// scroll = 0;
		slider.setValue(page);
		slider.repaint();
		manga.setPage(page);
		manga.setRead(chapter);
		repaint();
		return true;
	}

	public void previousPage() {
		page(-1);
	}

	public void nextPage() {
		page(+1);
	}

	public boolean chapter(int diff) {
		int newChapter = chapter + diff;

		if (manga == null || newChapter <= 0 || newChapter > manga.getDownloaded())
			return false;

		// M.print("newChapter: " + newChapter);

		if (mapImages.containsKey(newChapter)) {
			slider.setMaximum(mapImages.get(newChapter).size());
		} else {
			state = ReadingState.LOADING;
			operations.backgroundLoading(manga, newChapter);
		}

		this.chapter = newChapter;
		repaint();

		frame.getTray().update();
		
		operations.defaultProgress();

		manga.setRead(chapter);
		library.save(executors);

		if (page < 0)
			page = 0;
		if (page >= mapImages.get(chapter).size())
			page = mapImages.get(chapter).size()-1;
		
		// preloading
		if (!mapImages.containsKey(newChapter-1))
			operations.backgroundLoading(manga, newChapter-1);
		if (!mapImages.containsKey(newChapter+1))
			operations.backgroundLoading(manga, newChapter+1);

		return true;
	}

	public void previousChapter() {
		chapter(-1);
	}

	public void nextChapter() {
		chapter(+1);
	}

	public void setZoom(int zoom) {
		if(this.options.getReadingZoom() == zoom)
			return;
		state = ReadingState.RESIZING;
		operations.backgroundZooming(zoom);
//		this.zoom = zoom;
		options.setReadingZoom(zoom);
		repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
			scroll(-options.getReadingScroll());
		} else {
			scroll(options.getReadingScroll());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e))
			menu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
