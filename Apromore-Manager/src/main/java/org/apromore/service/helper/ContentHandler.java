package org.apromore.service.helper;

import org.apromore.dao.model.Content;
import org.apromore.graph.canonical.Canonical;

import java.util.Map;

/**
 * Content Handler Interface.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ContentHandler {

    /**
     * Tries to match an existing B fragment to the given B fragment f. f should be a post-extraction fragment.
     * This tries map a forward children and reverse children (i.e. loop) separately, as the given B fragment can
     * contain loops. If there are no matching fragments, this will fill the given new child mapping to suit
     * the matching content.
     * @param f                B Fragment to be matched.
     * @param matchingContent  Matching content.
     * @param childMappings    Original child mapping of the fragment f.
     * @param newChildMappings New child mapping to the matching content. Filled only if there is not matching
     *                         fragment. i.e. return value is null.
     * @return Matching fragment id. Null if there is no matching fragment.
     */
    String matchFragment(Canonical f, Content matchingContent, Map<String, String> childMappings,
        Map<String, String> newChildMappings);

}
