package org.apromore.logman.stats.calculators;

import org.apromore.logman.stats.StatsCalculator;

public class AttributeStats {
	private String attribute;
	
	public AttributeStats(String attribute) {
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return this.attribute;
	}

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }
}
