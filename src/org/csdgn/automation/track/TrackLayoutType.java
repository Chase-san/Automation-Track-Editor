package org.csdgn.automation.track;

import org.csdgn.automation.track.walker.*;

public enum TrackLayoutType {
	STRAIGHT(0, new StraightWalker()),
	LEFT(1, new LeftWalker()),
	RIGHT(-1, new RightWalker());

	public final int number;
	public final ITrackWalker walker;

	TrackLayoutType(int number, ITrackWalker walker) {
		this.number = number;
		this.walker = walker;
	}
}
