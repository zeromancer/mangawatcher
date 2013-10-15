/*
    MangaWatcher - a manga management program. 
    Copyright (C) 2013 David Siewert

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package misc;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.channels.FileChannel;

public class M {

	public static void print(String text){
		System.out.println(text);
	}
	
	public static void exception(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		print(sw.toString());
		print(e.getMessage());
	}
	
	
	
	

	public static BufferedImage scale(BufferedImage image, int fromZoom, int toZoom) {
		int width = (int) ((float) image.getWidth() * toZoom / fromZoom);
		int height = (int) ((float) image.getHeight() * toZoom / fromZoom);
		// M.print("  "+image.getWidth()+ " -> "+width);
		Image img = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage newer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		newer.getGraphics().drawImage(img, 0, 0, null);
		return newer;
	}
	
	@SuppressWarnings("resource")
	public static void copy(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			// inChannel.transferTo(0, inChannel.size(), outChannel); //
			// original -- apparently has trouble copying large files on Windows

			// magic number for Windows, 64Mb - 32Kb)
			int maxCount = (64 * 1024 * 1024) - (32 * 1024);
			long size = inChannel.size();
			long position = 0;
			while (position < size) {
				position += inChannel.transferTo(position, maxCount, outChannel);
			}
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
	
	public static void sleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
			print(e.getMessage());
		}
	}
	
	public static int getBounded(int min,int value, int max){
		if(value<min)
			return min;
		if(value>max)
			return max;
		return value;
	}
	
}
