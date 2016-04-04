/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Note that implementing classes of this baseclass should carry the
 * UIImportPlugin annotation
 * 
 * Subclasses of AbstractImportPlugin should use the @Plugin Annotation as
 * follows:
 * 
 * @Plugin( name = "{any name}", parameterLabels={"Filename"}, returnLabels = {
 *          {The right return labels} }, returnTypes = { {The right return
 *          classes} })
 * 
 * 
 * @author bfvdonge
 * 
 */
public abstract class AbstractImportPlugin {

	private File file = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.plugins.abstractplugins.ImportPlugin#getFile()
	 */
	public File getFile() {
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.abstractplugins.ImportPlugin#importFile(org
	 * .processmining.framework.plugin.PluginContext, java.lang.String)
	 */
	public Object importFile(String filename) throws Exception {
		file = new File(filename);
		return importFromStream(new FileInputStream(file), filename, file.length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.abstractplugins.ImportPlugin#importFile(org
	 * .processmining.framework.plugin.PluginContext, java.net.URI)
	 */
	public Object importFile(URI uri) throws Exception {
		return importFromStream(uri.toURL().openStream(), uri.toString(), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.abstractplugins.ImportPlugin#importFile(org
	 * .processmining.framework.plugin.PluginContext, java.net.URL)
	 */
	public Object importFile(URL url) throws Exception {
		file = new File(url.toURI());
		return importFromStream(url.openStream(), url.toString(), 0);
	}              

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.plugins.abstractplugins.ImportPlugin#importFile(org
	 * .processmining.framework.plugin.PluginContext, java.io.File)
	 */
	public Object importFile(File f) throws Exception {
		file = f;
		InputStream stream = getInputStream(f);
		return importFromStream(stream, file.getName(), file.length());
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
		return new FileInputStream(file);
	}

	/**
	 * This method is called by all plug-ins variants to do the actual importing.
	 * 
	 * @param context
	 * @param input
	 * @param filename
	 * @param fileSizeInBytes
	 * @return
	 * @throws Exception
	 */
	public abstract Object importFromStream(InputStream input, String filename,
			long fileSizeInBytes) throws Exception;

}