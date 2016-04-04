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
package de.hbrs.oryx.yawl.converter.handler.yawl;

import java.io.IOException;
import java.util.Iterator;

import org.jdom2.JDOMException;
import org.junit.Before;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YNetElement;

import de.hbrs.orxy.yawl.YAWLTestData;
import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.handler.HandlerFactoryImpl;
import de.hbrs.oryx.yawl.converter.layout.YAWLLayoutConverter;

public abstract class YAWLHandlerTest {

    protected YAWLConversionContext orderFContext;
    protected YAWLConversionContext testContext;

    @Before
    public void setUp() throws JDOMException, IOException {
        orderFContext = new YAWLConversionContext();
        orderFContext.setHandlerFactory(new HandlerFactoryImpl(orderFContext, new OryxConversionContext()));
        new YAWLLayoutConverter(YAWLTestData.orderFulfillmentSource, orderFContext).convertLayout();

        testContext = new YAWLConversionContext();
        testContext.setHandlerFactory(new HandlerFactoryImpl(testContext, new OryxConversionContext()));
        new YAWLLayoutConverter(YAWLTestData.testSource, testContext).convertLayout();
    }

    protected BasicShape findShapeInOrderF(final YNet net, final YNetElement task) {
        Iterator<BasicShape> iterator = orderFContext.getNet(net.getID()).getChildShapesReadOnly().iterator();
        while (iterator.hasNext()) {
            BasicShape child = iterator.next();
            if (child.getProperty("yawlid").equals(task.getID())) {
                return child;
            }
        }
        return null;
    }

    protected BasicShape findShapeInTest(final YNet net, final YNetElement task) {
        Iterator<BasicShape> iterator = testContext.getNet(net.getID()).getChildShapesReadOnly().iterator();
        while (iterator.hasNext()) {
            BasicShape child = iterator.next();
            if (child.getProperty("yawlid").equals(task.getID())) {
                return child;
            }
        }
        return null;
    }

}
