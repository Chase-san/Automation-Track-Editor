package org.csdgn.automation.editor;

import javax.swing.JDialog;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.Font;

import javax.swing.event.HyperlinkEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextPane;

import org.csdgn.maru.AppToolkit;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = -2529045191082929156L;

	/**
	 * Create the dialog.
	 */
	public AboutDialog(Window owner) {
		super(owner);
		setModal(true);
		setResizable(false);
		setTitle("About " + TrackEditor.VERSION);
		setBounds(100, 100, 400, 270);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(4, 4, 4, 4));
		setContentPane(content);
		content.setLayout(new BorderLayout(0, 4));

		JPanel panel = new JPanel();
		content.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(4, 4));

		JLabel lblBate = new JLabel("TrackEdit");
		lblBate.setIconTextGap(32);
		lblBate.setIcon(new ImageIcon(AppToolkit.getAppIconImages().get(2)));
		lblBate.setHorizontalAlignment(SwingConstants.CENTER);
		lblBate.setFont(new Font("Tahoma", Font.BOLD, 36));
		panel.add(lblBate, BorderLayout.CENTER);

		JLabel lblBittwiddlersAutomationTrack = new JLabel("Automation Track Editor");
		lblBittwiddlersAutomationTrack.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblBittwiddlersAutomationTrack, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		content.add(panel_1, BorderLayout.SOUTH);

		JButton btnOkay = new JButton("OK");
		btnOkay.addActionListener(e -> {
			dispose();
		});
		panel_1.add(btnOkay);
		getRootPane().setDefaultButton(btnOkay);

		JTextPane txtpnInfobox = new JTextPane();
		txtpnInfobox.setEditable(false);
		txtpnInfobox.setContentType("text/html");
		txtpnInfobox.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		txtpnInfobox
				.setText("<html>Copyright © 2014-2015 Robert Maupin (BitTwiddler)<br>"
						+ "Made for <a href=\"http://automationgame.com/\">Automation</a> by Camshaft Software.<br>"
						+ "Hosted by <a href=\"http://www.automationhub.net/\">AutomationHub</a>.<br>"
						+ "<p>TrackEdit was made for no other reason then I was unaware at the time that a track editor like it already existed for Automation> Lacking this knowledge I set to my task without any kind of preconception of how it should function. Thanks to all the people on the automation forum who made this program possible.</p></html>");
		txtpnInfobox.setOpaque(false);
		txtpnInfobox.addHyperlinkListener(e -> {
			if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if(Desktop.isDesktopSupported())
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch(Exception e1) {
					}
			}
		});

		content.add(txtpnInfobox, BorderLayout.CENTER);

		setLocationRelativeTo(owner);
	}

}
