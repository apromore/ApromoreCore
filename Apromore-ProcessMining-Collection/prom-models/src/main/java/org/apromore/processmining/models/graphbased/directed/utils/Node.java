/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.models.graphbased.directed.utils;

import java.util.ArrayList;
import java.util.List;

// taken from
// http://sujitpal.blogspot.com/2006/05/java-data-structure-generic-tree.html
// 27th August 2008
// modified for ProM by Arya Adriansyah (arya.adriansyah@gmail.com)

/**
 * Represents a node of the Tree<T> class. The Node<T> is also a container, and
 * can be thought of as instrumentation to determine the location of the type T
 * in the Tree<T>.
 */
public class Node<T> {

	private T data;
	private List<Node<T>> children;
	private Node<T> parent;

	/**
	 * Default ctor.
	 */
	public Node() {
		super();
		setParent(null);
	}

	/**
	 * Convenience ctor to create a Node<T> with an instance of T.
	 * 
	 * @param data
	 *            an instance of T.
	 */
	public Node(T data) {
		this();
		setData(data);
		setParent(null);
	}

	/**
	 * Return the children of Node<T>. The Tree<T> is represented by a single
	 * root Node<T> whose children are represented by a List<Node<T>>. Each of
	 * these Node<T> elements in the List can have children. The getChildren()
	 * method will return the children of a Node<T>.
	 * 
	 * @return the children of Node<T>
	 */
	public List<Node<T>> getChildren() {
		if (this.children == null) {
			return new ArrayList<Node<T>>();
		}
		return this.children;
	}

	/**
	 * Sets the children of a Node<T> object. See docs for getChildren() for
	 * more information.
	 * 
	 * @param children
	 *            the List<Node<T>> to set.
	 */
	public void setChildren(List<Node<T>> children) {
		this.children = children;
	}

	/**
	 * Returns the number of immediate children of this Node<T>.
	 * 
	 * @return the number of immediate children.
	 */
	public int getNumberOfChildren() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	/**
	 * Adds a child to the list of children for this Node<T>. The addition of
	 * the first child will create a new List<Node<T>>.
	 * 
	 * @param child
	 *            a Node<T> object to set.
	 */
	public void addChild(Node<T> child) {
		if (children == null) {
			children = new ArrayList<Node<T>>();
		}
		children.add(child);
	}

	/**
	 * Inserts a Node<T> at the specified position in the child list. Will *
	 * throw an ArrayIndexOutOfBoundsException if the index does not exist.
	 * 
	 * @param index
	 *            the position to insert at.
	 * @param child
	 *            the Node<T> object to insert.
	 * @throws IndexOutOfBoundsException
	 *             if thrown.
	 */
	public void insertChildAt(int index, Node<T> child) throws IndexOutOfBoundsException {
		if (index == getNumberOfChildren()) {
			// this is really an append
			addChild(child);
			return;
		} else {
			children.get(index); //just to throw the exception, and stop here
			children.add(index, child);
		}
	}

	/**
	 * Remove the Node<T> element at index index of the List<Node<T>>.
	 * 
	 * @param index
	 *            the index of the element to delete.
	 * @throws IndexOutOfBoundsException
	 *             if thrown.
	 */
	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(getData().toString()).append(",[");
		int i = 0;
		for (Node<T> e : getChildren()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(e.getData().toString());
			i++;
		}
		sb.append("]").append("}");
		return sb.toString();
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}
}
