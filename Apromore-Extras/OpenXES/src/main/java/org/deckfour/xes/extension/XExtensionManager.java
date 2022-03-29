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
package org.deckfour.xes.extension;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import org.deckfour.xes.extension.std.XArtifactLifecycleExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XCostExtension;
import org.deckfour.xes.extension.std.XIdentityExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XMicroExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XSemanticExtension;
import org.deckfour.xes.extension.std.XSoftwareCommunicationExtension;
import org.deckfour.xes.extension.std.XSoftwareEventExtension;
import org.deckfour.xes.extension.std.XSoftwareTelemetryExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.util.XRuntimeUtils;

/**
 * The extension manager is used to access, store, and manage extensions in a
 * system. Extensions can be loaded from their given URI, which should point to
 * the file defining the extension. Also, extensions can be registered locally,
 * which then override any remotely-loaded extensions (which are more generic
 * placeholders).
 * 
 * Extension files downloaded from remote sources (which happens when the
 * extension cannot be resolved locally) are cached on the local system, so that
 * the network source of extension files is not put under extensive stress.
 * 
 * The extension manager is a singleton, there is no need to instantiate more
 * than one extension manager, which is necessary to avoid states of
 * inconsistency.
 * 
 * @author Christian W. Guenther (christian@deckfour.org)
 * 
 */
public class XExtensionManager {

	/**
	 * Maximal time for caching remotely-defined extensions in milliseconds. The
	 * default for this value is 30 days.
	 */
	public static final long MAX_CACHE_MILLIS = 2592000000L; // = 30 * 24 * 60 *
																// 60 * 1000;

	/**
	 * Singleton instance of the system-wide extension manager.
	 */
	private static XExtensionManager singleton = new XExtensionManager();

	/**
	 * Accesses the singleton instance of the extension manager.
	 * 
	 * @return Singleton extension manager.
	 */
	public static XExtensionManager instance() {
		return singleton;
	}

	/**
	 * Map storing all extensions currently registered, indexed by their unique
	 * URI.
	 */
	private UnifiedMap<URI, XExtension> extensionMap;
	/**
	 * List mapping each extension currently registered to a unique index.
	 */
	private ArrayList<XExtension> extensionList;

	/**
	 * Creates a new extension manager instance (hidden constructor)
	 */
	private XExtensionManager() {
		extensionMap = new UnifiedMap<URI, XExtension>();
		extensionList = new ArrayList<XExtension>();
		registerStandardExtensions();
		// loadExtensionCache();
	}

	/**
	 * Explicitly registers an extension instance with the extension manager.
	 * 
	 * @param extension
	 *            The extension to be registered.
	 */
	public void register(XExtension extension) {
		extensionMap.put(extension.getUri(), extension);
		// replace the registered index in the list with the new extension.
		int i = extensionList.indexOf(extension);
		if (i < 0) {
			extensionList.add(extension);
		} else {
			extensionList.remove(i);
			extensionList.add(i, extension);
		}
	}

	/**
	 * Retrieves an extension instance by its unique URI. If the extension has
	 * not been registered before, it is looked up in the local cache. If it
	 * cannot be found in the cache, the manager attempts to download it from
	 * its unique URI, and add it to the set of managed extensions.
	 * 
	 * @param uri
	 *            The unique URI of the requested extension.
	 * @return The requested extension.
	 */
	public XExtension getByUri(URI uri) {
		XExtension extension = extensionMap.get(uri);
		// if (extension == null) {
		// try {
		// extension = XExtensionParser.instance().parse(uri);
		// register(extension);
		// XLogging.log("Imported XES extension '" + extension.getUri()
		// + "' from remote source", XLogging.Importance.DEBUG);
		// } catch (IOException e) {
		// // Now do something if the Internet is down...
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// return null;
		// }
		// cacheExtension(uri);
		// }
		return extension;
	}

	/**
	 * Retrieves an extension by its name. If no extension by that name can be
	 * found, this method returns <code>null</code>.
	 * 
	 * @param name
	 *            The name of the requested extension.
	 * @return The requested extension (may be <code>null</code>, if it cannot
	 *         be found).
	 */
	public XExtension getByName(String name) {
		for (XExtension ext : extensionList) {
			if (ext.getName().equals(name)) {
				return ext;
			}
		}
		return null;
	}

	/**
	 * Retrieves an extension by its prefix. If no extension by that prefix can
	 * be found, this method returns <code>null</code>.
	 * 
	 * @param prefix
	 *            The prefix of the requested extension.
	 * @return The requested extension (may be <code>null</code>, if it cannot
	 *         be found).
	 */
	public XExtension getByPrefix(String prefix) {
		for (XExtension ext : extensionList) {
			if (ext.getPrefix().equals(prefix)) {
				return ext;
			}
		}
		return null;
	}

	/**
	 * Retrieves an extension by ints index. If no extension with the given
	 * index is found, this method returns <code>null</code>.
	 * 
	 * @param index
	 *            The index of the requested extension.
	 * @return The requested extension (may be <code>null</code>, if it cannot
	 *         be found).
	 */
	public XExtension getByIndex(int index) {
		if (index < 0 || index >= extensionList.size()) {
			return null;
		}
		return extensionList.get(index);
	}

	/**
	 * Resolves the index of an extension, given that this extension has been
	 * previously registered with this manager instance. If the given index has
	 * not been registered previously, this method returns <code>-1</code>.
	 * 
	 * @param extension
	 *            The extension to look up the index for.
	 * @return Unique index of the requested extension (positive integer).
	 */
	public int getIndex(XExtension extension) {
		for (int i = 0; i < extensionList.size(); i++) {
			if (extensionList.get(i).equals(extension)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Registers all defined standard extensions with the extension manager
	 * before caching.
	 */
	protected void registerStandardExtensions() {
		register(XConceptExtension.instance());
		register(XCostExtension.instance());
		register(XIdentityExtension.instance());
		register(XLifecycleExtension.instance());
		// FIXME: BvD: Removed this extension as it breaks ProMLite
		// register(XMicroExtension.instance());
		register(XMicroExtension.instance());
		register(XOrganizationalExtension.instance());
		register(XSemanticExtension.instance());
		register(XSoftwareCommunicationExtension.instance());
		register(XSoftwareEventExtension.instance());
		register(XSoftwareTelemetryExtension.instance());
		register(XTimeExtension.instance());
		register(XArtifactLifecycleExtension.instance());
	}

	/**
	 * Downloads and caches an extension from its remote definition file. The
	 * extension is subsequently placed in the local cache, so that future
	 * loading is accelerated.
	 * 
	 * @param uri
	 *            Unique URI of the extension which is to be cached.
	 */
	protected void cacheExtension(URI uri) {
		// extract extension file name from URI
		String uriStr = uri.toString().toLowerCase();
		if (uriStr.endsWith("/")) {
			uriStr = uriStr.substring(0, uriStr.length() - 1);
		}
		String fileName = uriStr.substring(uriStr.lastIndexOf('/'));
		if (fileName.endsWith(".xesext") == false) {
			fileName += ".xesext";
		}
		File cacheFile = new File(XRuntimeUtils.getExtensionCacheFolder()
				.getAbsolutePath() + File.separator + fileName);
		// download extension file to cache directory
		try (BufferedInputStream bis = new BufferedInputStream(uri.toURL()
                                        .openStream())) {
			byte[] buffer = new byte[1024];
			cacheFile.createNewFile();
			try (BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(cacheFile))) {
			    int read = bis.read(buffer);
			    while (read >= 0) {
				bos.write(buffer, 0, read);
				read = bis.read(buffer);
			    }
			    bos.flush();
			}
			XLogging.log("Cached XES extension '" + uri + "'",
					XLogging.Importance.DEBUG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all extensions stored in the local cache. Cached extensions which
	 * exceed the maximum caching age are discarded, and downloaded freshly.
	 */
	protected void loadExtensionCache() {
		// threshold for discarding old cache files
		long minModified = System.currentTimeMillis() - MAX_CACHE_MILLIS;
		File extFolder = XRuntimeUtils.getExtensionCacheFolder();
		XExtension extension;
		File[] extFiles = extFolder.listFiles();
		if (extFiles == null) {
			// Extension folder may be non-existant for virtual users with no
			// home directory
			XLogging.log(
					"Extension caching disabled (Could not access cache directory)!",
					XLogging.Importance.WARNING);
			return;
		}
		for (File extFile : extFiles) {
			if (extFile.getName().toLowerCase().endsWith(".xesext") == false) {
				// no real extension
				continue;
			}
			if (extFile.lastModified() < minModified) {
				// remove outdated cache files
				if (extFile.delete() == false) {
					extFile.deleteOnExit();
				}
			} else {
				// load extension file
				try {
					extension = XExtensionParser.instance().parse(extFile);
					if (extensionMap.containsKey((extension).getUri()) == false) {
						extensionMap.put(extension.getUri(), extension);
						extensionList.add(extension);
						XLogging.log(
								"Loaded XES extension '" + extension.getUri()
										+ "' from cache",
								XLogging.Importance.DEBUG);
					} else {
						XLogging.log("Skipping cached XES extension '"
								+ extension.getUri() + "' (already defined)",
								XLogging.Importance.DEBUG);
					}
				} catch (Exception e) {
					// ignore bad apples for now
					e.printStackTrace();
				}
			}
		}

	}

}
