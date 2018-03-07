/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.canoniser.yawl.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.graph.abs.IEdge;
import org.jbpt.hypergraph.abs.AbstractMultiHyperGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.utils.DotSerializer;

public class GraphvizVisualiser {

    public static final String DEFAULT_GRAPHVIZ_DEFAULT_PATH = "dot";
    public static final String DEFAULT_GRAPHVIZ_WINDOWS_PATH = "C://Program Files (x86)//Graphviz 2.28//bin//dot.exe";
    public static final String DEFAULT_GRAPHVIZ_LINUX_PATH = "/usr/bin/dot";

    public GraphvizVisualiser() {
        super();
    }

    public void createImageAsPNG(final NetType net, final boolean withLabels, final File file) throws IOException {
        final String[] args = new String[] { getDOTPath(), "-Eshape=normal", "-Nshape=rectangle", "-Tpng" };
        final ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectOutput(file);
        pb.redirectErrorStream(false);
        final Process dotProcess = pb.start();
        createImageAsDOT(net, withLabels, new BufferedOutputStream(dotProcess.getOutputStream()));
        while (dotProcess.getErrorStream().read() != -1) {
            // No Op
        }
        try {
            dotProcess.waitFor();
        } catch (final InterruptedException e) {
            Thread.interrupted();
        }
    }

    public void createImageAsPNG(final NetType net, final boolean withLabels, final OutputStream output) throws IOException {
        final File tempFile = File.createTempFile(net.getId(), null);
        createImageAsPNG(net, withLabels, tempFile);
        final FileInputStream fileInputStream = new FileInputStream(tempFile);
        try {
            final FileChannel source = fileInputStream.getChannel();
            final WritableByteChannel target = Channels.newChannel(output);
            source.transferTo(0, source.size(), target);
        } finally {
            fileInputStream.close();
        }
    }

    private String getDOTPath() throws IOException {
        if (DEFAULT_GRAPHVIZ_DEFAULT_PATH != null) {
            if (new File(DEFAULT_GRAPHVIZ_DEFAULT_PATH).exists()) {
                return DEFAULT_GRAPHVIZ_DEFAULT_PATH;
            }
        }

        if (DEFAULT_GRAPHVIZ_LINUX_PATH != null) {
            if (new File(DEFAULT_GRAPHVIZ_LINUX_PATH).exists()) {
                return DEFAULT_GRAPHVIZ_LINUX_PATH;
            }
        }

        if (DEFAULT_GRAPHVIZ_WINDOWS_PATH != null) {
            if (new File(DEFAULT_GRAPHVIZ_WINDOWS_PATH).exists()) {
                return DEFAULT_GRAPHVIZ_WINDOWS_PATH;
            }
        }

        throw new IOException("Can not find Graphviz binary!");
    }

    public void createImageAsDOT(final NetType net, final boolean withLabels, final OutputStream os) {
        try (PrintWriter writer = new PrintWriter(os)) {
            writer.write(createImageAsDOT(net, withLabels));
            writer.flush();
            writer.close();
        }
    }

    public String createImageAsDOT(final NetType net, final boolean withLabels) {
        final DirectedGraph graph = createGraph(net, withLabels);
        return new DotSerializer(new DotSerializer.GraphDecorator() {

            @Override
            public String decorate(final AbstractMultiHyperGraph<?, ?> graph) {
                return "rankdir=\"LR\";\n" + "node [style=\"n\"];\n" + "edge [style=\"e\",lblstyle=\"auto\"];\n";
            }
        }, new DotSerializer.NodeDecorator() {

            @Override
            public String decorate(final IVertex vertex) {
                final NodeType node = (NodeType) vertex.getTag();
                return "style=\"n"+node.getClass().getSimpleName()+"\"";
            }
        }, new DotSerializer.EdgeDecorator() {

            @Override
            public String decorate(final IEdge<?> edge) {
                final EdgeType edgeType = (EdgeType) edge.getTag();
                if (edgeType.getConditionExpr() != null) {
                    return "style=\"econdition\" ";
                }
                return "";
            }
        }).serialize(graph);
    }

    private DirectedGraph createGraph(final NetType net, final boolean withLabels) {
        final Map<String, Vertex> nodeMap = new HashMap<String, Vertex>();
        final Map<String, Vertex> nodeNameMap = new HashMap<String, Vertex>();
        final DirectedGraph g = new DirectedGraph();

        int edgeCounter = 0;
        int nodeNameCounter = 0;
        int nodeIdCounter = 0;

        for (final NodeType node : net.getNode()) {
            final Vertex vertex = new Vertex();
            if (withLabels) {
                String nodeType = node.getClass().getSimpleName().replaceFirst("Type$", "");
                if (node.getName() != null) {
                    // Already present in YAWL
                    vertex.setName(node.getName() + " (" + nodeType + ")");
                } else {
                    // Introduced node in the conversion
                    vertex.setName("N" + (nodeNameCounter++) + " (" + nodeType + ")");
                }
            }
            if (node.getName() != null) {
                if (withLabels) {
                    vertex.setId(node.getId());
                } else {
                    if (nodeNameMap.get(node.getName()) == null) {
                        // Use Name as ID if unique
                        vertex.setId(node.getName().replaceAll(" ", ""));
                    } else {
                        // Otherwise use ID
                        vertex.setId(node.getId());
                    }
                }
            } else {
                vertex.setId("N" + (nodeIdCounter++));
            }
            vertex.setTag(node);
            g.addVertex(vertex);
            nodeMap.put(node.getId(), vertex);
            nodeNameMap.put(node.getName(), vertex);
        }

        for (final EdgeType edgeType : net.getEdge()) {
            final Vertex source = nodeMap.get(edgeType.getSourceId());
            final Vertex target = nodeMap.get(edgeType.getTargetId());
            if (source != null && target != null) {
                final DirectedEdge edge = g.addEdge(source, target);
                edge.setTag(edgeType);
                if (edgeType.getConditionExpr() != null) {
                    edge.setName("c" + edgeCounter++);
                }
            }
        }
        return g;
    }

}
