package org.apromore.service.perfmining.parameters;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * This interface defines changes on settings
 * 
 * @author Hoang Nguyen
 * 
 */
public interface SPFSettingsListener {
	//	public void setActivityList(List<String> activityList);
	//	public void setGateList(List<String> gateList);
	public void setStageList(List<String> stageList);

	public void setExitStatusList(List<String> statusList);

	public void setEventStageMap(Map<String, String> eventStageMap);

	public void setTimeZone(TimeZone timezone);

	public void setCheckStartCompleteEvents(boolean check);
}
