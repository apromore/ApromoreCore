/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
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
package org.deckfour.xes.extension.std;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.id.XIDFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;

/**
 * Helper class. This class can be used to dynamically wrap any event, and
 * provides an extended set of getter and setter methods for typically-available
 * extension attributes.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XExtendedEvent implements XEvent {

	/**
	 * ID of this event.
	 */
	private XID id;

	/**
	 * Static wrapper method. Wraps the given event into an instance of this
	 * class, which transparently provides extended access to attributes.
	 * 
	 * @param event
	 *            The original event to be wrapped.
	 * @return A wrapped event.
	 */
	public static XExtendedEvent wrap(XEvent event) {
		return new XExtendedEvent(event);
	}

	/**
	 * The original, wrapped event.
	 */
	protected XEvent original;

	/**
	 * Constructs a new wrapper object.
	 * 
	 * @param original
	 *            The original event to be wrapped.
	 */
	public XExtendedEvent(XEvent original) {
		this.original = original;
	}

	/**
	 * Retrieves the activity name of this event, as defined by the Concept
	 * extension.
	 * 
	 * @return Activity name of the event.
	 */
	public String getName() {
		return XConceptExtension.instance().extractName(original);
	}

	/**
	 * Sets the activity name of this event, as defined by the Concept
	 * extension.
	 * 
	 * @param name
	 *            Activity name of the event.
	 */
	public void setName(String name) {
		XConceptExtension.instance().assignName(original, name);
	}

	/**
	 * Retrieves the activity instance of this event, as defined by the Concept
	 * extension.
	 * 
	 * @return Activity instance of the event.
	 */
	public String getInstance() {
		return XConceptExtension.instance().extractInstance(original);
	}

	/**
	 * Sets the activity instance of this event, as defined by the Concept
	 * extension.
	 * 
	 * @param instance
	 *            Activity instance of the event.
	 */
	public void setInstance(String instance) {
		XConceptExtension.instance().assignInstance(original, instance);
	}

	/**
	 * Retrieves the timestamp of the event, as defined by the Time extension.
	 * 
	 * @return Timestamp as Date object, or <code>null</code> if not defined.
	 */
	public Date getTimestamp() {
		return XTimeExtension.instance().extractTimestamp(original);
	}

	/**
	 * Sets the timestamp of the event, as defined by the Time extension.
	 * 
	 * @param timestamp
	 *            Timestamp, as Date, to be set.
	 */
	public void setTimestamp(Date timestamp) {
		XTimeExtension.instance().assignTimestamp(original, timestamp);
	}

	/**
	 * Sets the timestamp of the event, as defined by the Time extension.
	 * 
	 * @param timestamp
	 *            Timestamp, as long value in milliseconds, to be set.
	 */
	public void setTimestamp(long timestamp) {
		XTimeExtension.instance().assignTimestamp(original, timestamp);
	}

	/**
	 * Returns the resource of the event, as defined by the Organizational
	 * extension.
	 * 
	 * @return Resource string. Can be <code>null</code>, if not defined.
	 */
	public String getResource() {
		return XOrganizationalExtension.instance().extractResource(original);
	}

	/**
	 * Sets the resource of the event, as defined by the Organizational
	 * extension.
	 * 
	 * @param resource
	 *            Resource string.
	 */
	public void setResource(String resource) {
		XOrganizationalExtension.instance().assignResource(original,
				resource.trim());
	}

	/**
	 * Returns the role of the event, as defined by the Organizational
	 * extension.
	 * 
	 * @return Role string. Can be <code>null</code>, if not defined.
	 */
	public String getRole() {
		return XOrganizationalExtension.instance().extractRole(original);
	}

	/**
	 * Sets the role of the event, as defined by the Organizational extension.
	 * 
	 * @param role
	 *            Role string.
	 */
	public void setRole(String role) {
		XOrganizationalExtension.instance().assignRole(original, role.trim());
	}

	/**
	 * Returns the group of the event, as defined by the Organizational
	 * extension.
	 * 
	 * @return Group string. Can be <code>null</code>, if not defined.
	 */
	public String getGroup() {
		return XOrganizationalExtension.instance().extractGroup(original);
	}

	/**
	 * Sets the group of the event, as defined by the Organizational extension.
	 * 
	 * @param group
	 *            Group string.
	 */
	public void setGroup(String group) {
		XOrganizationalExtension.instance().assignGroup(original, group.trim());
	}

	/**
	 * Returns the lifecycle transition of the event, as defined by the
	 * Lifecycle extension.
	 * 
	 * @return Lifecycle transition string. Can be <code>null</code>, if not
	 *         defined.
	 */
	public String getTransition() {
		return XLifecycleExtension.instance().extractTransition(original);
	}

	/**
	 * Sets the lifecycle transition of the event, as defined by the Lifecycle
	 * extension.
	 * 
	 * @param transition
	 *            Lifecycle transition string.
	 */
	public void setTransition(String transition) {
		XLifecycleExtension.instance().assignTransition(original, transition);
	}

	/**
	 * Returns the standard lifecycle transition of the event, as defined by the
	 * Lifecycle extension.
	 * 
	 * @return Standard lifecycle transition object. Can be <code>null</code>,
	 *         if not defined.
	 */
	public XLifecycleExtension.StandardModel getStandardTransition() {
		return XLifecycleExtension.instance().extractStandardTransition(
				original);
	}

	/**
	 * Sets the standard lifecycle transition of the event, as defined by the
	 * Lifecycle extension.
	 * 
	 * @param transition
	 *            Standard lifecycle transition object.
	 */
	public void setStandardTransition(
			XLifecycleExtension.StandardModel transition) {
		XLifecycleExtension.instance().assignStandardTransition(original,
				transition);
	}

	/**
	 * Returns the list of model references defined for this event, as defined
	 * in the Semantic extension.
	 * 
	 * @return List of model reference strings.
	 */
	public List<String> getModelReferences() {
		return XSemanticExtension.instance().extractModelReferences(original);
	}

	/**
	 * Sets the list of model reference strings defined for this event, as
	 * defined in the Semantic extension.
	 * 
	 * @param modelReferences
	 *            List of model reference strings.
	 */
	public void setModelReferences(List<String> modelReferences) {
		XSemanticExtension.instance().assignModelReferences(original,
				modelReferences);
	}

	/**
	 * Returns the list of model reference URIs defined for this event, as
	 * defined in the Semantic extension.
	 * 
	 * @return List of model reference URIs.
	 */
	public List<URI> getModelReferenceURIs() {
		return XSemanticExtension.instance()
				.extractModelReferenceURIs(original);
	}

	/**
	 * Sets the list of model reference URIs defined for this event, as defined
	 * in the Semantic extension.
	 * 
	 * @param modelReferenceURIs
	 *            List of model reference URIs.
	 */
	public void setModelReferenceURIs(List<URI> modelReferenceURIs) {
		XSemanticExtension.instance().assignModelReferenceUris(original,
				modelReferenceURIs);
	}

	// --------------------------------------------------------------
	// Delegate all standard-access methods to original, wrapped event instance.

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.XAttributable#getAttributes()
	 */
	public XAttributeMap getAttributes() {
		return original.getAttributes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.XAttributable#getExtensions()
	 */
	public Set<XExtension> getExtensions() {
		return original.getExtensions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.deckfour.xes.model.XAttributable#setAttributes(org.deckfour.xes.model
	 * .XAttributeMap)
	 */
	public void setAttributes(XAttributeMap attributes) {
		original.setAttributes(attributes);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.XAttributable#hasAttributes()
	 */
	@Override
	public boolean hasAttributes() {
		return original.hasAttributes();
	}

	/**
	 * Clones this event, i.e. creates a deep copy, but with a new ID, so equals
	 * does not hold between this and the clone
	 */
	public Object clone() {
		try {
			XExtendedEvent clone = (XExtendedEvent) super.clone();
			clone.id = XIDFactory.instance().createId();
			clone.original = (XEvent) original.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Tests for equality of type and ID
	 */
	public boolean equals(Object o) {
		if (o instanceof XExtendedEvent) {
			return ((XExtendedEvent) o).id.equals(id);
		} else {
			return false;
		}
	}

	/**
	 * Returns the hashCode of the ID
	 */
	public int hashCode() {
		return id.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.deckfour.xes.model.XEvent#getID()
	 */
	public XID getID() {
		return id;
	}

	/*
	 * Runs the given visitor for the given trace on this event.
	 * 
	 * (non-Javadoc)
	 * @see org.deckfour.xes.model.XEvent#accept(org.deckfour.xes.model.XVisitor, org.deckfour.xes.model.XTrace)
	 */
	public void accept(XVisitor visitor, XTrace trace) {
		/*
		 * First call.
		 */
		visitor.visitEventPre(this, trace);
		/*
		 * Visit the attributes.
		 */
		for (XAttribute attribute: getAttributes().values()) {
			attribute.accept(visitor, this);
		}
		/*
		 * Last call.
		 */
		visitor.visitEventPost(this, trace);
	}

}
