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
        String bpmn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"  xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"  xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"  xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  targetNamespace=\"http://www.omg.org/bpmn20\"  xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"><process id=\"proc_1274596437\"> <startEvent id=\"node_43acecf3-d202-4647-94ff-2d94d2cc65c5\" name=\"|&gt;\" isInterrupting=\"false\"/> <endEvent id=\"node_598cf4d1-aa59-4c70-9460-1cd78a5b4c3e\" name=\"[]\" isInterrupting=\"false\"/> <task id=\"node_084af194-ecd8-4bdf-a887-137cfddb538d\" name=\"Release B\"/> <task id=\"node_e5c8ccb5-bfdb-4dca-9ab3-301e97e6d971\" name=\"Release D\"/> <task id=\"node_66c002d8-73b8-4d85-8939-c298fd97461e\" name=\"ER Registration\"/> <task id=\"node_76406cfd-a559-44cf-ae8a-b005ce8d3ecf\" name=\"LacticAcid\"/> <task id=\"node_e18e9d9d-cf61-4a91-b4d2-c083c00550a3\" name=\"ER Triage\"/> <task id=\"node_e4f7eb58-a9d5-4dd6-825c-b237e372b8bb\" name=\"Leucocytes\"/> <task id=\"node_b0a9188b-3d81-430f-a013-5befa128e818\" name=\"ER Sepsis Triage\"/> <task id=\"node_40bb3071-37e6-4bf2-b687-56c6cf68134b\" name=\"Admission IC\"/> <task id=\"node_b822bc37-7abb-4dd4-98fd-85b47ae9c6cd\" name=\"CRP\"/> <task id=\"node_fca76fd1-9ee2-4bb4-8206-bc5d363b3c45\" name=\"Release A\"/> <task id=\"node_a59bda53-0780-4982-aa36-bead0cfaf46a\" name=\"Admission NC\"/> <task id=\"node_5dad9c9a-ce9f-4e54-b50b-27c245e402c3\" name=\"Release E\"/> <task id=\"node_89e02023-b0b8-41cd-a814-6599c24b21cb\" name=\"Release C\"/> <task id=\"node_d2b69820-f77c-421f-8a1f-60a38fe947fd\" name=\"IV Antibiotics\"/> <task id=\"node_6c7eed82-8464-432f-ab31-a8702e756639\" name=\"Return ER\"/> <task id=\"node_0f69d67d-6c28-4e84-b2d6-f5d593461be2\" name=\"IV Liquid\"/> <exclusiveGateway id=\"node_2b01e159-c356-480b-8c76-777a5942425b\" name=\"\" gatewayDirection=\"Converging\"> <incoming> node_2b2127c9-ed86-4bbd-818d-59059f0aea8d</incoming> <incoming> node_408fda73-4824-480f-b242-2cafe4e6bc2e</incoming> <incoming> node_72538e2c-22ee-46d3-b2d4-8beecb5b410c</incoming> <incoming> node_93624105-da73-4ed7-870e-43761126cb9e</incoming> <incoming> node_84a0f0af-aa19-4603-918d-98c7cc4caba3</incoming> <outgoing> node_eb8a2454-dd78-4440-9ae0-1bf1d63b00ef</outgoing> </exclusiveGateway> <exclusiveGateway id=\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\" name=\"\" gatewayDirection=\"Diverging\"> <incoming> node_ce73ab5f-6f76-4adb-b4be-858f372fa159</incoming> <outgoing> node_2b26b295-9c55-48d1-8b3a-6ef6784805a3</outgoing> <outgoing> node_11329ea4-879e-45b2-bdd5-25624cf5f8b5</outgoing> </exclusiveGateway> <exclusiveGateway id=\"node_69039846-c4a8-4c22-9937-c5310ff82431\" name=\"\" gatewayDirection=\"Converging\"> <incoming> node_4c92f4e7-c8e0-4468-b2bb-e9c84fa9a902</incoming> <incoming> node_f02dddac-277e-4e76-ac38-49c4544a1195</incoming> <outgoing> node_8a39782a-4ff2-464c-8856-dcb3d6313930</outgoing> </exclusiveGateway> <exclusiveGateway id=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\" name=\"\" gatewayDirection=\"Diverging\"> <incoming> node_6a6e5562-b303-4f48-948d-ac0a767b148e</incoming> <outgoing> node_57d6c720-eb4a-4a7a-8ea2-bc56e00ea018</outgoing> <outgoing> node_c5a02ce3-e8e0-4ff2-bdb9-f915cf70083f</outgoing> <outgoing> node_0dcb35d4-08a3-42b4-9117-caa51f9dbfa0</outgoing> <outgoing> node_4697857d-d859-465a-9bb1-0bf9ff52ad16</outgoing> <outgoing> node_2795c58e-5fc1-4825-a78d-8d539421ff42</outgoing> </exclusiveGateway> <sequenceFlow id=\"node_2b26b295-9c55-48d1-8b3a-6ef6784805a3\" name=\"[489.00]\" sourceRef=\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\" targetRef=\"node_a59bda53-0780-4982-aa36-bead0cfaf46a\"/> <sequenceFlow id=\"node_2b2127c9-ed86-4bbd-818d-59059f0aea8d\" name=\"[14.00]\" sourceRef=\"node_e5c8ccb5-bfdb-4dca-9ab3-301e97e6d971\" targetRef=\"node_2b01e159-c356-480b-8c76-777a5942425b\"/> <sequenceFlow id=\"node_93624105-da73-4ed7-870e-43761126cb9e\" name=\"[19.00]\" sourceRef=\"node_89e02023-b0b8-41cd-a814-6599c24b21cb\" targetRef=\"node_2b01e159-c356-480b-8c76-777a5942425b\"/> <sequenceFlow id=\"node_72538e2c-22ee-46d3-b2d4-8beecb5b410c\" name=\"[5.00]\" sourceRef=\"node_5dad9c9a-ce9f-4e54-b50b-27c245e402c3\" targetRef=\"node_2b01e159-c356-480b-8c76-777a5942425b\"/> <sequenceFlow id=\"node_2795c58e-5fc1-4825-a78d-8d539421ff42\" name=\"[12.00]\" sourceRef=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\" targetRef=\"node_e5c8ccb5-bfdb-4dca-9ab3-301e97e6d971\"/> <sequenceFlow id=\"node_c5a02ce3-e8e0-4ff2-bdb9-f915cf70083f\" name=\"[3.00]\" sourceRef=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\" targetRef=\"node_5dad9c9a-ce9f-4e54-b50b-27c245e402c3\"/> <sequenceFlow id=\"node_f02dddac-277e-4e76-ac38-49c4544a1195\" name=\"[408.00]\" sourceRef=\"node_a59bda53-0780-4982-aa36-bead0cfaf46a\" targetRef=\"node_69039846-c4a8-4c22-9937-c5310ff82431\"/> <sequenceFlow id=\"node_4697857d-d859-465a-9bb1-0bf9ff52ad16\" name=\"[19.00]\" sourceRef=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\" targetRef=\"node_084af194-ecd8-4bdf-a887-137cfddb538d\"/> <sequenceFlow id=\"node_a92d7582-702e-4462-a25d-c0b03ada62f7\" name=\"[1778.00]\" sourceRef=\"node_e4f7eb58-a9d5-4dd6-825c-b237e372b8bb\" targetRef=\"node_b822bc37-7abb-4dd4-98fd-85b47ae9c6cd\"/> <sequenceFlow id=\"node_1b14d2f2-0484-4e7b-a5ce-4766ca0c7aca\" name=\"[285.00]\" sourceRef=\"node_b0a9188b-3d81-430f-a013-5befa128e818\" targetRef=\"node_0f69d67d-6c28-4e84-b2d6-f5d593461be2\"/> <sequenceFlow id=\"node_408fda73-4824-480f-b242-2cafe4e6bc2e\" name=\"[291.00]\" sourceRef=\"node_6c7eed82-8464-432f-ab31-a8702e756639\" targetRef=\"node_2b01e159-c356-480b-8c76-777a5942425b\"/> <sequenceFlow id=\"node_eb8a2454-dd78-4440-9ae0-1bf1d63b00ef\" name=\"[384.00]\" sourceRef=\"node_2b01e159-c356-480b-8c76-777a5942425b\" targetRef=\"node_598cf4d1-aa59-4c70-9460-1cd78a5b4c3e\"/> <sequenceFlow id=\"node_8a39782a-4ff2-464c-8856-dcb3d6313930\" name=\"[973.00]\" sourceRef=\"node_69039846-c4a8-4c22-9937-c5310ff82431\" targetRef=\"node_e4f7eb58-a9d5-4dd6-825c-b237e372b8bb\"/> <sequenceFlow id=\"node_592f393f-ede7-43b3-9dfc-3702505c2d5c\" name=\"[905.00]\" sourceRef=\"node_e18e9d9d-cf61-4a91-b4d2-c083c00550a3\" targetRef=\"node_b0a9188b-3d81-430f-a013-5befa128e818\"/> <sequenceFlow id=\"node_6a6e5562-b303-4f48-948d-ac0a767b148e\" name=\"[369.00]\" sourceRef=\"node_b822bc37-7abb-4dd4-98fd-85b47ae9c6cd\" targetRef=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\"/> <sequenceFlow id=\"node_89305be2-ee4a-47de-9325-7d0adbdc50a0\" name=\"[971.00]\" sourceRef=\"node_66c002d8-73b8-4d85-8939-c298fd97461e\" targetRef=\"node_e18e9d9d-cf61-4a91-b4d2-c083c00550a3\"/> <sequenceFlow id=\"node_4c92f4e7-c8e0-4468-b2bb-e9c84fa9a902\" name=\"[565.00]\" sourceRef=\"node_76406cfd-a559-44cf-ae8a-b005ce8d3ecf\" targetRef=\"node_69039846-c4a8-4c22-9937-c5310ff82431\"/> <sequenceFlow id=\"node_11329ea4-879e-45b2-bdd5-25624cf5f8b5\" name=\"[46.00]\" sourceRef=\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\" targetRef=\"node_40bb3071-37e6-4bf2-b687-56c6cf68134b\"/> <sequenceFlow id=\"node_4b210d19-e2a7-4219-aef2-98f676c81649\" name=\"[995.00]\" sourceRef=\"node_43acecf3-d202-4647-94ff-2d94d2cc65c5\" targetRef=\"node_66c002d8-73b8-4d85-8939-c298fd97461e\"/> <sequenceFlow id=\"node_f8c8dc67-5088-4c00-800d-ffba953225c7\" name=\"[41.00]\" sourceRef=\"node_40bb3071-37e6-4bf2-b687-56c6cf68134b\" targetRef=\"node_76406cfd-a559-44cf-ae8a-b005ce8d3ecf\"/> <sequenceFlow id=\"node_837155b2-1c17-4df4-8cca-79ec0135a9b9\" name=\"[276.00]\" sourceRef=\"node_fca76fd1-9ee2-4bb4-8206-bc5d363b3c45\" targetRef=\"node_6c7eed82-8464-432f-ab31-a8702e756639\"/> <sequenceFlow id=\"node_0dcb35d4-08a3-42b4-9117-caa51f9dbfa0\" name=\"[322.00]\" sourceRef=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\" targetRef=\"node_fca76fd1-9ee2-4bb4-8206-bc5d363b3c45\"/> <sequenceFlow id=\"node_ce73ab5f-6f76-4adb-b4be-858f372fa159\" name=\"[535.00]\" sourceRef=\"node_d2b69820-f77c-421f-8a1f-60a38fe947fd\" targetRef=\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\"/> <sequenceFlow id=\"node_84a0f0af-aa19-4603-918d-98c7cc4caba3\" name=\"[55.00]\" sourceRef=\"node_084af194-ecd8-4bdf-a887-137cfddb538d\" targetRef=\"node_2b01e159-c356-480b-8c76-777a5942425b\"/> <sequenceFlow id=\"node_290b6188-766e-40e4-8b18-f20075edc3bb\" name=\"[501.00]\" sourceRef=\"node_0f69d67d-6c28-4e84-b2d6-f5d593461be2\" targetRef=\"node_d2b69820-f77c-421f-8a1f-60a38fe947fd\"/> <sequenceFlow id=\"node_57d6c720-eb4a-4a7a-8ea2-bc56e00ea018\" name=\"[13.00]\" sourceRef=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\" targetRef=\"node_89e02023-b0b8-41cd-a814-6599c24b21cb\"/> </process> <bpmndi:BPMNDiagram id=\"id_1955295222\"> <bpmndi:BPMNPlane bpmnElement=\"proc_1274596437\"> <bpmndi:BPMNShape bpmnElement=\"node_e4f7eb58-a9d5-4dd6-825c-b237e372b8bb\"> <dc:Bounds x=\"1136.0\" y=\"239.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_e5c8ccb5-bfdb-4dca-9ab3-301e97e6d971\"> <dc:Bounds x=\"1601.0\" y=\"311.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_a59bda53-0780-4982-aa36-bead0cfaf46a\"> <dc:Bounds x=\"931.0\" y=\"202.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_76406cfd-a559-44cf-ae8a-b005ce8d3ecf\"> <dc:Bounds x=\"931.0\" y=\"272.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_598cf4d1-aa59-4c70-9460-1cd78a5b4c3e\"> <dc:Bounds x=\"1806.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_2b01e159-c356-480b-8c76-777a5942425b\"> <dc:Bounds x=\"1731.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_40bb3071-37e6-4bf2-b687-56c6cf68134b\"> <dc:Bounds x=\"801.0\" y=\"271.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_b822bc37-7abb-4dd4-98fd-85b47ae9c6cd\"> <dc:Bounds x=\"1266.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_0f69d67d-6c28-4e84-b2d6-f5d593461be2\"> <dc:Bounds x=\"466.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_d2b69820-f77c-421f-8a1f-60a38fe947fd\"> <dc:Bounds x=\"596.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_5dad9c9a-ce9f-4e54-b50b-27c245e402c3\"> <dc:Bounds x=\"1601.0\" y=\"101.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_69039846-c4a8-4c22-9937-c5310ff82431\"> <dc:Bounds x=\"1061.0\" y=\"245.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_89e02023-b0b8-41cd-a814-6599c24b21cb\"> <dc:Bounds x=\"1601.0\" y=\"381.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_e18e9d9d-cf61-4a91-b4d2-c083c00550a3\"> <dc:Bounds x=\"206.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\"> <dc:Bounds x=\"726.0\" y=\"251.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_66c002d8-73b8-4d85-8939-c298fd97461e\"> <dc:Bounds x=\"76.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_b0a9188b-3d81-430f-a013-5befa128e818\"> <dc:Bounds x=\"336.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\"> <dc:Bounds x=\"1396.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_43acecf3-d202-4647-94ff-2d94d2cc65c5\"> <dc:Bounds x=\"1.0\" y=\"248.5\" width=\"25.0\" height=\"25.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_084af194-ecd8-4bdf-a887-137cfddb538d\"> <dc:Bounds x=\"1601.0\" y=\"171.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_6c7eed82-8464-432f-ab31-a8702e756639\"> <dc:Bounds x=\"1601.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNShape bpmnElement=\"node_fca76fd1-9ee2-4bb4-8206-bc5d363b3c45\"> <dc:Bounds x=\"1471.0\" y=\"241.0\" width=\"80.0\" height=\"40.0\"/> <bpmndi:BPMNLabel/> </bpmndi:BPMNShape> <bpmndi:BPMNEdge bpmnElement=\"node_2b26b295-9c55-48d1-8b3a-6ef6784805a3\"> <di:waypoint x=\"738.5\" y=\"264.0\"/> <di:waypoint x=\"841.0\" y=\"241.0\"/> <di:waypoint x=\"971.0\" y=\"222.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_2b2127c9-ed86-4bbd-818d-59059f0aea8d\"> <di:waypoint x=\"1641.0\" y=\"331.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_72538e2c-22ee-46d3-b2d4-8beecb5b410c\"> <di:waypoint x=\"1641.0\" y=\"121.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_89305be2-ee4a-47de-9325-7d0adbdc50a0\"> <di:waypoint x=\"116.0\" y=\"261.0\"/> <di:waypoint x=\"246.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_8a39782a-4ff2-464c-8856-dcb3d6313930\"> <di:waypoint x=\"1073.5\" y=\"258.0\"/> <di:waypoint x=\"1176.0\" y=\"259.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_2795c58e-5fc1-4825-a78d-8d539421ff42\"> <di:waypoint x=\"1408.5\" y=\"261.0\"/> <di:waypoint x=\"1511.0\" y=\"311.0\"/> <di:waypoint x=\"1641.0\" y=\"331.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_a92d7582-702e-4462-a25d-c0b03ada62f7\"> <di:waypoint x=\"1176.0\" y=\"259.0\"/> <di:waypoint x=\"1306.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_837155b2-1c17-4df4-8cca-79ec0135a9b9\"> <di:waypoint x=\"1511.0\" y=\"261.0\"/> <di:waypoint x=\"1641.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_eb8a2454-dd78-4440-9ae0-1bf1d63b00ef\"> <di:waypoint x=\"1743.5\" y=\"261.0\"/> <di:waypoint x=\"1818.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_592f393f-ede7-43b3-9dfc-3702505c2d5c\"> <di:waypoint x=\"246.0\" y=\"261.0\"/> <di:waypoint x=\"376.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_57d6c720-eb4a-4a7a-8ea2-bc56e00ea018\"> <di:waypoint x=\"1408.5\" y=\"261.0\"/> <di:waypoint x=\"1511.0\" y=\"341.0\"/> <di:waypoint x=\"1641.0\" y=\"401.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_93624105-da73-4ed7-870e-43761126cb9e\"> <di:waypoint x=\"1641.0\" y=\"401.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_4c92f4e7-c8e0-4468-b2bb-e9c84fa9a902\"> <di:waypoint x=\"971.0\" y=\"292.0\"/> <di:waypoint x=\"1073.5\" y=\"258.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_0dcb35d4-08a3-42b4-9117-caa51f9dbfa0\"> <di:waypoint x=\"1408.5\" y=\"261.0\"/> <di:waypoint x=\"1511.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_c5a02ce3-e8e0-4ff2-bdb9-f915cf70083f\"> <di:waypoint x=\"1408.5\" y=\"261.0\"/> <di:waypoint x=\"1511.0\" y=\"154.0\"/> <di:waypoint x=\"1641.0\" y=\"121.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_ce73ab5f-6f76-4adb-b4be-858f372fa159\"> <di:waypoint x=\"636.0\" y=\"261.0\"/> <di:waypoint x=\"738.5\" y=\"264.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_4697857d-d859-465a-9bb1-0bf9ff52ad16\"> <di:waypoint x=\"1408.5\" y=\"261.0\"/> <di:waypoint x=\"1511.0\" y=\"211.0\"/> <di:waypoint x=\"1641.0\" y=\"191.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_f02dddac-277e-4e76-ac38-49c4544a1195\"> <di:waypoint x=\"971.0\" y=\"222.0\"/> <di:waypoint x=\"1073.5\" y=\"258.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_84a0f0af-aa19-4603-918d-98c7cc4caba3\"> <di:waypoint x=\"1641.0\" y=\"191.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_1b14d2f2-0484-4e7b-a5ce-4766ca0c7aca\"> <di:waypoint x=\"376.0\" y=\"261.0\"/> <di:waypoint x=\"506.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_4b210d19-e2a7-4219-aef2-98f676c81649\"> <di:waypoint x=\"13.5\" y=\"261.0\"/> <di:waypoint x=\"116.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_6a6e5562-b303-4f48-948d-ac0a767b148e\"> <di:waypoint x=\"1306.0\" y=\"261.0\"/> <di:waypoint x=\"1408.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_11329ea4-879e-45b2-bdd5-25624cf5f8b5\"> <di:waypoint x=\"738.5\" y=\"264.0\"/> <di:waypoint x=\"841.0\" y=\"291.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_408fda73-4824-480f-b242-2cafe4e6bc2e\"> <di:waypoint x=\"1641.0\" y=\"261.0\"/> <di:waypoint x=\"1743.5\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_290b6188-766e-40e4-8b18-f20075edc3bb\"> <di:waypoint x=\"506.0\" y=\"261.0\"/> <di:waypoint x=\"636.0\" y=\"261.0\"/> </bpmndi:BPMNEdge> <bpmndi:BPMNEdge bpmnElement=\"node_f8c8dc67-5088-4c00-800d-ffba953225c7\"> <di:waypoint x=\"841.0\" y=\"291.0\"/> <di:waypoint x=\"971.0\" y=\"292.0\"/> </bpmndi:BPMNEdge> </bpmndi:BPMNPlane> </bpmndi:BPMNDiagram> </definitions>";
        String layout = "{\"elements\":{\"nodes\":[{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"1\",\"textwidth\":\"90px\",\"gatewayId\":\"node_69039846-c4a8-4c22-9937-c5310ff82431\",\"height\":\"40px\"},\"position\":{\"x\":1344.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"2\",\"textwidth\":\"90px\",\"gatewayId\":\"node_f855a66e-6e8d-4c7e-9f95-312a7b8fe09d\",\"height\":\"40px\"},\"position\":{\"x\":921.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"3\",\"textwidth\":\"90px\",\"gatewayId\":\"node_96d76f6d-81b2-49c2-9043-33cc97cfd281\",\"height\":\"40px\"},\"position\":{\"x\":1767.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"diamond\",\"color\":\"white\",\"borderwidth\":\"1\",\"textsize\":\"20\",\"name\":\"X\",\"textcolor\":\"black\",\"width\":\"40px\",\"id\":\"4\",\"textwidth\":\"90px\",\"gatewayId\":\"node_2b01e159-c356-480b-8c76-777a5942425b\",\"height\":\"40px\"},\"position\":{\"x\":2190.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#e9e9f2\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Admission IC\\n\\n117\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"5\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1052.5,\"y\":398},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#9ebad1\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Admission NC\\n\\n1182\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"6\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1213.5,\"y\":257},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#0c5f91\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"CRP\\n\\n3262\",\"textcolor\":\"white\",\"width\":\"100px\",\"id\":\"7\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1636.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a7c0d5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Registration\\n\\n1050\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"8\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":146.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a8c0d5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Sepsis Triage\\n\\n1049\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"9\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":468.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#a7c0d5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"ER Triage\\n\\n1053\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"10\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":307.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#b7cadc\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"IV Antibiotics\\n\\n823\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"11\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":790.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#bccddf\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"IV Liquid\\n\\n753\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"12\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":629.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#8aaec8\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"LacticAcid\\n\\n1466\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"13\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1213.5,\"y\":398},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#045a8d\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Leucocytes\\n\\n3383\",\"textcolor\":\"white\",\"width\":\"100px\",\"id\":\"14\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1475.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#c2d1e1\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release A\\n\\n671\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"15\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":1898.5,\"y\":609.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#edecf4\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release B\\n\\n56\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"16\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":45.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#efedf5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release C\\n\\n25\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"17\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":186.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#efedf5\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release D\\n\\n24\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"18\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#f1eef6\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Release E\\n\\n6\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"19\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":468.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"roundrectangle\",\"color\":\"#dce1ed\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"Return ER\\n\\n294\",\"textcolor\":\"black\",\"width\":\"100px\",\"id\":\"20\",\"textwidth\":\"90px\",\"height\":\"80px\"},\"position\":{\"x\":2059.5,\"y\":609.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C0A3A1\",\"textsize\":\"15\",\"borderwidth\":\"3\",\"name\":\"\",\"textcolor\":\"black\",\"width\":\"30px\",\"id\":\"21\",\"textwidth\":\"90px\",\"height\":\"30px\"},\"position\":{\"x\":2287.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"shape\":\"ellipse\",\"color\":\"#C1C9B0\",\"textsize\":\"15\",\"borderwidth\":\"1\",\"name\":\"\",\"textcolor\":\"black\",\"width\":\"30px\",\"id\":\"22\",\"textwidth\":\"90px\",\"height\":\"30px\"},\"position\":{\"x\":20.5,\"y\":327.5},\"group\":\"nodes\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}],\"edges\":[{\"data\":{\"strength\":2.59,\"color\":\"#626262\",\"style\":\"solid\",\"source\":\"2\",\"label\":\"46\",\"target\":\"5\",\"id\":\"1805ce9d-167d-46ef-8cd6-b7d419af6a59\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":27.5,\"color\":\"#545454\",\"style\":\"solid\",\"source\":\"2\",\"label\":\"489\",\"target\":\"6\",\"id\":\"ee294eb2-5c5f-405e-8ba3-47c2febba4a7\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":54.72,\"color\":\"#444444\",\"style\":\"solid\",\"source\":\"1\",\"label\":\"973\",\"target\":\"14\",\"id\":\"38d78b60-e610-4507-a8ee-547eaa276e07\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":18.11,\"color\":\"#595959\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"322\",\"target\":\"15\",\"id\":\"bf459f25-16db-44c6-baa0-57180d0e1dbb\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":1.07,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"19\",\"target\":\"16\",\"id\":\"1e4b4364-0545-4e8b-903e-e37617099983\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.73,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"13\",\"target\":\"17\",\"id\":\"669bf60f-1249-4bd4-ab01-fc9237081182\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.67,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"12\",\"target\":\"18\",\"id\":\"0fd962eb-451e-46fa-b2f7-d525428e98a9\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.17,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"3\",\"label\":\"3\",\"target\":\"19\",\"id\":\"270b5d2d-c843-4c57-8e0c-3840469bff4b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":21.6,\"color\":\"#575757\",\"style\":\"solid\",\"source\":\"4\",\"label\":\"384\",\"target\":\"21\",\"id\":\"f33dc0a4-016e-4945-99f8-0542684a24f0\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":2.31,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"5\",\"label\":\"41\",\"target\":\"13\",\"id\":\"bd205e29-e6c4-4d0d-b831-ccd06f4c8f1f\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":22.95,\"color\":\"#565656\",\"style\":\"solid\",\"source\":\"6\",\"label\":\"408\",\"target\":\"1\",\"id\":\"d1131308-b765-4201-a462-da442de4832e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":20.75,\"color\":\"#585858\",\"style\":\"solid\",\"source\":\"7\",\"label\":\"369\",\"target\":\"3\",\"id\":\"2d3c27fa-123e-421d-8f36-ece3c0f5e1c3\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":54.61,\"color\":\"#444444\",\"style\":\"solid\",\"source\":\"8\",\"label\":\"971\",\"target\":\"10\",\"id\":\"445cb6ce-210a-4cc2-9d7a-cded8fe3565e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":16.03,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"9\",\"label\":\"285\",\"target\":\"12\",\"id\":\"5a059ba3-ca78-46b6-867b-ad596f1f1c9d\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":50.9,\"color\":\"#464646\",\"style\":\"solid\",\"source\":\"10\",\"label\":\"905\",\"target\":\"9\",\"id\":\"fe870c9f-c38b-49c2-ae8f-70c017689888\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":30.09,\"color\":\"#525252\",\"style\":\"solid\",\"source\":\"11\",\"label\":\"535\",\"target\":\"2\",\"id\":\"50a276b8-239c-4159-9d05-368b07ccf092\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":28.18,\"color\":\"#535353\",\"style\":\"solid\",\"source\":\"12\",\"label\":\"501\",\"target\":\"11\",\"id\":\"f879bf67-8d13-471d-beaf-9719c0df808b\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":31.78,\"color\":\"#515151\",\"style\":\"solid\",\"source\":\"13\",\"label\":\"565\",\"target\":\"1\",\"id\":\"d7796fde-d675-43d9-9646-6f19a665b126\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":100,\"color\":\"#292929\",\"style\":\"solid\",\"source\":\"14\",\"label\":\"1778\",\"target\":\"7\",\"id\":\"4ac8eff2-b801-4138-9cab-5d751c5bd9ff\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":15.52,\"color\":\"#5b5b5b\",\"style\":\"solid\",\"source\":\"15\",\"label\":\"276\",\"target\":\"20\",\"id\":\"2ad4f4e3-0d02-4325-bf0c-664402cba724\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":3.09,\"color\":\"#626262\",\"style\":\"solid\",\"source\":\"16\",\"label\":\"55\",\"target\":\"4\",\"id\":\"7e7fd446-cec2-4cf4-803d-672eecbb636e\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":1.07,\"color\":\"#636363\",\"style\":\"solid\",\"source\":\"17\",\"label\":\"19\",\"target\":\"4\",\"id\":\"18025798-caf6-492e-b63c-ac4aecfb9448\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.79,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"18\",\"label\":\"14\",\"target\":\"4\",\"id\":\"dd06aa69-27dd-47d7-a2ea-ca165220ae82\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":0.28,\"color\":\"#646464\",\"style\":\"solid\",\"source\":\"19\",\"label\":\"5\",\"target\":\"4\",\"id\":\"7ad84887-ebc6-4249-9b31-9b539884c3ba\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":16.37,\"color\":\"#5a5a5a\",\"style\":\"solid\",\"source\":\"20\",\"label\":\"291\",\"target\":\"4\",\"id\":\"18fa48d9-2e36-4222-b73f-5406d7f0f39d\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"},{\"data\":{\"strength\":55.96,\"color\":\"#434343\",\"style\":\"solid\",\"source\":\"22\",\"label\":\"995\",\"target\":\"8\",\"id\":\"56f0d3d7-c083-4341-aaf6-d9b4227268cd\"},\"position\":{},\"group\":\"edges\",\"removed\":false,\"selected\":false,\"selectable\":true,\"locked\":false,\"grabbable\":true,\"classes\":\"\"}]},\"style\":[{\"selector\":\"node\",\"style\":{\"background-color\":\"data(color)\",\"border-color\":\"black\",\"border-width\":\"data(borderwidth)\",\"color\":\"data(textcolor)\",\"label\":\"data(name)\",\"font-size\":\"data(textsize)\",\"height\":\"data(height)\",\"padding\":\"5px\",\"shape\":\"data(shape)\",\"text-border-width\":\"10px\",\"text-max-width\":\"data(textwidth)\",\"text-valign\":\"center\",\"text-wrap\":\"wrap\",\"width\":\"data(width)\"}},{\"selector\":\":selected\",\"style\":{\"border-width\":\"4px\",\"border-color\":\"#333\"}},{\"selector\":\"edge\",\"style\":{\"color\":\"data(color)\",\"curve-style\":\"bezier\",\"text-rotation\":\"0rad\",\"font-size\":\"11px\",\"label\":\"data(label)\",\"line-color\":\"data(color)\",\"line-style\":\"data(style)\",\"loop-sweep\":\"181rad\",\"loop-direction\":\"-41rad\",\"opacity\":\"1\",\"source-arrow-color\":\"data(color)\",\"target-arrow-color\":\"data(color)\",\"target-arrow-shape\":\"triangle\",\"text-background-color\":\"#ffffff\",\"text-background-opacity\":\"1\",\"text-background-padding\":\"5px\",\"text-background-shape\":\"roundrectangle\",\"width\":\"mapData(strength, 0, 100, 1, 6)\"}},{\"selector\":\"edge.questionable\",\"style\":{\"line-style\":\"dotted\",\"target-arrow-shape\":\"diamond\"}},{\"selector\":\".faded\",\"style\":{\"opacity\":\"0.25\",\"text-opacity\":\"0\"}}],\"zoomingEnabled\":true,\"userZoomingEnabled\":true,\"zoom\":0.5936823885763739,\"minZoom\":1.0E-50,\"maxZoom\":1.0E50,\"panningEnabled\":true,\"userPanningEnabled\":true,\"pan\":{\"x\":30.593682388576326,\"y\":116.06901774123756},\"boxSelectionEnabled\":true,\"renderer\":{\"name\":\"canvas\"},\"wheelSensitivity\":0.1}";
        BPMNUpdater updater = new BPMNUpdater();
        String s = updater.getUpdatedBPMN(bpmn, layout, false);
        System.out.println(s);
    }

    public Set<String> getRemovedFlowIDs() {
        return removedFlowIDs;
    }

    public String getUpdatedBPMN(String bpmn, String layout, boolean remove_gateways) {
        Map<String, Set<String>> mapSource = new HashMap<>();
        Map<String, Set<String>> mapTarget = new HashMap<>();

        Map<String, ElementLayout> layoutMap = LayoutGenerator.generateLayout(layout);

        bpmn = bpmn.substring(0, bpmn.indexOf("<bpmndi:BPMNShape"));
        String jsonEnding = "</bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>";

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
                    if(sourceID.equals(getTargetID(bpmn, flowID2)) && targetID.equals(getSourceID(bpmn, flowID2))) {
                        bend_point = true;
                        break;
                    }
                }
            }

            String edge = createBPMNEdge(flowID, source_x, source_y, target_x, target_y, bend_point);
            bpmn += edge;
        }

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