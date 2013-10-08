package gui.add;

import gui.GuiFrame;
import gui.threading.BackgroundExecutors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import misc.M;
import net.miginfocom.swing.MigLayout;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class GuiMangaAdd extends JPanel {

	final private GuiFrame frame;
	final private MangaLibrary library;
	final private BackgroundExecutors executors;

	final private JLabel source;
	final private JComboBox<String> combo;
	final private JButton button;

	public GuiMangaAdd(final GuiFrame frame) {
		super(new MigLayout("align center", "0:10%:20%[grow,fill]0:10%:20%", ""));
//		super(new MigLayout("align center,fillx", "[grow,fill]rel[grow,fill]", "[]1[]"));
		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.frame = frame;
		this.library = frame.getLibrary();
		this.executors = frame.getExecutors();

		source = new JLabel("MangaReader.net");
		source.setFont(frame.getOptions().getTitelFont());
		add(source, "wrap");

		// combo box
		List<String> all = new ArrayList<>(library.getAvailable(MangaSource.MANGAREADER).keySet());
		Collections.sort(all);
		String[] array = all.toArray(new String[0]);
		combo = new JComboBox<>(array);
		add(combo, "wrap");
		combo.setMaximumRowCount(35);

		// button
		button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String name = (String) combo.getSelectedItem();
				M.print("selected: " + name);
				library.add(MangaSource.MANGAREADER, name, MangaCollection.WATCHING);
				library.save();
				// System.exit(0);
			}
		});
		add(button);
		//panel.add(button, "growx, span 2");
	}

	// public static void main(String[] args) {
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// JFrame frame = new JFrame("Test Manga Add");
	// frame.setSize(300, 300);
	// frame.setLocationRelativeTo(null);
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	//
	// String configDirectory = "config";
	// MangaLibrary library = LibraryManager.loadLibrary(configDirectory);
	// //ReaderAvailable.tryRefresh(library);
	// // LibraryManager.saveLibrary(configDirectory, library);
	// GuiMangaAdd component = new GuiMangaAdd(library);
	//
	// frame.add(component);
	// frame.pack();
	// frame.setVisible(true);
	// }
	// });
	// }
}
