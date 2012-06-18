package org.apromore.service.helper;

import org.apromore.common.Constants;
import org.apromore.dao.model.Content;
import org.apromore.exception.UnlocatablePocketsException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.GraphService;
import org.apromore.service.impl.GraphServiceImpl;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.pm.IFlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * @param f Fragment with pockets
     * @param g Process Model Graph
	 * @param content content
	 * @return Mapping from pockets in f to pockets in the content. null if pockets cannot be mapped.
	 */
    @SuppressWarnings("unchecked")
	public Map<String, String> mapPockets(RPSTNode f, CPF g, Content content) {
		CPF c = gSrv.getGraph(content.getContentId());
		List<IFlowNode> cPockets = getPockets(new ArrayList<IFlowNode>(c.getVertices()), c);
		List<IFlowNode> pockets = getPockets(f.getFragment().getVertices(), g);
		
		// let's handle the easy cases first. content with no pockets and content with only one pocket.
		if (cPockets.size() == 0) {
			return new HashMap<String, String>(0);
		} else if (cPockets.size() == 1) {
			Map<String, String> pocketMapping = new HashMap<String, String>(0);
			pocketMapping.put(pockets.get(0).getId(), cPockets.get(0).getId());
			return pocketMapping;
		}
		
		Map<IFlowNode, PocketLocator> locators;
		try {
			locators = calculateLocators(pockets, f.getFragmentEdges(), g);
		} catch (UnlocatablePocketsException e) {
			return null;
		}
		if (locators == null) {
			return mapPocketsArbitrary(pockets, cPockets);
		}

		// get locators for pockets in c
		Map<IFlowNode, PocketLocator> cLocators;
		try {
			cLocators = calculateLocators(cPockets, new ArrayList<AbstractDirectedEdge>(c.getEdges()), c);
		} catch (UnlocatablePocketsException e) {
			return null;
		}

		// map pockets based on locators
		return mapPocketsByLocators(locators, cLocators);
	}

	private static Map<String, String> mapPocketsArbitrary(List<IFlowNode> fps, List<IFlowNode> cps) {
		Map<String, String> pocketMapping = new HashMap<String, String>(0);
		List<IFlowNode> fpList = new ArrayList<IFlowNode>(fps);
		List<IFlowNode> cpList = new ArrayList<IFlowNode>(cps);
		for (int i = 0; i < fpList.size(); i++) {
			pocketMapping.put(fpList.get(i).getId(), cpList.get(i).getId());
		}
		return pocketMapping;
	}

	private static Map<String, String> mapPocketsByLocators(Map<IFlowNode, PocketLocator> flmap,
			Map<IFlowNode, PocketLocator> clmap) {
		Map<String, String> pocketMappings = new HashMap<String, String>(0);
		Set<IFlowNode> fps = flmap.keySet();
		for (IFlowNode fp : fps) {
			PocketLocator fpl = flmap.get(fp);
            IFlowNode cp = getMatchingPocket(fpl, clmap);
			if (cp == null) {
				String msg = "Failed to get a mapping pocket by locators. Forcing to create new structure...";
                LOGGER.error(msg);
				return null;
			}
			pocketMappings.put(fp.getId(), cp.getId());
		}
		return pocketMappings;
	}

	private static IFlowNode getMatchingPocket(PocketLocator pl, Map<IFlowNode, PocketLocator> lmap) {
        IFlowNode matchingPocket = null;
		Set<IFlowNode> ps = lmap.keySet();
		for (IFlowNode p : ps) {
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
	 * @param edges Edges of the fragment
     * @param g The process Model Graph
	 * @return Pocket to locator mappings if unique locators can be calculated.
	 *         null if all pockets are equal (i.e. pockets can be assigned
	 *         arbitrary).
	 * @throws org.apromore.exception.UnlocatablePocketsException thrown if unique locators cannot be calculated.
	 */
	private static Map<IFlowNode, PocketLocator> calculateLocators(List<IFlowNode> pockets,
            Collection<AbstractDirectedEdge> edges, CPF g) throws UnlocatablePocketsException {
		Map<IFlowNode, PocketLocator> locators = new HashMap<IFlowNode, PocketLocator>(0);
		for (IFlowNode p : pockets) {
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
	 * @return 	0 - pockets cannot be mapped
	 * 			1 - all pockets are equal. pockets can be mapped arbitrarily
	 * 			2 - pockets can be mapped by unique pocket locators
	 */
	private static int refineLocators(Map<IFlowNode, PocketLocator> locators) {
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

	private static List<IFlowNode> getPockets(Collection<IFlowNode> vertices, CPF g) {
		List<IFlowNode> pockets = new ArrayList<IFlowNode>(0);
		for (IFlowNode v : vertices) {
			String type = g.getVertexProperty(v.getId(), Constants.TYPE);
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
	 * @param v Id of the pocket.
	 * @param es Edges of the fragment in which the pocket is located.
	 * @param g Graph containing the fragment. This is used to get labels of preset and postset nodes of the pocket.
	 * @return PocketLocator for locating the pocket.
	 */
    @SuppressWarnings("unused")
	private static PocketLocator buildLocator(IFlowNode v, Collection<AbstractDirectedEdge> es, CPF g) {
		PocketLocator locator = new PocketLocator();
		
		List<IFlowNode> preset = FragmentUtil.getPreset(v, es);
		for (IFlowNode presetNode : preset) {
			locator.setPreset(presetNode);
			locator.setPresetLabel(presetNode.getName());
		}

		List<IFlowNode> postset = FragmentUtil.getPostset(v, es);
		for (IFlowNode postsetNode : postset) {
			locator.setPostset(postsetNode);
			locator.setPostsetLabel(postsetNode.getId());
		}
		return locator;
	}



    /**
     * Set the Graph Service object for this class. Mainly for spring tests.
     * @param gService the Graph Service.
     */
    public void setGraphService(GraphServiceImpl gService) {
        gSrv = gService;
    }
}
