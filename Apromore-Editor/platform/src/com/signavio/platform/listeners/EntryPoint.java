/**
 * Copyright (c) 2009, Signavio GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.signavio.platform.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformInstance;
import com.signavio.platform.core.impl.FsPlatformInstanceImpl;

/**
 * This is the entry point for the application. 
 * System configuration is loaded here.
 * 
 * It is implemented as a ServletContextListener.
 * So, this is the first and the last code that is called when starting
 * the server.
 * 
 * @author nico
 *
 */
public class EntryPoint implements ServletContextListener {
	
	private final Logger logger = Logger.getLogger(EntryPoint.class);
		
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Destroying platform...");
		Platform.shutdownInstance();
		logger.info("Done destroying platform!");
	}

	/**
	 * Boot the platform using the default {@link PlatformInstance} implementation
	 * for the servlet container.
	 */
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Initializing platform...");
		Platform.bootInstance(FsPlatformInstanceImpl.class, sce.getServletContext());
		logger.info("Done initializing platform!");
	}

}
