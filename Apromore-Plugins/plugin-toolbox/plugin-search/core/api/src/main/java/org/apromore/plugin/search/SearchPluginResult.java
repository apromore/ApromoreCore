package org.apromore.plugin.search;

import java.util.Collection;

import org.apromore.plugin.PluginResult;

public interface SearchPluginResult extends PluginResult {

    Collection<Long> getSearchResultIds();

}
