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

