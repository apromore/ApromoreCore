package org.apromore.graph.JBPT;

/**
 * Base class for a XOR-CpfGateway in a process model.
 * 
 * @author Tobias Hoppe
 *
 */
public class CpfXorGateway extends CpfGateway implements ICpfXorGateway {

	/**
	 * Create a new XOR-CpfGateway
	 */
	public CpfXorGateway(){
		super();
	}

	/**
	 * Create a new XOR-CpfGateway
	 * @param name
	 */
	public CpfXorGateway(String name){
		super(name);
	}
}
