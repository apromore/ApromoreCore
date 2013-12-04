package com.processconfiguration.quaestio;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import com.processconfiguration.cmap.CMAP;

/**
 * Abstracts away where exactly we're reading our cmaps from.
 */
public interface Cmap {

	/**
         * @return a configuration mapping
         */
        CMAP getCMAP() throws IOException, JAXBException;

	/**
         * @return the filename of the process model, <code>null</code> if the model has no file
         */
	String getText();
}

