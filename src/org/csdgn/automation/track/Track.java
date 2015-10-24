package org.csdgn.automation.track;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Track {
	public BufferedImage image;
	public String name;
	public int startX;
	public int startY;
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
		track.scale = 1.0;
		track.split1 = 0;
		track.split2 = 0;

		track.segments.add(new TrackSegment());

		return track;
	}
	
	
}
