package org.csdgn.automation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import org.csdgn.automation.track.Track;
import org.csdgn.automation.track.TrackSegment;
import org.csdgn.maru.Files;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class TrackFactory {
	public static Track load(File folder) throws Exception {
		Track track = new Track();

		try {
			track.image = ImageIO.read(new File(folder, "track.png"));
		} catch(IOException e) {
			throw new Exception("Failed to load image from file.");
		}

		if(track.image.getWidth() != 1280 || track.image.getHeight() != 720) {
			throw new Exception("Image must be 1280 x 720.");
		}

		String code = Files.getFileContents(new File(folder, "track.lua"), StandardCharsets.UTF_8);
		if(code == null) {
			throw new Exception("Failed to load lua from file.");
		}

		Globals globals = JsePlatform.standardGlobals();
		LuaValue chunk = globals.load(code);
		chunk.call();

		LuaTable table = (LuaTable) globals.get("Track");

		track.name = table.get("Name").toString();
		LuaTable table2 = (LuaTable) table.get("Start");
		track.startX = table2.get(1).toint();
		track.startY = table2.get(2).toint();
		track.startAngle = -table.get("Start_Angle").todouble();
		track.scale = table.get("Scale").todouble();
		track.split1 = table.get("Split1").todouble();
		track.split2 = table.get("Split2").todouble();

		int[] layout;
		double[] layoutInfo;
		double[] cornerRadius;
		double[] slope;
		double[] sportiness;
		double[] camber;

		{ // layout
			table2 = (LuaTable) table.get("Layout");
			layout = new int[table2.length()];
			for(int i = 0; i < layout.length; ++i) {
				layout[i] = table2.get(i + 1).toint();
			}
		}

		{ // Layout Info
			table2 = (LuaTable) table.get("LayoutInfo");
			layoutInfo = new double[layout.length];
			for(int i = 0; i < layout.length; ++i) {
				layoutInfo[i] = table2.get(i + 1).todouble();
			}
		}

		{ // Corner Radius
			table2 = (LuaTable) table.get("CornerRadius");
			cornerRadius = new double[layout.length];
			for(int i = 0; i < layout.length; ++i) {
				cornerRadius[i] = table2.get(i + 1).todouble();
			}
		}

		{ // Slope
			table2 = (LuaTable) table.get("Slope");
			slope = new double[layout.length];
			for(int i = 0; i < layout.length; ++i) {
				slope[i] = table2.get(i + 1).todouble();
			}
		}

		{ // Sportiness
			table2 = (LuaTable) table.get("Sportiness");
			sportiness = new double[layout.length];
			for(int i = 0; i < layout.length; ++i) {
				sportiness[i] = table2.get(i + 1).todouble();
			}
		}

		{ // Camber
			table2 = (LuaTable) table.get("Camber");
			camber = new double[layout.length];
			for(int i = 0; i < layout.length; ++i) {
				camber[i] = table2.get(i + 1).todouble();
			}
		}

		track.segments.clear();
		for(int i = 0; i < layout.length; ++i) {
			TrackSegment segment = new TrackSegment();
			segment.layout = layout[i];
			segment.layoutInfo = layoutInfo[i];
			segment.cornerRadius = cornerRadius[i];
			segment.slope = slope[i];
			segment.sportiness = (int) sportiness[i];
			segment.camber = camber[i];

			track.segments.add(segment);
		}

		return track;
	}

	/**
	 * float to string with minimum precision, and error correction
	 */
	private static String ftswmp(double value) {
		if(value == (long) value) {
			return String.format("%d", (long) value);
		}

		// acceptable margin of error
		double delta = 0.00001;
		if(Math.abs(value - Math.rint(value)) < delta) {
			return String.format("%d", (long) Math.rint(value));
		}

		for(int i = 1; i <= 4; ++i) {
			long precision = (long) Math.pow(10, i);
			if(Math.abs(value * precision - Math.rint(value * precision)) < delta) {
				return BigDecimal.valueOf(Math.rint(value * precision)).stripTrailingZeros().divide(BigDecimal.valueOf(precision))
						.toPlainString();
			}
		}

		return String.format("%s", value);
	}

	public static String generateLua(Track track) {
		StringBuilder buf = new StringBuilder();
		buf.append("--Generated by TrackEdit, the Automation Track Editor\n");
		buf.append("--Track: " + track.name + "\n");
		buf.append("Track = {\n");
		buf.append("\tName = \"" + track.name + "\",\n");
		buf.append("\tStart = { " + track.startX + ", " + track.startY + " },\n");
		buf.append("\tStart_Angle = " + ftswmp(-track.startAngle) + "\n");
		buf.append("\tScale = " + ftswmp(track.scale) + ",\n");

		buf.append("\tLayout = {");
		boolean first = true;
		for(TrackSegment seg : track.segments) {
			if(!first) {
				buf.append(", ");
			}
			first = false;
			buf.append(seg.layout);
		}

		buf.append("},\n\tLayoutInfo = {");
		first = true;
		for(TrackSegment seg : track.segments) {
			if(!first) {
				buf.append(", ");
			}
			first = false;
			buf.append(ftswmp(seg.layoutInfo));
		}

		buf.append("},\n\tCornerRadius = {");
		first = true;
		for(TrackSegment seg : track.segments) {
			if(!first) {
				buf.append(", ");
			}
			first = false;
			buf.append(ftswmp(seg.cornerRadius));
		}

		buf.append("},\n\tSlope = {");
		first = true;
		for(TrackSegment seg : track.segments) {
			if(!first) {
				buf.append(", ");
			}
			first = false;
			buf.append(ftswmp(seg.slope));
		}

		buf.append("},\n\tSportiness = {");
		first = true;
		for(TrackSegment seg : track.segments) {
			if(!first) {
				buf.append(", ");
			}
			first = false;
			buf.append(ftswmp(seg.sportiness));
		}

		buf.append("},\n\tCamber = {");
		first = true;
		for(TrackSegment seg : track.segments) {
			if(!first) {
				buf.append(", ");
			}
			first = false;
			buf.append(ftswmp(seg.camber));
		}

		buf.append("},\n\tSplit1 = " + ftswmp(track.split1) + ",\n");
		buf.append("\tSplit2 = " + ftswmp(track.split2) + "\n");
		buf.append("}\n");

		return buf.toString();
	}
}
