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
package org.csdgn.automation.track;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Track {
	public BufferedImage image;
	public String name;
	public int startX;
	public int startY;
	public double startAngle;
	public double scale;
	public double split1;
	public double split2;

	public ArrayList<TrackSegment> segments;

	public Track() {
		segments = new ArrayList<TrackSegment>();
	}

	public void rescale(double scale) {
		double segmentScale = this.scale / scale;
		this.scale = scale;

		split1 *= segmentScale;
		split2 *= segmentScale;

		for(TrackSegment segment : segments) {
			if(segment.layout == TrackLayoutType.STRAIGHT.number) {
				segment.layoutInfo *= segmentScale;
			} else {
				segment.cornerRadius *= segmentScale;
			}
		}
	}

	public static Track createNewTrack() {
		Track track = new Track();
		track.image = null;
		track.name = "New Track";
		track.startX = 640;
		track.startY = 360;
		track.startAngle = 0;
		track.scale = 1.0;
		track.split1 = 0;
		track.split2 = 0;

		track.segments.add(new TrackSegment());

		return track;
	}
	
	
}
