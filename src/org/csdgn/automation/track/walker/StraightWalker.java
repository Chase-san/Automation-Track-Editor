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

		final double length = segment.layoutInfo;
		final double length2d = length * state.track.scale;

		// calculate 3d length of segment
		double slopeHeight = segment.slope / 100.0 * length;
		double segmentLength = Math.sqrt(length * length + slopeHeight * slopeHeight);

		double stepsRaw = segmentLength / 0.2;
		double steps = ((int) stepsRaw + 2);

		double totalLength = segmentLength / stepsRaw * steps;
		double totalHeight = slopeHeight / stepsRaw * steps;

		double totalLength2D = length2d / stepsRaw * steps;

		next.x += totalLength2D * Math.cos(next.angle);
		next.y += totalLength2D * Math.sin(next.angle);

		next.length += totalLength;
		next.elevation += totalHeight;

		next.shape = new Line2D.Double(state.x, state.y, next.x, next.y);

		return next;
	}
}
