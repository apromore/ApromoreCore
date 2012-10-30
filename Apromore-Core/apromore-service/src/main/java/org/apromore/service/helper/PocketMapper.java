package org.apromore.service.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.dao.model.Content;
import org.apromore.exception.UnlocatablePocketsException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.Node;
import org.apromore.service.GraphService;
import org.apromore.service.impl.GraphServiceImpl;
import org.apromore.util.FragmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service("PocketMapper")
@Transactional(propagation = Propagation.REQUIRED)
public class PocketMapper {

    private static Logger LOGGER = LoggerFactory.getLogger(PocketMapper.class);

    @Autowired @Qualifier("GraphService")
    private GraphService gSrv;


    /**
     * Maps each pocket in f to its corresponding pocket in an existing content.
     * A pocket is mapped, only if its counterpart in the content can be
     * accurately identified.
     *
     * @param f       Fragment with pockets
     * @param g       Process Model Graph
     * @param content content
     * @return Mapping from pockets in f to pockets in the content. null if pockets cannot be mapped.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> mapPockets(Canonical f, Canonical g, Content content) {
        Canonical c = gSrv.getGraph(content.getId());
        List<Node> cPockets = getPockets(new ArrayList<Node>(c.getVertices()), c);
        List<Node> pockets = getPockets(f.getNodes(), g);

        // Content with no pockets and content with only one pocket.
        Map<String, String> pocketMapping = new HashMap<String, String>(0);
        if (cPockets.size() == 0) {
            return pocketMapping;
        } else if (cPockets.size() == 1) {
            pocketMapping.put(pockets.get(0).getId(), cPockets.get(0).getId());
            return pocketMapping;
        }

        Map<Node, PocketLocator> locators;
        try {
            locators = calculateLocators(pockets, f.getEdges(), g);
        } catch (UnlocatablePocketsException e) {
            return null;
        }
        if (locators == null) {
            return mapPocketsArbitrary(pockets, cPockets);
        }

        // get locators for pockets in c
        Map<Node, PocketLocator> cLocators;
        try {
            cLocators = calculateLocators(cPockets, new ArrayList<Edge>(c.getEdges()), c);
        } catch (UnlocatablePocketsException e) {
            return null;
        }

        // map pockets based on locators
        return mapPocketsByLocators(locators, cLocators);
    }

    private static Map<String, String> mapPocketsArbitrary(List<Node> fps, List<Node> cps) {
        Map<String, String> pocketMapping = new HashMap<String, String>(0);
        List<Node> fpList = new ArrayList<Node>(fps);
        List<Node> cpList = new ArrayList<Node>(cps);
        for (int i = 0; i < fpList.size(); i++) {
            pocketMapping.put(fpList.get(i).getId(), cpList.get(i).getId());
        }
        return pocketMapping;
    }

    private static Map<String, String> mapPocketsByLocators(Map<Node, PocketLocator> flmap, Map<Node, PocketLocator> clmap) {
        Map<String, String> pocketMappings = new HashMap<String, String>(0);
        Set<Node> fps = flmap.keySet();
        for (Node fp : fps) {
            PocketLocator fpl = flmap.get(fp);
            Node cp = getMatchingPocket(fpl, clmap);
            if (cp == null) {
                String msg = "Failed to get a mapping pocket by locators. Forcing to create new structure...";
                LOGGER.error(msg);
                return null;
            }
            pocketMappings.put(fp.getId(), cp.getId());
        }
        return pocketMappings;
    }

    private static Node getMatchingPocket(PocketLocator pl, Map<Node, PocketLocator> lmap) {
        Node matchingPocket = null;
        Set<Node> ps = lmap.keySet();
        for (Node p : ps) {
            PocketLocator mappedpl = lmap.get(p);
            if (pl.matches(mappedpl)) {
                matchingPocket = p;
                break;
            }
        }
        return matchingPocket;
    }

    /**
     * Calculates an unique locator for each pocket based on it adjacent nodes.
     *
     * @param pockets Pockets of the fragment
     * @param edges   Edges of the fragment
     * @param g       The process Model Graph
     * @return Pocket to locator mappings if unique locators can be calculated.
     *         null if all pockets are equal (i.e. pockets can be assigned
     *         arbitrary).
     * @throws org.apromore.exception.UnlocatablePocketsException
     *          thrown if unique locators cannot be calculated.
     */
    private static Map<Node, PocketLocator> calculateLocators(List<Node> pockets, Collection<Edge> edges, Canonical g)
            throws UnlocatablePocketsException {
        Map<Node, PocketLocator> locators = new HashMap<Node, PocketLocator>(0);
        for (Node p : pockets) {
            PocketLocator locator = buildLocator(p, edges, g);
            locators.put(p, locator);
        }

        int locatability = refineLocators(locators);
        if (locatability == 0) {
            String msg = "Could not create unique locators for pockets.";
            LOGGER.info(msg);
            throw new UnlocatablePocketsException(msg);
        } else if (locatability == 1) {
            return null;
        } else {
            return locators;
        }
    }

    /**
     * @param locators some locators
     * @return 0 - pockets cannot be mapped
     *         1 - all pockets are equal. pockets can be mapped arbitrarily
     *         2 - pockets can be mapped by unique pocket locators
     */
    private static int refineLocators(Map<Node, PocketLocator> locators) {
        // if all locators have same preset and same postset, pockets can be
        // mapped arbitrary. so just return null.
        // otherwise, locators are valid only if no two pockets have same preset
        // or same postset labels (i.e. no two locators PocketLocator.match()
        // each other)
        List<PocketLocator> equivalentLocators = new ArrayList<PocketLocator>(0);
        Collection<PocketLocator> ls = locators.values();
        for (PocketLocator l : ls) {
            for (PocketLocator otherL : ls) {
                if (!l.equals(otherL)) {
                    if (l.matches(otherL)) {
                        if (l.equivalent(otherL)) {
                            if (!equivalentLocators.contains(l)) equivalentLocators.add(l);
                            if (!equivalentLocators.contains(otherL)) equivalentLocators.add(otherL);
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }

        if (equivalentLocators.size() > 0) {
            if (ls.size() == equivalentLocators.size()) {
                return 1;
            } else {
                return 0;
            }
        }

        return 2;
    }

    private static List<Node> getPockets(Collection<Node> vertices, Canonical g) {
        List<Node> pockets = new ArrayList<Node>(0);
        for (Node v : vertices) {
            String type = g.getNodeProperty(v.getId(), Constants.TYPE);
            if (Constants.POCKET.equals(type)) {
                pockets.add(v);
            }
        }
        return pockets;
    }

    /**
     * Pocket is identified (i.e. located) by its preset node and postset node. As fragments are always SESE
     * pocket can have only one preset node and only one postset node.
     *
     * @param v  Id of the pocket.
     * @param es Edges of the fragment in which the pocket is located.
     * @param g  Graph containing the fragment. This is used to get labels of preset and postset nodes of the pocket.
     * @return PocketLocator for locating the pocket.
     */
    @SuppressWarnings("unused")
    private static PocketLocator buildLocator(Node v, Collection<Edge> es, Canonical g) {
        PocketLocator locator = new PocketLocator();

        List<Node> preset = FragmentUtil.getPreset(v, es);
        for (INode presetNode : preset) {
            locator.setPreset(presetNode);
            locator.setPresetLabel(presetNode.getName());
        }

        List<Node> postset = FragmentUtil.getPostset(v, es);
        for (INode postsetNode : postset) {
            locator.setPostset(postsetNode);
            locator.setPostsetLabel(postsetNode.getId());
        }
        return locator;
    }


    /**
     * Set the Graph Service object for this class. Mainly for spring tests.
     *
     * @param gService the Graph Service.
     */
    public void setGraphService(GraphServiceImpl gService) {
        gSrv = gService;
    }
}
