/**
 * 
 */
package org.apromore.graph.JBPT;

/**
 * Basic interface for all gateway implementations.
 * 
 * @author Cindy Fhnrich, TObias Hoppe
 *
 */
public interface ICpfGateway extends ICpfNode {
	
	/**
	 * Check if {@link org.jbpt.pm.ICpfGateway} is split, has one incoming and multiple outgoing control flow edges
	 * @return <code>true</code> if {@link org.jbpt.pm.ICpfGateway} is a split gateway, <code>false</code> otherwise
	 */
	boolean isSplit();
	
	/**
	 * Check if {@link org.jbpt.pm.ICpfGateway} is join, has one outgoing and multiple incoming control flow edges
	 * @return <code>true</code> if {@link org.jbpt.pm.ICpfGateway} is a join gateway, <code>false</code> otherwise
	 */
	boolean isJoin();
}
