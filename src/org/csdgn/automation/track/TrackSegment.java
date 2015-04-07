package org.csdgn.automation.track;

public class TrackSegment {

	public int layout;
	public double layoutInfo;
	public double cornerRadius;
	public double slope;
	public int sportiness;
	public double camber;

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