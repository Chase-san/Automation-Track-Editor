package org.csdgn.automation.track.walker;

import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;

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

		final double step = 0.2;
		final double radius2d = segment.cornerRadius;
		
		final double rho = Math.atan(segment.slope / 100);
		final double tanrho = Math.tan(rho);
		final double slopeScale = Math.sqrt(tanrho*tanrho+1);
		final double angleRad = Math.toRadians(segment.layoutInfo);
		final double segmentLength = angleRad * radius2d * slopeScale;
		
		for(double distance = 0; distance <= segmentLength; distance += step) {
			next.angle += -1 * segment.layout * Math.atan(Math.abs(angleRad/(segmentLength/step)));
			next.x += step * Math.cos(rho) * Math.cos(next.angle) * state.track.scale;
			next.y += step * Math.cos(rho) * Math.sin(next.angle) * state.track.scale;
			next.elevation += step * Math.sin(rho);
			next.length += step;
		}
		
		//calculate the center of the arc
		

		// create graphic arc java can understand.
		next.shape = new Line2D.Double(state.x,state.y,next.x,next.y);

		return next;
	}
}
