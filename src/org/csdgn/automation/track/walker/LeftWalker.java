package org.csdgn.automation.track.walker;

import java.awt.geom.Arc2D;

import org.csdgn.automation.track.ITrackWalker;
import org.csdgn.automation.track.TrackSegment;
import org.csdgn.automation.track.TrackState;

public class LeftWalker implements ITrackWalker {
	private static final double HALF_PI = Math.PI / 2.0;

	@Override
	public TrackState nextState(TrackState state, TrackSegment segment) {
		TrackState next = new TrackState(state);

		final double radius2d = segment.cornerRadius * state.track.scale;
		final double angleRad = Math.toRadians(segment.layoutInfo);

		final double centerX = state.x + radius2d * Math.cos(state.angle - HALF_PI);
		final double centerY = state.y + radius2d * Math.sin(state.angle - HALF_PI);

		double arcLength = segment.cornerRadius * angleRad;

		next.x = centerX + radius2d * Math.cos(next.angle - angleRad + HALF_PI);
		next.y = centerY + radius2d * Math.sin(next.angle - angleRad + HALF_PI);

		double slopeHeight = segment.slope / 100.0 * arcLength;
		next.length += Math.sqrt(arcLength * arcLength + slopeHeight * slopeHeight);
		next.elevation += slopeHeight;

		next.angle -= angleRad;

		// create graphic arc java can understand.
		next.shape = new Arc2D.Double(centerX - radius2d, centerY - radius2d, radius2d * 2, radius2d * 2,
				270 - Math.toDegrees(state.angle), // start
				segment.layoutInfo, // extend
				Arc2D.OPEN);

		return next;
	}

	/** TODO Currently Broken */
	@Override
	public TrackState nextSimState(TrackState state, TrackSegment segment) {
		TrackState next = new TrackState(state);

		final double radius2d = segment.cornerRadius * state.track.scale;
		final double angleRad = Math.toRadians(segment.layoutInfo);

		final double centerX = state.x + radius2d * Math.cos(state.angle - HALF_PI);
		final double centerY = state.y + radius2d * Math.sin(state.angle - HALF_PI);

		double arcLength = segment.cornerRadius * angleRad;

		double slopeHeight = segment.slope / 100.0 * arcLength;
		double segmentLength = Math.sqrt(arcLength * arcLength + slopeHeight * slopeHeight);

		double stepsRaw = segmentLength / 0.2;
		double steps = ((int) stepsRaw + 2);

		double totalAngle = angleRad / stepsRaw * steps;
		double totalLength = segmentLength / stepsRaw * steps;
		double totalHeight = slopeHeight / stepsRaw * steps;

		next.x = centerX + radius2d * Math.cos(next.angle - totalAngle + HALF_PI);
		next.y = centerY + radius2d * Math.sin(next.angle - totalAngle + HALF_PI);

		next.length += totalLength;
		next.elevation += totalHeight;
		next.angle -= totalAngle;

		// create graphic arc java can understand.
		next.shape = new Arc2D.Double(centerX - radius2d, centerY - radius2d, radius2d * 2, radius2d * 2,
				270 - Math.toDegrees(state.angle), // start
				Math.toDegrees(totalAngle), // extend
				Arc2D.OPEN);

		return next;
	}
}
