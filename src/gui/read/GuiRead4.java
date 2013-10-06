package gui.read;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import misc.M;

public class GuiRead4 {

	public static class Image extends JPanel {

		private static final long serialVersionUID = 1152742277827616450L;
		BufferedImage image = null;

		public Image() {
		}

		public void load(File file) {
			try {
				image = ImageIO.read(file);
				M.print("image loaded: " + file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image != null)
				g.drawImage(image, (getWidth() - image.getWidth()) / 2, 0, null);
		}

		@Override
		public Dimension getPreferredSize() {
			if (image == null)
				return new Dimension(200, 500);
			else
				return new Dimension(image.getWidth(), image.getHeight());
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "/home/divakar/Mangas/Naruto/0001";
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		File folder = new File(path);

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".jpg");
			}
		};
		List<File> files = new ArrayList<File>();
		files.addAll(Arrays.asList(folder.listFiles(filter)));
		Collections.sort(files);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(33, 33, 33));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// panel.setPreferredSize(new Dimension(500, 1000));

		for (int i = 0; i < files.size(); i++) {
			File file = files.get(i);
			// ImageIcon icon = new ImageIcon(ImageIO.read(file));
			Image image = new Image();
			image.setAlignmentX(Component.CENTER_ALIGNMENT);
			image.load(file);
			panel.add(image);

		}
		final JScrollPane scroll = new JScrollPane(panel);
		scroll.getVerticalScrollBar().setUnitIncrement(70);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(scroll);

		
		scroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
		    @Override
		    public void adjustmentValueChanged(AdjustmentEvent ae) {
		        int extent = scroll.getVerticalScrollBar().getModel().getExtent();
		        System.out.println("Value: " + (scroll.getVerticalScrollBar().getValue()+extent) + " Max: " + scroll.getVerticalScrollBar().getMaximum());
		    }
		});
		
		// Image icon = new Image();
		// icon.loadImage(files.get(0));
		// frame.add(new JScrollPane(icon));

		//frame.pack();
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}