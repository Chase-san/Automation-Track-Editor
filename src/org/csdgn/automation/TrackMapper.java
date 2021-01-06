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
package org.csdgn.automation;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.geom.Line2D;

import org.csdgn.automation.track.Track;
import org.csdgn.automation.track.TrackSegment;
import org.csdgn.automation.track.TrackState;

public class TrackMapper {
	private ArrayList<TrackState> states;
	private Track track;
	private double elevationDifference;
	private boolean dirty;
	private boolean simulate = false;

	public TrackMapper(Track track) {
		this.states = new ArrayList<TrackState>();
		this.track = track;
		this.dirty = true;
		this.elevationDifference = 0;
	}

	public double getElevationDifference() {
		if(dirty) {
			run();
		}
		return elevationDifference;
	}

	public void setSimulateLayout(boolean simulate) {
		this.simulate = simulate;
		markDirty();
	}

	public void clear() {
		states.clear();
		dirty = true;
	}

	public void markDirty() {
		dirty = true;
	}

	public int size() {
		if(dirty) {
			run();
		}
		return states.size();
	}

	public TrackState get(int index) {
		if(dirty) {
			run();
		}
		return states.get(index);
	}

	public TrackState getLast() {
		if(dirty) {
			run();
		}
		return states.get(states.size() - 1);
	}

	public Track getTrack() {
		return track;
	}

	public ArrayList<TrackState> states() {
		if(dirty) {
			run();
		}
		return states;
	}

	public void run() {
		states.clear();

		TrackState state = new TrackState(track);
		states.add(state);

		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;

		for(TrackSegment segment : track.segments) {
			if(state.elevation > max)
				max = state.elevation;
			if(state.elevation < min)
				min = state.elevation;
			if(simulate) {
				state = segment.nextSimState(state);
			} else {
				state = segment.nextState(state);
			}

			states.add(state);
		}
		if(state.elevation > max)
			max = state.elevation;
		if(state.elevation < min)
			min = state.elevation;

		elevationDifference = Math.abs(max - min);

		dirty = false;
	}

	private static double appQuadDistSq(double x0, double y0, double x1, double y1, double x2, double y2, double x, double y) {
		double bestDist = Double.POSITIVE_INFINITY;
		double lx = x0;
		double ly = x1;
		for(double i = 1; i <= 10; ++i) {
			double t = i / 10.0;
			double bx = (1 - t) * (1 - t) * x0 + 2 * (1 - t) * t * x1 + t * t * x2;
			double by = (1 - t) * (1 - t) * y0 + 2 * (1 - t) * t * y1 + t * t * y2;
			double dist = Line2D.ptSegDistSq(lx, ly, bx, by, x, y);
			if(dist < bestDist) {
				bestDist = dist;
			}
			lx = bx;
			ly = by;
		}
		return bestDist;
	}

	private static double appCubicDistSq(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double x,
			double y) {
		double bestDist = Double.POSITIVE_INFINITY;
		double lx = x0;
		double ly = x1;
		for(double i = 1; i <= 10; ++i) {
			double t = i / 10.0;
			double bx = (1 - t) * (1 - t) * (1 - t) * x0 + 3 * t * (1 - t) * (1 - t) * x1 + 3 * t * t * (1 - t) * x2 + t * t * t * x3;
			double by = (1 - t) * (1 - t) * (1 - t) * y0 + 3 * t * (1 - t) * (1 - t) * y1 + 3 * t * t * (1 - t) * y2 + t * t * t * y3;
			double dist = Line2D.ptSegDistSq(lx, ly, bx, by, x, y);
			if(dist < bestDist) {
				bestDist = dist;
			}
			lx = bx;
			ly = by;
		}
		return bestDist;
	}

	private static double pathDistSq(PathIterator path, double x, double y) {
		double[] segment = new double[6];
		double bestDist = Double.POSITIVE_INFINITY;
		double lx = 0, ly = 0;
		while(!path.isDone()) {
			switch(path.currentSegment(segment)) {
			case PathIterator.SEG_MOVETO:
				lx = segment[0];
				ly = segment[1];
				break;
			case PathIterator.SEG_LINETO: {
				double dist = Line2D.ptSegDistSq(lx, ly, segment[0], segment[1], x, y);
				lx = segment[0];
				ly = segment[1];
				if(dist < bestDist) {
					bestDist = dist;
				}
				break;
			}
			case PathIterator.SEG_QUADTO: {
				double dist = appQuadDistSq(lx, ly, segment[0], segment[1], segment[2], segment[3], x, y);
				lx = segment[2];
				ly = segment[3];
				if(dist < bestDist) {
					bestDist = dist;
				}
				break;
			}
			case PathIterator.SEG_CUBICTO:
				double dist = appCubicDistSq(lx, ly, segment[0], segment[1], segment[2], segment[3], segment[4], segment[5], x, y);
				lx = segment[4];
				ly = segment[5];
				if(dist < bestDist) {
					bestDist = dist;
				}
				break;
			case PathIterator.SEG_CLOSE:
				// TODO 0 points, go back to last moveto
				break;
			}
			path.next();
		}

		return bestDist;
	}

	public int getIndexOfSegmentNear(double x, double y) {
		if(x < 0 || y < 0 || x > 1280 || y > 720) {
			return -1;
		}

		if(dirty) {
			run();
		}

		// This will be a real pain in the... you know
		double bestDist = Double.POSITIVE_INFINITY;
		int bestIndex = -1;
		for(int i = 1; i < states.size(); ++i) {
			Shape s = states.get(i).shape;
			Rectangle2D bounds = s.getBounds2D();
			bounds = new Rectangle2D.Double(bounds.getX() - 10, bounds.getY() - 10, bounds.getWidth() + 20, bounds.getHeight() + 20);

			if(s != null && bounds.contains(x, y)) {
				// check to see if we are near the path
				double dist = pathDistSq(s.getPathIterator(null), x, y);
				if(dist < bestDist) {
					bestDist = dist;
					bestIndex = i - 1;
				}
			}
		}

		return bestIndex;
	}
}
