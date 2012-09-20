package org.apromore.plugin.provider.impl;

import java.util.ArrayList;

import org.apromore.plugin.Plugin;
import org.junit.Before;

public class PluginProviderImplTest {

	@Before
	public void setUp() {
		final PluginProviderImpl mockProvider = new PluginProviderImpl();
		final ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
		mockProvider.setPluginList(pluginList);
	}

}
