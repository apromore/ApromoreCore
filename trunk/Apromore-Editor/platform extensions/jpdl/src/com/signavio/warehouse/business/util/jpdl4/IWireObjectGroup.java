package com.signavio.warehouse.business.util.jpdl4;

import org.json.JSONException;
import org.json.JSONObject;

public interface IWireObjectGroup {

	public abstract String toJpdl();

	public abstract JSONObject toJson() throws JSONException;

}