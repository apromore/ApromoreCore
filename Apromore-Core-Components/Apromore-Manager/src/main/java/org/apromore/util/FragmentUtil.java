/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class FragmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentUtil.class);

    /* Returns the Node Type. */
    public static String getType(final INode node) {
        String type = null;
        if (node != null) {
            if (node.getNodeType() != null) {
                if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                    type = Constants.FUNCTION;
                } else if (node.getNodeType().equals(NodeTypeEnum.EVENT)) {
                    type = Constants.EVENT;
                } else if (node.getNodeType().equals(NodeTypeEnum.MESSAGE)) {
                    type = Constants.EVENT;
                } else if (node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                    type = Constants.EVENT;
                } else {
                    if (node.getNodeType().equals(NodeTypeEnum.ORJOIN) || node.getNodeType().equals(NodeTypeEnum.XORJOIN) ||
                            node.getNodeType().equals(NodeTypeEnum.ANDJOIN) || node.getNodeType().equals(NodeTypeEnum.ORSPLIT) ||
                            node.getNodeType().equals(NodeTypeEnum.XORSPLIT) || node.getNodeType().equals(NodeTypeEnum.ANDSPLIT) ||
                            node.getNodeType().equals(NodeTypeEnum.STATE)) {
                        type = Constants.CONNECTOR;
                    } else {
                        String nodeName = node.getName();
                        if (nodeName != null && (nodeName.equals("OrJoin") || nodeName.equals("XOrJoin") ||
                                nodeName.equals("AndJoin") || nodeName.equals("OrSplit") || nodeName.equals("XOrSplit") ||
                                nodeName.equals("AndSplit") || nodeName.equals("State"))) {
                            type = Constants.CONNECTOR;
                        }
                    }
                }
            } else {
                LOGGER.warn("Unable to determine Node Type, Type is NULL (Could be a Pocket). " + node.getId());
            }
        } else {
            LOGGER.warn("Unable to determine Node Type, Node is NULL. ");
        }
        return type;
    }


    public static List<CPFNode> getPreset(final CPFNode v, final Collection<CPFEdge> es) {
        List<CPFNode> preset = new ArrayList<>();
        if (v != null) {
            for (CPFEdge e: es) {
                if (e.getTarget().getId().equals(v.getId())) {
                    preset.add(e.getSource());
                }
            }
        }
        return preset;
    }

    public static List<CPFNode> getPostset(final CPFNode v, final Collection<CPFEdge> es) {
        List<CPFNode> postset = new ArrayList<>();
        if (v != null) {
            for (CPFEdge e: es) {
                if (e.getSource().getId().equals(v.getId())) {
                    postset.add(e.getTarget());
                }
            }
        }
        return postset;
    }

}
