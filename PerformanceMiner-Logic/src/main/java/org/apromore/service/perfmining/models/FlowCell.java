package org.apromore.service.perfmining.models;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class FlowCell {
	private final DateTime timePoint;
	Map<String, Double> characteristicMap = new HashMap<String, Double>();

	public FlowCell(DateTime timePoint) {
		this.timePoint = timePoint;
	}

	public DateTime getTimePoint() {
		return timePoint;
	}

	public Double getCharacteristic(String characteristicCode) {
		if (characteristicMap.containsKey(characteristicCode)) {
			return characteristicMap.get(characteristicCode);
		} else {
			return 0.0;
		}
	}

	public void setCharacteristic(String characteristicCode, Double newValue) {
		characteristicMap.put(characteristicCode, newValue);
	}

	public void clear() {
		characteristicMap.clear();
	}
}
