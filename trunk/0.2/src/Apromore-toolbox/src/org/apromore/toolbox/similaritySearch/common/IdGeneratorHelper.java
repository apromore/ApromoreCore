package org.apromore.toolbox.similaritySearch.common;

import java.math.BigInteger;

public class IdGeneratorHelper {
	private BigInteger nextId = BigInteger.ONE;

	public BigInteger getNextId() {
		BigInteger toreturn = new BigInteger(nextId.toString());
		nextId = nextId.add(BigInteger.ONE);
		return toreturn;
	}

}
