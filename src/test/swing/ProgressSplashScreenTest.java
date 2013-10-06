package test.swing;

import java.awt.*;
import java.beans.*;
import java.util.List;
import javax.swing.*;

public class ProgressSplashScreenTest {
public static void main(String[] args) {
  EventQueue.invokeLater(new Runnable() {
    @Override public void run() {
      createAndShowGUI();
    }
  });
}
public static void createAndShowGUI() {
  final JFrame frame = new JFrame();
  final JDialog splashScreen  = new JDialog(
    frame, Dialog.ModalityType.DOCUMENT_MODAL);
  final JProgressBar progress = new JProgressBar();
  final JTabbedPane tabbedPane = new JTabbedPane();
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  frame.getContentPane().add(tabbedPane);
  progress.setStringPainted(true);

  EventQueue.invokeLater(new Runnable() {
    @Override public void run() {
      splashScreen.setUndecorated(true);
      splashScreen.getContentPane().add(
          new JLabel(new SplashScreenIcon()));
      splashScreen.getContentPane().add(progress, BorderLayout.SOUTH);
      splashScreen.pack();
      splashScreen.setLocationRelativeTo(null);
      splashScreen.setVisible(true);
    }
  });

  SwingWorker<Void,String> worker = new SwingWorker<Void,String>() {
    @Override public Void doInBackground() {
      try {
        int current = 0;
        int lengthOfTask = 120;
        while(current<=lengthOfTask && !isCancelled()) {
          try {
            Thread.sleep(50); //dummy
          } catch(InterruptedException ie) {
            ie.printStackTrace();
            return null;
          }
          if(current == 20) {
            publish("showFrame");
          } else if(current%24==0) {
            publish("title: "+current);
          }
          setProgress(100 * current++ / lengthOfTask);
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
      return null;
    }
    @Override protected void process(List<String> chunks) {
      for(String cmd : chunks) {
        if(cmd.equals("showFrame")) {
          frame.setSize(512, 320);
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        } else {
          tabbedPane.addTab(cmd, new JLabel(cmd));
          tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
          progress.setString("Loading: "+cmd);
        }
      }
    }
    @Override public void done() {
      splashScreen.dispose();
    }
  };
  worker.addPropertyChangeListener(new PropertyChangeListener() {
    @Override public void propertyChange(PropertyChangeEvent e) {
      if("progress".equals(e.getPropertyName())) {
        progress.setValue((Integer)e.getNewValue());
      }
    }
  });
  worker.execute();
}
}
class SplashScreenIcon implements Icon {
@Override public void paintIcon(Component c, Graphics g, int x, int y) {
  Graphics2D g2 = (Graphics2D)g.create();
  g2.translate(x, y);
  g2.setPaint(Color.GREEN);
  g2.fillRect(10,10,180,80);
  g2.translate(-x,-y);
  g2.dispose();
}
@Override public int getIconWidth()  {
  return 200;
}
@Override public int getIconHeight() {
  return 100;
}
}