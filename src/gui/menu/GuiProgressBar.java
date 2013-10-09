package gui.menu;

import gui.GuiFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JProgressBar;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiProgressBar extends JProgressBar {

	private final GuiFrame frame;

	private String text;

	public GuiProgressBar(GuiFrame frame) {
		this.frame = frame;
		text = "unselected";
		//setStringPainted(true);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.setFont(frame.getOptions().getSubtitelFont());
		String text = getText();
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(Color.BLACK);
		int x = (getWidth() - metrics.stringWidth(text)) / 2;
		int y = metrics.getAscent();
		g.drawString(getText(), x, y);

//		int y = (getHeight() ) / 2 + (+metrics.getDescent() + metrics.getAscent()) / 2;
//		System.out.println(g.getClip());
//		M.print("metrics.getDescent(): " + metrics.getDescent() + " , metrics.getAscent(): " + metrics.getAscent());
//		M.print("metrics.width: " + width + " , metrics.height: " + height);
//		M.print("");
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension((int)d.getWidth(), (int)frame.getOptions().getSubtitelFont().getSize()+3);
//		return super.getPreferredSize();
	}

	//	public void setText(String text){
	//		setString(text);
	//	}

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