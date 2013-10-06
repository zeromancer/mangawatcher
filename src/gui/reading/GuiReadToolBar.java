package gui.reading;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import misc.M;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("unused")
public class GuiReadToolBar extends JPanel {

	private static final long serialVersionUID = 5673903268744027432L;

	private JSlider zoom;
	private JSlider scroll;
	private GuiRead gui;
	private GuiReadView view;

	private JToggleButton showZoom;
	private JToggleButton showScroll;
	private JButton save;
	private JButton previousChapter;
	private JButton previousPage;
	private JButton nextChapter;
	private JButton nextPage;

	public GuiReadToolBar(GuiRead gui, GuiReadView view) {
		this.gui = gui;
		this.view = view;
		constructGuiOptions();
	}

	public void constructGuiOptions() {
		setLayout(new MigLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));
		save = new JButton("Save");
		showZoom = new JToggleButton("Show Zoom");
		showScroll = new JToggleButton("Show Scroll");
		previousChapter = new JButton("Chapter--");
		previousPage = new JButton("Page--");
		nextPage = new JButton("Page++");
		nextChapter = new JButton("Chapter++");
		zoom = new JSlider(10, 400, view.getZoom());
		zoom.setPaintTicks(true);
		zoom.setMinorTickSpacing(10);
		zoom.setSnapToTicks(true);
		scroll = new JSlider(1, 500, view.getScrollAmount());
		scroll.setPaintTicks(true);
		scroll.setMinorTickSpacing(10);
		scroll.setSnapToTicks(true);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("10%"));
		labelTable.put(new Integer(100), new JLabel("100%"));
		labelTable.put(new Integer(200), new JLabel("200%"));
		labelTable.put(new Integer(300), new JLabel("300%"));
		labelTable.put(new Integer(400), new JLabel("400%"));
		zoom.setLabelTable(labelTable);
		zoom.setPaintLabels(true);
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("10 px"));
		labelTable.put(new Integer(100), new JLabel("100 px"));
		labelTable.put(new Integer(200), new JLabel("200 px"));
		labelTable.put(new Integer(300), new JLabel("300 px"));
		labelTable.put(new Integer(400), new JLabel("400 px"));
		labelTable.put(new Integer(500), new JLabel("400 px"));
		scroll.setLabelTable(labelTable);
		scroll.setPaintLabels(true);

		add(save);
		add(showZoom);
		add(showScroll);
		add(previousChapter);
		add(previousPage);
		add(nextPage);
		add(nextChapter, "wrap");
		final String sliderOptions = "growx, span";
		// add(zoom, zoomAdd);

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				M.print("TODO: manga save");
			}
		});

		// TODO: disable next/previous Page/Chapter, then no next/previous
		// Chapter available


		showZoom.setSelected(false);
		showZoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				System.out.println("Action - selected=" + selected + "\n");
				if (selected) {
					add(zoom, sliderOptions);
					// setSize(getPreferredSize());
					revalidate();
				} else {
					remove(zoom);
					// setSize(getWidth(), getPreferredSize());
					// setSize(getPreferredSize());
					revalidate();
				}
				repaint();
			}
		});


		showScroll.setSelected(false);
		showScroll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				// System.out.println("Action - selected=" + selected + "\n");
				if (selected) {
					add(scroll, sliderOptions);
					// setSize(getPreferredSize());
					revalidate();
				} else {
					remove(scroll);
					// setSize(getWidth(), getPreferredSize());
					// setSize(getPreferredSize());
					revalidate();
				}
				repaint();
			}
		});

		previousChapter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.previousChapter();
			}
		});
		previousPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.previousPage();
			}
		});
		nextPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.nextPage();
			}
		});
		nextChapter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.nextChapter();
			}
		});

		// TODO: load default zoom value from file
		// TODO: Disable zoom while loading images
		zoom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (zoom.getValueIsAdjusting())
					return;
				int value = zoom.getValue();
				view.setZoom(value);
			}
		});
		scroll.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (scroll.getValueIsAdjusting())
					return;
				int value = scroll.getValue();
				view.setScrollAmount(value);
			}
		});
	}

	public void previousChapter(boolean enabled) {
		previousChapter.setEnabled(enabled);
	}

	public void nextChapter(boolean enabled) {
		nextChapter.setEnabled(enabled);
	}

}
