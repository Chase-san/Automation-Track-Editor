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

import org.csdgn.automation.Messages;
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
		setTitle(String.format("%s %s", Messages.getString("AboutAbout"), Messages.getTitleAndVersion()));
		setBounds(100, 100, 400, 350);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel content = new JPanel();
		content.setBorder(new EmptyBorder(4, 4, 4, 4));
		setContentPane(content);
		content.setLayout(new BorderLayout(0, 4));

		JPanel panel = new JPanel();
		content.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(4, 4));

		JLabel lblBate = new JLabel(Messages.getString("Title"));
		lblBate.setIconTextGap(32);
		lblBate.setIcon(new ImageIcon(AppToolkit.getAppIconImages().get(2)));
		lblBate.setHorizontalAlignment(SwingConstants.CENTER);
		lblBate.setFont(new Font("Tahoma", Font.BOLD, 36));
		panel.add(lblBate, BorderLayout.CENTER);

		panel.add(new JLabel(Messages.getString("TitleDesc"), SwingConstants.CENTER), BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		content.add(panel_1, BorderLayout.SOUTH);

		JButton btnOkay = new JButton(Messages.getString("ButtonOK"));
		btnOkay.addActionListener(e -> {
			dispose();
		});
		panel_1.add(btnOkay);
		getRootPane().setDefaultButton(btnOkay);

		JTextPane txtpnInfobox = new JTextPane();
		txtpnInfobox.setEditable(false);
		txtpnInfobox.setContentType("text/html");
		txtpnInfobox.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		
		txtpnInfobox.setText(String.format("<html>%s<br>%s<br>%s<p>%s</p><p>%s</p></html>",
				Messages.getString("AboutCopyright"),
				Messages.getString("AboutMadeFor"),
				Messages.getString("AboutHostedBy"),
				Messages.getString("AboutUpdate"),
				Messages.getString("AboutDesc"))
			);
		
		txtpnInfobox.setOpaque(false);
		txtpnInfobox.addHyperlinkListener(e -> {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if (Desktop.isDesktopSupported())
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (Exception e1) {
					}
			}
		});

		content.add(txtpnInfobox, BorderLayout.CENTER);

		setLocationRelativeTo(owner);
	}

}
