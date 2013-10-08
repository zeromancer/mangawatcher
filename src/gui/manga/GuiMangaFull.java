package gui.manga;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
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

//	JComboBox<String> combo;
	private final List<JGradientButton> buttons;
	
	private final JComboBox<MangaCollection> collection;

	public GuiMangaFull(final GuiFrame frame, final Manga manga) {
		//		super(new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]"));
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "5:50:200[align right,grow,200::300][align left,grow,100::400,shrink]5:50:200", "[top][top]"));
		setViewportView(panel);
		
		this.frame = frame;
		this.library = frame.getLibrary();

		JLabel label;

		//Title
		JLabel title = new JLabel(manga.getName());
		title.setFont(frame.getOptions().getTitelFont());
		panel.add(title, "align center,span 2, wrap");

		// Image
		String path = manga.getMangaImagePath(library);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
			JLabel picLabel = new JLabel(new ImageIcon(image));
			panel.add(picLabel,"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Description
		JTextArea description = new JTextArea(manga.getDescription());
		description.setColumns(20);
		description.setLineWrap(true);
		description.setFont(frame.getOptions().getLabelFont());
		description.setLineWrap(true);
//		panel.add(description, "wrap,grow,shrink");
		JScrollPane scroll = new JScrollPane(description);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scroll, "grow 9,shrink, wrap");

		//		// Status
		//		label = new JLabel("Status:");
		//		panel.add(label, "gapleft 5:50:100");
		//		String text = manga.getRead() == manga.getDownloaded() ? "Up to Date" : "New Available";
		//		label = new JLabel(text);
		//		panel.add(label, "growx, wrap");

		label = new JLabel("Chapters");
		label.setFont(frame.getOptions().getSubtitelFont());
//		panel.add(label, "growx, wrap");
		panel.add(label, "align left, span 2, wrap");

		// Chapters
		JPanel grid = new JPanel(new GridLayout(0, 10));
		grid.setBorder(BorderFactory.createEmptyBorder());
		panel.add(grid, "growx, span 2, wrap");

		buttons = new ArrayList<>();
		for (int i = 1; i < manga.getDownloaded()+1000; i++) {
			JGradientButton button = new JGradientButton("" + i,manga.getRead()>=i);
			final int j = i;
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					// M.print("" + (String) combo.getSelectedItem());
					//System.exit(0)
					GuiRead read = frame.getRead();
					JTabbedPane tabbed = frame.getTabbed();
					read.view(manga, j, manga.getPage());
					tabbed.setSelectedComponent(read);
					tabbed.setEnabledAt(tabbed.indexOfComponent(read), true);
				}
			});
			// panel.add(button, "growx, span 2");
			grid.add(button);
			buttons.add(button);
		}
//		label = new JLabel("");
//		panel.add(label, "growx, wrap");

		// Options
		label = new JLabel("Options");
		label.setFont(frame.getOptions().getSubtitelFont());
//		panel.add(label, "growx, wrap");
		panel.add(label, "align left, span 2, wrap");
		
		String optionsAddLabel = "gapleft 40, align left";
		String optionsAddComponent = "width 200!, wrap";
		
		label = new JLabel("Change Colllection:");
		label.setFont(frame.getOptions().getLabelFont());
		panel.add(label,optionsAddLabel);

		collection = new JComboBox<MangaCollection>(MangaCollection.values());
		collection.setSelectedItem(manga.getCollection());
		collection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//JComboBox box = (JComboBox) e.getSource();
				MangaCollection newCollection = (MangaCollection) collection.getSelectedItem();
				manga.changeCollection(library, newCollection);
			}
		});
		panel.add(collection,optionsAddComponent);

		
		label = new JLabel("Sync:");
		label.setFont(frame.getOptions().getLabelFont());
		panel.add(label,optionsAddLabel);
		
		JButton button = new JButton("Now");
		panel.add(button,optionsAddComponent);
		
		
		label = new JLabel("Redownload:");
		label.setFont(frame.getOptions().getLabelFont());
		panel.add(label,optionsAddLabel);
		
		button = new JButton("All Chapters");
		panel.add(button,optionsAddComponent);
		
		
		
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
	


    private static final @Getter @Setter class JGradientButton extends JButton{
    	
    	boolean read = false;
    	Color colorRead = Color.GREEN;
    	Color colorUnread = Color.YELLOW;
    	Color color2 = Color.GRAY.darker();
    	
        private JGradientButton(String text,boolean read){
            super(text);
            this.read = read;
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g){
        	Color color = read ? colorRead : colorUnread;
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setPaint(new GradientPaint(
                    new Point(0, 0), 
                    color, 
                    new Point(0, getHeight()), 
                    color2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.drawString(getText(), getWidth()/2, getHeight());
            g2.dispose();
            super.paintComponent(g);
        }

    }
}
