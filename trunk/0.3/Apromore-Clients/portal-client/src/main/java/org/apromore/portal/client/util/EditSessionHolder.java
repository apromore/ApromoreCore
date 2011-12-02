package org.apromore.portal.client.util;

import java.util.HashMap;
import java.util.Map;

import org.apromore.portal.model.EditSessionType;

public class EditSessionHolder {

	private static Map<Integer, EditSessionType> editSessions;
	
	public static EditSessionType getEditSession(Integer sessionCode)
	{
		if(editSessions==null)
			init();
		return editSessions.get(sessionCode);
	}
	
	public static void addEditSession(Integer sessionCode,EditSessionType editSessionType)
	{
		if(editSessions==null)
			init();
		editSessions.put(sessionCode,editSessionType);
	}
	
	public static void removeEditSession(Integer sessionCode)
	{
		throw new UnsupportedOperationException("this method is not implemented yet. [portal should know when oryx is getting closed]");
	}

	private static void init()
	{
		editSessions=new HashMap<Integer, EditSessionType>();
	}
	
}
