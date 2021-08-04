package org.apromore.plugin.portal.calendar.pageutil;

import java.io.IOException;
import java.io.InputStreamReader;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.PageDefinition;

public class PageUtils {

    public static PageDefinition getPageDefinition(String uri) throws IOException {

	String url = "static/" + uri;
	Execution current = Executions.getCurrent();
	PageDefinition pageDefinition = current.getPageDefinitionDirectly(
	        new InputStreamReader(PageUtils.class.getClassLoader().getResourceAsStream(url)), "zul");
	return pageDefinition;
    }
}
