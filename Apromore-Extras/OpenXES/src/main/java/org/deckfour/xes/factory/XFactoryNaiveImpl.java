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
package org.deckfour.xes.factory;

import java.net.URI;
import java.util.Date;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContainer;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContainerImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeIDImpl;
import org.deckfour.xes.model.impl.XAttributeListImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

/**
 * This factory will create the naive implementations of
 * all model hierarchy elements, i.e., no buffering or
 * further optimizations will be employed.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XFactoryNaiveImpl implements XFactory {

	// Use String interning to save memory
	private final Interner<String> interner;
	private boolean useInterner = true;

	public XFactoryNaiveImpl() {
		super();
		// Use weak references as this factory may stay alive for a long time 
		interner = Interners.newWeakInterner();
	}
	
	private String intern(String s) {
		if (useInterner) {
			return interner.intern(s);	
		} else {
			return s;
		}
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#getAuthor()
	 */
	public String getAuthor() {
		return "Christian W. Günther";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#getDescription()
	 */
	public String getDescription() {
		return "Creates naive implementations for all available "
			+ "model hierarchy elements, i.e., no optimizations "
			+ "will be employed.";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#getName()
	 */
	public String getName() {
		return "Standard / naive";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#getUri()
	 */
	public URI getUri() {
		return URI.create("http://www.xes-standard.org/");
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#getVendor()
	 */
	public String getVendor() {
		return "xes-standard.org";
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createLog()
	 */
	public XLog createLog() {
		return new XLogImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactory#createLog(org.deckfour.xes.model.XAttributeMap)
	 */
	public XLog createLog(XAttributeMap attributes) {
		return new XLogImpl(attributes);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createTrace()
	 */
	public XTrace createTrace() {
		return new XTraceImpl(new XAttributeMapLazyImpl<XAttributeMapImpl>(XAttributeMapImpl.class));
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactory#createTrace(org.deckfour.xes.model.XAttributeMap)
	 */
	public XTrace createTrace(XAttributeMap attributes) {
		return new XTraceImpl(attributes);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createEvent()
	 */
	public XEvent createEvent() {
		return new XEventImpl();
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactory#createEvent(org.deckfour.xes.model.XAttributeMap)
	 */
	public XEvent createEvent(XAttributeMap attributes) {
		return new XEventImpl(attributes);
	}

	/*
	 * (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactory#createEvent(org.deckfour.xes.id.XID, org.deckfour.xes.model.XAttributeMap)
	 */
	public XEvent createEvent(XID id, XAttributeMap attributes) {
		return new XEventImpl(id, attributes);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeMap()
	 */
	public XAttributeMap createAttributeMap() {
		return new XAttributeMapImpl();
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeBoolean(java.lang.String, boolean, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeBoolean createAttributeBoolean(String key, boolean value,
			XExtension extension) {
		return new XAttributeBooleanImpl(intern(key), value, extension);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeContinuous(java.lang.String, double, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeContinuous createAttributeContinuous(String key,
			double value, XExtension extension) {
		return new XAttributeContinuousImpl(intern(key), value, extension);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeDiscrete(java.lang.String, long, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeDiscrete createAttributeDiscrete(String key, long value,
			XExtension extension) {
		return new XAttributeDiscreteImpl(intern(key), value, extension);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeLiteral(java.lang.String, java.lang.String, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeLiteral createAttributeLiteral(String key, String value,
			XExtension extension) {
		return new XAttributeLiteralImpl(intern(key), intern(value), extension);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeTimestamp(java.lang.String, java.util.Date, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeTimestamp createAttributeTimestamp(String key, Date value,
			XExtension extension) {
		return new XAttributeTimestampImpl(intern(key), value, extension);
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeTimestamp(java.lang.String, long, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeTimestamp createAttributeTimestamp(String key,
			long millis, XExtension extension) {
		return new XAttributeTimestampImpl(intern(key), millis, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createAttributeID(java.lang.String, org.deckfour.xes.id.XID, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeID createAttributeID(String key, XID value,
			XExtension extension) {
		return new XAttributeIDImpl(intern(key), value, extension);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactory#createAttributeList(java.lang.String, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeList createAttributeList(String key, XExtension extension) {
		return new XAttributeListImpl(intern(key), extension);
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactory#createAttributeContainer(java.lang.String, org.deckfour.xes.extension.XExtension)
	 */
	public XAttributeContainer createAttributeContainer(String key, XExtension extension) {
		return new XAttributeContainerImpl(intern(key), extension);
	}
	
	public boolean isUseInterner() {
		return useInterner;
	}

	public void setUseInterner(boolean useInterner) {
		this.useInterner = useInterner;
	}
	
}
