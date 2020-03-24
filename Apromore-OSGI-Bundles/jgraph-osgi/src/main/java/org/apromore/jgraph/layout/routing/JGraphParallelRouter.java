/*
 * $Id: JGraphParallelRouter.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2004-2007 Gaudenz Alder
 * Copyright (c) 2005-2007 David Benson
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.routing;

import org.apromore.jgraph.util2.ParallelEdgeRouter;

/**
 * Algorithm which create intermediates points for parallel edges. Note that if
 * you require promotes edge (edges assigned to parents when the connected child
 * cells are invisible) to be shown as parallel on the parents, you must set the
 * graph on this router correct so that it was the view information.
 */
public class JGraphParallelRouter extends ParallelEdgeRouter {
}