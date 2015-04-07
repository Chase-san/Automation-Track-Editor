package org.csdgn.maru;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

public class ImagePanel extends JComponent {
	private static final long serialVersionUID = -7610647109972013777L;

	private Image image;

	public ImagePanel() {
		setOpaque(false);
	}

	public ImagePanel(Image image) {
		setOpaque(false);
		setImage(image);
	}

	public void setImage(Image image) {
		this.image = image;
		setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
		RepaintManager.currentManager(this).markCompletelyClean(this);
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
