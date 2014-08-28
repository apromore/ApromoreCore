/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.quaestio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.processconfiguration.cmap.CMAP;

/**
 * A Cmap hosted on an Apromore WebDAV store.
 */
public class UrlCmap implements Cmap {

	final URL url;

	public UrlCmap(final String urlString) throws MalformedURLException {
		this.url = new URL(urlString);
	}

	/**
         * @return a configuration mapping
         */
        public CMAP getCMAP() throws IOException, JAXBException {
		URLConnection connection = url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + DatatypeConverter.printBase64Binary("admin:password".getBytes()));
		return (CMAP) JAXBContext.newInstance("com.processconfiguration.cmap").createUnmarshaller().unmarshal(connection.getInputStream());
	}

	/**
         * @return <code>null</code>
         */
	public String getText() {
		return null;
	}
}

