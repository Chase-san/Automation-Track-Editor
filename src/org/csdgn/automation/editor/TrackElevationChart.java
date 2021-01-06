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
import java.util.ArrayList;

import org.csdgn.automation.TrackMapper;
import org.csdgn.automation.track.TrackState;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import com.xeiam.xchart.StyleManager.ChartTheme;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.XChartPanel;

public class TrackElevationChart {
	public static class ElevationData {
		private ArrayList<Double> xData;
		private ArrayList<Double> yData;

		private ElevationData(int size) {
			xData = new ArrayList<Double>();
			yData = new ArrayList<Double>();
		}
	}

	public static ElevationData createElevationData(TrackMapper runner) {
		ElevationData data = new ElevationData(runner.size());
		for(TrackState state : runner.states()) {
			data.xData.add(state.length);
			data.yData.add(state.elevation);
		}

		return data;
	}

	public static XChartPanel createChart(ElevationData data) {
		Chart chart = new ChartBuilder().chartType(ChartType.Area).theme(ChartTheme.Matlab).title("Track Elevation")
				.xAxisTitle("Distance (m)").yAxisTitle("Elevation (m)").build();

		BasicStroke stroke = new BasicStroke(1);

		StyleManager style = chart.getStyleManager();
		style.setMarkerSize(0);
		style.setPlotBorderVisible(false);
		style.setPlotGridLinesColor(Color.LIGHT_GRAY);
		style.setPlotPadding(0);
		style.setAxisTickPadding(0);
		style.setChartTitleVisible(false);
		style.setLegendVisible(false);
		style.setXAxisTitleVisible(false);
		style.setYAxisTitleVisible(false);

		style.setPlotGridLinesStroke(stroke);
		style.setAxisTickMarksStroke(stroke);

		Series series = chart.addSeries("Elevation", data.xData, data.yData);
		series.setLineStyle(stroke);

		ElevationData ed = new ElevationData(2);
		ed.xData.add(data.xData.get(0));
		ed.xData.add(data.xData.get(1));
		ed.yData.add(data.yData.get(0));
		ed.yData.add(data.yData.get(1));

		series = chart.addSeries("Highlight", ed.xData, ed.yData);

		XChartPanel panel = new XChartPanel(chart);
		panel.getInputMap().clear(); // Disable the save bullshit
		panel.getActionMap().clear();

		return panel;
	}

	public static void updateChart(XChartPanel chart, ElevationData data, int index) {
		chart.updateSeries("Elevation", data.xData, data.yData);
		if(index < 0) {
			index = 0;
		}
		ElevationData ed = new ElevationData(2);
		ed.xData.add(data.xData.get(index));
		ed.xData.add(data.xData.get(index + 1));
		ed.yData.add(data.yData.get(index));
		ed.yData.add(data.yData.get(index + 1));
		chart.updateSeries("Highlight", ed.xData, ed.yData);

	}
}
