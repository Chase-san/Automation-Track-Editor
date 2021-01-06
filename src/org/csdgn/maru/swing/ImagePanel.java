/**
 * Copyright (c) 2014-2021 Robert Maupin
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 *
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 *
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.maru.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * A component to display an image.
 */
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
