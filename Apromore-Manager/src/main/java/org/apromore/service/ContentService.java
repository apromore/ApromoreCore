package org.apromore.service;

import java.util.Set;

import org.apromore.dao.model.Edge;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.Resource;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.INode;
import org.apromore.service.helper.OperationContext;

/**
 * Interface for the Content Service. Defines all the methods that will do the majority of the work for
 * adding new content into the repository.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ContentService {

    /**
     * Updates the Cancel Nodes and Edges with the correct Information.
     * @param operationContext the current Operational Context.
     */
    public void updateCancelNodes(OperationContext operationContext);

    /**
     * Adds a Single node to the Repository.
     * @param cpfNode the cpfNode we are persisting.
     * @param graphType the Graph Type (Bond, Rigid, Polygon)
     * @param objects any objects attached to this node
     * @param resources any resources attached to this node
     * @return the saved Node.
     */
    Node addNode(final INode cpfNode, final String graphType, Set<org.apromore.dao.model.Object> objects,
        Set<Resource> resources);

    /**
     * Add a single Edge to the Repository.
     *
     *
     * @param cpfEdge the cpfEdge we are persisting.
     * @param fv
     * @param op
     * @return the saved Edge.
     */
    Edge addEdge(final CPFEdge cpfEdge, FragmentVersion fv, OperationContext op);
}
