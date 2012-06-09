/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package org.apromore.common.converters.epml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apromore.common.converters.epml.context.EPMLConversionContext;
import org.apromore.common.converters.epml.handler.epml.EPMLHandlerFactory;
import org.apromore.common.converters.epml.handler.epml.EPMLHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;

import de.epml.TypeEPC;
import de.epml.TypeEPML;

/**
 * Converts a EPML Stream to a Signavio/Oryx JSON Stream
 * 
 * @author Felix Mannhardt
 *
 */
public class EPMLToJSONConverter {

	public static final String EPML_CONTEXT = "de.epml";

	/**
	 * @param epmlStream
	 * @param jsonStream
	 */
	public void convert(InputStream epmlStream, OutputStream jsonStream) {

		try {
			JAXBElement<TypeEPML> nativeElement = unmarshalNativeFormat(epmlStream);
			convertEPML(nativeElement.getValue(), jsonStream);
		} catch (JAXBException e) {
			//TODO Logging
		} catch (JSONException e) {
			//TODO Logging
		} catch (IOException e) {
			//TODO Logging
		}
	}

	/**
	 * 
	 * @param epmlString
	 * @param encoding
	 * @param jsonStream
	 * @throws UnsupportedEncodingException
	 */
	public void convert(String epmlString, String encoding,
			OutputStream jsonStream) throws UnsupportedEncodingException {
		convert(new ByteArrayInputStream(epmlString.getBytes(encoding)),
				jsonStream);
	}
	
	@SuppressWarnings("unchecked")
	private JAXBElement<TypeEPML> unmarshalNativeFormat(InputStream nativeFormat)
			throws JAXBException {
		JAXBContext jc1 = JAXBContext.newInstance(EPML_CONTEXT);
		Unmarshaller u = jc1.createUnmarshaller();
		return (JAXBElement<TypeEPML>) u.unmarshal(nativeFormat);
	}

	private void convertEPML(TypeEPML epml, OutputStream jsonStream)
			throws JSONException, IOException {

		EPMLConversionContext context = new EPMLConversionContext();
		EPMLHandlerFactory converterFactory = new EPMLHandlerFactory(
				context);
		context.setConverterFactory(converterFactory);

		// TODO
		// List<TypeDirectory> directoryList = epml.getDirectory();

		// TODO
		// TypeDefinitions definitions = epml.getDefinitions();

		List<TypeEPC> epcsList = epml.getEpcs();
		for (TypeEPC epc : epcsList) {
			String stencilSetNs = "http://b3mn.org/stencilset/epc#";
			BasicDiagram diagram = new BasicDiagram(epc.getName(), "Diagram",
					new StencilSetReference(stencilSetNs));
			context.addDiagram(diagram);
			for (Object obj : epc.getEventOrFunctionOrRole()) {

				if (obj instanceof JAXBElement<?>) {
					JAXBElement<?> element = (JAXBElement<?>) obj;

					EPMLHandler converter = converterFactory
							.createConverter(element.getValue());
					if (converter != null) {
						diagram.addChildShape(converter.convert());
					}

				}

			}
			for (JAXBElement<?> element : epc
					.getConfigurationRequirementOrConfigurationGuidelineOrConfigurationOrder()) {
				EPMLHandler converter = converterFactory
						.createConverter(element.getValue());
				if (converter != null) {
					diagram.addChildShape(converter.convert());
				}
			}
		}

		writeJson(context, jsonStream);
	}

	private void writeJson(EPMLConversionContext context,
			OutputStream jsonStream) throws JSONException, IOException {
		BasicDiagram diagram = context.getDiagram(0);
		JSONObject jsonDiagram = GenericJSONBuilder.parseModel(diagram);
		OutputStreamWriter outWriter = new OutputStreamWriter(jsonStream,
				"UTF-8");
		jsonDiagram.write(outWriter);
		outWriter.flush();
	}

}
