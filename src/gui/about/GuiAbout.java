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
package gui.about;

import gui.GuiFrame;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import data.Engine.Icons;
import data.Options;

@SuppressWarnings("unused") public class GuiAbout extends JScrollPane {

	private final GuiFrame frame;
	private final Options options;

	private final JPanel panel;

	private final String layoutTitle = "span 2, align center";
	private final String layoutLabel = "align right";
	private final String layoutComponent = "align left, gaptop 30";

	public GuiAbout(GuiFrame frame) {
		this.frame = frame;
		this.options = frame.getOptions();
		
		panel = new JPanel();
		panel .setLayout(new MigLayout("wrap 2", "5%::20%[grow,shrink]20::50[grow,shrink]5%::20%", ""));
		this.setViewportView(panel);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setUnitIncrement(frame.getOptions().getScrollAmount());
		
		JLabel title = createMiddle("About");
		title.setFont(options.getTitelFont());
		//title.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel icon = new JLabel(new ImageIcon(frame.getEngine().getIcon(Icons.LOGO)));
		panel.add(icon,layoutTitle);
		
		createMiddle("Source Code:");
		

		JLabel license = createLeft("License:");
		JLabel licenseContent = createRightLabel("GPL v3");
		JLabel location = createLeft("Location:");
		JLabel locationContent = createRightLabel("GitHub");
		JLabel link = createLeft("Link:");
		JLabel linkContent = createRightLabel("https://github.com/zeromancer/mangawatcher");
//		JLabel linkContent = createRightLabel("<HTML><a href=\"https://github.com/zeromancer/mangawatcher\" >https://github.com/zeromancer/mangawatcher</a></HTML>");
//		linkContent.set
		JLabel author = createLeft("Author:");
		JLabel autherContent = createRightLabel("David Siewert");
		JLabel contact = createLeft("Contact:");
		JLabel contactContent = createRightLabel("david"+"0"+"siewert"+"@"+"gmail.com");
//		autherContent.

		
		createMiddle("Icons:");
		
		JLabel iconLicense = createLeft("License:");
		JLabel iconLicenseContent = createRightLabel("Creative Commons Attribution-NoDerivs 3.0 Unported");
		
		JLabel iconLink = createLeft("Link:");
		JLabel iconLinkContent= createRightLabel("http://icons8.com/");
		
		
	}

	private JLabel createLeft(String text) {
		JLabel label = new JLabel(text);
		label.setFont(options.getLabelFont());
		panel.add(label, layoutLabel);
		return label;
	}

	private JLabel createMiddle(String text) {
		JLabel label = new JLabel(text);
		label.setFont(options.getSubtitelFont());
		//label.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(label, layoutTitle);
		return label;
	}

	private JLabel createRightLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(options.getLabelFont());
		panel.add(label, layoutComponent);
		return label;
	}

	private JTextArea createRightArea(String text) {
		JTextArea area = new JTextArea(text);
		area.setFont(options.getLabelFont());
		area.setEditable(false);
		panel.add(area, layoutComponent);
		return area;
	}

	private static void open(URI uri) {
		if (!Desktop.isDesktopSupported())
			return;
		try {
			Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
		}
	}

}
