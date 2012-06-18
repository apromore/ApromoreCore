package org.apromore.graph.JBPT;

/**
 * Base class for a OR-CpfGateway in a process model.
 * @author Tobias Hoppe
 */
public class CpfOrGateway extends CpfGateway implements ICpfOrGateway {

	/**
	 * Create a new OR-CpfGateway.
	 */
	public CpfOrGateway(){
		super();
	}

	/**
	 * Create a new OR-CpfGateway with the given name.
	 * @param name
	 */
	public CpfOrGateway(String name){
		super(name);
	}
}
