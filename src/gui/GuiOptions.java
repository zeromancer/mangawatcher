package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiOptions extends JPanel {

	private int checkInterval; // in minutes

	private Font buttonFont;
	private Font titelFont;
	private Font subtitelFont;
	private Font labelFont;
	private Font textFont;
	
	private Color readingBackgroundColor;
	
	private int scrollAmount;
	
	
	public GuiOptions() {
		checkInterval = 1;
		buttonFont = new Font("Sans", Font.PLAIN, 16);
		titelFont = new Font("Sans", Font.BOLD, 48);
		subtitelFont = new Font("Sans", Font.BOLD, 28);
		labelFont = new Font("Sans", Font.PLAIN, 16);
		textFont = new Font("Sans", Font.PLAIN, 12);
		scrollAmount = 50;
	}

}
