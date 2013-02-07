package org.apromore.plugin.provider.impl;

import org.apromore.plugin.Plugin;


public class TestPlugin implements Plugin {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getType() {
        return "org.apromore.plugin.provider.impl.test";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public String getAuthor() {
        return "test";
    }

    @Override
    public String getEMail() {
        return "test@test.com";
    }

}
