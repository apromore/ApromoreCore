package org.apromore.graph.JBPT;

import org.jbpt.pm.ProcessModel;

/**
 * Abstract base class for all {@link org.jbpt.pm.CpfGateway}s of a {@link IProcessModel}.
 * 
 * @author Tobias Hoppe
 *
 */
public abstract class CpfGateway extends CpfNode implements ICpfGateway {

	/**
	 * Creates a new {@link org.jbpt.pm.CpfGateway} with an empty name.
	 */
	public CpfGateway() {
		super();
	}
	
	/**
	 * Creates a new {@link org.jbpt.pm.CpfGateway} with the given name.
	 * @param name of this {@link org.jbpt.pm.CpfGateway}
	 */
	public CpfGateway(String name){
		super(name);
	}
	
	@Override
	public boolean isJoin() {
		ProcessModel model = this.getModel();
		if (model != null && model.getIncomingControlFlow(this).size() > 1 && model.getOutgoingControlFlow(this).size() == 1){
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isSplit() {
		ProcessModel model = this.getModel();
		if (model != null && model.getIncomingControlFlow(this).size() == 1 && model.getOutgoingControlFlow(this).size() > 1){
			return true;
		}
		
		return false;
	}
}
