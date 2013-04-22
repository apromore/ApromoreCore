package org.apromore.service.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apromore.dao.model.Edge;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.Node;
import org.apromore.graph.TreeVisitor;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;

/**
 * @author Chathura Ekanayake
 */
public class OperationContext {

    private Canonical graph;
    private TreeVisitor treeVisitor;
    private Map<Integer, Integer> contentUsage;
    private Map<String, Integer> processedFragmentTypes;

    private FragmentVersion currentFragment;
    private Set<Node> nodes = new HashSet<>(0);
    private Set<CPFNode> cpfNodes = new HashSet<>(0);
    private Set<Edge> edges = new HashSet<>(0);
    private Set<CPFEdge> cpfEdges = new HashSet<>(0);
    private Set<FragmentVersion> fragmentVersions = new HashSet<>(0);

    private Map<String, Node> persistedNodes = new HashMap<>(0);
    private Map<String, Edge> persistedEdges = new HashMap<>(0);


    public OperationContext() {
        contentUsage = new HashMap<>();
        processedFragmentTypes = new HashMap<>();
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

    public Map<String, Node> getPersistedNodes() {
        return persistedNodes;
    }

    public void addPersistedNode(String uri, Node pNode) {
        this.persistedNodes.put(uri, pNode);
    }

    public Map<String, Edge> getPersistedEdges() {
        return persistedEdges;
    }

    public void addPersistedEdge(String uri, Edge pEdge) {
        this.persistedEdges.put(uri, pEdge);
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
