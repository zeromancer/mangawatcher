package test.swing;

import java.awt.BorderLayout;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class StateChangedDemo {

    public static void main(String[] args) throws UnsupportedEncodingException {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout());
        final JPanel buttons = new JPanel();
        final JScrollPane pane = new JScrollPane(buttons);
        pane.getViewport().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                System.err.println("Change in " + e.getSource());
                System.err.println("Vertical visible? " + pane.getVerticalScrollBar().isVisible());
                System.err.println("Horizontal visible? " + pane.getHorizontalScrollBar().isVisible());
            }
        });
        panel.add(pane);
        frame.setContentPane(panel);
        frame.setSize(300, 200);
        frame.setVisible(true);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(800);
                    buttons.add(new JButton("Hello " + i));
                    buttons.revalidate();
                }
                return null;
            }
        };
        worker.execute();
    }
}