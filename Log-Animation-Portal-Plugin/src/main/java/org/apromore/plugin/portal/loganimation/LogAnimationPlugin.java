/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

// Java 2 Standard Edition
import java.util.*;

// Java 2 Enterprise Edition
import javax.inject.Inject;

// Third party packages
import org.apromore.manager.client.ManagerService;
import org.apromore.model.*;
import org.deckfour.xes.model.XLog;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

// Local packages
import org.apromore.exception.RepositoryException;
import org.apromore.helper.Version;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.loganimation.LogAnimationService;

@Component("plugin")
public class LogAnimationPlugin extends DefaultPortalPlugin implements LogAnimationPluginInterface {

    public static void main(String[] args) {

        String bpmnModel = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" targetNamespace=\"http://www.omg.org/bpmn20\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"><process id=\"proc_1294675513\"><endEvent id=\"node_5e45a7a0-9f7b-4dd8-8a28-e80f70178853\" name=\"[]\" isInterrupting=\"false\"/><task id=\"node_263258e8-3463-40c1-b881-48f3851f00b2\" name=\"Test Repair\"/><task id=\"node_bdb69f54-bb42-47b0-8ce7-1e7414a22f08\" name=\"Receive Enquiry\"/><task id=\"node_aef3f10f-c325-4503-a7b3-eddf9b38f665\" name=\"Order Component\"/><task id=\"node_05615cc1-bdaf-48e4-b6be-3438a2ae33f6\" name=\"Archive Repair\"/><task id=\"node_7facd3b5-eed9-4b82-857c-a1af01a62475\" name=\"Repair\"/><task id=\"node_c7c0b85f-891e-469f-a170-5062767d94b0\" name=\"Inform User\"/><task id=\"node_25bbc649-d4c3-4fe4-9642-a5e45797b30a\" name=\"Analyze Defect\"/><task id=\"node_4f748b05-ba68-46ba-8aac-d0c72a2aa3d4\" name=\"Cancel Orders\"/><task id=\"node_03919805-fd3b-432b-ab97-973386916d49\" name=\"Contact Courier\"/><task id=\"node_c9233d07-c6ce-4171-b898-0c75074f9c60\" name=\"Disassemble Product\"/><task id=\"node_ea38e19a-c91b-4977-802a-28654529eb39\" name=\"Check Repair Status\"/><task id=\"node_c8661976-1452-4b65-84a7-0dc0eb4a0f1a\" name=\"Install Component\"/><exclusiveGateway id=\"node_d92c0aaa-27a9-4257-beef-47be21f3d932\" name=\"\" gatewayDirection=\"Converging\"><incoming>node_10986126-881d-44cc-8565-01e106e5952f</incoming><incoming>node_586e00fe-013d-4681-aedd-7c2383f3d70c</incoming><outgoing>node_237cf3ec-4c3a-41dd-b124-93ee3f9c80a3</outgoing></exclusiveGateway><exclusiveGateway id=\"node_f8193361-6ab5-462c-b9de-18f6944e86fd\" name=\"\" gatewayDirection=\"Diverging\"><incoming>node_a80501c7-92b9-476b-a02d-27c33fe0ceb4</incoming><outgoing>node_7479c2f3-6f7b-49ef-80d4-2a395778c82e</outgoing><outgoing>node_3b9556a0-2fa7-4928-8db8-ed5c0df1ccd6</outgoing></exclusiveGateway><exclusiveGateway id=\"node_e18194a0-c63d-4506-b531-a7d3fab198b0\" name=\"\" gatewayDirection=\"Diverging\"><incoming>node_f9c173c8-821c-4544-8833-0cfec21da5ef</incoming><outgoing>node_10986126-881d-44cc-8565-01e106e5952f</outgoing><outgoing>node_f24cc9b2-d29d-44f6-9534-685430271d45</outgoing><outgoing>node_02aa900e-9e3b-45e2-ae9f-934c9cdf59d3</outgoing></exclusiveGateway><exclusiveGateway id=\"node_c80c7c9d-4ee5-49af-8697-1ec9db34d4e4\" name=\"\" gatewayDirection=\"Converging\"><incoming>node_c08f8e4d-ef5f-42cc-a84f-bd1b219bfc1a</incoming><incoming>node_8c6e5bd3-4d24-41d2-9b03-3b18a9d5d60c</incoming><incoming>node_e7d53c76-1e09-455f-ac81-351415e0c369</incoming><outgoing>node_2fddb74d-81dd-47f7-8426-4112d35a2de8</outgoing></exclusiveGateway><sequenceFlow id=\"node_237cf3ec-4c3a-41dd-b124-93ee3f9c80a3\" name=\"\" sourceRef=\"node_d92c0aaa-27a9-4257-beef-47be21f3d932\" targetRef=\"node_263258e8-3463-40c1-b881-48f3851f00b2\"/><sequenceFlow id=\"node_a80501c7-92b9-476b-a02d-27c33fe0ceb4\" name=\"\" sourceRef=\"node_263258e8-3463-40c1-b881-48f3851f00b2\" targetRef=\"node_f8193361-6ab5-462c-b9de-18f6944e86fd\"/><sequenceFlow id=\"node_f9c173c8-821c-4544-8833-0cfec21da5ef\" name=\"\" sourceRef=\"node_7facd3b5-eed9-4b82-857c-a1af01a62475\" targetRef=\"node_e18194a0-c63d-4506-b531-a7d3fab198b0\"/><sequenceFlow id=\"node_2fddb74d-81dd-47f7-8426-4112d35a2de8\" name=\"\" sourceRef=\"node_c80c7c9d-4ee5-49af-8697-1ec9db34d4e4\" targetRef=\"node_5e45a7a0-9f7b-4dd8-8a28-e80f70178853\"/><sequenceFlow id=\"node_586e00fe-013d-4681-aedd-7c2383f3d70c\" name=\"\" sourceRef=\"node_03919805-fd3b-432b-ab97-973386916d49\" targetRef=\"node_d92c0aaa-27a9-4257-beef-47be21f3d932\"/><sequenceFlow id=\"node_8c6e5bd3-4d24-41d2-9b03-3b18a9d5d60c\" name=\"\" sourceRef=\"node_c7c0b85f-891e-469f-a170-5062767d94b0\" targetRef=\"node_c80c7c9d-4ee5-49af-8697-1ec9db34d4e4\"/><sequenceFlow id=\"node_3b9556a0-2fa7-4928-8db8-ed5c0df1ccd6\" name=\"\" sourceRef=\"node_f8193361-6ab5-462c-b9de-18f6944e86fd\" targetRef=\"node_c7c0b85f-891e-469f-a170-5062767d94b0\"/><sequenceFlow id=\"node_c08f8e4d-ef5f-42cc-a84f-bd1b219bfc1a\" name=\"\" sourceRef=\"node_05615cc1-bdaf-48e4-b6be-3438a2ae33f6\" targetRef=\"node_c80c7c9d-4ee5-49af-8697-1ec9db34d4e4\"/><sequenceFlow id=\"node_524f9297-69a2-4c24-b731-724f235837da\" name=\"\" sourceRef=\"node_bdb69f54-bb42-47b0-8ce7-1e7414a22f08\" targetRef=\"node_7facd3b5-eed9-4b82-857c-a1af01a62475\"/><sequenceFlow id=\"node_e7d53c76-1e09-455f-ac81-351415e0c369\" name=\"\" sourceRef=\"node_4f748b05-ba68-46ba-8aac-d0c72a2aa3d4\" targetRef=\"node_c80c7c9d-4ee5-49af-8697-1ec9db34d4e4\"/><sequenceFlow id=\"node_354bdcc1-0a08-4de2-8ab5-30661ec0f03f\" name=\"\" sourceRef=\"node_c8661976-1452-4b65-84a7-0dc0eb4a0f1a\" targetRef=\"node_4f748b05-ba68-46ba-8aac-d0c72a2aa3d4\"/><sequenceFlow id=\"node_b6acf838-d801-4c46-8246-43e71dcbd937\" name=\"\" sourceRef=\"node_aef3f10f-c325-4503-a7b3-eddf9b38f665\" targetRef=\"node_c8661976-1452-4b65-84a7-0dc0eb4a0f1a\"/><sequenceFlow id=\"node_f24cc9b2-d29d-44f6-9534-685430271d45\" name=\"\" sourceRef=\"node_e18194a0-c63d-4506-b531-a7d3fab198b0\" targetRef=\"node_c9233d07-c6ce-4171-b898-0c75074f9c60\"/><sequenceFlow id=\"node_7479c2f3-6f7b-49ef-80d4-2a395778c82e\" name=\"\" sourceRef=\"node_f8193361-6ab5-462c-b9de-18f6944e86fd\" targetRef=\"node_05615cc1-bdaf-48e4-b6be-3438a2ae33f6\"/><sequenceFlow id=\"node_a2f00a1a-8dcc-4b2a-b5e8-559fe1cd4ba4\" name=\"\" sourceRef=\"node_ea38e19a-c91b-4977-802a-28654529eb39\" targetRef=\"node_aef3f10f-c325-4503-a7b3-eddf9b38f665\"/><sequenceFlow id=\"node_02aa900e-9e3b-45e2-ae9f-934c9cdf59d3\" name=\"\" sourceRef=\"node_e18194a0-c63d-4506-b531-a7d3fab198b0\" targetRef=\"node_03919805-fd3b-432b-ab97-973386916d49\"/><sequenceFlow id=\"node_5bad1a2c-8296-4bd3-92f4-e9b992731d27\" name=\"\" sourceRef=\"node_25bbc649-d4c3-4fe4-9642-a5e45797b30a\" targetRef=\"node_bdb69f54-bb42-47b0-8ce7-1e7414a22f08\"/><sequenceFlow id=\"node_a4cc56da-b69a-4189-91d2-57cbc73e3092\" name=\"\" sourceRef=\"node_c9233d07-c6ce-4171-b898-0c75074f9c60\" targetRef=\"node_ea38e19a-c91b-4977-802a-28654529eb39\"/><sequenceFlow id=\"node_10986126-881d-44cc-8565-01e106e5952f\" name=\"\" sourceRef=\"node_e18194a0-c63d-4506-b531-a7d3fab198b0\" targetRef=\"node_d92c0aaa-27a9-4257-beef-47be21f3d932\"/></process><bpmndi:BPMNDiagram id=\"id_-2106327581\"><bpmndi:BPMNPlane bpmnElement=\"proc_1294675513\"><bpmndi:BPMNShape bpmnElement=\"node_03919805-fd3b-432b-ab97-973386916d49\"><dc:Bounds x=\"466.0\" y=\"151.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_5e45a7a0-9f7b-4dd8-8a28-e80f70178853\"><dc:Bounds x=\"1191.0\" y=\"178.5\" width=\"25.0\" height=\"25.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_bdb69f54-bb42-47b0-8ce7-1e7414a22f08\"><dc:Bounds x=\"131.0\" y=\"158.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_f8193361-6ab5-462c-b9de-18f6944e86fd\"><dc:Bounds x=\"883.5\" y=\"139.5\" width=\"25.0\" height=\"25.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_c7c0b85f-891e-469f-a170-5062767d94b0\"><dc:Bounds x=\"986.0\" y=\"101.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_4f748b05-ba68-46ba-8aac-d0c72a2aa3d4\"><dc:Bounds x=\"986.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_c8661976-1452-4b65-84a7-0dc0eb4a0f1a\"><dc:Bounds x=\"856.0\" y=\"234.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_aef3f10f-c325-4503-a7b3-eddf9b38f665\"><dc:Bounds x=\"726.0\" y=\"229.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_d92c0aaa-27a9-4257-beef-47be21f3d932\"><dc:Bounds x=\"623.5\" y=\"133.5\" width=\"25.0\" height=\"25.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_263258e8-3463-40c1-b881-48f3851f00b2\"><dc:Bounds x=\"726.0\" y=\"129.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_c80c7c9d-4ee5-49af-8697-1ec9db34d4e4\"><dc:Bounds x=\"1116.0\" y=\"178.5\" width=\"25.0\" height=\"25.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_e18194a0-c63d-4506-b531-a7d3fab198b0\"><dc:Bounds x=\"391.0\" y=\"159.5\" width=\"25.0\" height=\"25.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_25bbc649-d4c3-4fe4-9642-a5e45797b30a\"><dc:Bounds x=\"1.0\" y=\"158.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_05615cc1-bdaf-48e4-b6be-3438a2ae33f6\"><dc:Bounds x=\"986.0\" y=\"171.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_7facd3b5-eed9-4b82-857c-a1af01a62475\"><dc:Bounds x=\"261.0\" y=\"156.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_ea38e19a-c91b-4977-802a-28654529eb39\"><dc:Bounds x=\"596.0\" y=\"225.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"node_c9233d07-c6ce-4171-b898-0c75074f9c60\"><dc:Bounds x=\"466.0\" y=\"221.0\" width=\"80.0\" height=\"40.0\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape><bpmndi:BPMNEdge bpmnElement=\"node_f9c173c8-821c-4544-8833-0cfec21da5ef\"><di:waypoint x=\"301.0\" y=\"176.0\"/><di:waypoint x=\"403.5\" y=\"172.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_237cf3ec-4c3a-41dd-b124-93ee3f9c80a3\"><di:waypoint x=\"636.0\" y=\"146.0\"/><di:waypoint x=\"766.0\" y=\"149.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_524f9297-69a2-4c24-b731-724f235837da\"><di:waypoint x=\"171.0\" y=\"178.0\"/><di:waypoint x=\"301.0\" y=\"176.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_02aa900e-9e3b-45e2-ae9f-934c9cdf59d3\"><di:waypoint x=\"403.5\" y=\"172.0\"/><di:waypoint x=\"506.0\" y=\"171.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_e7d53c76-1e09-455f-ac81-351415e0c369\"><di:waypoint x=\"1026.0\" y=\"261.0\"/><di:waypoint x=\"1128.5\" y=\"191.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_a80501c7-92b9-476b-a02d-27c33fe0ceb4\"><di:waypoint x=\"766.0\" y=\"149.0\"/><di:waypoint x=\"896.0\" y=\"152.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_586e00fe-013d-4681-aedd-7c2383f3d70c\"><di:waypoint x=\"506.0\" y=\"171.0\"/><di:waypoint x=\"636.0\" y=\"146.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_3b9556a0-2fa7-4928-8db8-ed5c0df1ccd6\"><di:waypoint x=\"896.0\" y=\"152.0\"/><di:waypoint x=\"1026.0\" y=\"121.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_a4cc56da-b69a-4189-91d2-57cbc73e3092\"><di:waypoint x=\"506.0\" y=\"241.0\"/><di:waypoint x=\"636.0\" y=\"245.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_7479c2f3-6f7b-49ef-80d4-2a395778c82e\"><di:waypoint x=\"896.0\" y=\"152.0\"/><di:waypoint x=\"1026.0\" y=\"191.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_354bdcc1-0a08-4de2-8ab5-30661ec0f03f\"><di:waypoint x=\"896.0\" y=\"254.0\"/><di:waypoint x=\"1026.0\" y=\"261.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_a2f00a1a-8dcc-4b2a-b5e8-559fe1cd4ba4\"><di:waypoint x=\"636.0\" y=\"245.0\"/><di:waypoint x=\"766.0\" y=\"249.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_f24cc9b2-d29d-44f6-9534-685430271d45\"><di:waypoint x=\"403.5\" y=\"172.0\"/><di:waypoint x=\"506.0\" y=\"241.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_2fddb74d-81dd-47f7-8426-4112d35a2de8\"><di:waypoint x=\"1128.5\" y=\"191.0\"/><di:waypoint x=\"1203.5\" y=\"191.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_c08f8e4d-ef5f-42cc-a84f-bd1b219bfc1a\"><di:waypoint x=\"1026.0\" y=\"191.0\"/><di:waypoint x=\"1128.5\" y=\"191.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_8c6e5bd3-4d24-41d2-9b03-3b18a9d5d60c\"><di:waypoint x=\"1026.0\" y=\"121.0\"/><di:waypoint x=\"1128.5\" y=\"191.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_10986126-881d-44cc-8565-01e106e5952f\"><di:waypoint x=\"403.5\" y=\"172.0\"/><di:waypoint x=\"506.0\" y=\"121.0\"/><di:waypoint x=\"636.0\" y=\"146.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_5bad1a2c-8296-4bd3-92f4-e9b992731d27\"><di:waypoint x=\"41.0\" y=\"178.0\"/><di:waypoint x=\"171.0\" y=\"178.0\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"node_b6acf838-d801-4c46-8246-43e71dcbd937\"><di:waypoint x=\"766.0\" y=\"249.0\"/><di:waypoint x=\"896.0\" y=\"254.0\"/></bpmndi:BPMNEdge></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions> ";

        String layout = "{\"elements\":{\"nodes\":[{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#6899b9\",\"name\":\"Analyze Defect\\n\\n604\",\"width\":\"66px\",\"id\":\"1\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":116,\"y\":305},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#b1c6da\",\"name\":\"Archive Repair\\n\\n282\",\"width\":\"66px\",\"id\":\"2\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":1140,\"y\":21},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#d0d9e7\",\"name\":\"Cancel Orders\\n\\n147\",\"width\":\"66px\",\"id\":\"3\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":1140,\"y\":163},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#cdd8e6\",\"name\":\"Check Repair Status\\n\\n157\",\"width\":\"66px\",\"id\":\"4\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":756,\"y\":163},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a2bdd3\",\"name\":\"Contact Courier\\n\\n349\",\"width\":\"66px\",\"id\":\"5\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":884,\"y\":305},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#cfd9e7\",\"name\":\"Disassemble Product\\n\\n151\",\"width\":\"66px\",\"id\":\"7\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":628,\"y\":163},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#cad6e5\",\"name\":\"Inform User\\n\\n171\",\"width\":\"66px\",\"id\":\"8\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":1140,\"y\":377},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#acc3d8\",\"name\":\"Install Component\\n\\n303\",\"width\":\"66px\",\"id\":\"9\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":1012,\"y\":163},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#abc2d7\",\"name\":\"Order Component\\n\\n308\",\"width\":\"66px\",\"id\":\"10\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":884,\"y\":163},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#5a8fb3\",\"name\":\"Receive Enquiry\\n\\n669\",\"width\":\"66px\",\"id\":\"11\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":244,\"y\":305},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"name\":\"Repair\\n\\n1047\",\"width\":\"66px\",\"id\":\"12\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":372,\"y\":305},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#176695\",\"name\":\"Test Repair\\n\\n964\",\"width\":\"66px\",\"id\":\"13\",\"textwidth\":\"56px\",\"height\":\"30px\"},\"position\":{\"x\":1012,\"y\":341},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C0A3A1\",\"name\":\"\",\"width\":\"15px\",\"id\":\"14\",\"textwidth\":\"56px\",\"height\":\"15px\"},\"position\":{\"x\":1242.5,\"y\":234},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C1C9B0\",\"name\":\"\",\"width\":\"15px\",\"id\":\"15\",\"textwidth\":\"56px\",\"height\":\"15px\"},\"position\":{\"x\":13.5,\"y\":305},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}],\"edges\":[{\"data\":{\"strength\":13.46,\"color\":\"#5c5c5c\",\"style\":\"solid\",\"source\":\"13\",\"label\":\"88\",\"target\":\"8\",\"id\":\"6d5d94b9-7d32-4a52-906b-fd890b70def9\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":47.09,\"color\":\"#484848\",\"style\":\"solid\",\"source\":\"1\",\"label\":\"308\",\"target\":\"11\",\"id\":\"1104f399-de80-4c32-b813-389e22a9c725\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":37.46,\"color\":\"#4e4e4e\",\"style\":\"dashed\",\"source\":\"2\",\"label\":\"245\",\"target\":\"14\",\"id\":\"0718faa3-0cc8-44cc-a2f1-c972c0243d05\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":22.32,\"color\":\"#575757\",\"style\":\"dashed\",\"source\":\"3\",\"label\":\"146\",\"target\":\"14\",\"id\":\"a69156b3-fe1c-4e6e-8d67-410cf73f7e75\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":14.22,\"color\":\"#5c5c5c\",\"style\":\"solid\",\"source\":\"4\",\"label\":\"93\",\"target\":\"10\",\"id\":\"1a3912ab-144f-4360-aabd-c197e25acfeb\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":20.8,\"color\":\"#585858\",\"style\":\"solid\",\"source\":\"5\",\"label\":\"136\",\"target\":\"13\",\"id\":\"36661a2b-6f1b-4066-be58-7e67fd95b465\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":60.86,\"color\":\"#404040\",\"style\":\"dashed\",\"source\":\"15\",\"label\":\"398\",\"target\":\"1\",\"id\":\"ef36f4ba-36ff-4fd8-be56-23038b8e6e57\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":14.68,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"7\",\"label\":\"96\",\"target\":\"4\",\"id\":\"119f49ed-a93d-4fdf-86b1-f86ac85c9d93\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":26.15,\"color\":\"#555555\",\"style\":\"dashed\",\"source\":\"8\",\"label\":\"171\",\"target\":\"14\",\"id\":\"60b450f2-604c-4c34-89c6-26822f98de7b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":21.87,\"color\":\"#575757\",\"style\":\"solid\",\"source\":\"9\",\"label\":\"143\",\"target\":\"3\",\"id\":\"4cc9a1f7-ad83-4993-bff5-fec303ac6239\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":20.18,\"color\":\"#585858\",\"style\":\"solid\",\"source\":\"10\",\"label\":\"132\",\"target\":\"9\",\"id\":\"5fa08a48-277d-414a-bea1-5e6920a13420\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":36.54,\"color\":\"#4e4e4e\",\"style\":\"solid\",\"source\":\"11\",\"label\":\"239\",\"target\":\"12\",\"id\":\"03bcdce0-dfad-4b1c-9295-8b8f1524d69a\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":20.8,\"color\":\"#585858\",\"style\":\"solid\",\"source\":\"12\",\"label\":\"136\",\"target\":\"5\",\"id\":\"c993d0a5-cde1-498f-be88-f8d2b1e77f12\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":50,\"color\":\"#424242\",\"style\":\"solid\",\"source\":\"12\",\"label\":100,\"target\":\"7\",\"id\":\"12x7\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"solid\",\"source\":\"12\",\"label\":\"654\",\"target\":\"13\",\"id\":\"003326c2-be06-4edd-aef1-8d3e4ee0067a\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":37,\"color\":\"#4e4e4e\",\"style\":\"solid\",\"source\":\"13\",\"label\":\"242\",\"target\":\"2\",\"id\":\"ce8035a7-5ba0-447c-9e62-622f570664ad\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}]},\"style\":[{\"selector\":\"node\",\"style\":{\"text-valign\":\"center\",\"text-border-width\":\"10px\",\"text-wrap\":\"wrap\",\"text-max-width\":\"data(textwidth)\",\"font-size\":\"7px\",\"height\":\"data(height)\",\"width\":\"data(width)\",\"shape\":\"data(shape)\",\"background-color\":\"data(color)\",\"padding\":\"5px\",\"label\":\"data(name)\"}},{\"selector\":\":selected\",\"style\":{\"border-color\":\"#333\",\"border-width\":\"3px\"}},{\"selector\":\"edge\",\"style\":{\"label\":\"data(label)\",\"text-margin-y\":\"-10px\",\"color\":\"data(color)\",\"font-size\":\"7px\",\"opacity\":\"1\",\"width\":\"mapData(strength, 0, 100, 1, 6)\",\"line-style\":\"data(style)\",\"line-color\":\"data(color)\",\"curve-style\":\"bezier\",\"target-arrow-shape\":\"triangle\",\"source-arrow-color\":\"data(color)\",\"target-arrow-color\":\"data(color)\"}},{\"selector\":\"edge.questionable\",\"style\":{\"line-style\":\"dotted\",\"target-arrow-shape\":\"diamond\"}},{\"selector\":\".faded\",\"style\":{\"text-opacity\":\"0\",\"opacity\":\"0.25\"}},{\"selector\":\".edgebendediting-hasbendpoints\",\"style\":{\"curve-style\":\"segments\",\"segment-distances\":\"fn\",\"segment-weights\":\"fn\",\"edge-distances\":\"node-position\"}}],\"zoomingEnabled\":true,\"userZoomingEnabled\":true,\"zoom\":1,\"minZoom\":1.0E-50,\"maxZoom\":1.0E50,\"panningEnabled\":true,\"userPanningEnabled\":true,\"pan\":{\"x\":0,\"y\":0},\"boxSelectionEnabled\":true,\"renderer\":{\"name\":\"canvas\"},\"wheelSensitivity\":0.1}";

        BPMNUpdater bpmnUpdater = new BPMNUpdater();
        bpmnModel = bpmnUpdater.getUpdatedBPMN(bpmnModel, layout, true);

        System.out.println("Original BPMN");
        System.out.println(bpmnModel);

//        System.out.println("animationData");
//        System.out.println(animationData);
    }

    private String label = "Animate Logs";
    private String groupLabel = "Analyze";

    @Inject private EventLogService eventLogService;
    @Inject private LogAnimationService logAnimationService;
    @Inject private ProcessService processService;
    @Inject private ManagerService managerService;

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public void execute(PortalContext portalContext) {

        List<Object[]> processes = new ArrayList<>();
        List<LogSummaryType> logSummaries = new ArrayList<>();

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        for (SummaryType summary: elements.keySet()) {
            if (summary instanceof ProcessSummaryType) {
                for (VersionSummaryType version: elements.get(summary)) {
                    Object[] pair = { (ProcessSummaryType) summary, version };
                    processes.add(pair);
                }
            }
            else if (summary instanceof LogSummaryType) {
                logSummaries.add((LogSummaryType) summary);
            }
        }

        if (processes.size() != 1 || logSummaries.size() < 1) {
            Messagebox.show("Select exactly one BPMN model and at least one log", "Attention", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        ProcessSummaryType process = (ProcessSummaryType) processes.get(0)[0];
        VersionSummaryType vst = (VersionSummaryType) processes.get(0)[1];

        // Fetch the BPMN serialization of the model
        int procID = process.getId();
        String procName = process.getName();
        String branch = vst.getName();
        Version version = new Version(vst.getVersionNumber());
        try {
            String bpmn = processService.getBPMNRepresentation(procName, procID, branch, version);

            // Fetch the XLog representations of the logs
            List<LogAnimationService.Log> logs = new ArrayList<>();
            Iterator<String> colors = Arrays.asList("#0088FF", "#FF8800", "#88FF00").iterator();
            for (LogSummaryType logSummary: logSummaries) {
                LogAnimationService.Log log = new LogAnimationService.Log();
                log.fileName = logSummary.getName();
                log.xlog     = eventLogService.getXLog(logSummary.getId());
                log.color    = colors.hasNext() ? colors.next() : "red";
                logs.add(log);
            }
            
            String username = portalContext.getCurrentUser().getUsername();
            EditSessionType editSession1 = createEditSession(username, process, vst, process.getOriginalNativeType(), null /*annotation*/);
            Set<RequestParameterType<?>> requestParameterTypes = new HashSet<>();
            SignavioSession session = new SignavioSession(editSession1, null, null, process, vst, null, null, requestParameterTypes);
            session.put("logAnimationService", logAnimationService);
            session.put("logs", logs);

            String id = UUID.randomUUID().toString();
            UserSessionManager.setEditSession(id, session);
            Clients.evalJavaScript("window.open('../loganimation/animateLogInSignavio.zul?id=" + id + "')");

        } catch (RepositoryException e) {
            Messagebox.show("Unable to read " + procName, "Attention", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void execute(PortalContext portalContext, String bpmn, String layout, XLog eventlog, boolean maintain_gateways) {
        try {
            List<LogAnimationService.Log> logs = new ArrayList<>();
            Iterator<String> colors = Arrays.asList("#0088FF", "#FF8800", "#88FF00").iterator();
            LogAnimationService.Log log = new LogAnimationService.Log();
            log.fileName = "Dummy";
            log.xlog     = eventlog;
            log.color    = colors.hasNext() ? colors.next() : "red";
            logs.add(log);

            String username = portalContext.getCurrentUser().getUsername();

            ProcessSummaryType processSummaryType = new ProcessSummaryType();
            processSummaryType.setDomain("Log-Visualizer");
            processSummaryType.setName("Log-Visualizer-Model");
            processSummaryType.setId(1);
            processSummaryType.setMakePublic(true);

            VersionSummaryType versionSummaryType = new VersionSummaryType();
            versionSummaryType.setName("Log-Visualizer-Model");
            versionSummaryType.setVersionNumber("1.0");

            EditSessionType editSession = createEditSession(username, processSummaryType, versionSummaryType, "BPMN 2.0", null);
            Set<RequestParameterType<?>> requestParameterTypes = new HashSet<>();
            SignavioSession session = new SignavioSession(editSession, null, null, processSummaryType, versionSummaryType, null, null, requestParameterTypes);

            String jsonDataEscape = escapeQuotedJavascript(bpmn);

            BPMNUpdater bpmnUpdater = new BPMNUpdater();
            jsonDataEscape = bpmnUpdater.getUpdatedBPMN(jsonDataEscape, layout, !maintain_gateways);

            System.out.println("Final BPMN");
//            System.out.println(jsonDataEscape);
            session.put("JSONData", jsonDataEscape);

            if (logAnimationService != null) {  // logAnimationService is null if invoked from the editor toobar
                String animationData = logAnimationService.createAnimation(bpmn, logs);

                AnimationUpdater animationUpdater = new AnimationUpdater();
                animationData = animationUpdater.updateAnimationData(animationData, bpmnUpdater.getRemovedFlowIDs());

                session.put("animationData", escapeQuotedJavascript(animationData));
                System.out.println("ANIMATIONDATA");
//                System.out.println(escapeQuotedJavascript(animationData));
            }

            session.put("logs", logs);

            String id = UUID.randomUUID().toString();
            UserSessionManager.setEditSession(id, session);
            Clients.evalJavaScript("window.open('../loganimation/animateLogInSignavio.zul?id=" + id + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static EditSessionType createEditSession(final String username, final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation) {

        EditSessionType editSession = new EditSessionType();

        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType.equals("XPDL 2.2")?"BPMN 2.0":nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(username);
        editSession.setPublicModel(process.isMakePublic());
        editSession.setOriginalBranchName(version.getName());
        editSession.setOriginalVersionNumber(version.getVersionNumber());
        editSession.setCurrentVersionNumber(version.getVersionNumber());
        editSession.setMaxVersionNumber(findMaxVersion(process));

        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        if (annotation == null) {
            editSession.setWithAnnotation(false);
        } else {
            editSession.setWithAnnotation(true);
            editSession.setAnnotation(annotation);
        }

        return editSession;
    }

    /* From a list of version summary types find the max version number. */
    private static String findMaxVersion(ProcessSummaryType process) {
        Version versionNum;
        Version max = new Version(0, 0);
        for (VersionSummaryType version : process.getVersionSummaries()) {
            versionNum = new Version(version.getVersionNumber());
            if (versionNum.compareTo(max) > 0) {
                max = versionNum;
            }
        }
        return max.toString();
    }

    /**
     * @param json
     * @return the <var>json</var> escaped so that it can be quoted in Javascript.
     *     Specifically, it replaces apostrophes with \\u0027 and removes embedded newlines and leading and trailing whitespace.
     */
    private static String escapeQuotedJavascript(String json) {
        return json.replace("\n", " ").replace("'", "\\u0027").trim();
    }
}
