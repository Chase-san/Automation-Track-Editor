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
package org.csdgn.maru.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Robert Maupin
 */
public class Accordion extends JComponent {
	private class Page implements ActionListener {
		Component comp;
		JButton head;

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO store numbers when adding a new page?
			showPage(pages.indexOf(this));
		}
	}

	private static final long serialVersionUID = 5087568024482579989L;
	private GridLayout bottomLayout;
	private JPanel bottomPanel;
	private JPanel centerPanel;
	private LinkedHashSet<ChangeListener> changeListeners;
	private int currentIndex = -1;
	private ArrayList<Page> pages;
	private GridLayout topLayout;
	private JPanel topPanel;

	/**
	 * Create the panel.
	 */
	public Accordion() {
		setLayout(new BorderLayout(0, 0));

		changeListeners = new LinkedHashSet<ChangeListener>();

		pages = new ArrayList<Page>();

		topLayout = new GridLayout(1, 0, 0, 0);
		bottomLayout = new GridLayout(1, 0, 0, 0);

		topPanel = new JPanel(topLayout);
		super.add(topPanel, BorderLayout.NORTH);

		// actually a card layout would work way better
		centerPanel = new JPanel(new BorderLayout());
		super.add(centerPanel, BorderLayout.CENTER);

		bottomPanel = new JPanel(bottomLayout);
		super.add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Adds a component with a page title defaulting to the name of the
	 * component which is the result of calling component.getName.
	 */
	@Override
	public Component add(Component component) {
		add(component.getName(), component);
		return component;
	}

	/**
	 * Adds a component at the specified page index with a page title defaulting
	 * to the name of the component.
	 */
	@Override
	public Component add(Component component, int index) {
		insertPage(component.getName(), component, index);
		return component;
	}

	/**
	 * Adds a component to the paged pane.
	 */
	@Override
	public void add(Component component, Object constraints) {
		insertPage(component.getName(), component, getPageCount());
	}

	/**
	 * Adds a component at the specified page index.
	 */
	@Override
	public void add(Component component, Object constraints, int index) {
		insertPage(component.getName(), component, index);
	}

	/**
	 * Adds a component with the specified page title.
	 */
	@Override
	public Component add(String title, Component component) {
		insertPage(title, component, getPageCount());
		return component;
	}

	public void addChangeListener(ChangeListener l) {
		changeListeners.add(l);
	}

	/**
	 * Adds a component represented by a title and no icon.
	 */
	public void addPage(String title, Component component) {
		insertPage(title, component, getPageCount());
	}

	protected void fireStateChanged() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener l : changeListeners) {
			l.stateChanged(e);
		}
	}

	public Component getPageButton(int index) {
		return pages.get(index).head;
	}

	public Component getPage(int index) {
		return pages.get(index).comp;
	}

	public int getPageCount() {
		return pages.size();
	}

	public Component getSelectedComponent() {
		return pages.get(currentIndex).comp;
	}

	public int getSelectedIndex() {
		return currentIndex;
	}

	public int indexOfPage(Component component) {
		for(int i = 0; i < pages.size(); ++i) {
			if(pages.get(i).comp == component) {
				return i;
			}
		}
		return -1;
	}

	public int indexOfPage(String title) {
		for(int i = 0; i < pages.size(); ++i) {
			if(pages.get(i).head.getText().equals(title)) {
				return i;
			}
		}
		return -1;
	}

	public void insertPage(Component component, int index) {
		Page page = new Page();
		page.head = new JButton(component.getName());
		page.head.setFocusPainted(false);
		page.head.addActionListener(page);
		page.comp = component;
		pages.add(index, page);
		if(currentIndex == -1) {
			showPageInternal(0);
		} else {
			showPageInternal(currentIndex);
		}
	}

	public void insertPage(String title, Component component, int index) {
		Page page = new Page();
		page.head = new JButton(title);
		page.head.setFocusPainted(false);
		page.head.addActionListener(page);
		page.comp = component;
		pages.add(index, page);
		if(currentIndex == -1) {
			showPageInternal(0);
		} else {
			showPageInternal(currentIndex);
		}
	}

	/**
	 * Removes the specified Component from the JTabbedPane.
	 */
	@Override
	public void remove(Component component) {

		int index = indexOfPage(component);
		if(index != -1) {
			remove(index);
		}
	}

	/**
	 * Removes the tab and component which corresponds to the specified index.
	 *
	 * @exception IndexOutOfBoundsException
	 *                may be thrown if outside of the range 0 inclusive to
	 *                getPageCount() exclusive.
	 */
	@Override
	public void remove(int index) {
		if(pages.size() == 1) {
			if(index == 0) {
				removeAll();
			} else {
				throw new IndexOutOfBoundsException("No index " + index + ".");
			}
			return;
		}
		// 2 or more
		if(index <= currentIndex) {
			// decrement
			pages.remove(index);
			showPageInternal(Math.max(0, index - 1));
		} else {
			pages.remove(index);
			showPageInternal(index);
		}
	}

	/**
	 * Removes all the tabs and their corresponding components from the
	 * tabbedpane.
	 */
	@Override
	public void removeAll() {
		currentIndex = -1;
		bottomPanel.removeAll();
		bottomLayout.setRows(1);
		topPanel.removeAll();
		topLayout.setRows(1);
		centerPanel.removeAll();
		pages.clear();
		revalidate();
	}

	public void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}

	public void removeTabAt(int index) {
		remove(index);
	}

	public void setPageTitle(int index, String title) {
		pages.get(index).head.setText(title);
	}

	public void showPage(int index) {
		if(index != currentIndex) {
			showPageInternal(index);
		}
	}

	private void showPageInternal(int index) {
		// maybe remove this if it causes trouble
		setVisible(false);

		topPanel.removeAll();
		bottomPanel.removeAll();

		topLayout.setRows(index + 1);
		bottomLayout.setRows(Math.max(1, pages.size() - index - 1));

		for(int i = 0; i <= index; ++i) {
			topPanel.add(pages.get(i).head);
		}

		for(int i = index + 1; i < pages.size(); ++i) {
			bottomPanel.add(pages.get(i).head);
		}

		centerPanel.removeAll();
		centerPanel.add(pages.get(index).comp, BorderLayout.CENTER);
		pages.get(index).comp.setVisible(true);

		currentIndex = index;

		setVisible(true);

		revalidate();

		fireStateChanged();
	}
}
