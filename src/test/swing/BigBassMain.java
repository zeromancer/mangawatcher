//package test.swing;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.KeyEvent;
//
//import javax.swing.AbstractAction;
//import javax.swing.ActionMap;
//import javax.swing.InputMap;
//import javax.swing.JComponent;
//import javax.swing.JFrame;
//import javax.swing.KeyStroke;
//
//@SuppressWarnings("serial") public class BigBassMain extends JFrame {
//
//	private static final String SPACE_BAR = "space bar";
//	// VARIABLES
//	public static String title = "Royal Casino";
//	public static String author = "bigbass1997";
//	public static String version = "0.0.0";
//
//	GamePanel gp;
//
//	public BigBassMain() {
//		gp = new GamePanel();
//		this.setSize(GamePanel.gameDim);
//		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		this.setVisible(true);
//		this.setTitle(title + " " + version);
//		this.setResizable(false);
//		this.setLocationRelativeTo(null);
//		this.add(gp);
//
//		ActionMap actionMap = gp.getActionMap();
//		InputMap inputMap = gp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//
//		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), SPACE_BAR);
//		actionMap.put(SPACE_BAR, new AbstractAction() {
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				Slots.slotsThread.start();
//				System.out.println("Slot THREAD Started");
//				GamePanel.slotsplaying = true;
//			}
//		});
//	}
//
//	public static void main(String[] args) {
//		@SuppressWarnings("unused")
//		BigBassMain m = new BigBassMain();
//	}
//
// //}