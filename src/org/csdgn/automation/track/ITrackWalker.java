package org.csdgn.automation.track;

public interface ITrackWalker {
	public TrackState nextState(TrackState state, TrackSegment segment);

	public TrackState nextSimState(TrackState state, TrackSegment segment);
}
