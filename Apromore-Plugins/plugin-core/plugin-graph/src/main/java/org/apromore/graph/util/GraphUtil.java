/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.graph.util;

import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GraphUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphUtil.class);

    /* Only here so people can't instantiate this class */
    private GraphUtil() { }

    /**
     * Finds out the type of this node.
     * @param node returns the type of this node.
     * @return the type of the node.
     */
    public static String getType(final INode node) {
        String type = null;
        if (node != null) {
            if (node.getNodeType() != null) {
                if (isTask(node.getNodeType())) {
                    type = GraphConstants.FUNCTION;
                } else if (isEvent(node.getNodeType())) {
                    type = GraphConstants.EVENT;
                } else if (isMessage(node.getNodeType())) {
                    type = GraphConstants.EVENT;
                } else if (isTimer(node.getNodeType())) {
                    type = GraphConstants.EVENT;
                } else if (isJoin(node.getNodeType(), node.getName()) || isSplit(node.getNodeType(), node.getName()) ||
                        isState(node.getNodeType(), node.getName())) {
                    type = GraphConstants.CONNECTOR;
                } else {
                    LOGGER.warn("Unable to determine Node Type, Type is UNKNOWN. " + node.getId());
                }
            } else {
                LOGGER.warn("Unable to determine Node Type, Type is NULL (Could be a Pocket). " + node.getId());
            }
        } else {
            LOGGER.warn("Unable to determine Node Type, Node is NULL. ");
        }
        return type;
    }

    private static boolean isTask(NodeTypeEnum nodeType) {
        return (nodeType.equals(NodeTypeEnum.TASK));
    }

    private static boolean isEvent(NodeTypeEnum nodeType) {
        return (nodeType.equals(NodeTypeEnum.EVENT));
    }

    private static boolean isMessage(NodeTypeEnum nodeType) {
        return (nodeType.equals(NodeTypeEnum.MESSAGE));
    }

    private static boolean isTimer(NodeTypeEnum nodeType) {
        return (nodeType.equals(NodeTypeEnum.TIMER));
    }

    private static boolean isJoin(NodeTypeEnum nodeType, String nodeName) {
        return (isOrJoin(nodeType, nodeName) || isXOrJoin(nodeType, nodeName) || isAndJoin(nodeType, nodeName));
    }

    private static boolean isSplit(NodeTypeEnum nodeType, String nodeName) {
        return (isOrSplit(nodeType, nodeName) || isXOrSplit(nodeType, nodeName) || isAndSplit(nodeType, nodeName));
    }

    private static boolean isState(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.STATE) || (nodeName != null && nodeName.equals("State"));
    }

    private static boolean isOrJoin(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.ORJOIN) || (nodeName != null && nodeName.equals("OrJoin"));
    }

    private static boolean isXOrJoin(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.XORJOIN) || (nodeName != null && nodeName.equals("XOrJoin"));
    }

    private static boolean isAndJoin(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.ANDJOIN) || (nodeName != null && nodeName.equals("AndJoin"));
    }

    private static boolean isOrSplit(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.ORSPLIT) || (nodeName != null && nodeName.equals("OrSplit"));
    }

    private static boolean isXOrSplit(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.XORSPLIT) || (nodeName != null && nodeName.equals("XOrSplit"));
    }

    private static boolean isAndSplit(NodeTypeEnum nodeType, String nodeName) {
        return nodeType.equals(NodeTypeEnum.ANDSPLIT) || (nodeName != null && nodeName.equals("AndSplit"));
    }
}
