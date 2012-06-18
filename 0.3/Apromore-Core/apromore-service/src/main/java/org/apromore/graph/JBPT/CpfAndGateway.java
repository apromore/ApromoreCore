package org.apromore.graph.JBPT;

/**
 * Base class for an AND-CpfGateway in a process model.
 * @author Tobias Hoppe
 *
 */
public class CpfAndGateway extends CpfGateway implements ICpfAndGateway {

	/**
	 * Create a new {@link org.jbpt.pm.CpfAndGateway} with an empty name.
	 */
	public CpfAndGateway(){
		super();
	}

	/**
	 * Create a new {@link org.jbpt.pm.CpfAndGateway} with the given name.
	 * @param name of this {@link org.jbpt.pm.CpfAndGateway}
	 */
	public CpfAndGateway(String name){
		super(name);
	}
}
