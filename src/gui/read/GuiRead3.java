package gui.read;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class GuiRead3 {

	public static void main(String[] args) throws IOException {
		String path = "/home/divakar/Mangas/Naruto/0001";
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		File folder = new File(path);
//		List<File> files = new ArrayList<File>();
//		files.addAll(Arrays.asList(folder.listFiles()));
//		Collections.sort(files);
		
		FilenameFilter filter = new FilenameFilter() {
		      public boolean accept(File dir, String name) {
		          return !name.endsWith(".jpg");
		      }
		  };
		
		File[] files = folder.listFiles(filter);
		DefaultListModel<ImageIcon> model = new DefaultListModel<ImageIcon>();
		int count = 0;
		for (int i = 0; i < files.length; i++) {
			System.out.println("check path" + files[i]);
			String name = files[i].toString();
			// load only JPEGs
			if (name.endsWith("jpg")) {
				ImageIcon ii = new ImageIcon(ImageIO.read(files[i]));
				model.add(count++, ii);
			}
		}
		JList<ImageIcon> list = new JList<ImageIcon>(model);
		//list.setVisibleRowCount(1);

		frame.add(new JScrollPane(list));

		frame.pack();
		frame.setVisible(true);
	}
}