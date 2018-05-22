package org.apromore.plugin.portal.loganimation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/11/17.
 */
public class LayoutGenerator {

    public static void main(String[] args) {
        String layout = "{\"elements\":{\"nodes\":[{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"name\":\"Analyze Defect\\n\\n172\",\"width\":\"52px\",\"id\":\"1\",\"textwidth\":\"42px\",\"height\":\"30px\"},\"position\":{\"x\":109,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#6798b9\",\"name\":\"Archive Repair\\n\\n100\",\"width\":\"52px\",\"id\":\"2\",\"textwidth\":\"42px\",\"height\":\"30px\"},\"position\":{\"x\":679,\"y\":21},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#6b9bbb\",\"name\":\"Contact Courier\\n\\n97\",\"width\":\"52px\",\"id\":\"3\",\"textwidth\":\"42px\",\"height\":\"30px\"},\"position\":{\"x\":451,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"name\":\"Receive Enquiry\\n\\n172\",\"width\":\"52px\",\"id\":\"5\",\"textwidth\":\"42px\",\"height\":\"30px\"},\"position\":{\"x\":223,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"name\":\"Repair\\n\\n172\",\"width\":\"52px\",\"id\":\"6\",\"textwidth\":\"42px\",\"height\":\"30px\"},\"position\":{\"x\":337,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"name\":\"Test Repair\\n\\n172\",\"width\":\"52px\",\"id\":\"7\",\"textwidth\":\"42px\",\"height\":\"30px\"},\"position\":{\"x\":565,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C0A3A1\",\"name\":\"\",\"width\":\"15px\",\"id\":\"8\",\"textwidth\":\"42px\",\"height\":\"15px\"},\"position\":{\"x\":774.5,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C1C9B0\",\"name\":\"\",\"width\":\"15px\",\"id\":\"9\",\"textwidth\":\"42px\",\"height\":\"15px\"},\"position\":{\"x\":13.5,\"y\":92},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}],\"edges\":[{\"data\":{\"strength\":58.14,\"color\":\"#424242\",\"style\":\"solid\",\"source\":\"7\",\"label\":\"100\",\"target\":\"2\",\"id\":\"4376c6d9-a775-43c0-8372-3f3ed0d80f4c\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"solid\",\"source\":\"1\",\"label\":\"172\",\"target\":\"5\",\"id\":\"08b2ab15-4041-4bbd-8bfe-b40754559be8\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":58.14,\"color\":\"#424242\",\"style\":\"dashed\",\"source\":\"2\",\"label\":\"100\",\"target\":\"8\",\"id\":\"c2102820-fdee-41f3-b2ce-b6fa0d1dc7e5\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":56.4,\"color\":\"#434343\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"97\",\"target\":\"7\",\"id\":\"6fbbe10b-3fff-447d-a345-9ca0af3934fa\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"dashed\",\"source\":\"9\",\"label\":\"172\",\"target\":\"1\",\"id\":\"b3010b6e-f32b-4a67-982f-bfbfe40762e4\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"solid\",\"source\":\"5\",\"label\":\"172\",\"target\":\"6\",\"id\":\"ea184f4c-830a-4ea2-80f9-28eaea2b2e8a\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":56.4,\"color\":\"#434343\",\"style\":\"solid\",\"source\":\"6\",\"label\":\"97\",\"target\":\"3\",\"id\":\"280e1a44-17cf-43af-8d46-e831a144d76e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}]},\"style\":[{\"selector\":\"node\",\"style\":{\"text-valign\":\"center\",\"text-border-width\":\"10px\",\"text-wrap\":\"wrap\",\"text-max-width\":\"data(textwidth)\",\"font-size\":\"7px\",\"height\":\"data(height)\",\"width\":\"data(width)\",\"shape\":\"data(shape)\",\"background-color\":\"data(color)\",\"padding\":\"5px\",\"label\":\"data(name)\"}},{\"selector\":\":selected\",\"style\":{\"border-color\":\"#333\",\"border-width\":\"3px\"}},{\"selector\":\"edge\",\"style\":{\"label\":\"data(label)\",\"text-margin-y\":\"-10px\",\"color\":\"data(color)\",\"font-size\":\"7px\",\"opacity\":\"1\",\"width\":\"mapData(strength, 0, 100, 1, 6)\",\"line-style\":\"data(style)\",\"line-color\":\"data(color)\",\"curve-style\":\"bezier\",\"target-arrow-shape\":\"triangle\",\"source-arrow-color\":\"data(color)\",\"target-arrow-color\":\"data(color)\"}},{\"selector\":\"edge.questionable\",\"style\":{\"line-style\":\"dotted\",\"target-arrow-shape\":\"diamond\"}},{\"selector\":\".faded\",\"style\":{\"text-opacity\":\"0\",\"opacity\":\"0.25\"}},{\"selector\":\".edgebendediting-hasbendpoints\",\"style\":{\"curve-style\":\"segments\",\"segment-distances\":\"fn\",\"segment-weights\":\"fn\",\"edge-distances\":\"node-position\"}}],\"zoomingEnabled\":true,\"userZoomingEnabled\":true,\"zoom\":1,\"minZoom\":1.0E-50,\"maxZoom\":1.0E50,\"panningEnabled\":true,\"userPanningEnabled\":true,\"pan\":{\"x\":0,\"y\":0},\"boxSelectionEnabled\":true,\"renderer\":{\"name\":\"canvas\"},\"wheelSensitivity\":0.1}";
        Map<String, ElementLayout> layoutMap = generateLayout(layout);
        System.out.println();
    }

    public static Map<String, ElementLayout> generateLayout(String layout) {
        Map<String, ElementLayout> elementLayoutMap = new HashMap<>();
        Map<String, String> nodeIDs = new HashMap<>();

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

            String id = element.substring(element.indexOf("\"id\":\"") + 6);
            id = id.substring(0, id.indexOf("\""));

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
            nodeIDs.put(id, elementName);

            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }
        while (tmp.contains("\"group\":\"edges\"")) {
            String element = tmp.substring(tmp.indexOf("{\"data\":"), tmp.indexOf("\"classes\":\"\"}") + 13);

            String source = element.substring(element.indexOf("\"source\":\"") + 10);
            source = source.substring(0, source.indexOf("\""));

            String target = element.substring(element.indexOf("\"target\":\"") + 10);
            target = target.substring(0, target.indexOf("\""));

            elementLayoutMap.put(nodeIDs.get(source) +" (~) "+ nodeIDs.get(target), null);

            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }

        return elementLayoutMap;
    }

}
