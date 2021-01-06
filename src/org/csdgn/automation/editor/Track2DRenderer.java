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
package org.csdgn.automation.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.csdgn.automation.TrackMapper;
import org.csdgn.automation.track.Track;
import org.csdgn.automation.track.TrackState;

public class Track2DRenderer {
	private static void drawCenteredString(Graphics2D g, String string, double x, double y) {
		FontRenderContext frc = g.getFontRenderContext();
		Rectangle2D bounds = g.getFont().createGlyphVector(frc, string).getVisualBounds();

		float xOffset = (float) (bounds.getWidth() / 2.0);
		float yOffset = (float) (bounds.getHeight() / 2.0);

		g.drawString(string, (float) x - xOffset, (float) y + yOffset);
	}

	private Color colorSegment = Color.MAGENTA;

	public Color getSegmentColor() {
		return colorSegment;
	}

	public void setSegmentColor(Color colorSegment) {
		this.colorSegment = colorSegment;
	}

	public Color getTickColor() {
		return colorTick;
	}

	public void setTickColor(Color colorTick) {
		this.colorTick = colorTick;
	}

	public Color getProjectionColor() {
		return colorProjection;
	}

	public void setProjectionColor(Color colorProjection) {
		this.colorProjection = colorProjection;
	}

	public Color getFontColor() {
		return colorFont;
	}

	public void setFontColor(Color colorFont) {
		this.colorFont = colorFont;
	}

	public Color getSplitColor() {
		return colorSplit;
	}

	public void setSplitColor(Color colorSplit) {
		this.colorSplit = colorSplit;
	}

	private Color colorTick = Color.RED;
	private Color colorProjection = Color.BLUE;
	private Color colorFont = Color.GREEN.darker();
	private Color colorSplit = Color.CYAN;
	private boolean drawIndexes = true;
	private boolean drawSplitMarkers = true;
	private boolean drawTicks = true;
	private boolean drawProjection = true;
	private int highlightedIndex = -1;
	private boolean highlightIndexes = true;
	private double indexDistance = 20;
	private double splitMarkerSize = 8;
	private double tickLength = 10;

	private void drawSplitMarker(Graphics2D g, Track track, TrackState state, TrackState next) {
		double split = track.split1 - state.length;
		if(split < 0) {
			split = track.split2 - state.length;
		}
		split /= next.length - state.length;

		g.setColor(colorSplit);
		if(next.shape instanceof Line2D) {
			Line2D line = (Line2D) next.shape;
			Point2D a = line.getP1();
			Point2D b = line.getP2();
			double x = a.getX() * (1.0 - split) + b.getX() * split - splitMarkerSize / 2.0;
			double y = a.getY() * (1.0 - split) + b.getY() * split - splitMarkerSize / 2.0;

			g.fillRect((int) x, (int) y, (int) splitMarkerSize, (int) splitMarkerSize);
		} else if(next.shape instanceof Arc2D) {
			Arc2D arc = (Arc2D) next.shape;
			arc = (Arc2D) arc.clone();
			arc.setAngleExtent(arc.getAngleExtent() * split);
			Point2D p = arc.getEndPoint();
			g.fillRect((int) (p.getX() - splitMarkerSize / 2.0), (int) (p.getY() - splitMarkerSize / 2.0), (int) splitMarkerSize,
					(int) splitMarkerSize);
		}
	}

	public int getHighlightedIndex() {
		return highlightedIndex;
	}

	public double getIndexDistance() {
		return indexDistance;
	}

	public double getSplitMarkerSize() {
		return splitMarkerSize;
	}

	public double getTickLength() {
		return tickLength;
	}

	public boolean isDrawingIndexes() {
		return drawIndexes;
	}

	public boolean isDrawingSplitMarkers() {
		return drawSplitMarkers;
	}

	public boolean isDrawingTicks() {
		return drawTicks;
	}

	public boolean isHighlightingIndexes() {
		return highlightIndexes;
	}

	public BufferedImage render(TrackMapper runner) {
		BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		BasicStroke highlightStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
		g.setStroke(stroke);

		Track track = runner.getTrack();

		int index = 0;
		for(TrackState state : runner.states()) {
			if(drawTicks) { // draw ticks at right angles
				g.setColor(colorTick);
				double cval = Math.cos(state.angle + Math.PI / 2.0);
				double sval = Math.sin(state.angle + Math.PI / 2.0);

				g.drawLine((int) (state.x + tickLength * cval), (int) (state.y + tickLength * sval), (int) (state.x - tickLength * cval),
						(int) (state.y - tickLength * sval));

				if(drawIndexes) {
					g.setColor(colorFont);
					String string = String.format("%d", index);
					drawCenteredString(g, string, state.x + indexDistance * cval, state.y + indexDistance * sval);
					drawCenteredString(g, string, state.x - indexDistance * cval, state.y - indexDistance * sval);
				}
				g.setColor(colorSegment);
			}

			TrackState next = runner.get(index + 1);

			if(highlightIndexes && highlightedIndex == index) {
				g.setStroke(highlightStroke);
			}

			if(next.shape != null) {
				g.draw(next.shape);

				if(drawSplitMarkers
						&& (track.split1 >= state.length && track.split1 < next.length || track.split2 >= state.length
								&& track.split2 < next.length)) {
					drawSplitMarker(g, track, state, next);
				}
			}

			if(highlightIndexes && highlightedIndex == index) {
				g.setStroke(stroke);
			}

			state = next;
			++index;

			if(index == runner.size() - 1) {
				break;
			}
		}

		if(drawProjection) {
			g.setColor(colorProjection);
			BasicStroke projectionStroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] { 4 }, 0);
			g.setStroke(projectionStroke);

			TrackState state = runner.getLast();

			g.draw(new Line2D.Double(state.x, state.y, state.x + 100 * Math.cos(state.angle), state.y + 100 * Math.sin(state.angle)));

			// state.angle
		}

		return image;
	}

	public void setDrawIndexes(boolean draw) {
		drawIndexes = draw;
	}

	public void setDrawSplitMarkers(boolean drawSplitMarkers) {
		this.drawSplitMarkers = drawSplitMarkers;
	}

	public void setDrawTicks(boolean draw) {
		drawTicks = draw;
	}

	public void setHighlightedIndex(int highlightedIndex) {
		this.highlightedIndex = highlightedIndex;
	}

	public void setHighlightIndexes(boolean highlightIndexes) {
		this.highlightIndexes = highlightIndexes;
	}

	public void setIndexDistance(double distance) {
		indexDistance = distance;
	}

	public void setSplitMarkerSize(double splitMarkerSize) {
		this.splitMarkerSize = splitMarkerSize;
	}

	public void setTickLength(double tickLength) {
		this.tickLength = tickLength;
	}
}
