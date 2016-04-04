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
package de.hbrs.oryx.yawl.util;

import java.util.List;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCondition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YNetElement;
import org.yawlfoundation.yawl.util.JDOMUtil;

import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;

public abstract class YAWLUtils {

    /**
     * Date Format used in whole conversion. Can't store a DateFormat here, because it is not synchronized! Just use like this: <br />
     * new SimpleDateFormat(YAWLUtils.DATE_FORMAT)
     */
    public static final String DATE_FORMAT = "MM/dd/yy";

    /**
     * YAWL XML Namespace
     */
    public static final String YAWL_NS = "http://www.yawlfoundation.org/yawlschema";

    /**
     * There are always implicit conditions between tasks in YAWL. Those conditions are not visible and therefore this method returns false on them.
     * 
     * @param yElement
     *            any YAWL element
     * @return true if element is a invisible condition
     */
    static public boolean isElementVisible(final YNetElement yElement) {
        return !(yElement instanceof YCondition) || (yElement instanceof YCondition) && (!((YCondition) yElement).isImplicit()); // Explicit
    }

    /**
     * Returns the next visible task or condition of a YAWL flow.
     * 
     * @param yFlow
     * @return
     */
    static public YExternalNetElement getNextVisibleElement(final YFlow yFlow) {
        if (isElementVisible(yFlow.getNextElement())) {
            return yFlow.getNextElement();
        } else {
            // There is a invisible condition with exact one successor
            return yFlow.getNextElement().getPostsetElements().iterator().next();
        }
    }

    static public Document parseToElement(final String source) throws ConversionException {
        // Use the YAWL method to parse String
        Document doc = JDOMUtil.stringToDocument(source);
        if (doc != null) {
            return doc;
        }
        throw new ConversionException("Could not parse " + source);
    }

    static public String elementToString(final List<Element> list) {
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        return out.outputString(list);
    }

    static public String elementToString(final Element el) {
        return JDOMUtil.elementToString(el);
    }

    /**
     * Checks if there is a YAWL ID (yawlid) property set either by the user or by a former import. If there is no such ID, a unique ID is generated
     * and both returned and added to the properties of the supplied Shape.
     * 
     * @param shape
     *            to get the yawlId property from
     * @return either a UUID or a manually specified ID
     */
    public static String convertYawlId(final YNet net, final BasicShape shape) {
        String yawlId = shape.getProperty("yawlid");
        boolean hasRewrittenYawlId = shape.hasProperty("hasChangedYawlId") ? shape.getPropertyBoolean("hasChangedYawlId") : false;
        if (!hasRewrittenYawlId) {
            if (yawlId == null || yawlId.isEmpty()) {
                // Generate a globally unique identifier
                return updateYawlId(shape, convertYawlId());
            } else {
                // Prefix with Net ID
                if (net != null) {
                    return updateYawlId(shape, net.getID() + "-" + yawlId);
                } else {
                    // Element is expected to have a unique Name!
                    return updateYawlId(shape, yawlId);
                }
            }
        } else {
            return yawlId;
        }
    }

    public static String convertYawlId(final BasicShape shape) {
        return convertYawlId(null, shape);
    }

    /**
     * Generates a new random YAWL ID
     * 
     * @return a UUID
     */
    public static String convertYawlId() {
        return "id" + UUID.randomUUID().toString();
    }

    private static String updateYawlId(final BasicShape shape, final String uuid) {
        shape.setProperty("yawlid", uuid);
        shape.setProperty("hasChangedYawlId", true);
        return uuid;
    }

}
