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

package org.apromore.common.converters.pnml;

import de.hpi.diagram.SignavioUUID;
import org.apromore.common.converters.pnml.context.PNMLConversionContext;
import org.apromore.common.converters.pnml.handler.PNMLHandler;
import org.apromore.common.converters.pnml.handler.PNMLHandlerFactory;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.NetType;
import org.apromore.pnml.PNMLSchema;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.PnmlType;
import org.apromore.pnml.TransitionType;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Takes the pnml data and creates a PNML object and converts it into JSON for the editor to visualise.
 *
 * @author Cameron James
 */
public class PNMLToJSONConverter {


    /**
     * Does the conversion from pnml to json.
     * @param pnmlData   the pnml data
     * @param jsonStream the output stream.
     */
    public void convert(InputStream pnmlData, OutputStream jsonStream) {
        try {
            JAXBElement<PnmlType> nativeElement = unmarshalNativeFormat(pnmlData);
            convertPNML(nativeElement.getValue(), jsonStream);
        } catch (JAXBException | JSONException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void convert(final String pnmlData, final String encoding, final OutputStream jsonStream) throws UnsupportedEncodingException {
        convert(new ByteArrayInputStream(pnmlData.getBytes(encoding)), jsonStream);
    }

    @SuppressWarnings("unchecked")
    private JAXBElement<PnmlType> unmarshalNativeFormat(final InputStream nativeFormat) throws JAXBException, SAXException {
        return PNMLSchema.unmarshalPNMLFormat(nativeFormat, false);
    }

    /* Convert the PNML to JSON. */
    private void convertPNML(PnmlType pnmlNet, OutputStream jsonStream) throws JSONException, IOException {
        PNMLConversionContext context = new PNMLConversionContext();
        PNMLHandlerFactory converterFactory = new PNMLHandlerFactory(context);
        context.setConverterFactory(converterFactory);

        String stencilSetNs = "http://b3mn.org/stencilset/petrinet#";
        for (NetType net : pnmlNet.getNet()) {
            BasicDiagram diagram = new BasicDiagram(SignavioUUID.generate(), net.getId(), new StencilSetReference(stencilSetNs));

            Bounds bounds = new Bounds();
            bounds.setCoordinates(0, 0, 600, 600);
            diagram.setBounds(bounds);
            if (net.getName() != null) {
                diagram.setProperty("title", net.getName().getText());
            } else {
                diagram.setProperty("title", "noName");
            }

            context.addDiagram(diagram);

            for (TransitionType transition : net.getTransition()) {
                PNMLHandler converter = converterFactory.createNodeConverter(transition);
                if (converter != null) {
                    diagram.addChildShape(converter.convert());
                }
            }
            for (PlaceType place : net.getPlace()) {
                PNMLHandler converter = converterFactory.createNodeConverter(place);
                if (converter != null) {
                    diagram.addChildShape(converter.convert());
                }
            }
            for (ArcType flow : net.getArc()) {
                PNMLHandler converter = converterFactory.createEdgeConverter(flow);
                if (converter != null) {
                    diagram.addChildShape(converter.convert());
                }
            }
        }

        //PetriNetLayouter layouter = new PetriNetLayouter(pnmlNet);
        //layouter.layout();

        writeJson(context, jsonStream);
    }

    /* Convert the basic diagram to json in an output stream. */
    private void writeJson(final PNMLConversionContext context, final OutputStream jsonStream) throws JSONException, IOException {
        BasicDiagram diagram = context.getDiagram(0);
        assert diagram != null;
        JSONObject jsonDiagram = GenericJSONBuilder.parseModel(diagram);
        OutputStreamWriter outWriter = new OutputStreamWriter(jsonStream, "UTF-8");
        jsonDiagram.write(outWriter);
        outWriter.flush();
    }




    /**
     * Command line filter converting a PNML-formatted standard input stream into a Signavio JSON-formatted standard output stream.
     *
     * @param args  first argument names the input file
     * @throws java.io.FileNotFoundException  if <code>args[0]</code> isn't the name of a file
     */
    public static void main(String args[]) throws FileNotFoundException {
        new PNMLToJSONConverter().convert(new FileInputStream(args[0]), System.out);
    }
}
