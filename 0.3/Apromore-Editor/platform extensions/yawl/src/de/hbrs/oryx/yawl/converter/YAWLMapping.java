/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter;

import java.util.HashMap;
import java.util.Map;

import org.oryxeditor.server.diagram.Point;

import de.hbrs.oryx.yawl.converter.layout.NetElementLayout.DecoratorType;

/**
 * Contains static mappings between YAWL properties and their corresponding Oryx
 * counterparts. All properties are static and should be initialized using the
 * double bracket method.
 * 
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public final class YAWLMapping {

	public static final Map<Integer, DecoratorType> DECORATOR_TYPE_MAP = new HashMap<Integer, DecoratorType>() {

		private static final long serialVersionUID = 3887893686889925570L;

		{
			put(10, DecoratorType.TOP);
			put(11, DecoratorType.BOTTOM);
			put(12, DecoratorType.LEFT);
			put(13, DecoratorType.RIGHT);
			put(14, DecoratorType.NONE);
		}
	};

	public static final Map<Integer, Point> TASK_PORT_MAP = new HashMap<Integer, Point>() {

		private static final long serialVersionUID = -5420166595450623507L;

		{
			// TOP
			put(10, new Point(28.0, 12.0));
			// BOTTOM
			put(11, new Point(28.0, 44.0));
			// LEFT
			put(12, new Point(12.0, 28.0));
			// RIGHT
			put(13, new Point(44.0, 28.0));
			// NOWHERE, maps to Oryx Default
			put(14, new Point(28.0, 28.0));
		}
	};
	public static final Map<Integer, Point> CONDITION_PORT_MAP = new HashMap<Integer, Point>() {

		private static final long serialVersionUID = -468390840359626099L;

		{
			// TOP
			put(10, new Point(16.0, 0.0));
			// BOTTOM
			put(11, new Point(16.0, 32.0));
			// LEFT
			put(12, new Point(0.0, 16.0));
			// RIGHT
			put(13, new Point(32.0, 16.0));
			// NOWHERE, maps to the Oryx Default
			put(14, new Point(16.0, 16.0));
		}
	};

	public static final Map<Integer, Point> TOP_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

		private static final long serialVersionUID = 1164532423345798945L;

		{
			// From Left to Right
			put(0, new Point(12.0, 6.0));
			put(1, new Point(20.0, 0.0));
			put(2, new Point(28.0, 0.0));
			put(3, new Point(36.0, 0.0));
			put(4, new Point(44.0, 6.0));
		}
	};

	public static final Map<Integer, Point> BOTTOM_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

		private static final long serialVersionUID = -4866739586620322203L;

		{
			// From Right to Left
			put(0, new Point(44.0, 50.0));
			put(1, new Point(36.0, 56.0));
			put(2, new Point(28.0, 56.0));
			put(3, new Point(20.0, 56.0));
			put(4, new Point(12.0, 50.0));
		}
	};

	public static final Map<Integer, Point> LEFT_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

		private static final long serialVersionUID = -3451374695282471736L;

		{
			// From Bottom to Top
			put(0, new Point(6.0, 44.0));
			put(1, new Point(0.0, 20.0));
			put(2, new Point(0.0, 28.0));
			put(3, new Point(0.0, 36.0));
			put(4, new Point(6.0, 12.0));
		}
	};

	public static final Map<Integer, Point> RIGHT_DECORATOR_PORT_MAP = new HashMap<Integer, Point>() {

		private static final long serialVersionUID = -7299926129758801645L;

		{
			// From Top to Bottom
			put(0, new Point(50.0, 12.0));
			put(1, new Point(56.0, 20.0));
			put(2, new Point(56.0, 28.0));
			put(3, new Point(56.0, 36.0));
			put(4, new Point(50.0, 44.0));
		}
	};

}
