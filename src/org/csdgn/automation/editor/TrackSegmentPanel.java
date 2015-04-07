package org.csdgn.automation.editor;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.csdgn.automation.track.TrackLayoutType;
import org.csdgn.automation.track.TrackSegment;

import net.miginfocom.swing.MigLayout;

import java.awt.GridLayout;

public class TrackSegmentPanel extends JPanel {
	private static final long serialVersionUID = -1275983503843726435L;

	private final JComboBox<String> cmbType;
	private boolean ready = false;
	private final JLabel lblLength;
	private final JLabel lblLengthSub;

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

		setLayout(new MigLayout("", "[grow][grow]", "[][][][][][][][]"));

		JButton btnUp = new JButton("Up ▲");
		btnUp.addActionListener(e -> {
			if(!ready)
				return;
			TrackEditor.instance.moveUpSegment(this);
		});
		add(btnUp, "cell 0 0,growx");

		JButton btnDown = new JButton("Down ▼");
		btnDown.addActionListener(e -> {
			if(!ready)
				return;
			TrackEditor.instance.moveDownSegment(this);
		});
		add(btnDown, "cell 1 0,growx");

		JLabel lblType = new JLabel("Type");
		add(lblType, "cell 0 1,alignx trailing");

		cmbType = new JComboBox<String>();
		cmbType.setActionCommand("t");
		cmbType.addActionListener(e -> {
			if(!ready)
				return;
			switch(cmbType.getSelectedIndex()) {
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

		add(cmbType, "cell 1 1,growx");

		lblLength = new JLabel("Length");
		add(lblLength, "cell 0 2,alignx trailing");

		spnLength = new JSpinner();
		spnLength.addChangeListener(e -> {
			if(!ready)
				return;
			seg.layoutInfo = (double) spnLength.getValue();
			update();
		});
		add(spnLength, "flowx,cell 1 2,growx");

		lblLengthSub = new JLabel("m");
		add(lblLengthSub, "cell 1 2");

		JLabel lblCornerRadius = new JLabel("Radius");
		add(lblCornerRadius, "cell 0 3,alignx trailing");

		spnRadius = new JSpinner();
		spnRadius.setToolTipText("<html>This is for defining the corner radii (in meters). The larger<br>"
				+ "this number, the gentler the turn, and the faster the car can<br>travel through it.</html>");
		spnRadius.setModel(new SpinnerNumberModel(1.0, 0.0, 99999.0, 0.1));
		spnRadius.setEnabled(false);
		spnRadius.addChangeListener(e -> {
			if(!ready)
				return;
			seg.cornerRadius = (double) spnRadius.getValue();
			update();
		});
		add(spnRadius, "flowx,cell 1 3,growx");

		JLabel lblM_1 = new JLabel("m");
		add(lblM_1, "cell 1 3");

		lblSlope = new JLabel("Slope");
		add(lblSlope, "cell 0 4,alignx trailing");

		spnSlope = new JSpinner();
		spnSlope.setModel(new SpinnerNumberModel(0.0, -100.0, 100.0, 0.1));
		spnSlope.setToolTipText("<html>This defines the slopes of the segments (in percent). The simulation<br>"
				+ "automatically will calculate the actual, 3D length of the track segments.<br>"
				+ "Don't make slopes too steep or low-powered cars won't be able to make it<br>"
				+ "around the track resulting in errors.</html>");
		spnSlope.addChangeListener(e -> {
			if(!ready)
				return;
			seg.slope = (double) spnSlope.getValue();
			updateHelperImages();
			update();
		});
		add(spnSlope, "flowx,cell 1 4,growx");

		JLabel lblSportiness = new JLabel("Sportiness");
		add(lblSportiness, "cell 0 5,alignx trailing");

		sldSport = new JSlider();
		sldSport.setPaintLabels(true);
		sldSport.setPreferredSize(new Dimension(20, 23));
		sldSport.setPaintTicks(true);
		sldSport.setToolTipText("<html>This is for defining how difficult to drive the segments are,<br>"
				+ "You can imagine that this is the bumpiness of the track. The<br>"
				+ "simulation looks at these values and punishes cars with a high<br>" + "sportiness/tameness ratio.</html>");
		sldSport.setSnapToTicks(true);
		sldSport.setMajorTickSpacing(1);
		sldSport.setMaximum(5);
		sldSport.addChangeListener(e -> {
			if(!ready)
				return;
			seg.sportiness = sldSport.getValue();
			update();
		});
		add(sldSport, "cell 1 5,growx");

		lblCamber = new JLabel("Camber");
		add(lblCamber, "cell 0 6,alignx trailing");

		JLabel label = new JLabel("%");
		add(label, "cell 1 4");

		spnCamber = new JSpinner();
		spnCamber.setToolTipText("<html>This defines the camber or banking of the track (in degrees).<br>"
				+ "Negative numbers make it banked to the left (/) and positive<br>"
				+ "numbers banked to the right (\\) when you are facing in driving<br>"
				+ "direction. Values up to 45 degrees should be fine, use everything<br>" + "above at your own risk.</html>");
		spnCamber.setModel(new SpinnerNumberModel(0.0, -90.0, 90.0, 0.1));
		spnCamber.addChangeListener(e -> {
			if(!ready)
				return;
			seg.camber = (double) spnCamber.getValue();
			updateHelperImages();
			update();
		});
		add(spnCamber, "flowx,cell 1 6,growx");

		JLabel label_1 = new JLabel("°");
		add(label_1, "cell 1 6");

		ready = true;

		updateFromModel();

		setName(getName(index));

		JPanel panel = new JPanel();
		add(panel, "cell 0 7 2 1,growx");
		panel.setLayout(new GridLayout(1, 0, 4, 0));

		JButton btnInsert = new JButton("Add");
		panel.add(btnInsert);
		btnInsert.addActionListener(e -> {
			if(!ready)
				return;
			TrackEditor.instance.addSegment(this);
		});

		JButton btnSplit = new JButton("Split");
		btnSplit.addActionListener(e -> {
			if(!ready)
				return;
			TrackEditor.instance.splitSegment(this);
		});
		panel.add(btnSplit);

		JButton btnDelete = new JButton("Delete");
		panel.add(btnDelete);
		btnDelete.addActionListener(e -> {
			if(!ready)
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

		switch(seg.getType()) {
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
		if(reset) {
			seg.layoutInfo = 50.0;
			seg.cornerRadius = 0.0;
			spnRadius.setValue(0.0);
			spnLength.setValue(50.0);
		} else {
			spnLength.setValue(seg.layoutInfo);
		}
		spnRadius.setEnabled(false);
		lblLength.setText("Length");
		lblLengthSub.setText("m");
	}

	private void setTurn(boolean reset) {
		spnLength.setToolTipText("<html>This is to define the angles of the corners (in degrees). This<br>"
				+ "defines only the 2D-projection of the track, so you don't have to<br>"
				+ "bother with how long a sloped track segment really is when driving<br>"
				+ "over it. Just define the track out of the bird-view.</html>");
		spnLength.setModel(new SpinnerNumberModel(1.0, 0.1, 360.0, 0.1));
		if(reset) {
			seg.layoutInfo = 45.0;
			seg.cornerRadius = 20.0;
			spnLength.setValue(45.0);
			spnRadius.setValue(20.0);
		} else {
			spnLength.setValue(seg.layoutInfo);
		}
		spnRadius.setEnabled(true);
		lblLength.setText("Angle");
		lblLengthSub.setText("°");
	}

	private void updateHelperImages() {
		lblSlope.setIcon(new javax.swing.ImageIcon(EditorTools.slopeImage(seg.slope)));
		lblCamber.setIcon(new javax.swing.ImageIcon(EditorTools.camberImage(seg.camber)));
	}

	private void update() {
		if(!ready) {
			return;
		}
		TrackEditor.instance.updatePageName(this);
		TrackEditor.instance.updateTrack();
	}
}
