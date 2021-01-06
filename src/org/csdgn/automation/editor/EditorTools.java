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
package org.csdgn.automation.editor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class EditorTools {
	public static BufferedImage slopeImage(double percent) {
		BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);

		double height = percent / 100.0 * -18;
		percent = Math.abs(percent);

		Path2D.Double path = new Path2D.Double();
		path.moveTo(0, 0);
		path.lineTo(0, 4);
		path.lineTo(18, height + 4);
		path.lineTo(18, height);
		path.lineTo(0, 0);

		// center path on 12, 12
		Rectangle2D bounds = path.getBounds2D();
		path.transform(AffineTransform.getTranslateInstance(3, -bounds.getY() - bounds.getHeight() / 2.0 + 12));

		Graphics2D gx = image.createGraphics();
		gx.setColor(Color.CYAN);

		// make steeper slopes "more dangerous" via color
		if(percent >= 80) {
			gx.setColor(Color.RED);
		} else if(percent >= 60) {
			gx.setColor(Color.ORANGE);
		} else if(percent >= 40) {
			gx.setColor(Color.YELLOW);
		} else if(percent >= 20) {
			gx.setColor(Color.GREEN);
		}
		gx.fill(path);
		gx.setColor(Color.BLACK);
		gx.draw(path);

		return image;
	}

	public static BufferedImage camberImage(double angle) {
		// easiest way, just change the angle to a percent
		angle = -Math.toRadians(angle);
		return slopeImage(-100.0 / Math.sin(Math.PI / 2.0 - angle) * Math.sin(angle));
	}
}
