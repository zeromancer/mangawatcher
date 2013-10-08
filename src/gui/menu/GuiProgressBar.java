package gui.menu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JProgressBar;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiProgressBar extends JProgressBar {

	private static final long serialVersionUID = -5807268907142977794L;
	private String text;

	public GuiProgressBar() {
		// TODO Auto-generated constructor stub
		text = "test";
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics();
		// int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		g.setColor(Color.BLACK);
		g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
	}

}

// @formatter:off
/*

public @Getter @Setter class GuiProgressBar extends JProgressBar {

	private static final long serialVersionUID = -5807268907142977794L;
	private String text;

	public GuiProgressBar() {
		// TODO Auto-generated constructor stub
		text = "test";
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		FontMetrics metrics = g.getFontMetrics();
		// int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		g.setColor(Color.BLACK);
		g.drawString(text, (getWidth() - width) / 2, (getHeight() + 8) / 2);
	}

}


*/