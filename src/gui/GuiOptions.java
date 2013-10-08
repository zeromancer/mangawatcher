package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;

public @Getter @Setter class GuiOptions extends JPanel {

	private int checkInterval; //in min

	private Font buttonFont;
	private Font titelFont;
	private Font subtitelFont;
	private Font labelFont;
	
	private Color readingBackgroundColor;
	
	private int scrollAmount;
	
	public GuiOptions() {
		buttonFont = new Font("Sans", Font.PLAIN, 16);
		titelFont = new Font("Sans", Font.BOLD, 48);
		subtitelFont = new Font("Sans", Font.BOLD, 28);
		labelFont = new Font("Sans", Font.PLAIN, 16);
		scrollAmount = 50;
	}

}
