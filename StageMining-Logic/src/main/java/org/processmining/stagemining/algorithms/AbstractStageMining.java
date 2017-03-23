package org.processmining.stagemining.algorithms;

import org.processmining.stagemining.models.DecompositionTree;
import org.deckfour.xes.model.XLog;

public abstract class AbstractStageMining {
	protected boolean debug = false;
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean getDebug() {
		return this.debug;
	}
	
	public DecompositionTree mine(XLog log, int minStageSize) throws Exception {
		return null;
	}
	
	/**
	 * Stage mining
	 * @param log
	 * @param minStageSize: 0 means noused 
	 * @param stageCount: 0 means noused
	 * @return
	 * @throws Exception
	 */
	public DecompositionTree mine(XLog log, int minStageSize, int stageCount) throws Exception {
		return null;
	}
}
