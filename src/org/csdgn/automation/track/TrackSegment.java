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

public class TrackSegment {

	public int layout; //1
	public double layoutInfo; //2
	public double cornerRadius; //3
	public double slope; //4
	public int sportiness; //5
	public double camber; //6

	public TrackSegment() {
		layout = 0;
		layoutInfo = 50;
		cornerRadius = 0;
		slope = 0;
		sportiness = 0;
		camber = 0;
	}

	public TrackSegment(TrackSegment seg) {
		layout = seg.layout;
		layoutInfo = seg.layoutInfo;
		cornerRadius = seg.cornerRadius;
		slope = seg.slope;
		sportiness = seg.sportiness;
		camber = seg.camber;
	}

	public TrackLayoutType getType() {
		for(TrackLayoutType type : TrackLayoutType.values()) {
			if(type.number == layout) {
				return type;
			}
		}
		return null;
	}

	public TrackState nextState(TrackState state) {
		TrackLayoutType type = getType();
		if(type != null) {
			return getType().walker.nextState(state, this);
		}
		return null;
	}

	public TrackState nextSimState(TrackState state) {
		TrackLayoutType type = getType();
		if(type != null) {
			return getType().walker.nextSimState(state, this);
		}
		return null;
	}
}