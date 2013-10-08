package gui.manga;

import gui.GuiFrame;
import gui.reading.GuiRead;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import data.Manga;
import data.MangaLibrary;

public class GuiMangaQuickNewest extends JPanel{

	private final GuiFrame frame;
	private final MangaLibrary library;
	private final Manga manga;

	private GuiMangaQuick image;
	private final JButton button;
	
	public GuiMangaQuickNewest(final GuiFrame frame, final Manga manga) {
		super(new BorderLayout());
		this.frame = frame;
		this.library = frame.getLibrary();
		this.manga = manga;

//		setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		image = new GuiMangaQuick(frame, manga);
		add(image);
		button = new JButton();
		add(button,BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// M.print("" + (String) combo.getSelectedItem());
				GuiRead read = frame.getRead();
				JTabbedPane tabbed = frame.getTabbed();
				read.view(manga, manga.getRead() + 1, manga.getPage());
				tabbed.setSelectedComponent(read);
				tabbed.setEnabledAt(tabbed.indexOfComponent(read), true);
			}
		});

		update();
	}

	public void update() {
		button.setText("Read " + (manga.getRead() + 1));
	}

}
