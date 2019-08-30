package org.apromore.plugin.portal.generic;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PluginParams extends ArrayList<Object> {
    public PluginParams(Object...objects) {
        super();
        this.addAll(Arrays.asList(objects));
    }
}
