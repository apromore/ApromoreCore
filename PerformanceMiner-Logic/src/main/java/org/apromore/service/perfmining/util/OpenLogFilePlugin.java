/*
 * Copyright © 2009-2014 The Apromore Initiative.
 * 
 * This file is part of "Apromore".
 * 
 * "Apromore" is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * "Apromore" is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.perfmining.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XParser;
import org.deckfour.xes.in.XParserRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

public class OpenLogFilePlugin extends AbstractImportPlugin {

	protected Object importFromStream(InputStream input, String filename, long fileSizeInBytes, XFactory factory)
			throws Exception {
		//	System.out.println("Open file");
		XParser parser;
		if (filename.toLowerCase().endsWith(".xes") || filename.toLowerCase().endsWith(".xez")
				|| filename.toLowerCase().endsWith(".xes.gz")) {
			parser = new XesXmlParser(factory);
		} else {
			parser = new XMxmlParser(factory);
		}
		Collection<XLog> logs = null;
		Exception firstException = null;
		String errorMessage = "";
		try {
			//logs = parser.parse(new XContextMonitoredInputStream(input, fileSizeInBytes, context.getProgress()));
			logs = parser.parse(input);
		} catch (Exception e) {
			logs = null;
			firstException = e;
			errorMessage = errorMessage + e;
		}
		if (logs == null) {
			// try any other parser
			for (XParser p : XParserRegistry.instance().getAvailable()) {
				if (p == parser) {
					continue;
				}
				try {
					//logs = p.parse(new XContextMonitoredInputStream(input, fileSizeInBytes, context.getProgress()));
					logs = p.parse(input);
					if (logs.size() > 0) {
						break;
					}
				} catch (Exception e1) {
					// ignore and move on.
					logs = null;
					errorMessage = errorMessage + " [" + p.name() + ":" + e1 + "]";
				}
			}
		}

		// log sanity checks;
		// notify user if the log is awkward / does miss crucial information
		if (logs == null) {
			throw new Exception("Could not open log file, possible cause: " + errorMessage, firstException);
		}
		if (logs.size() == 0) {
			throw new Exception("No processes contained in log!");
		}

		XLog log = logs.iterator().next();
		if (XConceptExtension.instance().extractName(log) == null) {
			/*
			 * Log name not set. Create a default log name.
			 */
			XConceptExtension.instance().assignName(log, "Anonymous log imported from " + filename);
		}

		if (log.isEmpty()) {
			throw new Exception("No process instances contained in log!");
		}

		return log;

	}

	/**
	 * This method returns an inputStream for a file. Note that the default
	 * implementation returns "new FileInputStream(file);"
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	protected InputStream getInputStream(File file) throws Exception {
		FileInputStream stream = new FileInputStream(file);
		if (file.getName().endsWith(".gz") || file.getName().endsWith(".xez")) {
			return new GZIPInputStream(stream);
		}
		if (file.getName().endsWith(".zip")) {
			ZipFile zip = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			ZipEntry zipEntry = entries.nextElement();
			if (entries.hasMoreElements()) {
				throw new InvalidParameterException("Zipped log files should not contain more than one entry.");
			}
			return zip.getInputStream(zipEntry);
		}
		return stream;
	}

	@Override
	public Object importFromStream(InputStream input, String filename, long fileSizeInBytes) throws Exception {
		// TODO Auto-generated method stub
		return importFromStream(input, filename, fileSizeInBytes, XFactoryRegistry.instance().currentDefault());
	}

}
