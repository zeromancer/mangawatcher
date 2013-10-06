package test.swing;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class ThreeWayGradient {

    public static BufferedImage getThreeWayGradient(
            int size,
            Color primaryLeft,
            Color primaryRight,
            Color shadeColor) {
        BufferedImage image = new BufferedImage(
                size, size, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();
        GradientPaint primary = new GradientPaint(
                0f, 0f, primaryLeft, size, 0f, primaryRight);
        int rC = shadeColor.getRed();
        int gC = shadeColor.getGreen();
        int bC = shadeColor.getBlue();
        GradientPaint shade = new GradientPaint(
                0f, 0f, new Color(rC, gC, bC, 0),
                0f, size, shadeColor);
        g.setPaint(primary);
        g.fillRect(0, 0, size, size);
        g.setPaint(shade);
        g.fillRect(0, 0, size, size);

        g.dispose();
        return image;
    }

    /**
     * Presumed to have a layout that shows multiple components.
     */
    public static void addGradient(
            JPanel p, int s, Color pL, Color pR, Color sh) {

        JLabel l = new JLabel(new ImageIcon(getThreeWayGradient(s, pL, pR, sh)));
        p.add(l);
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                JPanel gui = new JPanel(new GridLayout(2,4,1,1));
                addGradient(gui,100,Color.YELLOW,Color.RED,Color.GREEN);
                addGradient(gui,100,Color.GREEN,Color.YELLOW,Color.RED);
                addGradient(gui,100,Color.RED,Color.GREEN,Color.YELLOW);
                addGradient(gui,100,Color.BLUE,Color.MAGENTA,Color.PINK);
                addGradient(gui,100,Color.WHITE,Color.RED,Color.BLACK);
                addGradient(gui,100,Color.RED,Color.GREEN,Color.BLACK);
                addGradient(gui,100,Color.BLUE,Color.PINK,Color.BLACK);
                addGradient(gui,100,Color.BLUE,Color.CYAN,Color.BLACK);
                JOptionPane.showMessageDialog(null, gui);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}