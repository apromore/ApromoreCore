package org.apromore.plugin.portal.loganimation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/11/17.
 */
public class LayoutGenerator {

    public static Map<String, ElementLayout> generateLayout(String layout) {
        Map<String, ElementLayout> elementLayoutMap = new HashMap<>();

        String tmp = layout;
        while (tmp.contains("\"group\":\"nodes\"")) {
            String element = tmp.substring(tmp.indexOf("{\"data\":"), tmp.indexOf("\"classes\":\"\"}") + 13);

            String elementName = null;
            if (element.contains("ellipse")) {
                if(element.contains("#C1C9B0")) {
                    elementName = "|&gt;";
                }else {
                    elementName = "[]";
                }
            } else {
                elementName = element.substring(element.indexOf("\"name\":\"") + 8, element.indexOf("\",\"width"));
                elementName = elementName.substring(0, elementName.indexOf("\\n"));
            }

            String width = element.substring(element.indexOf("\"width\":\"") + 9);
            width = width.substring(0, width.indexOf("px"));

            String height = element.substring(element.indexOf("\"height\":\"") + 10);
            height = height.substring(0, height.indexOf("px"));

            String x = element.substring(element.indexOf("\"x\":") + 4);
            x = x.substring(0, x.indexOf(","));

            String y = element.substring(element.indexOf("\"y\":") + 4);
            y = y.substring(0, y.indexOf("}"));

            String color = element.substring(element.indexOf("\"color\":") + 9);
            color = color.substring(0, color.indexOf("\",\"name"));

            elementLayoutMap.put(elementName, new ElementLayout(
                    elementName,
                    Double.parseDouble(width) * 2,
                    Double.parseDouble(height) * 2,
                    Double.parseDouble(x) * 2,
                    Double.parseDouble(y) * 2,
                    color));

            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }

        return elementLayoutMap;
    }

}
