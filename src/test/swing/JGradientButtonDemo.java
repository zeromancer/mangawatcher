package test.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class JGradientButtonDemo {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                createAndShowGUI();         
            }
        });
    }

    private static void createAndShowGUI(){
        final JFrame frame = new JFrame("Gradient JButton Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.add(JGradientButton.newInstance());
        frame.setSize(new Dimension(300, 150)); // used for demonstration
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static final class JGradientButton extends JButton{
        private JGradientButton(){
            super("Gradient Button");
            setContentAreaFilled(false);
            setFocusPainted(false); // used for demonstration
        }

        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setPaint(new GradientPaint(
                    new Point(0, 0), 
                    Color.YELLOW, 
                    new Point(0, getHeight()), 
                    Color.PINK.darker()));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();

            super.paintComponent(g);
        }

        public static final JGradientButton newInstance(){
            return new JGradientButton();
        }
    }
}