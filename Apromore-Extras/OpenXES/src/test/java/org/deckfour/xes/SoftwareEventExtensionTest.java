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
package org.deckfour.xes;
import java.util.Date;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XSoftwareEventExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

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

/**
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class SoftwareEventExtensionTest {

	private XFactory factory;
	private XConceptExtension conceptExt = XConceptExtension.instance();
	private XLifecycleExtension lifecycleExt = XLifecycleExtension.instance();
	private XTimeExtension timeExt = XTimeExtension.instance();
	private XSoftwareEventExtension swEventExt = XSoftwareEventExtension
			.instance();

	public XLog createExampleLog() {
		factory = XFactoryRegistry.instance().currentDefault();
		XLog log = factory.createLog();

		log.getExtensions().add(conceptExt);
		log.getExtensions().add(lifecycleExt);
		log.getExtensions().add(timeExt);
		log.getExtensions().add(swEventExt);

		swEventExt.assignHasData(log, true);
		swEventExt.assignHasException(log, true);

		XTrace trace = factory.createTrace();
		trace.add(createEventCallAf());
		trace.add(createEventCallingBg());
		log.add(trace);

		return log;
	}

	private XEvent createEventCallAf() {
		XEvent event = factory.createEvent();
		conceptExt.assignName(event, "demo.A.f(int)");
		lifecycleExt.assignTransition(event,
				XLifecycleExtension.StandardModel.START.getEncoding());
		timeExt.assignTimestamp(event, new Date("2017-06-15T10:02:30.287Z"));
		swEventExt.assignType(event,
				XSoftwareEventExtension.SoftwareEventType.CALL);
		swEventExt.assignCalleePackage(event, "demo");
		swEventExt.assignCalleeClass(event, "A");
		swEventExt.assignCalleeMethod(event, "f");
		swEventExt.assignCalleeParamSig(event, "(int)");
		swEventExt.assignCalleeReturnSig(event, "void");
		swEventExt.assignCalleeIsConstructor(event, false);
		swEventExt.assignCalleeInstanceId(event, "1074593562");
		swEventExt.assignCalleeFilename(event, "source/A.java");
		swEventExt.assignCalleeLineNr(event, 2);
		{
			XAttributeList params = swEventExt.assignParams(event);
			swEventExt.assignValueType(swEventExt.addParamValue(params, "0"),
					"int");
		}
		swEventExt.assignAppName(event, "Demo");
		swEventExt.assignThreadId(event, "1");
		swEventExt.assignNanotime(event, 493674332622147L);
		return event;
	}

	private XEvent createEventCallingBg() {
		XEvent event = factory.createEvent();
		conceptExt.assignName(event, "demo.B.g(int,int)");
		lifecycleExt.assignTransition(event,
				XLifecycleExtension.StandardModel.START.getEncoding());
		timeExt.assignTimestamp(event, new Date("2017-06-15T10:02:30.287Z"));
		swEventExt.assignType(event,
				XSoftwareEventExtension.SoftwareEventType.CALLING);
		swEventExt.assignCalleePackage(event, "demo");
		swEventExt.assignCalleeClass(event, "B");
		swEventExt.assignCalleeMethod(event, "g");
		swEventExt.assignCalleeParamSig(event, "(int,int)");
		swEventExt.assignCalleeReturnSig(event, "int");
		swEventExt.assignCalleeIsConstructor(event, false);
		swEventExt.assignCalleeInstanceId(event, "660017404");
		swEventExt.assignCalleeFilename(event, "source/B.java");
		swEventExt.assignCalleeLineNr(event, 13);
		swEventExt.assignCallerPackage(event, "demo");
		swEventExt.assignCallerClass(event, "A");
		swEventExt.assignCallerMethod(event, "f");
		swEventExt.assignCallerParamSig(event, "(int)");
		swEventExt.assignCallerReturnSig(event, "void");
		swEventExt.assignCallerIsConstructor(event, false);
		swEventExt.assignCallerInstanceId(event, "1074593562");
		swEventExt.assignCallerFilename(event, "source/A.java");
		swEventExt.assignCallerLineNr(event, 5);
		{
			XAttributeList params = swEventExt.assignParams(event);
			swEventExt.assignValueType(swEventExt.addParamValue(params, "12"),
					"int");
			swEventExt.assignValueType(swEventExt.addParamValue(params, "0"),
					"int");
		}
		swEventExt.assignAppName(event, "Demo");
		swEventExt.assignThreadId(event, "1");
		swEventExt.assignNanotime(event, 493674332823571L);
		return event;
	}

	private XEvent createEventThrowsBg() {
		XEvent event = factory.createEvent();
		conceptExt.assignName(event, "demo.B.g(int,int)");
		lifecycleExt.assignTransition(event,
				XLifecycleExtension.StandardModel.ATE_ABORT.getEncoding());
		timeExt.assignTimestamp(event, new Date("2017-06-15T10:02:30.287Z"));
		swEventExt.assignType(event,
				XSoftwareEventExtension.SoftwareEventType.THROWS);
		swEventExt.assignCalleePackage(event, "demo");
		swEventExt.assignCalleeClass(event, "B");
		swEventExt.assignCalleeMethod(event, "g");
		swEventExt.assignCalleeParamSig(event, "(int,int)");
		swEventExt.assignCalleeReturnSig(event, "int");
		swEventExt.assignCalleeIsConstructor(event, false);
		swEventExt.assignCalleeInstanceId(event, "1074593562");
		swEventExt.assignCalleeFilename(event, "source/B.java");
		swEventExt.assignCalleeLineNr(event, 15);
		swEventExt.assignAppName(event, "Demo");
		swEventExt.assignThreadId(event, "1");
		swEventExt.assignNanotime(event, 493674332936044L);
		swEventExt.assignExThrown(event, "ArithmeticException");
		return event;
	}

	private XEvent createEventHandleInAf() {
		XEvent event = factory.createEvent();
		conceptExt.assignName(event, "demo.A.f(int)");
		lifecycleExt.assignTransition(event,
				XLifecycleExtension.StandardModel.REASSIGN.getEncoding());
		timeExt.assignTimestamp(event, new Date("2017-06-15T10:02:30.287Z"));
		swEventExt.assignType(event,
				XSoftwareEventExtension.SoftwareEventType.HANDLE);
		swEventExt.assignCalleePackage(event, "demo");
		swEventExt.assignCalleeClass(event, "A");
		swEventExt.assignCalleeMethod(event, "f");
		swEventExt.assignCalleeParamSig(event, "(int)");
		swEventExt.assignCalleeReturnSig(event, "void");
		swEventExt.assignCalleeIsConstructor(event, false);
		swEventExt.assignCalleeInstanceId(event, "1074593562");
		swEventExt.assignCalleeFilename(event, "source/A.java");
		swEventExt.assignCalleeLineNr(event, 7);
		swEventExt.assignAppName(event, "Demo");
		swEventExt.assignThreadId(event, "1");
		swEventExt.assignNanotime(event, 493674333039536L);
		swEventExt.assignExThrown(event, "ArithmeticException");
		swEventExt.assignExCaught(event, "Exception");
		return event;
	}

	private XEvent createEventReturnAf() {
		XEvent event = factory.createEvent();
		conceptExt.assignName(event, "demo.A.f(int)");
		lifecycleExt.assignTransition(event,
				XLifecycleExtension.StandardModel.COMPLETE.getEncoding());
		timeExt.assignTimestamp(event, new Date("2017-06-15T10:02:30.287Z"));
		swEventExt.assignType(event,
				XSoftwareEventExtension.SoftwareEventType.RETURN);
		swEventExt.assignCalleePackage(event, "demo");
		swEventExt.assignCalleeClass(event, "A");
		swEventExt.assignCalleeMethod(event, "f");
		swEventExt.assignCalleeParamSig(event, "(int)");
		swEventExt.assignCalleeReturnSig(event, "void");
		swEventExt.assignCalleeIsConstructor(event, false);
		swEventExt.assignCalleeInstanceId(event, "1074593562");
		swEventExt.assignCalleeFilename(event, "source/A.java");
		swEventExt.assignCalleeLineNr(event, 10);
		swEventExt.assignValueType(swEventExt.assignReturnValue(event, ""),
				"void");
		swEventExt.assignAppName(event, "Demo");
		swEventExt.assignThreadId(event, "1");
		swEventExt.assignNanotime(event, 493674333162700L);
		return event;
	}

}
