package org.apromore.plugin.portal.logfilter.api;

import java.util.ArrayList;
import java.util.Arrays;

public class PluginParams extends ArrayList<Object> {
    public PluginParams(Object...objects) {
        super();
        this.addAll(Arrays.asList(objects));
    }
}
