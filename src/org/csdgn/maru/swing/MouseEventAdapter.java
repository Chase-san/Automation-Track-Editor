/**
 * Copyright (c) 2021 Robert Maupin
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Adapter for mouse events, used to allow the use of inline java lambda.
 * @author Robert Maupin
 *
 */
public abstract class MouseEventAdapter extends MouseAdapter {
	/**
	 * Simple handler for a single mouse event. Used for lambda construction.
	 */
	public static interface MouseEventHandler {
		public void onMouseEvent(MouseEvent e);
	}

	/**
	 * Used to handle a "clicked" mouse event.
	 */
	public static class Clicked extends MouseEventAdapter {
		public Clicked(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	/**
	 * Used to handle a "entered" mouse event.
	 */
	public static class Entered extends MouseEventAdapter {
		public Entered(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	/**
	 * Used to handle a "exited" mouse event.
	 */
	public static class Exited extends MouseEventAdapter {
		public Exited(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	/**
	 * Used to handle a "pressed" mouse event.
	 */
	public static class Pressed extends MouseEventAdapter {
		public Pressed(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	/**
	 * Used to handle a "released" mouse event.
	 */
	public static class Released extends MouseEventAdapter {
		public Released(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	/**
	 * Used to handle a "dragged" mouse event.
	 */
	public static class Dragged extends MouseEventAdapter {
		public Dragged(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	/**
	 * Used to handle a "moved" mouse event.
	 */
	public static class Moved extends MouseEventAdapter {
		public Moved(MouseEventHandler handler) {
			super(handler);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			handler.onMouseEvent(e);
		}
	}

	protected final MouseEventHandler handler;

	public MouseEventAdapter(MouseEventHandler handler) {
		this.handler = handler;
	}
}
