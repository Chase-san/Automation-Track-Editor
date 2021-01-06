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
