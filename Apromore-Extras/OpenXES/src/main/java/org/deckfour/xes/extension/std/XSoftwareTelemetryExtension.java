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
 * Copyright (c) 2017 Christian W. Guenther (christian@deckfour.org)
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

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XEvent;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 */
public class XSoftwareTelemetryExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7729488046106497747L;
	
	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI
			.create("http://www.xes-standard.org/swtelemetry.xesext");
	/**
	 * Prefix for this extension.
	 */
	public static final String PREFIX = "swtelemetry";

	/**
	 * Levels of all defined attributes.
	 */
	private static enum AttributeLevel {
		EVENT
	};

	/**
	 * Types of all defined attributes.
	 */
	private static enum AttributeType {
		FLOAT, INT
	};

	/**
	 * All defined attributes.
	 */
	private static enum DefinedAttribute {
		CPU_TOTAL_USER("cpuTotalUser", AttributeLevel.EVENT, AttributeType.INT,
				"CPU usage - total time in user space, in milliseconds"), //
		CPU_TOTAL_KERNEL("cpuTotalKernel", AttributeLevel.EVENT,
				AttributeType.INT,
				"CPU usage - total time in kernel space, in milliseconds"), //
		CPU_TOTAL_IDLE("cpuTotalIdle", AttributeLevel.EVENT, AttributeType.INT,
				"CPU usage - total time spent idle, in milliseconds"), //
		CPU_LOAD_USER("cpuLoadUser", AttributeLevel.EVENT, AttributeType.FLOAT,
				"CPU usage - load in user space"), //
		CPU_LOAD_KERNEL("cpuLoadKernel", AttributeLevel.EVENT,
				AttributeType.FLOAT, "CPU usage - load in kernel space"), //
		THREAD_TOTAL("threadTotal", AttributeLevel.EVENT, AttributeType.INT,
				"Total number of threads"), //
		THREAD_DAEMON("threadDaemon", AttributeLevel.EVENT, AttributeType.INT,
				"Number of daemon threads"), //
		MEMORY_USED("memoryUsed", AttributeLevel.EVENT, AttributeType.INT,
				"Total memory used, measured in bytes"), //
		MEMORY_TOTAL("memoryTotal", AttributeLevel.EVENT, AttributeType.INT,
				"Total memory available, measured in bytes"), //
		MEMORY_LOAD("memoryLoad", AttributeLevel.EVENT, AttributeType.FLOAT,
				"Memory usage load");

		String key;
		String alias;
		AttributeLevel level;
		AttributeType type;
		XAttribute prototype;

		DefinedAttribute(String key, AttributeLevel level, AttributeType type,
				String alias) {
			this.key = PREFIX + ":" + key;
			this.level = level;
			this.type = type;
			this.alias = alias;
		}

		void setPrototype(XAttribute prototype) {
			this.prototype = prototype;
		}
	}

	/**
	 * Global key place holders. Can be initialized immediately.
	 */
	public static final String KEY_CPU_TOTAL_USER = DefinedAttribute.CPU_TOTAL_USER.key;
	public static final String KEY_CPU_TOTAL_KERNEL = DefinedAttribute.CPU_TOTAL_KERNEL.key;
	public static final String KEY_CPU_TOTAL_IDLE = DefinedAttribute.CPU_TOTAL_IDLE.key;
	public static final String KEY_CPU_LOAD_USER = DefinedAttribute.CPU_LOAD_USER.key;
	public static final String KEY_CPU_LOAD_KERNEL = DefinedAttribute.CPU_LOAD_KERNEL.key;
	public static final String KEY_THREAD_TOTAL = DefinedAttribute.THREAD_TOTAL.key;
	public static final String KEY_THREAD_DAEMON = DefinedAttribute.THREAD_DAEMON.key;
	public static final String KEY_MEMORY_USED = DefinedAttribute.MEMORY_USED.key;
	public static final String KEY_MEMORY_TOTAL = DefinedAttribute.MEMORY_TOTAL.key;
	public static final String KEY_MEMORY_LOAD = DefinedAttribute.MEMORY_LOAD.key;

	/**
	 * Global prototype place holders. Need to be initialized by constructor.
	 */
	public static XAttributeDiscrete ATTR_CPU_TOTAL_USER;
	public static XAttributeDiscrete ATTR_CPU_TOTAL_KERNEL;
	public static XAttributeDiscrete ATTR_CPU_TOTAL_IDLE;
	public static XAttributeContinuous ATTR_CPU_LOAD_USER;
	public static XAttributeContinuous ATTR_CPU_LOAD_KERNEL;
	public static XAttributeDiscrete ATTR_THREAD_TOTAL;
	public static XAttributeDiscrete ATTR_THREAD_DAEMON;
	public static XAttributeDiscrete ATTR_MEMORY_USED;
	public static XAttributeDiscrete ATTR_MEMORY_TOTAL;
	public static XAttributeContinuous ATTR_MEMORY_LOAD;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XSoftwareTelemetryExtension singleton = new XSoftwareTelemetryExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XSoftwareTelemetryExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XSoftwareTelemetryExtension() {
		super("Software Telemetry", PREFIX, EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();

		/*
		 * Initialize all defined attributes.
		 */
		for (DefinedAttribute attribute : DefinedAttribute.values()) {
			/*
			 * Initialize the prototype of the attribute. Depends on its type.
			 */
			switch (attribute.type) {
			case INT: {
				attribute.setPrototype(factory.createAttributeDiscrete(
						attribute.key, -1, this));
				break;
			}
			case FLOAT: {
				attribute.setPrototype(factory.createAttributeContinuous(
						attribute.key, -1.0, this));
				break;
			}
			}
			/*
			 * Add the attribute to the proper list. Depends on the level.
			 */
			switch (attribute.level) {
			case EVENT: {
				this.eventAttributes.add((XAttribute) attribute.prototype
						.clone());
				break;
			}
			}
			/*
			 * Initialize the proper global prototype place holder.
			 */
			switch (attribute) {
			case CPU_TOTAL_USER: {
				ATTR_CPU_TOTAL_USER = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case CPU_TOTAL_KERNEL: {
				ATTR_CPU_TOTAL_KERNEL = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case CPU_TOTAL_IDLE: {
				ATTR_CPU_TOTAL_IDLE = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case CPU_LOAD_USER: {
				ATTR_CPU_LOAD_USER = (XAttributeContinuous) attribute.prototype;
				break;
			}
			case CPU_LOAD_KERNEL: {
				ATTR_CPU_LOAD_KERNEL = (XAttributeContinuous) attribute.prototype;
				break;
			}
			case THREAD_TOTAL: {
				ATTR_THREAD_TOTAL = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case THREAD_DAEMON: {
				ATTR_THREAD_DAEMON = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case MEMORY_USED: {
				ATTR_MEMORY_USED = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case MEMORY_TOTAL: {
				ATTR_MEMORY_TOTAL = (XAttributeDiscrete) attribute.prototype;
				break;
			}
			case MEMORY_LOAD: {
				ATTR_MEMORY_LOAD = (XAttributeContinuous) attribute.prototype;
				break;
			}
			}
			/*
			 * Initialize the key alias.
			 */
			XGlobalAttributeNameMap.instance().registerMapping(
					XGlobalAttributeNameMap.MAPPING_ENGLISH, attribute.key,
					attribute.alias);
		}
	}

	/*
	 * A list of handy assign and extract methods. Most are really
	 * straightforward.
	 */
	public long extractCPUTotalUser(XEvent event) {
		return extract(event, DefinedAttribute.CPU_TOTAL_USER, -1);
	}

	public XAttributeDiscrete assignCPUTotalUser(XEvent event, long cpuTotalUser) {
		return assign(event, DefinedAttribute.CPU_TOTAL_USER, cpuTotalUser);
	}

	public long extractCPUTotalKernel(XEvent event) {
		return extract(event, DefinedAttribute.CPU_TOTAL_KERNEL, -1);
	}

	public XAttributeDiscrete assignCPUTotalKernel(XEvent event, long cpuTotalKernel) {
		return assign(event, DefinedAttribute.CPU_TOTAL_KERNEL, cpuTotalKernel);
	}

	public long extractCPUTotalIdle(XEvent event) {
		return extract(event, DefinedAttribute.CPU_TOTAL_IDLE, -1);
	}

	public XAttributeDiscrete assignCPUTotalIdle(XEvent event, long cpuTotalIdle) {
		return assign(event, DefinedAttribute.CPU_TOTAL_IDLE, cpuTotalIdle);
	}

	public double extractCPULoadUser(XEvent event) {
		return extract(event, DefinedAttribute.CPU_LOAD_USER, -1.0);
	}

	public XAttributeContinuous assignCPULoadUser(XEvent event, double cpuLoadUser) {
		return assign(event, DefinedAttribute.CPU_LOAD_USER, cpuLoadUser);
	}

	public double extractCPULoadKernel(XEvent event) {
		return extract(event, DefinedAttribute.CPU_LOAD_KERNEL, -1.0);
	}

	public XAttributeContinuous assignCPULoadKernel(XEvent event, double cpuLoadKernel) {
		return assign(event, DefinedAttribute.CPU_LOAD_KERNEL, cpuLoadKernel);
	}

	public long extractThreadTotal(XEvent event) {
		return extract(event, DefinedAttribute.THREAD_TOTAL, -1);
	}

	public XAttributeDiscrete assignThreadTotal(XEvent event, long threadTotal) {
		return assign(event, DefinedAttribute.THREAD_TOTAL, threadTotal);
	}

	public long extractThreadDaemon(XEvent event) {
		return extract(event, DefinedAttribute.THREAD_DAEMON, -1);
	}

	public XAttributeDiscrete assignThreadDaemon(XEvent event, long threadDaemon) {
		return assign(event, DefinedAttribute.THREAD_DAEMON, threadDaemon);
	}

	public long extractMemoryUsed(XEvent event) {
		return extract(event, DefinedAttribute.MEMORY_USED, -1);
	}

	public XAttributeDiscrete assignMemoryUsed(XEvent event, long memoryUsed) {
		return assign(event, DefinedAttribute.MEMORY_USED, memoryUsed);
	}

	public long extractMemoryTotal(XEvent event) {
		return extract(event, DefinedAttribute.MEMORY_TOTAL, -1);
	}

	public XAttributeDiscrete assignMemoryTotal(XEvent event, long memoryTotal) {
		return assign(event, DefinedAttribute.MEMORY_TOTAL, memoryTotal);
	}

	public double extractMemoryLoad(XEvent event) {
		return extract(event, DefinedAttribute.MEMORY_LOAD, -1.0);
	}

	public XAttributeContinuous assignMemoryLoad(XEvent event, double memoryLoad) {
		return assign(event, DefinedAttribute.MEMORY_LOAD, memoryLoad);
	}


	/*
	 * Helper functions
	 */
	private long extract(XAttributable element,
			DefinedAttribute definedAttribute, long defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeDiscrete) attribute).getValue();
		}
	}

	private XAttributeDiscrete assign(XAttributable element,
			DefinedAttribute definedAttribute, long value) {
		XAttributeDiscrete attr = (XAttributeDiscrete) definedAttribute.prototype
				.clone();
		attr.setValue(value);
		element.getAttributes().put(definedAttribute.key, attr);
		return attr;
	}

	private double extract(XAttributable element,
			DefinedAttribute definedAttribute, double defaultValue) {
		XAttribute attribute = element.getAttributes()
				.get(definedAttribute.key);
		if (attribute == null) {
			return defaultValue;
		} else {
			return ((XAttributeContinuous) attribute).getValue();
		}
	}

	private XAttributeContinuous assign(XAttributable element,
			DefinedAttribute definedAttribute, double value) {
		XAttributeContinuous attr = (XAttributeContinuous) definedAttribute.prototype
				.clone();
		attr.setValue(value);
		element.getAttributes().put(definedAttribute.key, attr);
		return attr;
	}

}
