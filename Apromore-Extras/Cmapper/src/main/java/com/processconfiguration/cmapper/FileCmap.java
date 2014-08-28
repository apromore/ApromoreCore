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

package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

// Local classes
import com.processconfiguration.cmap.CMAP;

/**
 * Configuration mapping stored on the local filesystem.
 */
public class FileCmap implements Cmap {

    private CMAP cmap;
    private File file;

    /**
     * Sole constructor.
     */
    public FileCmap(File file) throws Exception {
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    public CMAP getCmap() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Unmarshaller u = jc.createUnmarshaller();
        this.cmap = (CMAP) u.unmarshal(file);

        return cmap;
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream() throws Exception {
        return new FileOutputStream(file);
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        return file.toURI();
    }
}

