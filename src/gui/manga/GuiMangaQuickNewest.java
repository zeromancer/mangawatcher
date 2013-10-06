package gui.manga;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic.LibraryManager;
import data.Manga;
import data.MangaLibrary;
import data.Manga.MangaCollection;

public class GuiMangaQuickNewest extends JPanel{

	GuiMangaQuick image;
	JButton button;
	
	public GuiMangaQuickNewest(MangaLibrary library, Manga manga) {
		super(new BorderLayout());
		JPanel panel = this;
//		setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		image = new GuiMangaQuick(library, manga);
		panel.add(image);
		
		
		button = new JButton("Read "+(manga.getRead()+1));
		panel.add(button,BorderLayout.SOUTH);
		
		
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Test Manga Add");
				// frame.setSize(300, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				String configDirectory = "config";
				MangaLibrary library = LibraryManager.loadLibrary(configDirectory);
				// MangaAll.try_refresh(library);
				// LibraryManager.saveLibrary(configDirectory, library);
				Manga manga = library.getCollection(MangaCollection.WATCHING).get(0);
				GuiMangaQuickNewest component = new GuiMangaQuickNewest(library, manga);
				frame.add(component);
				frame.pack();

				frame.setVisible(true);
			}
		});
	}
}
