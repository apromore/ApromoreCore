package org.apromore.plugin.search;

import java.util.ListIterator;

import org.apromore.graph.canonical.Canonical;
import org.apromore.plugin.ParameterAwarePlugin;
import org.apromore.plugin.PluginRequest;

public interface SearchByModelPlugin extends ParameterAwarePlugin {

    SearchPluginResult searchByModel(Canonical model, ListIterator<Canonical> processModelIterator, PluginRequest request);

}
