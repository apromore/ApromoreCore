/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See: http://www.gnu.org/licenses/lgpl-3.0
 *
 */
package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.editor.core.layout.YNetLayout;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YNetElement;
import org.yawlfoundation.yawl.elements.YSpecification;

import de.hbrs.orxy.yawl.OryxTestData;
import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.HandlerFactoryImpl;

public class OryxHandlerTest {

    protected OryxConversionContext context;

    public OryxHandlerTest() {
        super();
    }

    @Before
    public void setUp() throws Exception {
        context = new OryxConversionContext();
        context.setHandlerFactory(new HandlerFactoryImpl(new YAWLConversionContext(), context));
    }

    protected void mockParentNet(final BasicShape parentShape) {
        YSpecification specification = new YSpecification("test");
        context.setSpecification(specification);
        YNet net = new YNet(parentShape.getProperty("yawlid"), specification);
        context.addNet(parentShape, net);
        context.getLayout().addNetLayout(new YNetLayout(net, context.getNumberFormat()));
        context.setRootNetID(net.getID());
    }

    protected void mockSubnets() {
        try {
            // Get a list of all Diagrams representing Sub Nets and store them
            // for later use
            JSONArray subDiagramList = OryxTestData.orderFulfillmentAsJson.getJSONArray("subDiagrams");
            for (int i = 0; i < subDiagramList.length(); i++) {
                JSONObject subnetElement = (JSONObject) subDiagramList.get(i);
                BasicDiagram subnetDiagram = BasicDiagramBuilder.parseJson(subnetElement.getJSONObject("diagram"));
                context.addSubnetDiagram(subnetElement.getString("id"), subnetDiagram);
            }
        } catch (JSONException e) {
            // Should not happen if test correct
            throw new RuntimeException(e);
        }
    }

    private BasicDiagram findDiagramByNetId(final String id) throws JSONException {

        JSONArray subDiagrams = OryxTestData.orderFulfillmentAsJson.getJSONArray("subDiagrams");
        for (int i = 0; i < subDiagrams.length(); i++) {
            JSONObject diagramData = subDiagrams.getJSONObject(i);
            if (diagramData.getString("id").equals(id)) {
                return BasicDiagramBuilder.parseJson(diagramData.getJSONObject("diagram"));
            }
        }

        return BasicDiagramBuilder.parseJson(OryxTestData.orderFulfillmentAsJson.getJSONObject("rootDiagram"));
    }

    protected BasicShape findShapeById(final String id, final String netId) {
        try {
            return findDiagramByNetId(netId).getShapeById(id);
        } catch (JSONException e) {
            // Should not happen if test correct
            throw new RuntimeException(e);
        }
    }

    protected YNetElement findOriginalNetElementById(final String id, final String netId) {
        YNet net = (YNet) YAWLTestData.orderFulfillmentSpecification.getDecomposition(netId);
        return net.getNetElement(id);
    }

}