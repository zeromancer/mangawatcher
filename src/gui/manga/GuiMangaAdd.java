package gui.manga;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import logic.LibraryManager;
import logic.MangaLogic;
import misc.M;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class GuiMangaAdd extends JPanel {

	// GuiFrame frame;
	MangaLibrary library;

	JComboBox<String> combo;
	JButton button;

	public GuiMangaAdd(final MangaLibrary library) {
		//super(new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]"));
		this.library = library;

		// combo box
		List<String> all = new ArrayList<>(library.getAvailable(MangaSource.MANGAREADER).keySet());
		Collections.sort(all);
		String[] array = all.toArray(new String[0]);
		combo = new JComboBox<>(array);
		add(combo);
		combo.setMaximumRowCount(35);

		// button
		button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String name = (String) combo.getSelectedItem();
				M.print("selected: " + name);
				MangaLogic.add(library, MangaSource.MANGAREADER, name, MangaCollection.WATCHING);
				library.save();
				System.exit(0);
			}
		});
		add(button);
		//panel.add(button, "growx, span 2");
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Test Manga Add");
				frame.setSize(300, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


				String configDirectory = "config";
				MangaLibrary library = LibraryManager.loadLibrary(configDirectory);
				//ReaderAvailable.tryRefresh(library);
//				LibraryManager.saveLibrary(configDirectory, library);
				GuiMangaAdd component = new GuiMangaAdd(library);
				
				frame.add(component);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
