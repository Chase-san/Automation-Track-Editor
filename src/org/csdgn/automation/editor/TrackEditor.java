package org.csdgn.automation.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.OverlayLayout;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.csdgn.automation.Track2DRenderer;
import org.csdgn.automation.TrackElevationChart;
import org.csdgn.automation.TrackFactory;
import org.csdgn.automation.TrackRunner;
import org.csdgn.automation.TrackElevationChart.ElevationData;
import org.csdgn.automation.track.*;
import org.csdgn.maru.AppToolkit;
import org.csdgn.maru.Files;
import org.csdgn.maru.swing.Accordion;
import org.csdgn.maru.swing.ImagePanel;
import org.csdgn.maru.swing.MouseEventAdapter;
import org.csdgn.maru.swing.TableLayout;

import com.xeiam.xchart.XChartPanel;

import javax.swing.ScrollPaneConstants;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;

public class TrackEditor extends JFrame {
	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				TrackEditor trackview = new TrackEditor();
				trackview.setIconImages(AppToolkit.getAppIconImages());
				trackview.setLocationRelativeTo(null);
				trackview.setVisible(true);
			}
		});
	}

	public static TrackEditor instance;

	private static final long serialVersionUID = 1183587866699616635L;

	public static final String VERSION = "TrackEdit v0.15";
	public static final String SETTINGS = "trackedit.cfg";

	private JPanel chartPanel;
	private JCheckBox chckbxRescale;
	private JFileChooser chooser;

	private ImagePanel image;
	private JLabel lblSegmentCount;
	private JLabel lblTrackLength;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private ImagePanel overlay;
	private Accordion pages;
	private JScrollPane pageScrollPane;
	private File path;
	private boolean ready;
	private boolean saved;

	private JSpinner spnScale;
	private JSpinner spnSplit1;

	private JSpinner spnSplit2;
	private JSpinner spnStartX;
	private JSpinner spnStartY;
	private JSpinner spnStartAngle;

	private Track track;
	private Track2DRenderer trackRenderer;
	private TrackRunner trackRunner;
	private JTextField txtName;
	private JLabel lblEleEnds;
	private JLabel lblEleDiff;
	private final ButtonGroup layoutButtonGroup = new ButtonGroup();
	private JRadioButtonMenuItem rbmiPrecise;
	private JRadioButtonMenuItem rbmiSimulate;

	private JMenuItem mntmAddLap;
	private JMenuItem mntmRoundDistances;
	private JMenuItem mntmFlipCambers;

	public TrackEditor() {
		ready = false;

		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File base = new File(chooser.getCurrentDirectory(), "My Games/Automation/Tracks");
		if (base.exists()) {
			chooser.setCurrentDirectory(base);
		}

		trackRenderer = new Track2DRenderer();

		setTitle(VERSION);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JSplitPane imageSplit = new JSplitPane();
		imageSplit.setResizeWeight(1.0);
		imageSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JSplitPane mainSplit = new JSplitPane();
		mainSplit.setContinuousLayout(true);
		mainSplit.setResizeWeight(1.0);
		getContentPane().add(mainSplit, BorderLayout.CENTER);

		JPanel images = new JPanel();
		JScrollPane imagePane = new JScrollPane(images);
		imagePane.setPreferredSize(new Dimension(1280, 720));
		imageSplit.setLeftComponent(imagePane);
		mainSplit.setLeftComponent(imageSplit);

		OverlayLayout overlayLayout = new OverlayLayout(images);
		images.setLayout(overlayLayout);

		images.add(overlay = new ImagePanel());
		images.add(image = new ImagePanel());

		overlay.addMouseListener(new MouseEventAdapter.Clicked(e -> {
			tryMouseSelect(e.getX(), e.getY());
		}));

		chartPanel = new JPanel();
		imageSplit.setRightComponent(chartPanel);
		chartPanel.setLayout(new BorderLayout(0, 0));
		imageSplit.setDividerLocation(600);

		mainSplit.setDividerLocation(1220);

		JPanel sidebar = new JPanel();
		mainSplit.setRightComponent(sidebar);
		sidebar.setLayout(new BorderLayout(0, 0));

		pageScrollPane = new JScrollPane();
		pageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		sidebar.add(pageScrollPane);

		pages = new Accordion();
		pageScrollPane.setViewportView(pages);
		pages.addChangeListener(e -> {
			if (!ready) {
				return;
			}
			trackRenderer.setHighlightedIndex(pages.getSelectedIndex());
			redrawTrack();
			updateElevationChart();
		});

		sidebar.add(createGlobalControlPanel(), BorderLayout.NORTH);

		overlay.setPreferredSize(new Dimension(1280, 720));
		image.setPreferredSize(new Dimension(1280, 720));

		setJMenuBar(createMenu());

		pack();

		loadSettings();

		ready = true;
		instance = this;
	}

	protected void addSegment(TrackSegmentPanel panel) {
		// after this segment add a new one
		int n = pages.indexOfPage(panel);

		TrackSegment seg = new TrackSegment();
		pages.insertPage(new TrackSegmentPanel(seg, n + 1), n + 1);

		track.segments.add(n + 1, seg);

		trackRunner.markDirty();

		pages.showPage(n + 1);

		lblSegmentCount.setText("" + track.segments.size());

		updatePageNames();
		updateTrack();
	}

	private JComponent createGlobalControlPanel() {
		JPanel panel = new JPanel();

		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel.setLayout(new TableLayout(4, 4, true));

		JLabel lblName = new JLabel("Name", SwingConstants.TRAILING);
		panel.add(lblName, "x=0; y=0");

		txtName = new JTextField();
		txtName.setEnabled(false);
		panel.add(txtName, "x=1; y=last; colspan=2");
		txtName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				track.name = txtName.getText();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				track.name = txtName.getText();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				track.name = txtName.getText();
			}
		});

		JLabel lblScale = new JLabel("Scale", SwingConstants.TRAILING);
		panel.add(lblScale, "x=0; y=last+1");

		spnScale = new JSpinner();
		spnScale.setEnabled(false);
		spnScale.setModel(new SpinnerNumberModel(1.0, 0.1, 200.0, 0.1));
		spnScale.addChangeListener(e -> {
			if (chckbxRescale.isSelected()) {
				track.rescale((double) spnScale.getValue());
				spnSplit1.setValue(track.split1);
				spnSplit2.setValue(track.split2);
				updateSegmentPanelsFromModel();
			} else {
				track.scale = (double) spnScale.getValue();
			}
			updateTrack();
		});
		panel.add(spnScale, "x=1; y=last");

		chckbxRescale = new JCheckBox("Rescale");
		chckbxRescale.setToolTipText("This will rescale the track when scaling (it won't change size).");
		chckbxRescale.setEnabled(false);
		panel.add(chckbxRescale, "x=2; y=last");

		JLabel lblStart = new JLabel("Start", SwingConstants.TRAILING);
		panel.add(lblStart, "x=0; y=last+1");

		spnStartX = new JSpinner();
		spnStartX.setEnabled(false);
		spnStartX.setPreferredSize(new Dimension(60, spnStartX.getPreferredSize().height));
		spnStartX.setModel(new SpinnerNumberModel(0, 0, 1279, 1));
		spnStartX.addChangeListener(e -> {
			track.startX = (int) spnStartX.getValue();
			updateTrack();
		});
		panel.add(spnStartX, "x=1; y=last");

		spnStartY = new JSpinner();
		spnStartY.setEnabled(false);
		spnStartY.setPreferredSize(new Dimension(60, spnStartY.getPreferredSize().height));
		spnStartY.setModel(new SpinnerNumberModel(0, 0, 719, 1));
		spnStartY.addChangeListener(e -> {
			track.startY = (int) spnStartY.getValue();
			updateTrack();
		});
		panel.add(spnStartY, "x=2; y=last");

		JLabel lblStartAngle = new JLabel("Angle", SwingConstants.TRAILING);
		panel.add(lblStartAngle, "x=0; y=last+1");

		spnStartAngle = new JSpinner();
		spnStartAngle.setEnabled(false);
		spnStartAngle.setModel(new SpinnerNumberModel(0.0, -180, 180, 0.1));
		spnStartAngle.addChangeListener(e -> {
			track.startAngle = ((double) spnStartAngle.getValue()) * (Math.PI / 180.0);
			updateTrack();
		});
		panel.add(spnStartAngle, "x=1; y=last; colspan=2");

		JLabel lblSplit1 = new JLabel("Split1", SwingConstants.TRAILING);
		panel.add(lblSplit1, "x=0; y=last+1");

		spnSplit1 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 0.1));
		spnSplit1.setEnabled(false);
		spnSplit1.addChangeListener(e -> {
			track.split1 = (double) spnSplit1.getValue();
			updateTrack();
		});
		panel.add(spnSplit1, "x=1; y=last; colspan=2");

		JLabel lblSplit2 = new JLabel("Split2", SwingConstants.TRAILING);
		panel.add(lblSplit2, "x=0; y=last+1");

		spnSplit2 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 0.1));
		spnSplit2.setEnabled(false);
		spnSplit2.addChangeListener(e -> {
			track.split2 = (double) spnSplit2.getValue();
			updateTrack();
		});
		panel.add(spnSplit2, "x=1; y=last; colspan=2");

		JLabel lblLength = new JLabel("Length", SwingConstants.TRAILING);
		panel.add(lblLength, "x=0; y=last+1");

		lblTrackLength = new JLabel("0 m", SwingConstants.TRAILING);
		panel.add(lblTrackLength, "x=1; y=last; colspan=2");

		JLabel lblElevation = new JLabel("Elevation", SwingConstants.TRAILING);
		panel.add(lblElevation, "x=0; y=last+1");

		panel.add(new JLabel("Start/Finish Diff", SwingConstants.LEFT), "x=1; y=last");

		lblEleEnds = new JLabel("0 m", SwingConstants.TRAILING);
		lblEleEnds.setToolTipText("This is the difference between the elevation at the start and end of the track.");
		panel.add(lblEleEnds, "x=2; y=last");
		panel.add(new JLabel("Min/Max Diff", SwingConstants.LEFT), "x=1; y=last+1");

		lblEleDiff = new JLabel("0 m", SwingConstants.TRAILING);
		lblEleDiff.setToolTipText("This is the difference between the maximum and minimum elevation.");
		panel.add(lblEleDiff, "x=2; y=last");

		JLabel lblTrackSegments = new JLabel("Track Segments");
		panel.add(lblTrackSegments, "x=0; y=last+1; colspan=2");

		lblSegmentCount = new JLabel("0", SwingConstants.TRAILING);
		panel.add(lblSegmentCount, "x=2; y=last");

		return panel;
	}

	private String colorToHexString(Color color) {
		String code = Integer.toHexString(color.getRGB() & 0xffffff);
		while (code.length() < 6) {
			code = "0" + code;
		}
		return code;
	}

	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		mntmNew.setMnemonic(KeyEvent.VK_N);
		mntmNew.addActionListener(e -> {
			setTrack(Track.createNewTrack());

			saved = false;
		});
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(e -> {
			open();
		});
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mntmOpen.setMnemonic(KeyEvent.VK_O);
		mnFile.add(mntmOpen);
		mnFile.addSeparator();

		mntmSave = new JMenuItem("Save");
		mntmSave.setEnabled(false);
		mntmSave.addActionListener(e -> {
			if (path == null) {
				saveAs();
				return;
			}
			save();
		});
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mntmSave.setMnemonic(KeyEvent.VK_S);
		mnFile.add(mntmSave);

		mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.setEnabled(false);
		mntmSaveAs.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		mntmSaveAs.setMnemonic(KeyEvent.VK_A);
		mntmSaveAs.addActionListener(e -> {
			saveAs();
		});
		mnFile.add(mntmSaveAs);
		mnFile.addSeparator();

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(e -> {
			instance.dispose();
		});
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		mnEdit.setMnemonic(KeyEvent.VK_E);
		menuBar.add(mnEdit);

		mntmAddLap = new JMenuItem("Double Track");
		mntmAddLap.setEnabled(false);
		mntmAddLap.setToolTipText("This function will double the size of the track.");
		mntmAddLap.setMnemonic(KeyEvent.VK_D);
		mntmAddLap.addActionListener(e -> {
			int size = track.segments.size();
			for (int i = 0; i < size; ++i) {
				track.segments.add(track.segments.get(i));
			}
			setValuesFromTrack();
			updateTrack();
		});
		mnEdit.add(mntmAddLap);

		mntmRoundDistances = new JMenuItem("Round Distances");
		mntmRoundDistances.setEnabled(false);
		mntmRoundDistances.addActionListener(e -> {
			for (TrackSegment segment : track.segments) {
				if (segment.getType() == TrackLayoutType.STRAIGHT) {
					segment.layoutInfo = Math.round(segment.layoutInfo * 10.0) / 10.0;
				} else {
					segment.cornerRadius = Math.round(segment.cornerRadius * 10.0) / 10.0;
				}
			}
			updateSegmentPanelsFromModel();
			updateTrack();
		});

		mntmRoundDistances.setToolTipText(
				"This rounds distances to thier nearest tenth, don't use it unless you know what that means.");
		mntmRoundDistances.setMnemonic(KeyEvent.VK_R);
		mnEdit.add(mntmRoundDistances);

		mntmFlipCambers = new JMenuItem("Flip Cambers");
		mntmFlipCambers.setEnabled(false);
		mntmFlipCambers.setToolTipText("This function will flip the sign of all cambers on the track.");
		mntmFlipCambers.setMnemonic(KeyEvent.VK_F);
		mntmFlipCambers.addActionListener(e -> {
			for (TrackSegment segment : track.segments) {
				segment.camber = -segment.camber;
			}
			updateSegmentPanelsFromModel();
			updateTrack();
		});
		mnEdit.add(mntmFlipCambers);

		JMenu mnSettings = new JMenu("Layout");
		mnSettings.setMnemonic(KeyEvent.VK_L);
		menuBar.add(mnSettings);

		rbmiPrecise = new JRadioButtonMenuItem("Precise");
		layoutButtonGroup.add(rbmiPrecise);
		rbmiPrecise.setEnabled(false);
		rbmiPrecise.setSelected(true);
		rbmiPrecise.addActionListener(e -> {
			if (!ready) {
				return;
			}
			trackRunner.setSimulateLayout(false);
			updateTrack();
		});
		rbmiPrecise.setMnemonic(KeyEvent.VK_P);
		mnSettings.add(rbmiPrecise);

		rbmiSimulate = new JRadioButtonMenuItem("Simulator");
		layoutButtonGroup.add(rbmiSimulate);
		rbmiSimulate.setEnabled(false);
		rbmiSimulate.addActionListener(e -> {
			if (!ready) {
				return;
			}
			trackRunner.setSimulateLayout(true);
			updateTrack();
		});
		rbmiSimulate.setMnemonic(KeyEvent.VK_S);

		mnSettings.add(rbmiSimulate);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic(KeyEvent.VK_H);
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(e -> {
			new AboutDialog(this).setVisible(true);
		});
		mntmAbout.setMnemonic(KeyEvent.VK_A);
		mnHelp.add(mntmAbout);

		return menuBar;
	}

	protected void deleteSegment(TrackSegmentPanel panel) {
		if (track.segments.size() == 1) {
			return;
		}

		int n = pages.indexOfPage(panel);

		track.segments.remove(n);
		pages.remove(n);

		lblSegmentCount.setText("" + track.segments.size());

		updatePageNames();
		updateTrack();
	}

	@Override
	public void dispose() {
		saveSettings();
		super.dispose();
	}

	private void enableFeatures() {
		txtName.setEnabled(true);
		spnScale.setEnabled(true);
		chckbxRescale.setEnabled(true);
		spnStartX.setEnabled(true);
		spnStartY.setEnabled(true);
		spnStartAngle.setEnabled(true);
		spnSplit1.setEnabled(true);
		spnSplit2.setEnabled(true);
		mntmSave.setEnabled(true);
		mntmSaveAs.setEnabled(true);

		mntmAddLap.setEnabled(true);
		mntmRoundDistances.setEnabled(true);
		mntmFlipCambers.setEnabled(true);

		rbmiPrecise.setEnabled(true);
		rbmiSimulate.setEnabled(true);
	}

	private void loadSettings() {
		try {
			Properties properties = new Properties();
			properties.load(AppToolkit.getLocalResource(SETTINGS));

			String color = properties.getProperty("color_segment");
			if (color != null) {
				trackRenderer.setSegmentColor(Color.decode("#" + color));
			}
			color = properties.getProperty("color_tick");
			if (color != null) {
				trackRenderer.setTickColor(Color.decode("#" + color));
			}
			color = properties.getProperty("color_projection");
			if (color != null) {
				trackRenderer.setProjectionColor(Color.decode("#" + color));
			}
			color = properties.getProperty("color_font");
			if (color != null) {
				trackRenderer.setFontColor(Color.decode("#" + color));
			}
			color = properties.getProperty("color_split");
			if (color != null) {
				trackRenderer.setSplitColor(Color.decode("#" + color));
			}
		} catch (Exception e) {
			System.err.println("Failed to load settings.");
		}

	}

	private void loadTrack(File file) {
		if (!ready) {
			return;
		}

		path = file;
		if (!new File(file, "track.png").exists() || !new File(file, "track.lua").exists()) {
			AppToolkit.showError(instance,
					"Could not find track.png or track.lua, make sure you selected a valid track folder.");
			return;
		}

		ready = false;

		try {
			track = TrackFactory.load(file);
		} catch (Exception e) {
			AppToolkit.showError(instance, e.getMessage());
			ready = true;
			return;
		}
		setTrack(track);

		ready = true;
		updateTrack();
		saved = true;
		updateTitle();
	}

	protected void moveDownSegment(TrackSegmentPanel panel) {
		int n = pages.indexOfPage(panel);
		if (n != pages.getPageCount()) {
			pages.remove(n);
			pages.insertPage(panel, n + 1);

			TrackSegment seg = track.segments.remove(n);
			track.segments.add(n + 1, seg);

			pages.showPage(n + 1);

		}
		updatePageNames();
		updateTrack();
	}

	protected void moveUpSegment(TrackSegmentPanel panel) {
		int n = pages.indexOfPage(panel);
		if (n != 0) {
			pages.remove(n);
			pages.insertPage(panel, n - 1);

			TrackSegment seg = track.segments.remove(n);
			track.segments.add(n - 1, seg);

			pages.showPage(n - 1);
		}
		updatePageNames();
		updateTrack();
	}

	private void open() {
		AppToolkit.setFileChooserReadOnly(true);
		if (chooser.showOpenDialog(instance) == JFileChooser.APPROVE_OPTION) {
			loadTrack(chooser.getSelectedFile());
		}
	}

	protected void redrawTrack() {
		overlay.setImage(trackRenderer.render(trackRunner));
		overlay.repaint();
	}

	private void save() {
		if (path == null) {
			saveAs();
		}

		Files.setFileContents(new File(path, "track.lua"), TrackFactory.generateLua(track), StandardCharsets.UTF_8);
		if (track.image == null) {
			try {
				ImageIO.write(trackRenderer.render(trackRunner), "png", new File(path, "track.png"));
			} catch (IOException e) {
			}
		}

		saved = true;
		updateTitle();
	}

	private void saveAs() {
		AppToolkit.setFileChooserReadOnly(false);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			// actually save stuff!
			path = chooser.getSelectedFile();
			if (path == null) {
				return;
			}
			while (!path.isDirectory()) {
				path = path.getParentFile();
			}
			save();
		}
	}

	private void saveSettings() {
		if (AppToolkit.getLocalResource(SETTINGS) == null) {
			Properties properties = new Properties();

			properties.setProperty("color_segment", colorToHexString(trackRenderer.getSegmentColor()));
			properties.setProperty("color_tick", colorToHexString(trackRenderer.getTickColor()));
			properties.setProperty("color_projection", colorToHexString(trackRenderer.getProjectionColor()));
			properties.setProperty("color_font", colorToHexString(trackRenderer.getFontColor()));
			properties.setProperty("color_split", colorToHexString(trackRenderer.getSplitColor()));

			try {
				properties.store(new FileWriter(new File(AppToolkit.getLocalDirectory(), SETTINGS)),
						VERSION + " configuration file");
			} catch (IOException e) {
				// ignore failure here as well
			}
		}
	}

	private void setTrack(Track track) {
		this.track = track;

		rbmiPrecise.setSelected(true);

		trackRunner = new TrackRunner(track);
		ElevationData ted = TrackElevationChart.createElevationData(trackRunner);
		chartPanel.removeAll();
		chartPanel.add(TrackElevationChart.createChart(ted));
		setValuesFromTrack();

		enableFeatures();
	}

	private void setValuesFromTrack() {
		// fill in values
		pages.removeAll();
		if (track.image != null) {
			image.setImage(track.image);
		} else {
			image.setBackground(Color.WHITE);
		}
		updateTrack();

		ready = false;
		txtName.setText(track.name);
		spnScale.setValue(track.scale);
		spnStartX.setValue(track.startX);
		spnStartY.setValue(track.startY);
		double angle = track.startAngle * (180.0 / Math.PI);
		while (angle < -180) {
			angle += 360;
		}
		while (angle > 180) {
			angle -= 360;
		}
		spnStartAngle.setValue(angle);
		spnSplit1.setValue(track.split1);
		spnSplit2.setValue(track.split2);

		lblSegmentCount.setText("" + track.segments.size());

		for (int i = 0; i < track.segments.size(); ++i) {
			pages.add(new TrackSegmentPanel(track.segments.get(i), i));
		}
		ready = true;
	}

	protected void splitSegment(TrackSegmentPanel panel) {
		// half of layoutInfo to each, otherwise identical

		// after this segment add a new one
		int n = pages.indexOfPage(panel);

		panel.seg.layoutInfo /= 2;
		panel.updateFromModel();

		TrackSegment seg = new TrackSegment(panel.seg);
		pages.insertPage(new TrackSegmentPanel(seg, n + 1), n + 1);

		track.segments.add(n + 1, seg);

		pages.showPage(n + 1);

		lblSegmentCount.setText("" + track.segments.size());

		updatePageNames();
		updateTrack();

	}

	private void tryMouseSelect(int x, int y) {
		if (track == null) {
			return;
		}
		int index = trackRunner.getIndexOfSegmentNear(x, y);
		if (index == -1) {
			return;
		}

		pages.showPage(index);
		pageScrollPane.getVerticalScrollBar().setValue(0);
		pageScrollPane.validate();

		Rectangle rect = pages.getPageButton(index).getBounds();
		pageScrollPane.getVerticalScrollBar().setValue(rect.y);

		trackRenderer.setHighlightedIndex(index);
		redrawTrack();
		updateElevationChart();
	}

	protected void updateElevationChart() {
		TrackElevationChart.updateChart((XChartPanel) chartPanel.getComponent(0),
				TrackElevationChart.createElevationData(trackRunner), pages.getSelectedIndex());
		lblEleEnds.setText(
				new DecimalFormat().format(Math.abs(trackRunner.get(0).elevation - trackRunner.getLast().elevation))
						+ " m");
		lblEleDiff.setText(new DecimalFormat().format(trackRunner.getElevationDifference()) + " m");
	}

	protected void updatePageName(TrackSegmentPanel panel) {
		if (!ready) {
			return;
		}
		int n = pages.indexOfPage(panel);
		pages.setPageTitle(n, ((TrackSegmentPanel) pages.getPage(n)).getName(n));
	}

	private void updatePageNames() {
		if (!ready) {
			return;
		}
		for (int i = 0; i < pages.getPageCount(); ++i) {
			pages.setPageTitle(i, ((TrackSegmentPanel) pages.getPage(i)).getName(i));
		}
	}

	protected void updateSegmentPanelsFromModel() {
		for (int i = 0; i < pages.getPageCount(); ++i) {
			((TrackSegmentPanel) pages.getPage(i)).updateFromModel();
		}
	}

	protected void updateTitle() {
		StringBuilder sb = new StringBuilder();
		sb.append(VERSION);
		if (track != null) {
			sb.append(" - ");
			sb.append(track.name);
			if (!saved) {
				sb.append("*");
			}
		}
		setTitle(sb.toString());
	}

	protected void updateTrack() {
		if (!ready) {
			return;
		}
		trackRunner.markDirty();
		updateElevationChart();
		redrawTrack();
		lblTrackLength.setText(new DecimalFormat().format(trackRunner.getLast().length) + " m");
		saved = false;
		updateTitle();
	}
}
