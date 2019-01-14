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
package org.apromore.common.converters.pnml.context;

import org.apromore.common.converters.pnml.handler.PNMLHandlerFactory;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PNMLConversionContext {

    private PNMLHandlerFactory converterFactory;
    private List<BasicDiagram> pnmlDiagrams;
    private Map<String, BasicShape> shapeMap;


    public PNMLConversionContext() {
        super();
        this.pnmlDiagrams = new ArrayList<>();
        this.shapeMap = new HashMap<>();
    }


    public void addDiagram(BasicDiagram diagram) {
        pnmlDiagrams.add(diagram);
    }

    public BasicDiagram getDiagram(int i) {
        return pnmlDiagrams.get(i);
    }

    public void setConverterFactory(PNMLHandlerFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public PNMLHandlerFactory getConverterFactory() {
        return converterFactory;
    }

    public void addShape(String id, BasicShape shape) {
        shapeMap.put(id, shape);
    }

    public BasicShape getShape(String id) {
        return shapeMap.get(id);
    }

}
