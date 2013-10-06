package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class MigLayout_HelloWorld extends JFrame {

    public MigLayout_HelloWorld() {
        
       setTitle("Simple example");
       setSize(300, 200);
       setLocationRelativeTo(null);
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       
       MigLayout layout = new MigLayout("fillx", "[right]rel[grow,fill]", "[]10[]");
       
       JPanel panel = new JPanel(layout);
       add(panel);
       //Container panel = this.getContentPane();
       panel.add(new JLabel("Enter size:"),   "");
       panel.add(new JTextField(""),          "wrap");
       panel.add(new JLabel("Enter weight:"), "");
       panel.add(new JTextField(""),          "wrap");
       JButton button = new JButton("Quit");

       button.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent event) {
               System.exit(0);
          }
       });
       panel.add(button,"growx, span 2");
    }
    

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	MigLayout_HelloWorld ex = new MigLayout_HelloWorld();
                ex.setVisible(true);
            }
        });
    }
}
