/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2011 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.model;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public abstract class XVisitor {

	/*
	 * Checks whether the visitor may run.
	 */
	public boolean precondition() {
		return true;
	}
	/*
	 * Initializes the visitor.
	 */
	public void init(XLog log) {
	}
	
	/*
	 * First and last call made when visiting a log.
	 */
	public void visitLogPre(XLog log) {
	}
	public void visitLogPost(XLog log) {
	}
	
	/*
	 * First and last call made when visiting an extension.
	 */
	public void visitExtensionPre(XExtension ext, XLog log) {
	}
	public void visitExtensionPost(XExtension ext, XLog log) {
	}
	
	/*
	 * First and last call made when visiting a classifier.
	 */
	public void visitClassifierPre(XEventClassifier classifier, XLog log) {
	}
	public void visitClassifierPost(XEventClassifier classifier, XLog log) {
	}
	
	/*
	 * First and last call made when visiting a trace.
	 */
	public void visitTracePre(XTrace trace, XLog log) {
	}
	public void visitTracePost(XTrace trace, XLog log) {
	}
	
	/*
	 * First and last call made when visiting an event.
	 */
	public void visitEventPre(XEvent event, XTrace trace) {
	}
	public void visitEventPost(XEvent event, XTrace trace) {
	}
	
	/*
	 * First and last call made when visiting an attribute.
	 */
	public void visitAttributePre(XAttribute attr, XAttributable parent) {
	}
	public void visitAttributePost(XAttribute attr, XAttributable parent) {
	}

}
