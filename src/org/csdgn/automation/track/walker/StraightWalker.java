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
