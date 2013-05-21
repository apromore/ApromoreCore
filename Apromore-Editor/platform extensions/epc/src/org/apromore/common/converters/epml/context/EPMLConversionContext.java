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
package org.apromore.common.converters.epml.context;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.common.converters.epml.handler.epml.EPMLHandlerFactory;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;

public class EPMLConversionContext {

    private EPMLHandlerFactory converterFactory;
    private List<BasicDiagram> epcDiagrams;
    private Map<BigInteger, BasicShape> shapeMap;


    public EPMLConversionContext() {
        super();
        this.epcDiagrams = new ArrayList<>();
        this.shapeMap = new HashMap<>();
    }


    public void addDiagram(BasicDiagram diagram) {
        epcDiagrams.add(diagram);
    }

    public BasicDiagram getDiagram(int i) {
        return epcDiagrams.get(i);
    }

    public void setConverterFactory(EPMLHandlerFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public EPMLHandlerFactory getConverterFactory() {
        return converterFactory;
    }

    public void addShape(BigInteger id, BasicShape shape) {
        shapeMap.put(id, shape);
    }

    public BasicShape getShape(BigInteger id) {
        return shapeMap.get(id);
    }

}
