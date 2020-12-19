/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.editor.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet allows a PDF in the Java's temporary file area to be displayed.
 *
 * The temporary file area is determined by the <code>java.io.tmpdir</code> system property.  
 * Only servlet paths starting with "/tmp" and ending in ".pdf" are acceptable; this is a
 * minimum effort to prevent misuse to snoop the temporary file area.
 */
public class TemporaryFileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        // A minimal security check: only allow filenames that look like PDFs
        if (!req.getServletPath().startsWith("/tmp") || !req.getServletPath().endsWith(".pdf")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Reconstruct the real path to the PDF in the temporary folder
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"));
        File pdfFile = new File(tmpFolder, req.getServletPath().substring("/tmp".length()));

        // Return the file, presumed to be a PDF
        res.setContentType("application/pdf");
        Files.copy(pdfFile.toPath(), res.getOutputStream());
    }
}
