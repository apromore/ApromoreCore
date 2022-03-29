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
/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2008 Christian W. Guenther (christian@deckfour.org)
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
package org.deckfour.xes.info.impl;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import java.util.Map;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.classification.XEventResourceClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.info.XAttributeInfo;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XTimeBounds;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * This class implements a bare-bones log info summary which can
 * be created on demand by using applications.
 * 
 * The log info summary is based on an event classifier, which is
 * used to identify event class abstractions. 
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XLogInfoImpl implements XLogInfo {
	
	/**
	 * Default event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have both
	 * the same event name and the same lifecycle transition
	 * (if available).
	 */
	public static final XEventClassifier STANDARD_CLASSIFIER = 
		new XEventAttributeClassifier(
			"MXML Legacy Classifier",
			XConceptExtension.KEY_NAME, 
			XLifecycleExtension.KEY_TRANSITION);
	
	/**
	 * Standard event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have 
	 * the same value for the event name attribute.
	 */
	public static final XEventClassifier NAME_CLASSIFIER
			= new XEventNameClassifier();
	
	/**
	 * Standard event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have 
	 * the same value for the resource attribute.
	 */
	public static final XEventClassifier RESOURCE_CLASSIFIER 
			= new XEventResourceClassifier();
	
	/**
	 * Standard event classifier. This classifier considers two 
	 * events as belonging to the same class, if they have 
	 * the same value for the lifecycle transition attribute.
	 */
	public static final XEventClassifier LIFECYCLE_TRANSITION_CLASSIFIER
			= new XEventLifeTransClassifier();
	
	
	
	/**
	 * Creates a new log info summary with the standard event classifier.
	 * 
	 * @param log The event log to create an info summary for.
	 * @return The log info for this log.
	 */
	public static XLogInfo create(XLog log) {
		return create(log, STANDARD_CLASSIFIER);
	}
	
	/**
	 * Creates a new log info summary with a custom event classifier.
	 * 
	 * @param log The event log to create an info summary for.
	 * @param defaultClassifier The default event classifier to be used.
	 * @return The log info summary for this log.
	 */
	public static XLogInfo create(XLog log, XEventClassifier defaultClassifier) {
		return create(log, defaultClassifier, null);
	}
	
	/**
	 * Creates a new log info summary with a collection of custom 
	 * event classifiers.
	 * 
	 * @param log The event log to create an info summary for.
	 * @param defaultClassifier The default event classifier to be used.
	 * @param classifiers A collection of additional event classifiers to
	 * be covered by the created log info instance.
	 * @return The log info summary for this log.
	 */
	public static XLogInfo create(XLog log, XEventClassifier defaultClassifier, 
			Collection<XEventClassifier> classifiers) {
		return new XLogInfoImpl(log, defaultClassifier, classifiers);
	}
	
	/**
	 * The event log which is summarized.
	 */
	protected XLog log;
	/**
	 * The total number of events in this log.
	 */
	protected int numberOfEvents;
	/**
	 * The number of traces in this log.
	 */
	protected int numberOfTraces;
	/**
	 * Maps the event classifiers covered in this log info
	 * to their respectively created event classes.
	 */
	protected Map<XEventClassifier,XEventClasses> eventClasses;
	/**
	 * The default event classifier for this
	 * log info instance.
	 */
	protected XEventClassifier defaultClassifier;
	/**
	 * Timestamp boundaries for the complete log.
	 */
	protected XTimeBoundsImpl logBoundaries;
	/**
	 * Map of timestamp boundaries for each trace, indexed
	 * by reference to the respective trace.
	 */
	protected UnifiedMap<XTrace,XTimeBoundsImpl> traceBoundaries;
	/**
	 * Attribute information registry on the log level.
	 */
	protected XAttributeInfoImpl logAttributeInfo;
	/**
	 * Attribute information registry on the trace level.
	 */
	protected XAttributeInfoImpl traceAttributeInfo;
	/**
	 * Attribute information registry on the event level.
	 */
	protected XAttributeInfoImpl eventAttributeInfo;
	/**
	 * Attribute information registry on the meta level.
	 */
	protected XAttributeInfoImpl metaAttributeInfo;
	
	/**
	 * Creates a new log summary.
	 * 
	 * @param log The log to create a summary of.
	 * @param classifier The event classifier to be used.
	 */
	public XLogInfoImpl(XLog log, XEventClassifier defaultClassifier, Collection<XEventClassifier> classifiers) {
		this.log = log;
		this.defaultClassifier = defaultClassifier;
		if(classifiers == null) {
			classifiers = Collections.emptyList();
		}
		this.eventClasses = new UnifiedMap<XEventClassifier,XEventClasses>(classifiers.size() + 4);
		for(XEventClassifier classifier : classifiers) {
			this.eventClasses.put(classifier, new XEventClasses(classifier));
		}
		this.eventClasses.put(this.defaultClassifier, new XEventClasses(this.defaultClassifier));
		this.eventClasses.put(XLogInfoImpl.NAME_CLASSIFIER, new XEventClasses(XLogInfoImpl.NAME_CLASSIFIER));
		this.eventClasses.put(XLogInfoImpl.RESOURCE_CLASSIFIER, new XEventClasses(XLogInfoImpl.RESOURCE_CLASSIFIER));
		this.eventClasses.put(XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER, new XEventClasses(XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER));
		this.numberOfEvents = 0;
		this.numberOfTraces = 0;
		this.logBoundaries = new XTimeBoundsImpl();
		this.traceBoundaries = new UnifiedMap<XTrace,XTimeBoundsImpl>();
		this.logAttributeInfo = new XAttributeInfoImpl();
		this.traceAttributeInfo = new XAttributeInfoImpl();
		this.eventAttributeInfo = new XAttributeInfoImpl();
		this.metaAttributeInfo = new XAttributeInfoImpl();
		setup();
	}

	/**
	 * Creates the internal data structures of this summary on setup
	 * from the log.
	 */
	protected synchronized void setup() {
		registerAttributes(logAttributeInfo, log);
		for(XTrace trace : log) {
			numberOfTraces++;
			registerAttributes(traceAttributeInfo, trace);
			XTimeBoundsImpl traceBounds = new XTimeBoundsImpl();
			for(XEvent event : trace) {
				registerAttributes(eventAttributeInfo, event);
				for(XEventClasses classes : this.eventClasses.values()) {
					classes.register(event);
				}
				traceBounds.register(event);
				numberOfEvents++;
			}
			this.traceBoundaries.put(trace, traceBounds);
			this.logBoundaries.register(traceBounds);
		}
		// harmonize event class indices
		for(XEventClasses classes : this.eventClasses.values()) {
			classes.harmonizeIndices();
		}
	}
	
	/**
	 * Registers all attributes of a given attributable, i.e.
	 * model type hierarchy element, in the given attribute info registry.
	 * 
	 * @param attributeInfo Attribute info registry to use for registration.
	 * @param attributable Attributable whose attributes to register.
	 */
	protected void registerAttributes(XAttributeInfoImpl attributeInfo, XAttributable attributable) {
		if (attributable.hasAttributes()) {
			for(XAttribute attribute : attributable.getAttributes().values()) {
				// register attribute in appropriate map
				attributeInfo.register(attribute);
				// register meta-attributes globally
				registerAttributes(metaAttributeInfo, attribute);
			}			
		}
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getLog()
	 */
	public XLog getLog() {
		return log;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getNumberOfEvents()
	 */
	public int getNumberOfEvents() {
		return numberOfEvents;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getNumberOfTraces()
	 */
	public int getNumberOfTraces() {
		return numberOfTraces;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XLogInfo#getEventClasses(org.deckfour.xes.classification.XEventClassifier)
	 */
	public XEventClasses getEventClasses(XEventClassifier classifier) {
		return this.eventClasses.get(classifier);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XLogInfo#getEventClassifiers()
	 */
	public Collection<XEventClassifier> getEventClassifiers() {
		return Collections.unmodifiableCollection(this.eventClasses.keySet());
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getEventClasses()
	 */
	public XEventClasses getEventClasses() {
		return getEventClasses(this.defaultClassifier);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getResourceClasses()
	 */
	public XEventClasses getResourceClasses() {
		return getEventClasses(XLogInfoImpl.RESOURCE_CLASSIFIER);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getNameClasses()
	 */
	public XEventClasses getNameClasses() {
		return getEventClasses(XLogInfoImpl.NAME_CLASSIFIER);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getTransitionClasses()
	 */
	public XEventClasses getTransitionClasses() {
		return getEventClasses(XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getLogTimeBoundaries()
	 */
	public XTimeBounds getLogTimeBoundaries() {
		return logBoundaries;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.summary.XLogSummary#getTraceTimeBoundaries(org.deckfour.xes.model.XTrace)
	 */
	public XTimeBounds getTraceTimeBoundaries(XTrace trace) {
		return traceBoundaries.get(trace);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XLogInfo#getLogAttributeInfo()
	 */
	public XAttributeInfo getLogAttributeInfo() {
		return logAttributeInfo;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XLogInfo#getTraceAttributeInfo()
	 */
	public XAttributeInfo getTraceAttributeInfo() {
		return traceAttributeInfo;
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XLogInfo#getEventAttributeInfo()
	 */
	public XAttributeInfo getEventAttributeInfo() {
		return eventAttributeInfo;
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.info.XLogInfo#getMetaAttributeInfo()
	 */
	public XAttributeInfo getMetaAttributeInfo() {
		return metaAttributeInfo;
	}
	
	
}
