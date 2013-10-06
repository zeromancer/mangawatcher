package gui.manga;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import logic.LibraryManager;
import net.miginfocom.swing.MigLayout;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

/*
 * 
 * Quck View: Image Button "Read Newest" (if new available)
 * 
 * 
 * Full View
 * 
 * // Info
 * 
 * Image Description Reading Progressbar
 * 
 * // Read
 * 
 * Button "Read Newest" (if new available) Righclick -> redownload ? Button
 * Table <- all Chapters
 * 
 * // Change Settings
 * 
 * Button "Check for Updates" ComboBox Change Status Redownload + from
 * ComboBox + to ComboBox + Redownload Button
 */

public class GuiMangaFull extends JPanel {

	private static final long serialVersionUID = -8958146753424179536L;

	// GuiFrame frame;
	MangaLibrary library;

	JComboBox<String> combo;
	JButton button;

	public GuiMangaFull(MangaLibrary library, Manga manga) {
		// super(new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]"));
		super(new MigLayout(""));
		// super(new BorderLayout());
		this.library = library;
		String name = manga.getName();
		JPanel panel = this;

		// Image
		String path = library.getMangaDirectory() + File.separator + name + File.separator + name + ".jpg";
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
			JLabel picLabel = new JLabel(new ImageIcon(image));
			add(picLabel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Description
		JTextArea description = new JTextArea(manga.getDescription());
		description.setLineWrap(true);
		this.add(description, "grow, wrap");

		JLabel label = new JLabel("Chapters");
		this.add(label, "growx, wrap");

		// Chapters
		JPanel grid = new JPanel(new GridLayout(0, 10));
		this.add(grid, "growx, span 2");

		for (int i = 1; i < 102; i++) {
			button = new JButton("" + i);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					// M.print("" + (String) combo.getSelectedItem());
					System.exit(0);
				}
			});
			// panel.add(button, "growx, span 2");
			grid.add(button);
		}
		label = new JLabel("");
		this.add(label, "growx, wrap");

		label = new JLabel("Options");
		this.add(label, "growx, wrap");

		label = new JLabel("Status:");
		this.add(label);
		//JComboBox<MangaStatus> statusbox = new JComboBox<Manga.MangaStatus>(MangaStatus.values());
		String text = manga.getRead() == manga.getDownloaded()? "Up to Date": "New Available";
		label = new JLabel(text);
		this.add(label, "growx, wrap");
	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Test Manga Add");
				// frame.setSize(300, 300);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				MangaLibrary library = LibraryManager.loadLibrary("config");
				Manga manga = library.getCollection(MangaCollection.WATCHING).get(0);
				// LibraryManager.saveLibrary(configDirectory, library);
				GuiMangaFull component = new GuiMangaFull(library, manga);
				frame.add(component);
				frame.pack();

				frame.setVisible(true);
			}
		});
	}
}
