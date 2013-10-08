package gui.full;

import gui.GuiFrame;
import gui.reading.GuiRead;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import lombok.Getter;
import lombok.Setter;
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

public class GuiMangaFull extends JScrollPane {

	private final GuiFrame frame;
	private final MangaLibrary library;
	private Manga manga;

	// Description
	private JLabel title;
	private JLabel icon;
	private JTextArea description;
	private JScrollPane scroll;

	// Chapters
	private List<JGradientButton> buttons;
	private JPanel grid;

	// Options
	private JComboBox<String> collection;
	private JButton redownload;
	private JButton sync;

	public GuiMangaFull(GuiFrame frame) {
		this.frame = frame;
		this.library = frame.getLibrary();
		this.manga = null;

		// Content
		JPanel panel = new JPanel();
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		setViewportView(panel);

		// Layout
		panel.setLayout(new MigLayout("", "5:50:200[align right,grow,200::300][align left,grow,100::400,shrink]5:50:200", "[top][top]"));
		String subheaderAddLabel = "growx, span 2, wrap";
		String optionsAddLabel = "gapleft 40, align left";
		String optionsAddComponent = "width 200!, wrap";

		// Title
		title = new JLabel("Unselected");
		title.setFont(frame.getOptions().getTitelFont());
		panel.add(title, "align center, span 2, growx, shrink, wrap");

		// Image
		icon = new JLabel();
		panel.add(icon, "");

		// Description
		description = new JTextArea("Please select a manga");
		description.setColumns(20);
		description.setLineWrap(true);
		description.setFont(frame.getOptions().getLabelFont());
		description.setLineWrap(true);
		scroll = new JScrollPane(description);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scroll, "grow, shrink, wrap");

		// Chapters Title
		JLabel label = new JLabel("Chapters");
		label.setFont(frame.getOptions().getSubtitelFont());
		panel.add(label, subheaderAddLabel);

		// Chapters
		grid = new JPanel(new GridLayout(0, 10));
		grid.setBorder(BorderFactory.createEmptyBorder());
		panel.add(grid, "align center, growx, shrink, span 2, wrap");
		buttons = new ArrayList<>();

		// Options Title
		label = new JLabel("Options");
		label.setFont(frame.getOptions().getSubtitelFont());
		panel.add(label, subheaderAddLabel);

		// Change Collection Title
		label = new JLabel("Change Colllection:");
		label.setFont(frame.getOptions().getLabelFont());
		panel.add(label, optionsAddLabel);
		collection = new JComboBox<String>(MangaCollection.strings());
		collection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(manga==null) return;
				String selection = (String) collection.getSelectedItem();
				MangaCollection newCollection = MangaCollection.valueOf(selection);
				manga.changeCollection(library, newCollection);
			}
		});
		panel.add(collection, optionsAddComponent);

		// Sync Title
		label = new JLabel("Reync:");
		label.setFont(frame.getOptions().getLabelFont());
		panel.add(label, optionsAddLabel);
		sync = new JButton("Now");
		panel.add(sync, optionsAddComponent);

		// Redownload Title
		label = new JLabel("Redownload:");
		label.setFont(frame.getOptions().getLabelFont());
		panel.add(label, optionsAddLabel);
		redownload = new JButton("All Chapters");
		panel.add(redownload, optionsAddComponent);

	}

	public void update() {
		if(manga ==null)
			return;
		updateMinor();
		revalidate();
		repaint();
	}
	
	public void update(Manga manga) {

		if (this.manga == null || this.manga != manga) {
			this.manga = manga;
			updateMajor();
		} else {
			updateMinor();
		}

		revalidate();
		repaint();
	}

	private void updateMajor() {
		//		M.print("updating Major: "+manga.getName());
		title.setText(manga.getName());
		icon.setIcon(new ImageIcon(frame.getEngine().getCovers().get(manga)));
		description.setText(manga.getDescription());

//		String selection = (String) collection.getSelectedItem();
//		M.print("updating Major: "+selection+" , ordinal: "+manga.getCollection().ordinal()+" col: "+manga.getCollection());
		collection.setSelectedIndex(manga.getCollection().ordinal());
		updateButtons();
		addButtons();
		deleteButtons();
	}

	private void updateMinor() {
		//		M.print("updating Minor: "+manga.getName());
		updateButtons();
		addButtons();
	}

	private void updateButtons() {
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setRead(i <= manga.getRead());
		}
	}

	private void addButtons() {
		for (int i = buttons.size(); i < manga.getDownloaded() + 1; i++) {
			JGradientButton button = new JGradientButton("" + i, i <= manga.getRead());
			final int j = i;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					// M.print("" + (String) combo.getSelectedItem());
					GuiRead read = frame.getRead();
					JTabbedPane tabbed = frame.getTabbed();
					read.view(manga, j, manga.getPage());
					tabbed.setSelectedComponent(read);
					tabbed.setEnabledAt(tabbed.indexOfComponent(read), true);
				}
			});
			grid.add(button);
			buttons.add(button);
		}
	}

	private void deleteButtons() {
		//for (int i = manga.getDownloaded()+1; i < buttons.size(); i++) {
		for (int i = buttons.size() - 1; i > manga.getDownloaded(); i--) {
			// JGradientButton button = buttons.get(i);
			grid.remove(i);
			buttons.remove(i);
		}
	}

	// public static void main(String[] args) {
	//
	// SwingUtilities.invokeLater(new Runnable() {
	// @Override
	// public void run() {
	// JFrame frame = new JFrame("Test Manga Add");
	// // frame.setSize(300, 300);
	// frame.setLocationRelativeTo(null);
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//
	// MangaLibrary library = LibraryManager.loadLibrary("config");
	// Manga manga = library.getCollection(MangaCollection.WATCHING).get(0);
	// // LibraryManager.saveLibrary(configDirectory, library);
	// GuiMangaFull component = new GuiMangaFull(library, manga);
	// frame.add(component);
	// frame.pack();
	//
	// frame.setVisible(true);
	// }
	// });
	// }

	private static final @Getter @Setter class JGradientButton extends JButton {

		boolean read = false;
		Color colorRead = Color.GREEN;
		Color colorUnread = Color.YELLOW;
		Color color2 = Color.GRAY.darker();

		private JGradientButton(String text, boolean read) {
			super(text);
			this.read = read;
			setContentAreaFilled(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Color color = read ? colorRead : colorUnread;
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(new GradientPaint(new Point(0, 0), color, new Point(0, getHeight()), color2));
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.drawString(getText(), getWidth() / 2, getHeight());
			g2.dispose();
			super.paintComponent(g);
		}

	}
}
