/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

public class AlternativesRenderer extends HttpServlet {

    private static final long serialVersionUID = 8526319871562210085L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
        throws IOException, ServletException {

        try {
            String svgContent = req.getParameter("data");
            List<List<String>> hyperlinks = parseHyperlinks(req.getParameter("hyperlinks"));
            byte[] pdfByteArray = convertSvgToPdfWithHyperlinks(svgContent, hyperlinks);
            OutputStream os = res.getOutputStream();
            os.write(pdfByteArray);
            os.close();
        } catch (Exception e) {
            // pass
        }
    }

    private static List<List<String>> parseHyperlinks(String encodedHyperlinks) {
        List<List<String>> hyperlinks = new ArrayList<>();
        if (encodedHyperlinks == null || encodedHyperlinks.length() == 0) {
            return hyperlinks;
        }
        try {
            String decodedHyperlinks = URLDecoder.decode(encodedHyperlinks, StandardCharsets.UTF_8.toString());
            List<String> linkList = Arrays.asList(decodedHyperlinks.split(","));
            int linkCount = linkList.size() / 5;
            for (int i = 0; i < linkCount; i++) {
                List<String> linkSet = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    linkSet.add(linkList.get(i * 5 + j));
                }
                hyperlinks.add(linkSet);
            }
        } catch (Exception e) {
            return hyperlinks;
        }
        return hyperlinks;
    }

    private static byte[] convertSvgToPdfWithHyperlinks(
        String svgContent,
        List<List<String>> hyperlinks
    ) throws TranscoderException, IOException {

        TranscoderInput transcoderInput = new TranscoderInput(new ByteArrayInputStream(svgContent.getBytes()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
        PDFTranscoder transcoder = new PDFTranscoder();

        transcoder.transcode(transcoderInput, transcoderOutput);
        byte[] pdfByteArray = outputStream.toByteArray();
        PDDocument document = PDDocument.load(pdfByteArray);
        PDPageTree allPages = document.getDocumentCatalog().getPages();
        PDPage page = allPages.get(0);

        for (List<String> linkSet: hyperlinks) {
            // create a link annotation
            double left = Double.parseDouble(linkSet.get(1));
            double bottom = Double.parseDouble(linkSet.get(2)) - 2;
            double right = Double.parseDouble(linkSet.get(3));
            double top = Double.parseDouble(linkSet.get(4));

            PDRectangle position = new PDRectangle();

            position.setLowerLeftX(Math.round(left));
            position.setLowerLeftY(Math.round(bottom));
            position.setUpperRightX(Math.round(right));
            position.setUpperRightY(Math.round(top));

            PDAnnotationLink txtLink = new PDAnnotationLink();
            txtLink.setRectangle(position);

            PDBorderStyleDictionary linkBorder = new PDBorderStyleDictionary ();
            linkBorder.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
            linkBorder.setWidth(1);
            txtLink.setBorderStyle(linkBorder);

            PDActionURI action = new PDActionURI();
            action.setURI(linkSet.get(0));
            txtLink.setAction(action);
            page.getAnnotations().add(txtLink);
        }

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        document.save(outputStream2);
        return outputStream2.toByteArray();
    }
    
    protected static void makePDF(final File inFile, final File outFile) throws TranscoderException, IOException {
        try (InputStream in = new FileInputStream(inFile);
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {

            PDFTranscoder transcoder = new PDFTranscoder();
            transcoder.transcode(new TranscoderInput(in), new TranscoderOutput(out));
        }
    }

}
