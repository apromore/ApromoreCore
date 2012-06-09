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
package de.hbrs.oryx.yawl.converter.layout;

import org.oryxeditor.server.diagram.Bounds;

/**
 * Store information about the Layout of YAWL elements. <br>
 * TODO: Maybe create a class for each YAWL element. (condition, task, ..)
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NetElementLayout {

	public enum DecoratorType {
		TOP, LEFT, RIGHT, BOTTOM, NONE
	}

	/**
	 * True if this element is a condition
	 */
	private final boolean isCondition;

	private DecoratorType splitDecorator = DecoratorType.NONE;
	private DecoratorType joinDecorator = DecoratorType.NONE;
	private Bounds bounds;
	private String iconPath;

	public NetElementLayout(boolean isCondition) {
		super();
		this.isCondition = isCondition;
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setSplitDecorator(DecoratorType splitDecoratorType) {
		this.splitDecorator = splitDecoratorType;
	}

	public DecoratorType getSplitDecorator() {
		return splitDecorator;
	}

	public void setJoinDecorator(DecoratorType joinDecoratorType) {
		this.joinDecorator = joinDecoratorType;
	}

	public DecoratorType getJoinDecorator() {
		return joinDecorator;
	}

	public boolean isCondition() {
		return isCondition;
	}

	public boolean hasJoinDecorator() {
		return !getJoinDecorator().equals(DecoratorType.NONE);
	}

	public boolean hasSplitDecorator() {
		return !getSplitDecorator().equals(DecoratorType.NONE);
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getIconPath() {
		return iconPath;
	}

}
