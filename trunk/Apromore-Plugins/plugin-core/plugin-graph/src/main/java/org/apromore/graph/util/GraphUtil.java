package org.apromore.graph.util;

import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GraphUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphUtil.class);
    
    public static String getType(final INode node) {
        String type = null;
        if (node != null) {
            if (node.getNodeType() != null) {
                if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                    type = GraphConstants.FUNCTION;
                } else if (node.getNodeType().equals(NodeTypeEnum.EVENT)) {
                    type = GraphConstants.EVENT;
                } else if (node.getNodeType().equals(NodeTypeEnum.MESSAGE)) {
                    type = GraphConstants.EVENT;
                } else if (node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                    type = GraphConstants.EVENT;
                } else {
                    if (node.getNodeType().equals(NodeTypeEnum.ORJOIN) || node.getNodeType().equals(NodeTypeEnum.XORJOIN) ||
                            node.getNodeType().equals(NodeTypeEnum.ANDJOIN) || node.getNodeType().equals(NodeTypeEnum.ORSPLIT) ||
                            node.getNodeType().equals(NodeTypeEnum.XORSPLIT) || node.getNodeType().equals(NodeTypeEnum.ANDSPLIT) ||
                            node.getNodeType().equals(NodeTypeEnum.STATE)) {
                        type = GraphConstants.CONNECTOR;
                    } else {
                        String nodeName = node.getName();
                        if (nodeName != null && (nodeName.equals("OrJoin") || nodeName.equals("XOrJoin") ||
                                nodeName.equals("AndJoin") || nodeName.equals("OrSplit") || nodeName.equals("XOrSplit") ||
                                nodeName.equals("AndSplit") || nodeName.equals("State"))) {
                            type = GraphConstants.CONNECTOR;
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
    
}
