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

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.csdgn.automation.track.TrackLayoutType;
import org.csdgn.automation.track.TrackSegment;
import org.csdgn.maru.swing.TableLayout;

import java.awt.GridLayout;

public class TrackSegmentPanel extends JPanel {
	private static final long serialVersionUID = -1275983503843726435L;

	private final JComboBox<String> cmbType;
	private boolean ready = false;
	private final JLabel lblLength;
	private final JLabel lblLengthUnits;

	protected TrackSegment seg;
	private final JSlider sldSport;
	private final JSpinner spnCamber;
	private final JSpinner spnLength;
	private final JSpinner spnRadius;
	private final JSpinner spnSlope;

	private JLabel lblCamber;
	private JLabel lblSlope;

	public TrackSegmentPanel(TrackSegment seg, int index) {
		this.seg = seg;

		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		setLayout(new TableLayout(4, 4, true));

		JPanel upperButtonPanel = new JPanel(new GridLayout(1, 2));
		add(upperButtonPanel, "x=0; y=0; colspan=3");

		JButton btnUp = new JButton("Up \u25B2");
		btnUp.addActionListener(e -> {
			if (!ready) {
				return;
			}
			TrackEditor.instance.moveUpSegment(this);
		});
		upperButtonPanel.add(btnUp);

		JButton btnDown = new JButton("Down \u25BC");
		btnDown.addActionListener(e -> {
			if (!ready) {
				return;
			}
			TrackEditor.instance.moveDownSegment(this);
		});
		upperButtonPanel.add(btnDown);

		/* --------------------------------------------------- */

		JLabel lblType = new JLabel("Type", SwingConstants.RIGHT);
		add(lblType, "x=0; y=last+1");

		cmbType = new JComboBox<String>();
		cmbType.setActionCommand("t");
		cmbType.addActionListener(e -> {
			if (!ready)
				return;
			switch (cmbType.getSelectedIndex()) {
			case 0:
				setStraight(seg.layout != TrackLayoutType.STRAIGHT.number);
				seg.layout = TrackLayoutType.STRAIGHT.number;
				update();
				break;
			case 1:
				setTurn(seg.layout == TrackLayoutType.STRAIGHT.number);
				seg.layout = TrackLayoutType.LEFT.number;
				update();
				break;
			case 2:
				setTurn(seg.layout == TrackLayoutType.STRAIGHT.number);
				seg.layout = TrackLayoutType.RIGHT.number;
				update();
				break;
			}
		});
		cmbType.setModel(new DefaultComboBoxModel<String>(new String[] { "Straight", "Left", "Right" }));

		add(cmbType, "x=1; y=last; colspan=2");

		/* --------------------------------------------------- */

		lblLength = new JLabel("Length", SwingConstants.RIGHT);
		add(lblLength, "x=0; y=last+1");

		spnLength = new JSpinner();
		spnLength.addChangeListener(e -> {
			if (!ready)
				return;
			seg.layoutInfo = (double) spnLength.getValue();
			update();
		});

		add(spnLength, "x=1; y=last");

		lblLengthUnits = new JLabel("m");
		lblLengthUnits.setPreferredSize(new Dimension(20, 20));
		add(lblLengthUnits, "x=2; y=last");

		/* --------------------------------------------------- */

		JLabel lblCornerRadius = new JLabel("Radius", SwingConstants.RIGHT);
		add(lblCornerRadius, "x=0; y=last+1");

		spnRadius = new JSpinner();
		spnRadius.setToolTipText("<html>This is for defining the corner radii (in meters). The larger<br>"
				+ "this number, the gentler the turn, and the faster the car can<br>travel through it.</html>");
		spnRadius.setModel(new SpinnerNumberModel(1.0, 0.0, 99999.0, 0.1));
		spnRadius.setEnabled(false);
		spnRadius.addChangeListener(e -> {
			if (!ready)
				return;
			seg.cornerRadius = (double) spnRadius.getValue();
			update();
		});
		add(spnRadius, "x=1; y=last");

		JLabel lblRadiusUnits = new JLabel("m");
		lblRadiusUnits.setPreferredSize(new Dimension(20, 20));
		add(lblRadiusUnits, "x=2; y=last");

		/* --------------------------------------------------- */

		lblSlope = new JLabel("Slope", SwingConstants.RIGHT);
		add(lblSlope, "x=0; y=last+1");

		spnSlope = new JSpinner();
		spnSlope.setModel(new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1));
		spnSlope.setToolTipText("<html>This defines the slopes of the segments (in percent). The simulation<br>"
				+ "automatically will calculate the actual, 3D length of the track segments.<br>"
				+ "Don't make slopes too steep or low-powered cars won't be able to make it<br>"
				+ "around the track resulting in errors.</html>");
		spnSlope.addChangeListener(e -> {
			if (!ready)
				return;
			seg.slope = (double) spnSlope.getValue();
			updateHelperImages();
			update();
		});
		add(spnSlope, "x=1; y=last");

		JLabel lblSlopeUnits = new JLabel("%");
		lblSlopeUnits.setPreferredSize(new Dimension(20, 20));
		add(lblSlopeUnits, "x=2; y=last");

		/* --------------------------------------------------- */

		JLabel lblSportiness = new JLabel("Sportiness", SwingConstants.RIGHT);
		add(lblSportiness, "x=0; y=last+1");

		sldSport = new JSlider();
		sldSport.setPaintLabels(true);
		sldSport.setPreferredSize(new Dimension(20, 45));
		sldSport.setPaintTicks(true);
		sldSport.setToolTipText("<html>This is for defining how difficult to drive the segments are,<br>"
				+ "You can imagine that this is the bumpiness of the track. The<br>"
				+ "simulation looks at these values and punishes cars with a high<br>"
				+ "sportiness/tameness ratio.</html>");
		sldSport.setSnapToTicks(true);
		sldSport.setMajorTickSpacing(1);
		sldSport.setMaximum(5);
		sldSport.addChangeListener(e -> {
			if (!ready)
				return;
			seg.sportiness = sldSport.getValue();
			update();
		});
		add(sldSport, "x=1; y=last; colspan=2");

		/* --------------------------------------------------- */

		lblCamber = new JLabel("Camber", SwingConstants.RIGHT);
		add(lblCamber, "x=0; y=last+1");

		spnCamber = new JSpinner();
		spnCamber.setToolTipText("<html>This defines the camber or banking of the track (in degrees).<br>"
				+ "Negative numbers make it banked to the left and positive<br>"
				+ "numbers banked to the right when you are facing in driving<br>"
				+ "direction. Values up to 45 degrees should be fine, use everything<br>"
				+ "above at your own risk.</html>");
		spnCamber.setModel(new SpinnerNumberModel(0.0, -90.0, 90.0, 0.1));
		spnCamber.addChangeListener(e -> {
			if (!ready)
				return;
			seg.camber = (double) spnCamber.getValue();
			updateHelperImages();
			update();
		});
		add(spnCamber, "x=1; y=last");

		JLabel lblCamberUnits = new JLabel("\u00BA");
		lblCamberUnits.setPreferredSize(new Dimension(20, 20));
		add(lblCamberUnits, "x=2; y=last");

		/* --------------------------------------------------- */

		ready = true;

		updateFromModel();

		setName(getName(index));

		JPanel lowerButtonPanel = new JPanel();
		add(lowerButtonPanel, "x=0; y=last+1; colspan=3");
		lowerButtonPanel.setLayout(new GridLayout(1, 0, 4, 0));

		JButton btnInsert = new JButton("Add");
		lowerButtonPanel.add(btnInsert);
		btnInsert.addActionListener(e -> {
			if (!ready)
				return;
			TrackEditor.instance.addSegment(this);
		});

		JButton btnSplit = new JButton("Split");
		btnSplit.addActionListener(e -> {
			if (!ready)
				return;
			TrackEditor.instance.splitSegment(this);
		});
		lowerButtonPanel.add(btnSplit);

		JButton btnDelete = new JButton("Delete");
		lowerButtonPanel.add(btnDelete);
		btnDelete.addActionListener(e -> {
			if (!ready)
				return;
			TrackEditor.instance.deleteSegment(this);
		});

		updateHelperImages();
	}

	public String getName(int index) {
		return "Segment " + index + ": " + cmbType.getSelectedItem();
	}

	protected void updateFromModel() {
		ready = false;

		spnLength.setValue(seg.layoutInfo);
		spnRadius.setValue(seg.cornerRadius);
		spnSlope.setValue(seg.slope);
		sldSport.setValue(seg.sportiness);
		spnCamber.setValue(seg.camber);

		switch (seg.getType()) {
		case LEFT:
			setTurn(false);
			cmbType.setSelectedIndex(1);
			break;
		case RIGHT:
			setTurn(false);
			cmbType.setSelectedIndex(2);
			break;
		case STRAIGHT:
			setStraight(false);
			cmbType.setSelectedIndex(0);
			break;
		}

		updateHelperImages();

		ready = true;
	}

	private void setStraight(boolean reset) {
		spnLength.setToolTipText("<html>This is to define the length of the straights (in meters). This<br>"
				+ "defines only the 2D-projection of the track, so you don't have to<br>"
				+ "bother with how long a sloped track segment really is when driving<br>"
				+ "over it. Just define the track out of the bird-view.</html>");
		spnLength.setModel(new SpinnerNumberModel(1.0, 0.0, 100000.0, 0.1));
		if (reset) {
			seg.layoutInfo = 50.0;
			seg.cornerRadius = 0.0;
			spnRadius.setValue(0.0);
			spnLength.setValue(50.0);
		} else {
			spnLength.setValue(seg.layoutInfo);
		}
		spnRadius.setEnabled(false);
		lblLength.setText("Length");
		lblLengthUnits.setText("m");
	}

	private void setTurn(boolean reset) {
		spnLength.setToolTipText("<html>This is to define the angles of the corners (in degrees). This<br>"
				+ "defines only the 2D-projection of the track, so you don't have to<br>"
				+ "bother with how long a sloped track segment really is when driving<br>"
				+ "over it. Just define the track out of the bird-view.</html>");
		spnLength.setModel(new SpinnerNumberModel(1.0, 0.1, 360.0, 0.1));
		if (reset) {
			seg.layoutInfo = 45.0;
			seg.cornerRadius = 20.0;
			spnLength.setValue(45.0);
			spnRadius.setValue(20.0);
		} else {
			spnLength.setValue(seg.layoutInfo);
		}
		spnRadius.setEnabled(true);
		lblLength.setText("Angle");
		lblLengthUnits.setText("\u00BA");
	}

	private void updateHelperImages() {
		lblSlope.setIcon(new javax.swing.ImageIcon(EditorTools.slopeImage(seg.slope)));
		lblCamber.setIcon(new javax.swing.ImageIcon(EditorTools.camberImage(seg.camber)));
	}

	private void update() {
		if (!ready) {
			return;
		}
		TrackEditor.instance.updatePageName(this);
		TrackEditor.instance.updateTrack();
	}
}
