package org.apromore.plugin.portal.generic;

public abstract class PluginInputParams extends PluginParams {
    public PluginInputParams(Object...objects) {
        super(objects);
    }
    
    public abstract boolean checkInputParamsValidity();
}
