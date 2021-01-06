package org.csdgn.automation.track;

import java.awt.Shape;

public class TrackState {
	public final Track track;
	public double x;
	public double y;
	public double angle;
	public double length;
	public double elevation;

	public Shape shape;

	public TrackState(Track track) {
		this.track = track;
		this.x = track.startX;
		this.y = track.startY;
		this.angle = track.startAngle;
		this.length = 0;
		this.elevation = 0;
		this.shape = null;
	}

	public TrackState(TrackState state) {
		this.track = state.track;
		this.x = state.x;
		this.y = state.y;
		this.angle = state.angle;
		this.length = state.length;
		this.elevation = state.elevation;
	}
}
