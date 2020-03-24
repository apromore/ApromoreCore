/* 
 * $Id: JGraphLayout.java,v 1.2 2009/10/30 14:18:23 david Exp $
 * Copyright (c) 2001-2009, JGraph Ltd
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout;

import org.apromore.jgraph.JGraph;

/**
 * The class that defines a layout algorithm. This class enforces the following
 * principles on the layouts:
 * <ul>
 * <li>Layouts operate on cells, not cell views</li>
 * <li>All access to "current" (ie. changed) attributes should go via the
 * facade provided to the run method</li>
 * <li>All access to the graph model should go via the facade passed to the run
 * method</li>
 * <li>The facade should decouple the layout from special jgraph features such
 * as partial views, and collapsed/expanded (edge) groups</li>
 * <li>Custom layouts can define extended facades for common functionality
 * </li>
 * </ul>
 * Long-running layouts should additionally implement the Stoppable interface.
 */
public interface JGraphLayout {

	/**
	 * Global static product identifier.
	 */
	public static final String VERSION = JGraph.VERSION;

	/**
	 * Takes the graph detail and configuration information within the facade
	 * and creates the resulting laid out graph within that facade for further
	 * use
	 * 
	 * @param graph
	 *            The layout facade that the layout will use as input
	 */
	public void run(JGraphFacade graph);

	/**
	 * An additional interface that should be implemented if a layout is
	 * expected to run longer. Eg. if a layout for a few hundred cells is likely
	 * to take more than 1 second.
	 */
	public interface Stoppable {

		/**
		 * Returns the progress object that represents the progress of the
		 * current layout run. Once created, this instance should not be
		 * replaced during a layout run. For new runs you should use the reset
		 * method on the progress. Consequently, the max progress is only valid
		 * after the run method has been invoked, which means you should use a
		 * listener if you spawn a new thread.
		 * <p>
		 * By convention, the layout must check the isStopped method in its
		 * inner-most loops and return immediately if the method returns
		 * <code>true</code>.
		 * 
		 * @return Returns the progress for all layout runs.
		 */
		public JGraphLayoutProgress getProgress();

	}

}
