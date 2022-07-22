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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

public class ImageToPdfRenderer extends HttpServlet {

    private static final long serialVersionUID = 8526319871562210085L;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse res)
        throws IOException, ServletException {

        try {
            Part imagePart = req.getPart("data");
            byte[] pngContent = IOUtils.toByteArray(imagePart.getInputStream());
            int width = Integer.parseInt(
                IOUtils.toString(req.getPart("width").getInputStream(), StandardCharsets.UTF_8)
            );
            int height = Integer.parseInt(
                IOUtils.toString(req.getPart("height").getInputStream(), StandardCharsets.UTF_8)
            );
            List<List<String>> hyperlinks = parseHyperlinks(
                IOUtils.toString(req.getPart("hyperlinks").getInputStream(), StandardCharsets.UTF_8)
            );
            byte[] pdfByteArray = convertImageToPdfWithHyperlinks(width, height, pngContent, hyperlinks);
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

    private static byte[] convertImageToPdfWithHyperlinks(
        int width, int height,
        byte[] pngContent,
        List<List<String>> hyperlinks
    ) throws IOException {

        try (PDDocument doc = new PDDocument()) {
            PDRectangle rectangle = new PDRectangle(width, height);
            PDPage page = new PDPage(rectangle);
            doc.addPage(page);

            PDImageXObject pdImage = PDImageXObject.createFromByteArray(doc, pngContent, null);
            try (PDPageContentStream contentStream = new PDPageContentStream(
                doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                float scale = 1f;
                contentStream.drawImage(pdImage, 0, 0, width * scale, height * scale);
                contentStream.close(); // Must close here so that annotation can be added

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
                doc.save(outputStream2);
                return outputStream2.toByteArray();
            }
        }
    }

}