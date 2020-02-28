/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.loganimation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 3/11/17.
 */
public class LayoutGenerator {

    public static void main(String[] args) {
        String layout = "{\"elements\":{\"nodes\":[{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"1\",\"textwidth\":\"90px\",\"gatewayId\":\"node_69039846-c4a8-4c22-9937-c5310ff82431\",\"height\":\"40px\"},\"position\":{\"x\":1344.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"2\",\"textwidth\":\"90px\",\"gatewayId\":\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\",\"height\":\"40px\"},\"position\":{\"x\":921.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"3\",\"textwidth\":\"90px\",\"gatewayId\":\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\",\"height\":\"40px\"},\"position\":{\"x\":1767.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"4\",\"textwidth\":\"90px\",\"gatewayId\":\"node_2b01e159-c356-480b-8c76-777a5942425b\",\"height\":\"40px\"},\"position\":{\"x\":2190.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#e9e9f2\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Admission IC\\n\\n117\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"5\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1052.5,\"y\":398},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#9ebad1\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Admission NC\\n\\n1182\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"6\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1213.5,\"y\":257},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#0c5f91\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"CRP\\n\\n3262\",\"textcolor\":\"white\",\"width\":\"100px\",\"id\":\"7\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1636.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a7c0d5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Registration\\n\\n1050\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"8\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":146.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a8c0d5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Sepsis Triage\\n\\n1049\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"9\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":468.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a7c0d5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Triage\\n\\n1053\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"10\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":307.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#b7cadc\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"IV Antibiotics\\n\\n823\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"11\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":790.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#bccddf\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"IV Liquid\\n\\n753\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"12\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":629.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#8aaec8\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"LacticAcid\\n\\n1466\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"13\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1213.5,\"y\":398},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Leucocytes\\n\\n3383\",\"textcolor\":\"white\",\"width\":\"100px\",\"id\":\"14\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1475.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#c2d1e1\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release A\\n\\n671\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"15\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1898.5,\"y\":609.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#edecf4\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release B\\n\\n56\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"16\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":45.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#efedf5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release C\\n\\n25\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"17\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":186.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#efedf5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release D\\n\\n24\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"18\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#f1eef6\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release E\\n\\n6\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"19\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":468.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#dce1ed\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Return ER\\n\\n294\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"20\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":609.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C0A3A1\",\"textsize\":\"15\",\"borderwidth\":\"3\",\"name\":\"\",\"textcolor\":\"black\",\"width\":\"30px\",\"id\":\"21\",\"textwidth\":\"90px\",\"height\":\"30px\"},\"position\":{\"x\":2287.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C1C9B0\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"\",\"textcolor\":\"black\",\"width\":\"30px\",\"id\":\"22\",\"textwidth\":\"90px\",\"height\":\"30px\"},\"position\":{\"x\":20.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}],\"edges\":[{\"data\":{\"strength\":2.59,\"color\":\"#626262\",\"style\":\"solid\",\"source\":\"2\",\"label\":\"46\",\"target\":\"5\",\"id\":\"1805ce9d-167d-46ef-8cd6-b7d419af6a59\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":27.5,\"color\":\"#545454\",\"style\":\"solid\",\"source\":\"2\",\"label\":\"489\",\"target\":\"6\",\"id\":\"ee294eb2-5c5f-405e-8ba3-47c2febba4a7\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":54.72,\"color\":\"#444444\",\"style\":\"solid\",\"source\":\"1\",\"label\":\"973\",\"target\":\"14\",\"id\":\"38d78b60-e610-4507-a8ee-547eaa276e07\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":18.11,\"color\":\"#595959\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"322\",\"target\":\"15\",\"id\":\"bf459f25-16db-44c6-baa0-57180d0e1dbb\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":1.07,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"19\",\"target\":\"16\",\"id\":\"1e4b4364-0545-4e8b-903e-e37617099983\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.73,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"13\",\"target\":\"17\",\"id\":\"669bf60f-1249-4bd4-ab01-fc9237081182\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.67,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"12\",\"target\":\"18\",\"id\":\"0fd962eb-451e-46fa-b2f7-d525428e98a9\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.17,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"3\",\"target\":\"19\",\"id\":\"270b5d2d-c843-4c57-8e0c-3840469bff4b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":21.6,\"color\":\"#575757\",\"style\":\"solid\",\"source\":\"4\",\"label\":\"384\",\"target\":\"21\",\"id\":\"f33dc0a4-016e-4945-99f8-0542684a24f0\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":2.31,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"5\",\"label\":\"41\",\"target\":\"13\",\"id\":\"bd205e29-e6c4-4d0d-b831-ccd06f4c8f1f\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":22.95,\"color\":\"#565656\",\"style\":\"solid\",\"source\":\"6\",\"label\":\"408\",\"target\":\"1\",\"id\":\"d1131308-b765-4201-a462-da442de4832e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":20.75,\"color\":\"#585858\",\"style\":\"solid\",\"source\":\"7\",\"label\":\"369\",\"target\":\"3\",\"id\":\"2d3c27fa-123e-421d-8f36-ece3c0f5e1c3\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":54.61,\"color\":\"#444444\",\"style\":\"solid\",\"source\":\"8\",\"label\":\"971\",\"target\":\"10\",\"id\":\"445cb6ce-210a-4cc2-9d7a-cded8fe3565e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":16.03,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"9\",\"label\":\"285\",\"target\":\"12\",\"id\":\"5a059ba3-ca78-46b6-867b-ad596f1f1c9d\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":50.9,\"color\":\"#464646\",\"style\":\"solid\",\"source\":\"10\",\"label\":\"905\",\"target\":\"9\",\"id\":\"fe870c9f-c38b-49c2-ae8f-70c017689888\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":30.09,\"color\":\"#525252\",\"style\":\"solid\",\"source\":\"11\",\"label\":\"535\",\"target\":\"2\",\"id\":\"50a276b8-239c-4159-9d05-368b07ccf092\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":28.18,\"color\":\"#535353\",\"style\":\"solid\",\"source\":\"12\",\"label\":\"501\",\"target\":\"11\",\"id\":\"f879bf67-8d13-471d-beaf-9719c0df808b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":31.78,\"color\":\"#515151\",\"style\":\"solid\",\"source\":\"13\",\"label\":\"565\",\"target\":\"1\",\"id\":\"d7796fde-d675-43d9-9646-6f19a665b126\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"solid\",\"source\":\"14\",\"label\":\"1778\",\"target\":\"7\",\"id\":\"4ac8eff2-b801-4138-9cab-5d751c5bd9ff\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":15.52,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"15\",\"label\":\"276\",\"target\":\"20\",\"id\":\"2ad4f4e3-0d02-4325-bf0c-664402cba724\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":3.09,\"color\":\"#626262\",\"style\":\"solid\",\"source\":\"16\",\"label\":\"55\",\"target\":\"4\",\"id\":\"7e7fd446-cec2-4cf4-803d-672eecbb636e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":1.07,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"17\",\"label\":\"19\",\"target\":\"4\",\"id\":\"18025798-caf6-492e-b63c-ac4aecfb9448\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.79,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"18\",\"label\":\"14\",\"target\":\"4\",\"id\":\"dd06aa69-27dd-47d7-a2ea-ca165220ae82\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.28,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"19\",\"label\":\"5\",\"target\":\"4\",\"id\":\"7ad84887-ebc6-4249-9b31-9b539884c3ba\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":16.37,\"color\":\"#5a5a5a\",\"style\":\"solid\",\"source\":\"20\",\"label\":\"291\",\"target\":\"4\",\"id\":\"18fa48d9-2e36-4222-b73f-5406d7f0f39d\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":55.96,\"color\":\"#434343\",\"style\":\"solid\",\"source\":\"22\",\"label\":\"995\",\"target\":\"8\",\"id\":\"56f0d3d7-c083-4341-aaf6-d9b4227268cd\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}]},\"style\":[{\"selector\":\"node\",\"style\":{\"background-color\":\"data(color)\",\"border-color\":\"black\",\"border-width\":\"data(borderwidth)\",\"color\":\"data(textcolor)\",\"label\":\"data(name)\",\"font-size\":\"data(textsize)\",\"height\":\"data(height)\",\"padding\":\"5px\",\"shape\":\"data(shape)\",\"text-border-width\":\"10px\",\"text-max-width\":\"data(textwidth)\",\"text-valign\":\"center\",\"text-wrap\":\"wrap\",\"width\":\"data(width)\"}},{\"selector\":\":selected\",\"style\":{\"border-width\":\"4px\",\"border-color\":\"#333\"}},{\"selector\":\"edge\",\"style\":{\"color\":\"data(color)\",\"curve-style\":\"bezier\",\"text-rotation\":\"0rad\",\"font-size\":\"11px\",\"label\":\"data(label)\",\"line-color\":\"data(color)\",\"line-style\":\"data(style)\",\"loop-sweep\":\"181rad\",\"loop-direction\":\"-41rad\",\"opacity\":\"1\",\"source-arrow-color\":\"data(color)\",\"target-arrow-color\":\"data(color)\",\"target-arrow-shape\":\"triangle\",\"text-background-color\":\"#ffffff\",\"text-background-opacity\":\"1\",\"text-background-padding\":\"5px\",\"text-background-shape\":\"roundrectangle\",\"width\":\"mapData(strength, 0, 100, 1, 6)\"}},{\"selector\":\"edge.questionable\",\"style\":{\"line-style\":\"dotted\",\"target-arrow-shape\":\"diamond\"}},{\"selector\":\".faded\",\"style\":{\"opacity\":\"0.25\",\"text-opacity\":\"0\"}}],\"zoomingEnabled\":true,\"userZoomingEnabled\":true,\"zoom\":0.5936823885763739,\"minZoom\":1.0E-50,\"maxZoom\":1.0E50,\"panningEnabled\":true,\"userPanningEnabled\":true,\"pan\":{\"x\":30.593682388576326,\"y\":116.06901774123756},\"boxSelectionEnabled\":true,\"renderer\":{\"name\":\"canvas\"},\"wheelSensitivity\":0.1}";
        Map<String, ElementLayout> layoutMap = generateLayout(layout);
        System.out.println();
    }

    private static double scaling = 1;

    public static Map<String, ElementLayout> generateLayout(String layout) {
        Map<String, ElementLayout> elementLayoutMap = new HashMap<>();
        Map<String, String> nodeIDs = new HashMap<>();

        String tmp = layout;

        if(tmp.startsWith("{\"elements\":{\"edges\"")) {
            String edges = tmp.substring(tmp.indexOf("{\"edges\""), tmp.indexOf(",\"nodes\""));
            tmp = tmp.substring(tmp.indexOf(edges) + edges.length());
            tmp = analyseNodes(elementLayoutMap, nodeIDs, tmp);
            tmp = analyseEdges(elementLayoutMap, nodeIDs, edges);
        }else {
            tmp = analyseNodes(elementLayoutMap, nodeIDs, tmp);
            tmp = analyseEdges(elementLayoutMap, nodeIDs, tmp);
        }

        return elementLayoutMap;
    }

    private static String analyseEdges(Map<String, ElementLayout> elementLayoutMap, Map<String, String> nodeIDs, String tmp) {
        while (tmp.contains("\"group\":\"edges\"")) {
            String element = tmp.substring(tmp.indexOf("{\"data\":"), tmp.indexOf("\"classes\":\"\"}") + 13);

            String source = element.substring(element.indexOf("\"source\":\"") + 10);
            source = source.substring(0, source.indexOf("\""));

            String target = element.substring(element.indexOf("\"target\":\"") + 10);
            target = target.substring(0, target.indexOf("\""));

            elementLayoutMap.put(nodeIDs.get(source) + " (~) " + nodeIDs.get(target), null);

            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }
        return tmp;
    }

    private static String analyseNodes(Map<String, ElementLayout> elementLayoutMap, Map<String, String> nodeIDs, String tmp) {
        String tmp1 = tmp;
        while (tmp.contains("\"group\":\"nodes\"")) {
            String element = tmp.substring(tmp.indexOf("{\"data\":"), tmp.indexOf("\"classes\":\"\"}") + 13);

            String elementName = null;
            if (element.contains("ellipse")) {
                if(element.contains("#C1C9B0")) {
                    elementName = "|&gt;";
                }else {
                    elementName = "[]";
                }
            } else if (element.contains("diamond")) {
                elementName = "";
            } else {
                elementName = element.substring(element.indexOf("\"oriname\":\"") + 11, element.indexOf("\",\"name"));
                //elementName = element.substring(element.indexOf("\"name\":\"") + 8, element.indexOf("\",\"width"));
                //elementName = elementName.substring(0, elementName.indexOf("\\n"));
            }

            String height = element.substring(element.indexOf("\"height\":\"") + 10);
            height = height.substring(0, height.indexOf("px"));

            if(elementName.equals("[]")) {
                scaling = 30 / Double.parseDouble(height);
            }

            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }

        tmp = tmp1;
        while (tmp.contains("\"group\":\"nodes\"")) {
            String element = tmp.substring(tmp.indexOf("{\"data\":"), tmp.indexOf("\"classes\":\"\"}") + 13);

            String elementName = null;
            if (element.contains("ellipse")) {
                if(element.contains("#C1C9B0")) {
                    elementName = "|&gt;";
                }else {
                    elementName = "[]";
                }
            } else if (element.contains("diamond")) {
                elementName = "";
            } else {
            	elementName = element.substring(element.indexOf("\"oriname\":\"") + 11, element.indexOf("\",\"name"));
                //elementName = element.substring(element.indexOf("\"name\":\"") + 8, element.indexOf("\",\"width"));
                //elementName = elementName.substring(0, elementName.indexOf("\\n"));
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
            color = color.substring(0, color.indexOf("\",\""));

            String elementId = "";
            if(element.contains("gatewayId")) {
                elementId = element.substring(element.indexOf("\"gatewayId\":") + 13);
                elementId = elementId.substring(0, elementId.indexOf("\",\""));
            }

            double x_shift = (Double.parseDouble(width) * scaling / 2);
            double y_shift = (Double.parseDouble(height) * scaling / 2);
            String key = (elementName.isEmpty()) ? elementId : elementName;
            elementLayoutMap.put(key, new ElementLayout(
            		elementName,
                    elementId,
                    Double.parseDouble(width) * scaling,
                    Double.parseDouble(height) * scaling,
                    Double.parseDouble(x) * scaling - x_shift,
                    Double.parseDouble(y) * scaling - y_shift,
                    color));
            nodeIDs.put(id, key);

            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }
        return tmp;
    }
}
