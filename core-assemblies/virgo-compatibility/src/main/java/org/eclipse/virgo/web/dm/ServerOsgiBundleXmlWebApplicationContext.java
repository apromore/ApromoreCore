package org.eclipse.virgo.web.dm;

import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;

/**
 * Pretend to be the Virgo class so that preexisting web.xml doesn't need to change on Karaf.
 */
public class ServerOsgiBundleXmlWebApplicationContext extends OsgiBundleXmlWebApplicationContext {

  // No implementation
}

