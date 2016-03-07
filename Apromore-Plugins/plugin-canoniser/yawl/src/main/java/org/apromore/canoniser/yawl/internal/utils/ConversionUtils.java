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

package org.apromore.canoniser.yawl.internal.utils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;

/**
 * Helper class for all kind of various static methods and constants
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public final class ConversionUtils {

    private static final int MAX_ITERATION_COUNT = 10000;

    private static final Pattern COLOR_REGEX = Pattern.compile("R:([0-9][0-9][0-9])G:([0-9][0-9][0-9])B:([0-9][0-9][0-9])");

    /**
     * Hidden constructor as this class is not meant to be instantiated
     */
    private ConversionUtils() {
        super();
    }

    public static String convertColorToString(final int colorAsInt) {
        final int rgb = colorAsInt;
        final int red = (rgb >> 16) & 0x0ff;
        final int green = (rgb >> 8) & 0x0ff;
        final int blue = (rgb) & 0x0ff;
        return String.format("R:%dG:%dB:%d", red, green, blue);
    }

    public static BigInteger convertColorToBigInteger(final String colorAsString) {
        final Matcher matcher = COLOR_REGEX.matcher(colorAsString);
        if (matcher.matches()) {
            final int r = Integer.valueOf(matcher.group(1));
            final int g = Integer.valueOf(matcher.group(2));
            final int b = Integer.valueOf(matcher.group(3));
            final int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
            return BigInteger.valueOf(rgb);
        }
        return BigInteger.valueOf(0);
    }

    public static String generateUniqueName(final String originalName, final Set<String> nameSet) {
        int i = 1;
        String newName = originalName + i;
        while (nameSet.contains(newName) && (i < MAX_ITERATION_COUNT)) { // Prevent infinite loops on strange input data
            newName = originalName + (++i);
        }
        return newName;
    }

    public static boolean isCompositeTask(final TaskType task) {
        return task.getSubnetId() != null;
    }

    /**
     * Returns a nicely formatted String with information about a Collection of Nodes
     *
     * @param nodes
     *            Collection of Nodes
     * @return readable String
     */
    public static String nodesToString(final Collection<NodeType> nodes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final Iterator<NodeType> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            final NodeType node = nodeIterator.next();
            sb.append(toString(node));
            if (nodeIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a nicely formatted String with information about a Collection of Edges
     *
     * @param edges
     *            Collection of Edges
     * @return readable String
     */
    public static String edgesToString(final Collection<EdgeType> edges) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        final Iterator<EdgeType> edgeIterator = edges.iterator();
        while (edgeIterator.hasNext()) {
            final EdgeType edge = edgeIterator.next();
            sb.append(toString(edge));
            if (edgeIterator.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Returns a nicely formatted String with information about any Node
     *
     * @param node
     *            NodeType
     * @return readable String
     */
    public static String toString(final NodeType node) {
        final ToStringBuilder sb = new ToStringBuilder(node, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", node.getId()).append("name", node.getName()).toString();
    }

    /**
     * Returns a nicely formatted String with information about a Work Node
     *
     * @param node
     *            NodeType
     * @return readable String
     */
    public static String toString(final WorkType node) {
        final ToStringBuilder sb = new ToStringBuilder(node, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", node.getId()).append("name", node.getName()).append("cancelNode", node.getCancelNodeId())
                .append("cancelEdge", node.getCancelEdgeId()).append("inputExpr", node.getInputExpr()).append("outputExpr", node.getInputExpr())
                .toString();
    }

    /**
     * Returns a nicely formatted String with information about an Edge
     *
     * @param edge
     *            EdgeType
     * @return readable String
     */
    public static String toString(final EdgeType edge) {
        final ToStringBuilder sb = new ToStringBuilder(edge, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("source", edge.getSourceId()).append("target", edge.getTargetId()).append("id", edge.getId()).toString();
    }

    /**
     * Returns a nicely formatted String with information about an Net
     *
     * @param net
     *            NetType
     * @return readable String
     */
    public static String toString(final NetType net) {
        final ToStringBuilder sb = new ToStringBuilder(net, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", net.getId()).append("name", net.getName()).append("nodes", nodesToString(net.getNode()))
                .append("edge", edgesToString(net.getEdge())).toString();
    }

    /**
     * Returns a nicely formatted String with information about a HumanType
     *
     * @param resource
     *            HumanType
     * @return readable String
     */
    public static String toString(final HumanType resource) {
        final ToStringBuilder sb = new ToStringBuilder(resource, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", resource.getId()).append("name", resource.getName()).append("type", resource.getType()).toString();
    }

    /**
     * Returns a nicely formatted String with information about a NonhumanType
     *
     * @param resource
     *            NonhumanType
     * @return readable String
     */
    public static String toString(final NonhumanType resource) {
        final ToStringBuilder sb = new ToStringBuilder(resource, ToStringStyle.MULTI_LINE_STYLE);
        return sb.append("id", resource.getId()).append("name", resource.getName()).append("type", resource.getType()).toString();
    }

}