package gui.reading;

import gui.GuiFrame;
import gui.menu.GuiProgressBar;
import gui.threading.BackgroundExecutors;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;
import lombok.Setter;
import data.Manga;

public @Getter @Setter class GuiRead extends JPanel {

	private GuiFrame frame;
	private GuiProgressBar progress;
	private BackgroundExecutors executors;

	private JSlider slider;
	private GuiReadView view;
	private GuiReadToolBar bar;

	public GuiRead(GuiFrame frame) {
		this.frame = frame;
		this.executors = frame.getExecutors();

		setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// JPanel panel = new JPanel(new MigLayout("fillx",
		// "[grow,fill]rel[grow,fill]", ""));
		add(panel, BorderLayout.SOUTH);


		progress = new GuiProgressBar(frame);
		progress.setOrientation(JProgressBar.HORIZONTAL);
		progress.setMinimum(0);
		progress.setMaximum(100);
		//panel.add(progress, "");


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

		view = new GuiReadView(frame, this);
		view.requestFocus();
		view.requestFocusInWindow();
		add(view, BorderLayout.CENTER);

		bar = new GuiReadToolBar(frame, this, view);
		add(bar, BorderLayout.NORTH);

	}

	public void view(Manga manga, int chapter, int page) {
		view.view(manga, chapter, page);
	}

}
