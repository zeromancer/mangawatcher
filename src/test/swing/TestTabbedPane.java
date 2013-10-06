package test.swing;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class TestTabbedPane extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;

    public TestTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(300, 200));
        getContentPane().add(tabbedPane);
        JPanel panel = new JPanel();
        tabbedPane.add(panel, "null");
        JTextField one = new JTextField("one");
        tabbedPane.add(one, "one");
        JTextField two = new JTextField("two");
        tabbedPane.add(two, "<html> T<br>i<br>t<br>t<br>l<br>e <br> 1 </html>");
        tabbedPane.setEnabledAt(2, false);
        /*int comp = tabbedPane.getComponentCount();
        for (Component sc : tabbedPane.getComponents()) {
        if (sc instanceof javax.swing.JLabel) {
        JLabel lbl = (JLabel) sc;
        lbl.setForeground(Color.red);
        }
        if (sc instanceof javax.swing.JPanel) {
        JPanel pnl = (JPanel) sc;
        pnl.setName(pnl.getName());
        }
        if (sc instanceof javax.swing.JTextField) {
        JTextField txt = (JTextField) sc;
        txt.setForeground(Color.blue);
        txt.setDisabledTextColor(Color.red);
        }
        }
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.highlight", new Color(255, 0, 0));
        UIManager.put("TabbedPane.lightHighlight", new Color(0, 255, 0));
        UIManager.put("TabbedPane.darkShadow", new Color(0, 255, 0));
        UIManager.put("TabbedPane.shadow",new Color(0, 0, 255));
        UIManager.put("TabbedPane.light" ,  new Color(0, 255, 0));
        UIManager.put("TabbedPane.foreground", new Color(0, 0, 0));
        UIManager.put("JTabbedPane.font", new Font("Dialog", Font.ITALIC, 12));
        UIManager.put("TabbedPane.selected", new Color(255, 0, 0));
        UIManager.put("disable", new Color(255, 0, 0));
        UIManager.put("TabbedPane.selectHighlight" , new Color(0, 0, 0));
        UIManager.put("TabbedPane.background",  new Color(0, 0, 0));
        SwingUtilities.updateComponentTreeUI(tabbedPane);*/
        tabbedPane.setTitleAt(2, "<html><font color="
                + (tabbedPane.isEnabledAt(2) ? "black" : "red") + ">"
                + tabbedPane.getTitleAt(2) + "</font></html>");
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
    }

    public static void main(String args[]) {
        TestTabbedPane frame = new TestTabbedPane();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}