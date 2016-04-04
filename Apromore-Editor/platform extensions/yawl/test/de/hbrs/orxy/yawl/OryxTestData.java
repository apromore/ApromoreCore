/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.orxy.yawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class OryxTestData {

    public static String orderFulfillment;
    public static JSONObject orderFulfillmentAsJson;

    public static String orderFulfillmentRequestData;

    public static String filmProduction;

    static {
        try {
            orderFulfillment = new Scanner(new File("resources/orderfulfillment.json"), "UTF-8").useDelimiter("\\A").next();
            orderFulfillmentAsJson = new JSONObject(orderFulfillment);
            orderFulfillmentRequestData = new Scanner(new File("resources/orderfulfillment.json"), "UTF-8").useDelimiter("\\A").next();
            filmProduction = new Scanner(new File("resources/filmproduction.json"), "UTF-8").useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
