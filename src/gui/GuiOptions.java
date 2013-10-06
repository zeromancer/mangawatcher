package gui;

import java.awt.Color;
import java.awt.Font;

import lombok.Data;

public @Data class GuiOptions {

	private int checkInterval; //in min

	Font buttonFont;
	Font titelFont;
	Font labelFont;
	
	Color readingBackgroundColor;
	
	public GuiOptions() {
		// TODO Auto-generated constructor stub
	}

}
