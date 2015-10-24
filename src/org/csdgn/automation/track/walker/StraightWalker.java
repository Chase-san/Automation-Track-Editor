package org.csdgn.automation.track.walker;

import java.awt.geom.Line2D;

import org.csdgn.automation.track.ITrackWalker;
import org.csdgn.automation.track.TrackSegment;
import org.csdgn.automation.track.TrackState;

public class StraightWalker implements ITrackWalker {
	@Override
	public TrackState nextState(TrackState state, TrackSegment segment) {
		TrackState next = new TrackState(state);

		final double length = segment.layoutInfo;
		final double length2d = length * state.track.scale;

		next.x += length2d * Math.cos(next.angle);
		next.y += length2d * Math.sin(next.angle);

		// calculate 3d length of segment
		double slopeHeight = segment.slope / 100.0 * length;
		double segmentLength = Math.sqrt(length * length + slopeHeight * slopeHeight);

		next.length += segmentLength;
		next.elevation += slopeHeight;

		next.shape = new Line2D.Double(state.x, state.y, next.x, next.y);

		return next;
	}

	/** TODO Currently Broken */
	@Override
	public TrackState nextSimState(TrackState state, TrackSegment segment) {
		TrackState next = new TrackState(state);
		
		final double step = 0.2;

		final double rho = Math.atan(segment.slope / 100);
		final double tanrho = Math.tan(rho);
		final double slopeScale = Math.sqrt(tanrho*tanrho+1);
		final double segmentLength = segment.layoutInfo * slopeScale;
		
		for(double distance = 0; distance <= segmentLength; distance += step) {
			next.x += step * Math.cos(rho) * Math.cos(next.angle) * state.track.scale;
			next.y += step * Math.cos(rho) * Math.sin(next.angle) * state.track.scale;
			next.elevation += step * Math.sin(rho);
			next.length += step;
		}
		
		next.shape = new Line2D.Double(state.x, state.y, next.x, next.y);

		return next;
	}
}
