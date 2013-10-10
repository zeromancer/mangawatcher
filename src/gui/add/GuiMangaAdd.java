package gui.add;

import gui.GuiFrame;
import gui.downloading.GuiDownloading;
import gui.full.GuiMangaFull;
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
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import logic.MangaLogic;
import net.miginfocom.swing.MigLayout;
import data.Manga;
import data.Manga.MangaCollection;
import data.Manga.MangaSource;
import data.MangaLibrary;

public class GuiMangaAdd extends JPanel {

	final private GuiFrame frame;
	final private MangaLibrary library;
	final private MangaLogic logic;
	final private BackgroundExecutors executors;

	final private JLabel source;
	final private JComboBox<String> combo;
	
	final private JLabel label;
	final private JComboBox<String> collection;
	final private JButton button;

	public GuiMangaAdd(final GuiFrame frame) {
		super(new MigLayout("align center", "0:10%:20%[grow,fill]0:10%:20%", ""));
//		super(new MigLayout("align center,fillx", "[grow,fill]rel[grow,fill]", "[]1[]"));
		// setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.frame = frame;
		this.library = frame.getLibrary();
		this.logic = frame.getLogic();
		this.executors = frame.getExecutors();

		source = new JLabel("MangaReader.net");
		source.setFont(frame.getOptions().getTitelFont());
		add(source, "wrap");

		// combo box
		List<String> all = new ArrayList<>(library.getAvailable(MangaSource.MANGAREADER).keySet());
		Collections.sort(all);
		String[] array = all.toArray(new String[0]);
		combo = new JComboBox<>(array);
		combo.setMaximumRowCount(35);
		combo.setSelectedIndex(-1);
		add(combo, "wrap");
		
		
		label = new JLabel("Add to Colllection:");
		label.setFont(frame.getOptions().getLabelFont());
		add(label, "split 2");
		collection = new JComboBox<String>(MangaCollection.strings());
		collection.setSelectedIndex(0);
		add(collection, "wrap");
		
		// button
		button = new JButton("Add");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				final String name = (String) combo.getSelectedItem();
				final MangaCollection addCollection = MangaCollection.parse((String) collection.getSelectedItem());
				final JTabbedPane tabbed = frame.getTabbed();
				final GuiDownloading down = frame.getDownloading();
				tabbed.setSelectedComponent(down);
				combo.setSelectedIndex(-1);
				executors.runOnNetworkThread(new Runnable() {
					@Override
					public void run() {
						
						Manga manga = library.getManga(name);
						if(manga != null){
							switchToExisting(manga);
							return;
						}
						
						logic.add(MangaSource.MANGAREADER, name, addCollection);
						library.save(executors);
					}
				});
			}
		});
		add(button);
	}
	
	private void switchToExisting(final Manga manga){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final GuiMangaFull full = frame.getFull();
				frame.getTabbed().setSelectedComponent(full);
				full.update(manga);
			}
		});
	}
	
	

}
