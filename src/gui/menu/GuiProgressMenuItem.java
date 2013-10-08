package gui.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JMenuItem;
import javax.swing.JProgressBar;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiProgressMenuItem extends JMenuItem {

	private static final long serialVersionUID = -5807268907142977794L;
	private String text;

	private JProgressBar progress;

	public void setMaximum(int maximum) {
		progress.setMaximum(maximum);
	}

	public void setMinimum(int minimum) {
		progress.setMinimum(minimum);
	}

	public void setValue(int value) {
		progress.setValue(value);
	}

	public int getValue() {
		return progress.getValue();
	}

	public GuiProgressMenuItem() {
		// TODO Auto-generated constructor stub
		text = "test";

		progress = new JProgressBar() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				FontMetrics metrics = g.getFontMetrics();
				// int height = metrics.getHeight();
				int width = metrics.stringWidth(text);
				g.setColor(Color.BLACK);
				g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
			}
		};
	}

	// protected void paintComponent(Graphics g) {
	// super.paintComponent(g);
	// g.setColor(Color.BLACK);
	// FontMetrics metrics = g.getFontMetrics();
	// // int height = metrics.getHeight();
	// int width = metrics.stringWidth(text);
	// g.setColor(Color.BLACK);
	// g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
	// }

}