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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/11/17.
 */
public class BPMNUpdater {

    private Set<String> splitGatewayIDs = new HashSet<>();
    private Set<String> joinGatewayIDs = new HashSet<>();
    private Set<String> removedFlowIDs = new HashSet<>();

    public static void main(String[] args) {
        String bpmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"  xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"  xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"  xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  targetNamespace=\"http://www.omg.org/bpmn20\"  xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"><process id=\"proc_712393628\"> <startEvent id=\"node_f36eadb8-4f57-4fac-830c-edb91249567b\" name=\"|&gt;\" isInterrupting=\"false\"/> <endEvent id=\"node_38972e18-afb4-4240-9a06-4141ea5285f1\" name=\"[]\" isInterrupting=\"false\"/> <task id=\"node_7093d2d3-86fb-4364-b764-764413445450\" name=\"Release A\"/> <task id=\"node_136be6aa-acad-4d2f-92df-de896875eece\" name=\"Return ER\"/> <task id=\"node_f6f4a85f-6adb-4ac6-b646-15ee7e95dd81\" name=\"ER Triage\"/> <task id=\"node_4f4b60e5-5f39-4a01-9331-7b5c62be2311\" name=\"CRP\"/> <task id=\"node_399e7bca-0ddc-4340-a2b4-6d255b0cd163\" name=\"ER Sepsis Triage\"/> <task id=\"node_fb34e943-d739-4fac-bba0-f5559100ea61\" name=\"ER Registration\"/> <task id=\"node_37e2dfac-c034-4a19-9562-8c51ab79bfb5\" name=\"Release C\"/> <task id=\"node_4b72fe80-6be5-4182-bb29-094fc960ab1b\" name=\"Release B\"/> <task id=\"node_8581618c-77e0-49b3-a838-999e70809b17\" name=\"IV Antibiotics\"/> <task id=\"node_326f9691-0304-487a-b55c-2913726a5467\" name=\"IV Liquid\"/> <task id=\"node_2972a228-1279-4c42-90b2-5959b052dece\" name=\"LacticAcid\"/> <task id=\"node_768ac5ab-7780-4e7f-8506-02190ed5d78a\" name=\"Leucocytes\"/> <task id=\"node_ef5a7bf6-1665-442b-b6fd-cd42cdc79071\" name=\"Release D\"/> <task id=\"node_1623da42-cb40-4cc6-b994-bc5283198917\" name=\"Release E\"/> <task id=\"node_f15780ac-7379-4d34-8b9e-69b35fd27cce\" name=\"Admission IC\"/> <task id=\"node_9b42ee80-1ca7-4038-ba88-50a754bb8908\" name=\"Admission NC\"/> <exclusiveGateway id=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\" name=\"\" gatewayDirection=\"Diverging\"> <incoming> node_d3379277-598e-4738-8b2f-293570ad5b32</incoming> <outgoing> node_fd70f7d7-ced3-44da-8c62-4c706ab9a6a4</outgoing> <outgoing> node_8e06cb58-80bb-46ea-b34b-9fae605b0804</outgoing> <outgoing> node_d9fb3acc-10e2-4087-ae4a-d34c1acaf2fe</outgoing> <outgoing> node_93f55c17-de40-437f-905f-887428f3f1eb</outgoing> <outgoing> node_9689c594-864b-4d5f-a088-9ac26a57d153</outgoing> </exclusiveGateway> <exclusiveGateway id=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\" name=\"\" gatewayDirection=\"Converging\"> <incoming> node_221b4b4a-b00d-4dff-9da7-c0cc972edfc4</incoming> <incoming> node_ab2d0b5e-a0b9-4e4a-8274-dd07525149b7</incoming> <incoming> node_1e6d194e-71d3-4b16-87e7-1363ce9bb9fe</incoming> <incoming> node_39eb9ec0-288c-462e-8b08-7a5525735753</incoming> <incoming> node_e758a736-ac64-44a3-a631-276219d612f7</incoming> <outgoing> node_7da8530f-da2b-4b25-bc2e-e28296252ce4</outgoing> </exclusiveGateway> <exclusiveGateway id=\"node_afdf9b6e-052c-4ef9-8e1a-7e630025b192\" name=\"\" gatewayDirection=\"Converging\"> <incoming> node_a110e73f-5f3d-4aac-bfb0-3ffb91b4af04</incoming> <incoming> node_cc57b403-e35f-4e67-989a-c84046776990</incoming> <outgoing> node_86d54ca2-762b-4f72-9b17-d8dd0748cb0c</outgoing> </exclusiveGateway> <exclusiveGateway id=\"node_261a29e3-6a74-4cf1-9156-fa6d716f5e0d\" name=\"\" gatewayDirection=\"Diverging\"> <incoming> node_92d688c0-33d4-43e1-8c50-29dd7780e399</incoming> <outgoing> node_20b6c208-dacf-44d6-aa3b-69cf57bbdf56</outgoing> <outgoing> node_9b834854-4784-48da-9313-27d31cc0ca6d</outgoing> </exclusiveGateway> <sequenceFlow id=\"node_2e3955d3-9c66-4629-9d5a-76efd87e63e6\" name=\"[905.00 25000.00]\" sourceRef=\"node_f6f4a85f-6adb-4ac6-b646-15ee7e95dd81\" targetRef=\"node_399e7bca-0ddc-4340-a2b4-6d255b0cd163\"/> <sequenceFlow id=\"node_9b834854-4784-48da-9313-27d31cc0ca6d\" name=\"[46.00 162500.00]\" sourceRef=\"node_261a29e3-6a74-4cf1-9156-fa6d716f5e0d\" targetRef=\"node_f15780ac-7379-4d34-8b9e-69b35fd27cce\"/> <sequenceFlow id=\"node_221b4b4a-b00d-4dff-9da7-c0cc972edfc4\" name=\"[55.00 0.00]\" sourceRef=\"node_4b72fe80-6be5-4182-bb29-094fc960ab1b\" targetRef=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\"/> <sequenceFlow id=\"node_9d224052-fd98-4e72-a00f-859b2cf218db\" name=\"[276.00 4083842000.00]\" sourceRef=\"node_7093d2d3-86fb-4364-b764-764413445450\" targetRef=\"node_136be6aa-acad-4d2f-92df-de896875eece\"/> <sequenceFlow id=\"node_ab2d0b5e-a0b9-4e4a-8274-dd07525149b7\" name=\"[5.00 0.00]\" sourceRef=\"node_1623da42-cb40-4cc6-b994-bc5283198917\" targetRef=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\"/> <sequenceFlow id=\"node_fd70f7d7-ced3-44da-8c62-4c706ab9a6a4\" name=\"[13.00 351000000.00]\" sourceRef=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\" targetRef=\"node_37e2dfac-c034-4a19-9562-8c51ab79bfb5\"/> <sequenceFlow id=\"node_82f486c3-0c4a-4db7-b7a9-a2fe62091334\" name=\"[501.00 6000.00]\" sourceRef=\"node_326f9691-0304-487a-b55c-2913726a5467\" targetRef=\"node_8581618c-77e0-49b3-a838-999e70809b17\"/> <sequenceFlow id=\"node_d3379277-598e-4738-8b2f-293570ad5b32\" name=\"[369.00 0.00]\" sourceRef=\"node_4f4b60e5-5f39-4a01-9331-7b5c62be2311\" targetRef=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\"/> <sequenceFlow id=\"node_39eb9ec0-288c-462e-8b08-7a5525735753\" name=\"[14.00 0.00]\" sourceRef=\"node_ef5a7bf6-1665-442b-b6fd-cd42cdc79071\" targetRef=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\"/> <sequenceFlow id=\"node_d9fb3acc-10e2-4087-ae4a-d34c1acaf2fe\" name=\"[3.00 60300000.00]\" sourceRef=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\" targetRef=\"node_1623da42-cb40-4cc6-b994-bc5283198917\"/> <sequenceFlow id=\"node_93f55c17-de40-437f-905f-887428f3f1eb\" name=\"[12.00 223650000.00]\" sourceRef=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\" targetRef=\"node_ef5a7bf6-1665-442b-b6fd-cd42cdc79071\"/> <sequenceFlow id=\"node_a110e73f-5f3d-4aac-bfb0-3ffb91b4af04\" name=\"[565.00 0.00]\" sourceRef=\"node_2972a228-1279-4c42-90b2-5959b052dece\" targetRef=\"node_afdf9b6e-052c-4ef9-8e1a-7e630025b192\"/> <sequenceFlow id=\"node_f66b59a7-46c4-4483-809a-c91f2058c920\" name=\"[971.00 474000.00]\" sourceRef=\"node_fb34e943-d739-4fac-bba0-f5559100ea61\" targetRef=\"node_f6f4a85f-6adb-4ac6-b646-15ee7e95dd81\"/> <sequenceFlow id=\"node_8e06cb58-80bb-46ea-b34b-9fae605b0804\" name=\"[322.00 112500000.00]\" sourceRef=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\" targetRef=\"node_7093d2d3-86fb-4364-b764-764413445450\"/> <sequenceFlow id=\"node_86d54ca2-762b-4f72-9b17-d8dd0748cb0c\" name=\"[973.00 0.00]\" sourceRef=\"node_afdf9b6e-052c-4ef9-8e1a-7e630025b192\" targetRef=\"node_768ac5ab-7780-4e7f-8506-02190ed5d78a\"/> <sequenceFlow id=\"node_20b6c208-dacf-44d6-aa3b-69cf57bbdf56\" name=\"[489.00 352000.00]\" sourceRef=\"node_261a29e3-6a74-4cf1-9156-fa6d716f5e0d\" targetRef=\"node_9b42ee80-1ca7-4038-ba88-50a754bb8908\"/> <sequenceFlow id=\"node_7da8530f-da2b-4b25-bc2e-e28296252ce4\" name=\"[384.00 0.00]\" sourceRef=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\" targetRef=\"node_38972e18-afb4-4240-9a06-4141ea5285f1\"/> <sequenceFlow id=\"node_e170bee7-bb7a-4c04-8eb9-8cab076e4b18\" name=\"[41.00 11318000.00]\" sourceRef=\"node_f15780ac-7379-4d34-8b9e-69b35fd27cce\" targetRef=\"node_2972a228-1279-4c42-90b2-5959b052dece\"/> <sequenceFlow id=\"node_f8bda739-2e5a-4ee6-ba72-116042f24ae8\" name=\"[995.00 0.00]\" sourceRef=\"node_f36eadb8-4f57-4fac-830c-edb91249567b\" targetRef=\"node_fb34e943-d739-4fac-bba0-f5559100ea61\"/> <sequenceFlow id=\"node_2ca15e09-ea79-43ed-b214-2959c236558c\" name=\"[1778.00 0.00]\" sourceRef=\"node_768ac5ab-7780-4e7f-8506-02190ed5d78a\" targetRef=\"node_4f4b60e5-5f39-4a01-9331-7b5c62be2311\"/> <sequenceFlow id=\"node_e758a736-ac64-44a3-a631-276219d612f7\" name=\"[291.00 0.00]\" sourceRef=\"node_136be6aa-acad-4d2f-92df-de896875eece\" targetRef=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\"/> <sequenceFlow id=\"node_cc57b403-e35f-4e67-989a-c84046776990\" name=\"[408.00 89890500.00]\" sourceRef=\"node_9b42ee80-1ca7-4038-ba88-50a754bb8908\" targetRef=\"node_afdf9b6e-052c-4ef9-8e1a-7e630025b192\"/> <sequenceFlow id=\"node_0ddc0130-c0a6-4578-abba-7ae15244ad20\" name=\"[285.00 23000.00]\" sourceRef=\"node_399e7bca-0ddc-4340-a2b4-6d255b0cd163\" targetRef=\"node_326f9691-0304-487a-b55c-2913726a5467\"/> <sequenceFlow id=\"node_9689c594-864b-4d5f-a088-9ac26a57d153\" name=\"[19.00 81900000.00]\" sourceRef=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\" targetRef=\"node_4b72fe80-6be5-4182-bb29-094fc960ab1b\"/> <sequenceFlow id=\"node_1e6d194e-71d3-4b16-87e7-1363ce9bb9fe\" name=\"[19.00 0.00]\" sourceRef=\"node_37e2dfac-c034-4a19-9562-8c51ab79bfb5\" targetRef=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\"/> <sequenceFlow id=\"node_92d688c0-33d4-43e1-8c50-29dd7780e399\" name=\"[535.00 0.00]\" sourceRef=\"node_8581618c-77e0-49b3-a838-999e70809b17\" targetRef=\"node_261a29e3-6a74-4cf1-9156-fa6d716f5e0d\"/> </process> <bpmndi:BPMNDiagram id=\"id_1834577630\"> <bpmndi:BPMNPlane bpmnElement=\"proc_712393628\"> <bpmndi:BPMNShape bpmnElement=\"node_7093d2d3-86fb-4364-b764-764413445450\"> <dc:Bounds x=\"1471.0\" y=\"206.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_261a29e3-6a74-4cf1-9156-fa6d716f5e0d\"> <dc:Bounds x=\"726.0\" y=\"251.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_f36eadb8-4f57-4fac-830c-edb91249567b\"> <dc:Bounds x=\"1.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_fb34e943-d739-4fac-bba0-f5559100ea61\"> <dc:Bounds x=\"76.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_cfeffa21-082b-4474-9e52-ad680810dee4\"> <dc:Bounds x=\"1731.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_4f4b60e5-5f39-4a01-9331-7b5c62be2311\"> <dc:Bounds x=\"1266.0\" y=\"247.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_136be6aa-acad-4d2f-92df-de896875eece\"> <dc:Bounds x=\"1601.0\" y=\"171.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\"> <dc:Bounds x=\"1396.0\" y=\"260.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_9b42ee80-1ca7-4038-ba88-50a754bb8908\"> <dc:Bounds x=\"931.0\" y=\"202.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_f15780ac-7379-4d34-8b9e-69b35fd27cce\"> <dc:Bounds x=\"801.0\" y=\"271.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_ef5a7bf6-1665-442b-b6fd-cd42cdc79071\"> <dc:Bounds x=\"1601.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_8581618c-77e0-49b3-a838-999e70809b17\"> <dc:Bounds x=\"596.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_37e2dfac-c034-4a19-9562-8c51ab79bfb5\"> <dc:Bounds x=\"1601.0\" y=\"311.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_326f9691-0304-487a-b55c-2913726a5467\"> <dc:Bounds x=\"466.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_1623da42-cb40-4cc6-b994-bc5283198917\"> <dc:Bounds x=\"1601.0\" y=\"381.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_4b72fe80-6be5-4182-bb29-094fc960ab1b\"> <dc:Bounds x=\"1601.0\" y=\"101.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_afdf9b6e-052c-4ef9-8e1a-7e630025b192\"> <dc:Bounds x=\"1061.0\" y=\"246.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_399e7bca-0ddc-4340-a2b4-6d255b0cd163\"> <dc:Bounds x=\"336.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_2972a228-1279-4c42-90b2-5959b052dece\"> <dc:Bounds x=\"931.0\" y=\"272.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_f6f4a85f-6adb-4ac6-b646-15ee7e95dd81\"> <dc:Bounds x=\"206.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_38972e18-afb4-4240-9a06-4141ea5285f1\"> <dc:Bounds x=\"1806.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_768ac5ab-7780-4e7f-8506-02190ed5d78a\"> <dc:Bounds x=\"1136.0\" y=\"244.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNEdge bpmnElement=\"node_9689c594-864b-4d5f-a088-9ac26a57d153\"> <di:waypoint x=\"1408.5\" y=\"273.0\"/> <di:waypoint x=\"1511.0\" y=\"176.0\"/> <di:waypoint x=\"1641.0\" y=\"121.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_93f55c17-de40-437f-905f-887428f3f1eb\"> <di:waypoint x=\"1408.5\" y=\"273.0\"/> <di:waypoint x=\"1511.0\" y=\"276.0\"/> <di:waypoint x=\"1641.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_9b834854-4784-48da-9313-27d31cc0ca6d\"> <di:waypoint x=\"738.5\" y=\"264.0\"/> <di:waypoint x=\"841.0\" y=\"291.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_7da8530f-da2b-4b25-bc2e-e28296252ce4\"> <di:waypoint x=\"1743.5\" y=\"261.0\"/> <di:waypoint x=\"1818.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_2ca15e09-ea79-43ed-b214-2959c236558c\"> <di:waypoint x=\"1176.0\" y=\"264.0\"/> <di:waypoint x=\"1306.0\" y=\"267.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_82f486c3-0c4a-4db7-b7a9-a2fe62091334\"> <di:waypoint x=\"506.0\" y=\"261.0\"/> <di:waypoint x=\"636.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_e170bee7-bb7a-4c04-8eb9-8cab076e4b18\"> <di:waypoint x=\"841.0\" y=\"291.0\"/> <di:waypoint x=\"971.0\" y=\"292.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_e758a736-ac64-44a3-a631-276219d612f7\"> <di:waypoint x=\"1641.0\" y=\"191.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_a110e73f-5f3d-4aac-bfb0-3ffb91b4af04\"> <di:waypoint x=\"971.0\" y=\"292.0\"/> <di:waypoint x=\"1073.5\" y=\"259.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_8e06cb58-80bb-46ea-b34b-9fae605b0804\"> <di:waypoint x=\"1408.5\" y=\"273.0\"/> <di:waypoint x=\"1511.0\" y=\"226.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_92d688c0-33d4-43e1-8c50-29dd7780e399\"> <di:waypoint x=\"636.0\" y=\"261.0\"/> <di:waypoint x=\"738.5\" y=\"264.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_221b4b4a-b00d-4dff-9da7-c0cc972edfc4\"> <di:waypoint x=\"1641.0\" y=\"121.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_9d224052-fd98-4e72-a00f-859b2cf218db\"> <di:waypoint x=\"1511.0\" y=\"226.0\"/> <di:waypoint x=\"1641.0\" y=\"191.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_fd70f7d7-ced3-44da-8c62-4c706ab9a6a4\"> <di:waypoint x=\"1408.5\" y=\"273.0\"/> <di:waypoint x=\"1511.0\" y=\"306.0\"/> <di:waypoint x=\"1641.0\" y=\"331.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_d9fb3acc-10e2-4087-ae4a-d34c1acaf2fe\"> <di:waypoint x=\"1408.5\" y=\"273.0\"/> <di:waypoint x=\"1511.0\" y=\"336.0\"/> <di:waypoint x=\"1641.0\" y=\"401.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_86d54ca2-762b-4f72-9b17-d8dd0748cb0c\"> <di:waypoint x=\"1073.5\" y=\"259.0\"/> <di:waypoint x=\"1176.0\" y=\"264.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_d3379277-598e-4738-8b2f-293570ad5b32\"> <di:waypoint x=\"1306.0\" y=\"267.0\"/> <di:waypoint x=\"1408.5\" y=\"273.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_f8bda739-2e5a-4ee6-ba72-116042f24ae8\"> <di:waypoint x=\"13.5\" y=\"261.0\"/> <di:waypoint x=\"116.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_20b6c208-dacf-44d6-aa3b-69cf57bbdf56\"> <di:waypoint x=\"738.5\" y=\"264.0\"/> <di:waypoint x=\"841.0\" y=\"241.0\"/> <di:waypoint x=\"971.0\" y=\"222.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_cc57b403-e35f-4e67-989a-c84046776990\"> <di:waypoint x=\"971.0\" y=\"222.0\"/> <di:waypoint x=\"1073.5\" y=\"259.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_1e6d194e-71d3-4b16-87e7-1363ce9bb9fe\"> <di:waypoint x=\"1641.0\" y=\"331.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_39eb9ec0-288c-462e-8b08-7a5525735753\"> <di:waypoint x=\"1641.0\" y=\"261.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_f66b59a7-46c4-4483-809a-c91f2058c920\"> <di:waypoint x=\"116.0\" y=\"261.0\"/> <di:waypoint x=\"246.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_0ddc0130-c0a6-4578-abba-7ae15244ad20\"> <di:waypoint x=\"376.0\" y=\"261.0\"/> <di:waypoint x=\"506.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_ab2d0b5e-a0b9-4e4a-8274-dd07525149b7\"> <di:waypoint x=\"1641.0\" y=\"401.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_2e3955d3-9c66-4629-9d5a-76efd87e63e6\"> <di:waypoint x=\"246.0\" y=\"261.0\"/> <di:waypoint x=\"376.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> </bpmndi:BPMNPlane> </bpmndi:BPMNDiagram> </definitions>";
        String layout = "{\"elements\":{\"nodes\":[{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"50px\",\"id\":\"1\",\"textwidth\":\"90px\",\"gatewayId\":\"node_261a29e3-6a74-4cf1-9156-fa6d716f5e0d\",\"height\":\"50px\"},\"position\":{\"x\":1058.5,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"50px\",\"id\":\"2\",\"textwidth\":\"90px\",\"gatewayId\":\"node_afdf9b6e-052c-4ef9-8e1a-7e630025b192\",\"height\":\"50px\"},\"position\":{\"x\":1541.5,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"50px\",\"id\":\"3\",\"textwidth\":\"90px\",\"gatewayId\":\"node_5068b1e5-f641-4a06-854e-6c20b09d8764\",\"height\":\"50px\"},\"position\":{\"x\":2024.5,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"50px\",\"id\":\"4\",\"textwidth\":\"90px\",\"gatewayId\":\"node_cfeffa21-082b-4474-9e52-ad680810dee4\",\"height\":\"50px\"},\"position\":{\"x\":2507.5,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#6697b8\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Admission IC\\n\\n117\",\"textcolor\":\"black\",\"width\":\"125px\",\"id\":\"5\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":1207,\"y\":608},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#236d9b\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Admission NC\\n\\n1182\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"6\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":1393,\"y\":387},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#055b8d\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"CRP\\n\\n3262\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"7\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":1876,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#266f9c\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Registration\\n\\n1050\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"8\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":166,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#266f9c\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Sepsis Triage\\n\\n1049\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"9\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":538,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#266f9c\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Triage\\n\\n1053\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"10\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":352,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#2d749f\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"IV Antibiotics\\n\\n823\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"11\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":910,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#3075a0\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"IV Liquid\\n\\n753\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"12\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":724,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#1c6998\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"LacticAcid\\n\\n1466\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"13\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":1393,\"y\":608},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Leucocytes\\n\\n3383\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"14\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":1690,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#3377a2\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release A\\n\\n671\",\"textcolor\":\"white\",\"width\":\"125px\",\"id\":\"15\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":2173,\"y\":939.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#7ba4c2\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release B\\n\\n56\",\"textcolor\":\"black\",\"width\":\"125px\",\"id\":\"16\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":2359,\"y\":55.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#92b3cc\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release C\\n\\n25\",\"textcolor\":\"black\",\"width\":\"125px\",\"id\":\"17\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":2359,\"y\":276.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#93b3cc\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release D\\n\\n24\",\"textcolor\":\"black\",\"width\":\"125px\",\"id\":\"18\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":2359,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#b8cbdd\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release E\\n\\n6\",\"textcolor\":\"black\",\"width\":\"125px\",\"id\":\"19\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":2359,\"y\":718.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#4b86ad\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Return ER\\n\\n294\",\"textcolor\":\"black\",\"width\":\"125px\",\"id\":\"20\",\"textwidth\":\"90px\",\"height\":\"100px\"},\"position\":{\"x\":2359,\"y\":939.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C0A3A1\",\"textsize\":\"15\",\"borderwidth\":\"3\",\"name\":\"\",\"textcolor\":\"black\",\"width\":\"37px\",\"id\":\"21\",\"textwidth\":\"90px\",\"height\":\"37px\"},\"position\":{\"x\":2613,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C1C9B0\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"\",\"textcolor\":\"black\",\"width\":\"37px\",\"id\":\"22\",\"textwidth\":\"90px\",\"height\":\"37px\"},\"position\":{\"x\":24,\"y\":497.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}],\"edges\":[{\"data\":{\"strength\":2.59,\"color\":\"#626262\",\"style\":\"solid\",\"source\":\"1\",\"label\":\"46\",\"target\":\"5\",\"id\":\"b48b4a76-9432-43ca-8930-f4e6aa32630b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":27.5,\"color\":\"#545454\",\"style\":\"solid\",\"source\":\"1\",\"label\":\"489\",\"target\":\"6\",\"id\":\"341217ef-256f-498c-ad53-383b2656b6a2\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":54.72,\"color\":\"#444444\",\"style\":\"solid\",\"source\":\"2\",\"label\":\"973\",\"target\":\"14\",\"id\":\"a0990974-6f34-490b-994b-3613d690e7b1\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":18.11,\"color\":\"#595959\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"322\",\"target\":\"15\",\"id\":\"77d9234a-ea5f-460a-ad02-22c13e5be4ec\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":1.07,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"19\",\"target\":\"16\",\"id\":\"9d6c93e1-545f-48a3-ad9f-e866badd7d1d\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.73,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"13\",\"target\":\"17\",\"id\":\"1d72f890-3f0f-4489-8d43-2622bbac604f\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.67,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"12\",\"target\":\"18\",\"id\":\"7784116c-b266-45b5-bddc-45fb0925199d\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.17,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"3\",\"target\":\"19\",\"id\":\"c32ca2a9-fff0-4d16-b814-6d03b6c210cb\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":21.6,\"color\":\"#575757\",\"style\":\"solid\",\"source\":\"4\",\"label\":\"384\",\"target\":\"21\",\"id\":\"34fe3e36-277d-4081-98fc-e4f2ab0a3875\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":2.31,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"5\",\"label\":\"41\",\"target\":\"13\",\"id\":\"18b059c6-5bd3-44a9-867c-fb8a0f5647db\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":22.95,\"color\":\"#565656\",\"style\":\"solid\",\"source\":\"6\",\"label\":\"408\",\"target\":\"2\",\"id\":\"4b817b7e-51da-4f6c-8c3a-fd6ec7242949\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":20.75,\"color\":\"#585858\",\"style\":\"solid\",\"source\":\"7\",\"label\":\"369\",\"target\":\"3\",\"id\":\"a5e237e3-62de-4b68-a183-054b26278666\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":54.61,\"color\":\"#444444\",\"style\":\"solid\",\"source\":\"8\",\"label\":\"971\",\"target\":\"10\",\"id\":\"d699ef05-2134-4a48-b5b8-6eec18301e4a\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":16.03,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"9\",\"label\":\"285\",\"target\":\"12\",\"id\":\"49b1500a-b8a3-46ee-b04d-b2b7d10f6e2b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":50.9,\"color\":\"#464646\",\"style\":\"solid\",\"source\":\"10\",\"label\":\"905\",\"target\":\"9\",\"id\":\"d66b7b4f-5759-4a0e-856b-dbb4ed0a067b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":30.09,\"color\":\"#525252\",\"style\":\"solid\",\"source\":\"11\",\"label\":\"535\",\"target\":\"1\",\"id\":\"8f23caf1-5912-42e9-b120-eaf02820aceb\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":28.18,\"color\":\"#535353\",\"style\":\"solid\",\"source\":\"12\",\"label\":\"501\",\"target\":\"11\",\"id\":\"627d153a-0f4d-4c3c-901f-3bfa216db460\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":31.78,\"color\":\"#515151\",\"style\":\"solid\",\"source\":\"13\",\"label\":\"565\",\"target\":\"2\",\"id\":\"7c313218-998f-4652-b27f-ca0b945e2107\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"solid\",\"source\":\"14\",\"label\":\"1778\",\"target\":\"7\",\"id\":\"beb871d2-1ac0-42e6-b955-5d5cd162e604\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":15.52,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"15\",\"label\":\"276\",\"target\":\"20\",\"id\":\"8a2805dc-6729-45e4-8d74-9e26f9a9e041\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":3.09,\"color\":\"#626262\",\"style\":\"solid\",\"source\":\"16\",\"label\":\"55\",\"target\":\"4\",\"id\":\"a8d6e953-817f-4fc7-b849-8957abe0cb33\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":1.07,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"17\",\"label\":\"19\",\"target\":\"4\",\"id\":\"fb1b35cb-88aa-4486-a438-9bbe214647ff\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.79,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"18\",\"label\":\"14\",\"target\":\"4\",\"id\":\"fa40e352-2d4c-4929-867c-9876c553b75b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.28,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"19\",\"label\":\"5\",\"target\":\"4\",\"id\":\"680514ff-412e-4685-a379-07dfcd3197af\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":16.37,\"color\":\"#5a5a5a\",\"style\":\"solid\",\"source\":\"20\",\"label\":\"291\",\"target\":\"4\",\"id\":\"ea85a598-a229-4a19-860e-ea05c796cc05\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":55.96,\"color\":\"#434343\",\"style\":\"solid\",\"source\":\"22\",\"label\":\"995\",\"target\":\"8\",\"id\":\"179b32ae-c2b3-4f45-b3c9-f704058e8f24\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}]},\"style\":[{\"selector\":\"node\",\"style\":{\"background-color\":\"data(color)\",\"border-color\":\"black\",\"border-width\":\"data(borderwidth)\",\"color\":\"data(textcolor)\",\"label\":\"data(name)\",\"font-size\":\"data(textsize)\",\"height\":\"data(height)\",\"padding\":\"5px\",\"shape\":\"data(shape)\",\"text-border-width\":\"10px\",\"text-max-width\":\"data(textwidth)\",\"text-valign\":\"center\",\"text-wrap\":\"wrap\",\"width\":\"data(width)\"}},{\"selector\":\":selected\",\"style\":{\"border-width\":\"4px\",\"border-color\":\"#333\"}},{\"selector\":\"edge\",\"style\":{\"color\":\"data(color)\",\"curve-style\":\"bezier\",\"text-rotation\":\"0rad\",\"font-size\":\"11px\",\"label\":\"data(label)\",\"line-color\":\"data(color)\",\"line-style\":\"data(style)\",\"loop-sweep\":\"181rad\",\"loop-direction\":\"-41rad\",\"opacity\":\"1\",\"source-arrow-color\":\"data(color)\",\"target-arrow-color\":\"data(color)\",\"target-arrow-shape\":\"triangle\",\"text-background-color\":\"#ffffff\",\"text-background-opacity\":\"1\",\"text-background-padding\":\"5px\",\"text-background-shape\":\"roundrectangle\",\"text-wrap\":\"wrap\",\"width\":\"mapData(strength, 0, 100, 1, 6)\"}},{\"selector\":\"edge.questionable\",\"style\":{\"line-style\":\"dotted\",\"target-arrow-shape\":\"diamond\"}},{\"selector\":\".faded\",\"style\":{\"opacity\":\"0.25\",\"text-opacity\":\"0\"}}],\"zoomingEnabled\":true,\"userZoomingEnabled\":true,\"zoom\":0.5140151515151515,\"minZoom\":1.0E-50,\"maxZoom\":1.0E50,\"panningEnabled\":true,\"userPanningEnabled\":true,\"pan\":{\"x\":30.514015151515082,\"y\":56.277462121212125},\"boxSelectionEnabled\":true,\"renderer\":{\"name\":\"canvas\"},\"wheelSensitivity\":0.1} ";
        BPMNUpdater updater = new BPMNUpdater();
        String s = updater.getUpdatedBPMN(bpmn, layout, false);
        System.out.println(s);
    }

    public Set<String> getRemovedFlowIDs() {
        return removedFlowIDs;
    }

    /**
     * 
     * @param bpmn: original BPMN representation for the graph or process model
     * @param layout: contains the JSON representation of the graph or process model
     * @param remove_gateways: true = remove gateways in the BPMN. This is to remove all XOR gateways
     * to make it a graph in the animation
     * @return: the new BPMN representation with the BPMNDi section replaced with a new one created from the JSON representation
     */
    public String getUpdatedBPMN(String bpmn, String layout, boolean remove_gateways) {
//        System.out.println(bpmn);
//        System.out.println(layout);

        Map<String, Set<String>> mapSource = new HashMap<>();
        Map<String, Set<String>> mapTarget = new HashMap<>();

        Map<String, ElementLayout> layoutMap = LayoutGenerator.generateLayout(layout);

        bpmn = bpmn.substring(0, bpmn.indexOf("<bpmndi:BPMNShape"));
        String jsonEnding = "</bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>";

        //----------------------------------------------------
        // Remove gateways 
        //----------------------------------------------------
        if(remove_gateways) {
            while (bpmn.contains("<exclusiveGateway id=")) {
                String pre = bpmn.substring(0, bpmn.indexOf("<exclusiveGateway id="));
                String mid = bpmn.substring(bpmn.indexOf("<exclusiveGateway id=") + 22, bpmn.indexOf("</exclusiveGateway>") + 19);
                String id = mid.substring(0, mid.indexOf("\""));
                if (mid.contains("Diverging")) splitGatewayIDs.add(id);
                else joinGatewayIDs.add(id);
                String post = bpmn.substring(bpmn.indexOf("</exclusiveGateway>") + 19);
                bpmn = pre + post;
            }

            for (String split : splitGatewayIDs) {
                String target = "targetRef=\"" + split + "\"/>";
                String pre = bpmn.substring(0, bpmn.indexOf(target) + target.length());
                String flow = pre.substring(pre.lastIndexOf("<sequenceFlow"));
                String source_node = flow.substring(flow.indexOf("sourceRef=\"") + 11, flow.indexOf("targetRef") - 2);

                String flowId = getFlowID(flow);
                removedFlowIDs.add(flowId);
                bpmn = bpmn.replace(flow, "");

                String ref = "sourceRef=\"" + split;
                String tmp = bpmn;
                Set<String> sources = new HashSet<>();
                while (tmp.contains(ref)) {
                    tmp = tmp.substring(tmp.indexOf(ref) - 69);
                    sources.add(getFlowID(tmp));
                    tmp = tmp.substring(tmp.indexOf("/>"));
                }
                mapSource.put(flowId, sources);
                bpmn = bpmn.replaceAll(ref, "sourceRef=\"" + source_node);
            }

            for (String join : joinGatewayIDs) {
                String source = "sourceRef=\"" + join + "\"";
                String pre = bpmn.substring(0, bpmn.indexOf(source));
                pre = pre.substring(pre.lastIndexOf("<sequenceFlow"));
                String post = bpmn.substring(bpmn.indexOf(source));
                post = post.substring(0, post.indexOf("/>") + 2);
                String flow = pre + post;
                String target_node = flow.substring(flow.indexOf("targetRef=\"") + 11, flow.indexOf("/>") - 1);

                String flowId = getFlowID(flow);
                removedFlowIDs.add(flowId);
                bpmn = bpmn.replace(flow, "");

                String ref = "targetRef=\"" + join;
                String tmp = bpmn;
                Set<String> targets = new HashSet<>();
                while (tmp.contains(ref)) {
                    tmp = tmp.substring(tmp.indexOf(ref) - 123);
                    targets.add(getFlowID(tmp));
                    tmp = tmp.substring(tmp.indexOf("/>"));
                }
                mapTarget.put(flowId, targets);
                bpmn = bpmn.replaceAll("targetRef=\"" + join, "targetRef=\"" + target_node);
            }
        }

        //----------------------------------------------------
        // Recreate the BPMNDi section
        //----------------------------------------------------
        String startId = null;
        String endId = null;
        for(String elementID : getBPMNElementIDs(bpmn)) {
            String taskName = getBPMNElementName(bpmn, elementID);

            if(taskName.equals("|&gt;")) {
                startId = elementID;
            }else if(taskName.equals("[]")) {
                endId = elementID;
            }

            String taskRef = taskName.isEmpty() ? elementID : taskName;
            ElementLayout elementLayout = layoutMap.get(taskRef);

            if(elementLayout != null) {
                String shape = createBPMNShape(elementID, elementLayout.getWidth(), elementLayout.getHeight(), elementLayout.getX(), elementLayout.getY());
                bpmn += shape;
            }
        }

        for(String flowID : getFlowIDs(bpmn)) {
            String sourceID = getSourceID(bpmn, flowID);
            String targetID = getTargetID(bpmn, flowID);

            String sourceName = getBPMNElementName(bpmn, sourceID);
            String targetName = getBPMNElementName(bpmn, targetID);

            String sourceRef = sourceName.isEmpty() ? sourceID : sourceName;
            String targetRef = targetName.isEmpty() ? targetID : targetName;

            if(!layoutMap.containsKey(sourceRef + " (~) " + targetRef)) continue;

            ElementLayout sourceLayout = layoutMap.get(sourceRef);
            ElementLayout targetLayout = layoutMap.get(targetRef);

            double source_x = sourceLayout.getX() + (sourceLayout.getWidth() / 2);
            double source_y = sourceLayout.getY() + (sourceLayout.getHeight() / 2);
            double target_x = targetLayout.getX() + (targetLayout.getWidth() / 2);
            double target_y = targetLayout.getY() + (targetLayout.getHeight() / 2);

            boolean bend_point = false;
            if(sourceLayout.getX() == targetLayout.getX() && sourceLayout.getY() == targetLayout.getY()) {
                bend_point = true;
            }else {
                for(String flowID2 : getFlowIDs(bpmn)) {
                	// For loop L2, A-->B and B-->A, bend point is required for them not to overlay each other on the same straight line 
                    if(sourceID.equals(getTargetID(bpmn, flowID2)) && targetID.equals(getSourceID(bpmn, flowID2))) {
                        bend_point = true;
                        break;
                    }
                }
            }

            String edge = createBPMNEdge(flowID, source_x, source_y, target_x, target_y, bend_point);
            bpmn += edge;
        }

        //-----------------------------------------------------
        // The colors are set via extension elements of the editor
        //-----------------------------------------------------
        for(String elementID : getBPMNElementIDs(bpmn)) {
            if(!elementID.equals(startId) && !elementID.equals(endId)) {
                String element = getBPMNElement(bpmn, elementID);
                String taskName = getBPMNElementName(bpmn, elementID);
//                String taskRef = taskName.isEmpty() ? elementID : taskName;

                if(layoutMap.containsKey(taskName)) {
                    ElementLayout elementLayout = layoutMap.get(taskName);

                    String extensionElements = "<extensionElements>" +
                            "<signavio:signavioMetaData metaKey=\"bgcolor\" metaValue=\"" + elementLayout.getElementColor() + "\" />" +
                            "</extensionElements></task>";
                    String element2 = element.replace("/>", ">");
                    element2 += extensionElements;
                    bpmn = bpmn.replace(element, element2);
                }
            }
        }

        String element = getBPMNElement(bpmn, startId);
        String extensionElements = "<extensionElements>" +
                "<signavio:signavioMetaData metaKey=\"bgcolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, startId)).getElementColor() + "\" />" +
                "<signavio:signavioMetaData metaKey=\"bordercolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, startId)).getElementColor() + "\" />" +
                "</extensionElements></startEvent>";
        String element2 = element.replace("|&gt;", "");
        element2 = element2.replace("/>", ">");
        element2 += extensionElements;
        bpmn = bpmn.replace(element, element2);

        element = getBPMNElement(bpmn, endId);
        extensionElements = "<extensionElements>" +
                "<signavio:signavioMetaData metaKey=\"bgcolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, endId)).getElementColor() + "\" />" +
                "<signavio:signavioMetaData metaKey=\"bordercolor\" metaValue=\"" + layoutMap.get(getBPMNElementName(bpmn, endId)).getElementColor() + "\" />" +
                "</extensionElements></endEvent>";
        element2 = element.replace("[]", "");
        element2 = element2.replace("/>", ">");
        element2 += extensionElements;
        bpmn = bpmn.replace(element, element2);

        bpmn += jsonEnding;
        bpmn = bpmn.replace("xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"", "xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:signavio=\"http://www.signavio.com\"");
        bpmn = bpmn.replace("<incoming> ", "<incoming>");
        bpmn = bpmn.replace("<outgoing> ", "<outgoing>");
        bpmn = bpmn.replace("[", "");
        bpmn = bpmn.replace("]", "");
        bpmn = bpmn.replace(".00", "");
        return bpmn;
    }

    private String getFlowID(String flow) {
        return flow.substring(flow.indexOf("id=") + 4, flow.indexOf("\" name"));
    }

    //Extract a set of node names from the BPMN diagram representation
    private Set<String> getBPMNElementIDs(String bpmn) {
        Set<String> ids = new HashSet<>();
        String tmp = bpmn;
        String startEvent = "<startEvent id=\"";
        String endEvent = "<endEvent id=\"";
        String task = "<task id=\"";
        String xor = "<exclusiveGateway id=\"";
        String and = "<parallelGateway id=\"";
        String or = "<inclusiveGateway id=\"";
        String post;
        String name = "\" name";
        while (tmp.contains(startEvent) || tmp.contains(endEvent) || tmp.contains(task) || tmp.contains(xor) || tmp.contains(and) || tmp.contains(or)) {
            String intro;
            if(tmp.contains(startEvent)) {
                intro = startEvent;
                post = "/>";
            }else if(tmp.contains(endEvent)) {
                intro = endEvent;
                post = "/>";
            }else if(tmp.contains(task)) {
                intro = task;
                post = "/>";
            }else if(tmp.contains(xor)) {
                intro = xor;
                post = "</exclusiveGateway>";
            }else if(tmp.contains(and)) {
                intro = and;
                post = "</parallelGateway>";
            }else {
                intro = or;
                post = "</inclusiveGateway>";
            }
            String pre = tmp.substring(tmp.indexOf(intro));
            String element = pre.substring(0, pre.indexOf(post) + post.length());
            String id = element.substring(element.indexOf(intro) + intro.length(), element.indexOf(name));
            ids.add(id);
            tmp = tmp.substring(tmp.indexOf(element) + element.length());
        }
        return ids;
    }

    private String getBPMNElementName(String bpmn, String elementID) {
        String pre = "<task id=\"" + elementID;
        String task = null;
        if(bpmn.contains(pre)) {
            task = handleTask(bpmn, pre);
        }else {
            pre = "<startEvent id=\"" + elementID;
            if(bpmn.contains(pre)) {
                task = handleEvent(bpmn, pre);
            }else {
                pre = "<endEvent id=\"" + elementID;
                if(bpmn.contains(pre)) {
                    task = handleEvent(bpmn, pre);
                }else {
                    pre = "<exclusiveGateway id=\"" + elementID;
                    if(bpmn.contains(pre)) {
                        task = handleGateway(bpmn, pre);
                    }else {
                        pre = "<parallelGateway id=\"" + elementID;
                        if (bpmn.contains(pre)) {
                            task = handleGateway(bpmn, pre);
                        } else {
                            pre = "<inclusiveGateway id=\"" + elementID;
                            if (bpmn.contains(pre)) {
                                task = handleGateway(bpmn, pre);
                            }
                        }
                    }
                }
            }
        }
        task = task.replace("\\u0027", "");
        return task;
    }

    private String handleTask(String bpmn, String pre) {
        pre = bpmn.substring(bpmn.indexOf(pre));
        String post = "/>";
        return pre.substring(pre.indexOf("name") + 6, pre.indexOf(post) - 1);
    }

    private String handleEvent(String bpmn, String pre) {
        pre = bpmn.substring(bpmn.indexOf(pre));
        String post = "isInterrupting=\"false\"/>";
        if(bpmn.contains(post)) {
            return pre.substring(pre.indexOf("name") + 6, pre.indexOf(post) - 2);
        }else {
            return pre.substring(pre.indexOf("name") + 6, pre.indexOf("/>") - 1);
        }
    }

    private String handleGateway(String bpmn, String pre) {
        pre = bpmn.substring(bpmn.indexOf(pre));
        String post = "\" gatewayDirection";
        return pre.substring(pre.indexOf("name") + 6, pre.indexOf(post));
    }

    private String createBPMNShape(String shapeId, double width, double height, double x, double y) {
        return "<bpmndi:BPMNShape bpmnElement=\"" +
                shapeId +
                "\"><dc:Bounds x=\"" +
                (int) x +
                "\" y=\"" +
                (int) y +
                "\" width=\"" +
                (int) width +
                "\" height=\"" +
                (int) height +
                "\"/><bpmndi:BPMNLabel/></bpmndi:BPMNShape>";
    }

    // Extract a set of edgeIDs from the BPMN diagram representation
    // The edge ID is auto-generated by the BPMN library
    private Set<String> getFlowIDs(String bpmn) {
        Set<String> ids = new HashSet<>();
        String tmp = bpmn;
        while (tmp.contains("<sequenceFlow")) {
            String intro = "<sequenceFlow";
            String pre = tmp.substring(tmp.indexOf(intro));
            String post = "/>";
            String edge = pre.substring(0, pre.indexOf(post) + post.length());
            String id = edge.substring(edge.indexOf(intro) + intro.length() + 5, edge.indexOf("\" name"));
            ids.add(id);
            tmp = tmp.substring(tmp.indexOf(edge) + edge.length());
        }
        return ids;
    }

    private String getSourceID(String bpmn, String flowID) {
        String intro = "<sequenceFlow id=\"" + flowID;
        String pre = bpmn.substring(bpmn.indexOf(intro));
        String post = "\"/>";
        String edge = pre.substring(0, pre.indexOf(post) + post.length());
        String sourceRef = "sourceRef=\"";
        String targetRef = "\" targetRef=\"";
        String id = edge.substring(edge.indexOf(sourceRef) + sourceRef.length(), edge.indexOf(targetRef));
        return id;
    }

    private String getTargetID(String bpmn, String flowID) {
        String intro = "<sequenceFlow id=\"" + flowID;
        String pre = bpmn.substring(bpmn.indexOf(intro));
        String post = "\"/>";
        String edge = pre.substring(0, pre.indexOf(post) + post.length());
        String targetRef = "targetRef=\"";
        String id = edge.substring(edge.indexOf(targetRef) + targetRef.length(), edge.indexOf(post));
        return id;
    }

    private String createBPMNEdge(String flowID, double source_x, double source_y, double target_x, double target_y, boolean bent_point) {
        String edge = "<bpmndi:BPMNEdge bpmnElement=\"" +
                flowID +
                "\"><di:waypoint x=\"" +
                (int) source_x +
                "\" y=\"" +
                (int) source_y +
                "\"/>";

        if(bent_point) {
            double x = ((source_x + target_x) / 2);
            double y = ((source_y + target_y) / 2);

            if(source_x < target_x) {
                if(source_y < target_y) {
                    x += 30;
                    y -= 30;
                }else if(source_y > target_y) {
                    x -= 30;
                    y -= 30;
                }else {
                    y -= 30;
                }
            }else if(source_x > target_x) {
                if(source_y < target_y) {
                    x += 30;
                    y += 30;
                }else if(source_y > target_y) {
                    x -= 30;
                    y += 30;
                }else {
                    y += 30;
                }
            }else {
                if(source_y < target_y) {
                    x += 30;
                }else if(source_y > target_y) {
                    x -= 30;
                }else {
                    y -= 50;
                    x += 30;
                    edge += "<di:waypoint x=\"" +
                            (int) x +
                            "\" y=\"" +
                            (int) y +
                            "\"/>";
                    x -= 60;
                }
            }

            edge += "<di:waypoint x=\"" +
                    (int) x +
                    "\" y=\"" +
                    (int) y +
                    "\"/>";
        }

        edge += "<di:waypoint x=\"" +
                (int) target_x +
                "\" y=\"" +
                (int) target_y +
                "\"/></bpmndi:BPMNEdge>";

        return edge;
    }

    private String getBPMNElement(String bpmn, String elementID) {
        String pre = "<task id=\"" + elementID;
        String post = "/>";
        String gatewayPost = "";
        if(bpmn.contains(pre)) {
            pre = bpmn.substring(bpmn.indexOf(pre));
        }else {
            pre = "<startEvent id=\"" + elementID;
            if(bpmn.contains(pre)) {
                pre = bpmn.substring(bpmn.indexOf(pre));
            }else {
                pre = "<endEvent id=\"" + elementID;
                if(bpmn.contains(pre)) {
                    pre = bpmn.substring(bpmn.indexOf(pre));
                }else {
                    pre = "<exclusiveGateway id=\"" + elementID;
                    if(bpmn.contains(pre)) {
                        pre = bpmn.substring(bpmn.indexOf(pre));
                        post = "</exclusiveGateway>";
                    }else {
                        pre = "<parallelGateway id=\"" + elementID;
                        if (bpmn.contains(pre)) {
                            pre = bpmn.substring(bpmn.indexOf(pre));
                            post = "</parallelGateway>";
                        } else {
                            pre = "<inclusiveGateway id=\"" + elementID;
                            if (bpmn.contains(pre)) {
                                pre = bpmn.substring(bpmn.indexOf(pre));
                                post = "</inclusiveGateway>";
                            }
                        }
                    }
                }
            }
        }
        if(!gatewayPost.isEmpty()) {
            return pre.substring(0, pre.indexOf(gatewayPost) + gatewayPost.length());
        }
        return pre.substring(0, pre.indexOf(post) + post.length());
    }

    private Set<String> getOutgoingEdgesIDs(String bpmn, String elementID) {
        Set<String> flowIDs = getFlowIDs(bpmn);
        Set<String> outgoingEdgesIDs = new HashSet<>();
        for(String flowID : flowIDs) {
            if(getSourceID(bpmn, flowID).equals(elementID)) {
                outgoingEdgesIDs.add(flowID);
            }
        }
        return outgoingEdgesIDs;
    }

    private String getBPMNEdge(String bpmn, String flowID) {
        String intro = "<bpmndi:BPMNEdge bpmnElement=\"" + flowID;
        String pre = bpmn.substring(bpmn.indexOf(intro));
        String post = "</bpmndi:BPMNEdge>";
        String edge = pre.substring(0, pre.indexOf(post) + post.length());
        return edge;
    }

    private String getLastBPMNWayPoint(String edge) {
        String intro = "<di:waypoint x=\"";
        String pre = edge.substring(edge.lastIndexOf(intro));
        String post = "/>";
        String waypoint = pre.substring(0, pre.indexOf(post) + post.length());
        return waypoint;
    }

    private String getBPMNShape(String bpmn, String elementID) {
        String pre = "<bpmndi:BPMNShape bpmnElement=\"" + elementID;
        String post = "</bpmndi:BPMNShape>";
        pre = bpmn.substring(bpmn.indexOf(pre));
        return pre.substring(0, pre.indexOf(post) + post.length());
    }

    private String getFirstBPMNWayPoint(String edge) {
        String intro = "<di:waypoint x=\"";
        String pre = edge.substring(edge.indexOf(intro));
        String post = "/>";
        String waypoint = pre.substring(0, pre.indexOf(post) + post.length());
        return waypoint;
    }

    private Set<String> getIncomingEdgesIDs(String bpmn, String elementID) {
        Set<String> flowIDs = getFlowIDs(bpmn);
        Set<String> incomingEdgesIDs = new HashSet<>();
        for(String flowID : flowIDs) {
            if(getTargetID(bpmn, flowID).equals(elementID)) {
                incomingEdgesIDs.add(flowID);
            }
        }
        return incomingEdgesIDs;
    }

    private Double getMinXOffsetBPMNShape(String bpmn, String elementID) {
        String pre = "<bpmndi:BPMNShape bpmnElement=\"" + elementID;
        String post = "</bpmndi:BPMNShape>";
        pre = bpmn.substring(bpmn.indexOf(pre));
        String shape = pre.substring(0, pre.indexOf(post) + post.length());
        Double x = Double.parseDouble(shape.substring(shape.indexOf("x=\"") + 3, shape.indexOf("\" y=\"")));
        Double width = Double.parseDouble(shape.substring(shape.indexOf("width=\"") + 7, shape.indexOf("\" height=\"")));
        return x + width;
    }
}