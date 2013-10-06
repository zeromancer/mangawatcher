package gui.read;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import lombok.Data;
import misc.M;

public @Data class SingleImage extends JPanel {

	private static final long serialVersionUID = 1152742277827616450L;
	BufferedImage image = null;

	int zoom = 100;
	
	public SingleImage() {}

	public void load(File file) {
		
		try {
			Thread.sleep((int)(Math.random()*2000));
		} catch (InterruptedException e1) {
		}
		try {
			image = ImageIO.read(file);
			M.print("\tloaded image: " + file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null){
			g.drawImage(image, (getWidth() - image.getWidth()) / 2, 0, null);
			return;
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getBackground());
		g.fillRect(3, 3, getWidth()-3, getHeight()-3);
		String text = " Loading Image ... ";
		FontMetrics metrics = g.getFontMetrics();
		int height = metrics.getHeight();
		int width = metrics.stringWidth(text);
		g.setColor(Color.BLACK);
		g.drawString(text, (getWidth() - width-4) / 2, (getHeight() - height-2) / 2);
	}

	@Override
	public Dimension getPreferredSize() {
		if (image == null)
			return new Dimension(400, 1000);
		else
			return new Dimension(image.getWidth(), image.getHeight());
	}

//	public int getZoom() {
//		return zoom;
//	}
//
//	public void setZoom(int zoom) {
//		this.zoom = zoom;
//	}
	
	
}
