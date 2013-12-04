package com.processconfiguration.bddc;

import java.util.List;
import java.util.TreeMap;

public interface BDDService {

        /**
         * Initialises a process by setting all the facts (variables) to arguments.
         */
	public void init(String init);

	public boolean isViolated(String cond);

	public boolean isViolated(TreeMap<String, String> valuation);

	public boolean isXOR(List<String> factsList);

        /**
         * Once the fact's setting is accepted, it has to be set in the process.
         */ //TODO: do we need it?
	public void setFact(String fID, String value);

        /**
         * Given a partial configuration, it checks whether a set of facts is forceable to TRUE or FALSE.
         */
	public int isForceable(String fID);
}
