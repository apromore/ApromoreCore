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

/**
 * Copyright (c) 2006
 *
 * Philipp Berger, Martin Czuchra, Gero Decker, Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

import java.io.BufferedWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

/**
 * This servlet allows a PDF in the Java's temporary file area to be displayed.
 *
 * The temporary file area is determined by the <code>java.io.tmpdir</code> system property.
 * Only servlet paths starting with "/tmp" and ending in ".pdf" are acceptable; this is a
 * minimum effort to prevent misuse to snoop the temporary file area.
 */
public class AlternativesRenderer extends HttpServlet {

    private static final long serialVersionUID = 8526319871562210085L;

    private File inFile;
    private File outFile;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
        throws IOException, ServletException {

        String data = new String(req.getParameter("data").getBytes("UTF-8"));

        // create tmp folder
        File tmpFolder = new File(System.getProperty("java.io.tmpdir"));

        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }

        String baseFilename = String.valueOf(System.currentTimeMillis());
        this.inFile = new File(tmpFolder, baseFilename + ".svg");
        this.outFile = new File(tmpFolder, baseFilename + ".pdf");
        log("Real file " + this.outFile);

        try {
            String contextPath = req.getContextPath();
            BufferedWriter out = new BufferedWriter(new FileWriter(inFile));
            out.write(data);
            out.close();
            makePDF(inFile, outFile);
            log("Virtual path " + contextPath + "/tmp/" + baseFilename + ".pdf");
            res.getOutputStream().print(contextPath + "/tmp/" + baseFilename + ".pdf");
        } catch (TranscoderException e) {
            throw new ServletException("Unable to convert SVG to PDF", e);
        }
    }

    protected static void makePDF(final File inFile, final File outFile) throws TranscoderException, IOException {
        try (InputStream in = new FileInputStream(inFile);
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {

            PDFTranscoder transcoder = new PDFTranscoder();
            transcoder.transcode(new TranscoderInput(in), new TranscoderOutput(out));
        }
    }

}
