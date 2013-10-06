package gui.reading;

import gui.menu.GuiProgressBar;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logic.LibraryManager;
import lombok.Getter;
import lombok.Setter;
import data.Manga;
import data.Manga.MangaCollection;
import data.MangaLibrary;

public @Getter @Setter class GuiRead extends JPanel {

	private static final long serialVersionUID = -2034716361151613173L;

	private GuiProgressBar progress;
	private JSlider slider;
	private GuiReadView view;
	private GuiReadToolBar bar;

	public GuiRead() {
		// try {
		// //
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// //
		// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		// //
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		//
		// System.setProperty("awt.useSystemAAFontSettings", "on");
		// System.setProperty("swing.aatext", "true");
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// M.print(e.getMessage());
		// }

		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// JPanel panel = new JPanel(new MigLayout("fillx",
		// "[grow,fill]rel[grow,fill]", ""));
		add(panel, BorderLayout.SOUTH);


		progress = new GuiProgressBar();
		progress.setOrientation(JProgressBar.HORIZONTAL);
		progress.setMinimum(0);
		progress.setMaximum(100);
		progress.setText("test");
		panel.add(progress, "");


		slider = new JSlider();
		slider.setPaintTicks(true);
		slider.setMinorTickSpacing(1);
		slider.setSnapToTicks(true);
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setInverted(true);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();
				int index = view.getPage();
				if (slider.getValueIsAdjusting())
					return;
				// M.print("slider: " + value + ", index: " + index);
				if (value != index) {
					view.page(value - index);
					view.repaint();
					// gui.getManga().setPage(value);
				}
			}
		});
		add(slider, BorderLayout.EAST);

		view = new GuiReadView(this, progress, slider, null);
		view.requestFocus();
		view.requestFocusInWindow();
		add(view, BorderLayout.CENTER);

		bar = new GuiReadToolBar(this, view);
		add(bar, BorderLayout.NORTH);

	}

	public static void main(String[] args) throws IOException {

		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		MangaLibrary library = LibraryManager.loadLibrary("config");
		Manga manga = library.getCollection(MangaCollection.WATCHING).get(1);
		GuiRead gui = new GuiRead();
		gui.getView().view(library, manga, 2, 1);
		frame.getContentPane().add(gui);

		// frame.pack();
		frame.setSize(600, 800);
		frame.setVisible(true);
		frame.validate();
	}


}
