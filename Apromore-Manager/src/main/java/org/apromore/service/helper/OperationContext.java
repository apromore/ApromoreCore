package org.apromore.service.helper;

import org.apromore.dao.model.Edge;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.Node;
import org.apromore.graph.TreeVisitor;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;

import java.util.*;

/**
 * @author Chathura Ekanayake
 */
public class OperationContext {

    private Canonical graph;
    private TreeVisitor treeVisitor;
    private Map<Integer, Integer> contentUsage;
    private Map<String, Integer> processedFragmentTypes;

    private FragmentVersion currentFragment;
    private Set<Node> nodes = new HashSet<Node>(0);
    private Set<CPFNode> cpfNodes = new HashSet<CPFNode>(0);
    private Set<Edge> edges = new HashSet<Edge>(0);
    private Set<CPFEdge> cpfEdges = new HashSet<CPFEdge>(0);
    private Set<FragmentVersion> fragmentVersions = new HashSet<FragmentVersion>(0);


    public OperationContext() {
        contentUsage = new HashMap<Integer, Integer>();
        processedFragmentTypes = new HashMap<String, Integer>();
        processedFragmentTypes.put("S", 0);
        processedFragmentTypes.put("P", 0);
        processedFragmentTypes.put("R", 0);
    }

    public Canonical getGraph() {
        return graph;
    }

    public void setGraph(Canonical graph) {
        this.graph = graph;
    }

    public TreeVisitor getTreeVisitor() {
        return treeVisitor;
    }

    public void setTreeVisitor(TreeVisitor treeVisitor) {
        this.treeVisitor = treeVisitor;
    }

    public void addProcessedFragmentType(String fragmentType) {
        Integer typeCount = processedFragmentTypes.get(fragmentType);
        if (typeCount == null) {
            typeCount = 1;
        } else {
            typeCount++;
        }
        processedFragmentTypes.put(fragmentType, typeCount);
    }

    public int getContentUsage(Integer contentId) {
        if (contentUsage.containsKey(contentId)) {
            return contentUsage.get(contentId);
        } else {
            return 0;
        }
    }

    public void incrementContentUsage(Integer contentId) {
        if (!contentUsage.containsKey(contentId)) {
            contentUsage.put(contentId, 1);
        } else {
            int usage = contentUsage.get(contentId);
            usage++;
            contentUsage.put(contentId, usage);
        }
    }


    public FragmentVersion getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(final FragmentVersion newCurrentFragment) {
        this.currentFragment = newCurrentFragment;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public void addAllNodes(final Set<Node> newNodes) {
        this.nodes.addAll(newNodes);
    }

    public Set<CPFNode> getCpfNodes() {
        return cpfNodes;
    }

    public void addAllCpfNodes(final Set<CPFNode> newNodes) {
        this.cpfNodes.addAll(newNodes);
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void addAllEdges(final Set<Edge> newEdges) {
        this.edges.addAll(newEdges);
    }

    public Set<CPFEdge> getCpfEdges() {
        return cpfEdges;
    }

    public void addAllCpfEdges(final Set<CPFEdge> newEdges) {
        this.cpfEdges.addAll(newEdges);
    }

    public void addFragmentVersion(final FragmentVersion fragmentVersion) {
        this.fragmentVersions.add(fragmentVersion);
    }

    public Set<FragmentVersion> getFragmentVersions() {
        return this.fragmentVersions;
    }
}
