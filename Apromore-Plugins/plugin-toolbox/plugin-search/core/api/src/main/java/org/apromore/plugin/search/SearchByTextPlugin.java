package org.apromore.plugin.search;

import java.util.ListIterator;

import org.apromore.graph.canonical.Canonical;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.PluginRequest;

public interface SearchByTextPlugin extends ParameterAwarePlugin {

    SearchPluginResult searchByText(String queryString, ListIterator<Canonical> processModelIterator, PluginRequest request);

}
