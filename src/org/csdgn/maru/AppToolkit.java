/**
 * Copyright (c) 2011-2021 Robert Maupin
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
package org.csdgn.maru;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class AppToolkit {
	private static File directory = null;

	private static ArrayList<Image> icons = null;

	public static int confirmWarning(Component owner, String message) {
		return JOptionPane.showConfirmDialog(owner, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Get the program Icons
	 *
	 * @return
	 */
	public static ArrayList<Image> getAppIconImages() {
		if(icons != null) {
			return icons;
		}

		icons = new ArrayList<Image>();

		for(int val : new int[] { 16, 32, 48 }) {
			String iconString = String.format("icon%d.png", val);

			InputStream stream = getLocalResource(iconString);
			if(stream != null) {
				try {
					icons.add(ImageIO.read(stream));
				} catch(IOException e) {
					// If we fail, just show the default java icon
				}
			}
		}

		return icons;
	}

	/**
	 * Gets the directory for the program.
	 */
	public static File getLocalDirectory() {
		if(directory != null) {
			return directory;
		}

		// Determine our base directory
		String codePath = AppToolkit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File codeFile = new File(codePath);
		if(codeFile.exists() && codePath != null) {
			// So we have a predictable format
			codePath = codeFile.toString();
			int len = codePath.length();
			if(len > 3) {
				String last = codePath.substring(len - 3).toLowerCase();
				if(last.equals("jar") || last.equals("bin")) {
					// Executing from a jar or development path
					directory = codeFile.getParentFile();
					if("lib".equalsIgnoreCase(directory.getName())) {
						directory = directory.getParentFile();
					}
					return directory;
				}
			}
			if(directory == null) {
				directory = codeFile;
			}
			while(!directory.isDirectory()) {
				directory = directory.getParentFile();
			}
			if("lib".equalsIgnoreCase(directory.getName())) {
				directory = directory.getParentFile();
			}
			// System.out.println(directory);

			return directory;
		}

		// I guess we can't do anything else...
		return directory = new File(".");
	}

	/**
	 * Gets the given resource as a input stream
	 * @param resource resource to get
	 * @returnthe input stream, or null on failure
	 */
	public static InputStream getLocalResource(String resource) {
		// check within current directory first
		try {
			File file = new File(getLocalDirectory(), resource);
			if(file.exists()) {
				return new FileInputStream(file);
			}
		} catch(Exception e) {
			// TODO
		}

		// fall back to in jar
		InputStream stream = ClassLoader.getSystemResourceAsStream(resource);
		if(stream != null) {
			return stream;
		}

		// if that fails, try development location
		try {
			File file = new File(getLocalDirectory(), "resource/" + resource);
			if(file.exists()) {
				return new FileInputStream(file);
			}
		} catch(Exception e) {
			// TODO
		}

		return null;
	}

	public static void setFileChooserReadOnly(boolean readOnly) {
		UIManager.put("FileChooser.readOnly", readOnly);
	}

	public static void showError(Component owner, String message) {
		JOptionPane.showMessageDialog(owner, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void showWarning(Component owner, String message) {
		JOptionPane.showMessageDialog(owner, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static int showYesNoOption(Component owner, String message, String title) {
		return JOptionPane.showConfirmDialog(owner, message, title, JOptionPane.YES_NO_OPTION);
	}
}
