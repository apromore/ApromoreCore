/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2009 Christian W. Guenther (christian@deckfour.org)
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

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.buffered.XAttributeMapBufferedImpl;
import org.deckfour.xes.model.buffered.XAttributeMapSerializerImpl;
import org.deckfour.xes.model.buffered.XTraceBufferedImpl;
import org.deckfour.xes.model.impl.XAttributeMapLazyImpl;
import org.deckfour.xes.model.impl.XLogImpl;

/**
 * This factory will create buffered implementations of
 * all model hierarchy elements wherever possible, i.e., 
 * the latest optimizations available in OpenXES will be 
 * employed.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 *
 */
public class XFactoryBufferedImpl extends XFactoryNaiveImpl {

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryStandardImpl#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return "Christian W. Günther";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryStandardImpl#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Creates buffered implementations for all available "
		+ "model hierarchy elements, i.e., the latest OpenXES standard "
		+ "optimizations will be employed.";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryStandardImpl#getName()
	 */
	@Override
	public String getName() {
		return "Standard / buffered";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryStandardImpl#getUri()
	 */
	@Override
	public URI getUri() {
		return URI.create("http://www.xes-standard.org/");
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryStandardImpl#getVendor()
	 */
	@Override
	public String getVendor() {
		return "xes-standard.org";
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryImpl#createAttributeMap()
	 */
	@Override
	public XAttributeMap createAttributeMap() {
		return new XAttributeMapBufferedImpl();
	}
	
	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactory#createLog()
	 */
	public XLog createLog() {
		return new XLogImpl(new XAttributeMapLazyImpl<XAttributeMapBufferedImpl>(XAttributeMapBufferedImpl.class));
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.model.factory.XModelFactoryImpl#createTrace()
	 */
	@Override
	public XTrace createTrace() {
		return new XTraceBufferedImpl(
				new XAttributeMapLazyImpl<XAttributeMapBufferedImpl>(XAttributeMapBufferedImpl.class), 
				new XAttributeMapSerializerImpl());
	}

	/* (non-Javadoc)
	 * @see org.deckfour.xes.factory.XFactoryNaiveImpl#createTrace(org.deckfour.xes.model.XAttributeMap)
	 */
	@Override
	public XTrace createTrace(XAttributeMap attributes) {
		return new XTraceBufferedImpl(
				attributes, 
				new XAttributeMapSerializerImpl());
	}

}
