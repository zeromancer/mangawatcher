package test.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;

public class TilePainter extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame("Tiles");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.getContentPane().add(new JScrollPane(new TilePainter()));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    private final int TILE_SIZE = 50;
    private final int TILE_COUNT = 100;
    private final int visibleTiles = 10;
    private final boolean[][] loaded;
    private final boolean[][] loading;
    private final Random random;

    public TilePainter() {
        setPreferredSize(new Dimension(TILE_SIZE * TILE_COUNT, TILE_SIZE * TILE_COUNT));
        loaded = new boolean[TILE_COUNT][TILE_COUNT];
        loading = new boolean[TILE_COUNT][TILE_COUNT];
        random = new Random();
    }

    public boolean getTile(final int x, final int y) {
        boolean canPaint = loaded[x][y];
        if (!canPaint && !loading[x][y]) {
            loading[x][y] = true;
            Timer timer = new Timer(random.nextInt(500),
                    new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            loaded[x][y] = true;
                            repaint(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                    });
            timer.setRepeats(false);
            timer.start();
        }
        return canPaint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle clip = g.getClipBounds();
        int startX = clip.x - (clip.x % TILE_SIZE);
        int startY = clip.y - (clip.y % TILE_SIZE);
        for (int x = startX; x < clip.x + clip.width; x += TILE_SIZE) {
            for (int y = startY; y < clip.y + clip.height; y += TILE_SIZE) {
                if (getTile(x / TILE_SIZE, y / TILE_SIZE)) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.RED);
                }
                g.fillRect(x, y, TILE_SIZE - 1, TILE_SIZE - 1);
            }
        }
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(visibleTiles * TILE_SIZE, visibleTiles * TILE_SIZE);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return TILE_SIZE * Math.max(1, visibleTiles - 1);
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return TILE_SIZE;
    }
}