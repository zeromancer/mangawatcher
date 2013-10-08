package gui.menu;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GuiMenuBar extends JPanel {

	JButton button;

	public GuiMenuBar() {
		// TODO Auto-generated constructor stub
		button = new JButton("test");
		button.setPreferredSize(new Dimension(25, 16));
		add(button);
	}
	
	public void info(int progress,String text){
		
	}

}
