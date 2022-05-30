package org.apromore.portal.dialogController.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.junit.jupiter.api.Test;

class SubProcessItemUnitTest {

    @Test
    void testBuildSubProcessTree() {
        try {
            InputStream in = SubProcessItemUnitTest.class.getResourceAsStream("/testSubProcessImport.bpmn");
            String bpmnText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            BPMNDiagram bpmnDiagram = BPMNDiagramFactory.newDiagramFromProcessText(bpmnText);

            SubProcessItem rootItem = SubProcessItem.buildSubProcessTree(bpmnDiagram, "test");

            assertEquals("test", rootItem.getName());
            assertNull(rootItem.getSubProcessNode());
            assertEquals(bpmnDiagram, rootItem.getDiagram());
            assertNull(rootItem.getProcessSummaryType());
            assertTrue(rootItem.getSubProcessOldToNewNodeMap().isEmpty());
            assertEquals(3, rootItem.getChildren().size());

            List<SubProcessItem> level2Items = collectAllChildren(rootItem.getChildren());
            assertEquals(1, level2Items.size());

            List<SubProcessItem> level3Items = collectAllChildren(level2Items);
            assertEquals(1, level3Items.size());

            List<SubProcessItem> level4Items = collectAllChildren(level3Items);
            assertEquals(0, level4Items.size());

            List<SubProcessItem> allItems = getAllTreeItems(rootItem);
            assertEquals(6, allItems.size());

            String[] expectedNames = {"test", "test_subprocess1", "test_subprocess2", "test_subprocess3",
                "test_subprocess4", "test_subprocess5"};
            assertArrayEquals(expectedNames,
                allItems.stream().map(SubProcessItem::getName).sorted().toArray());

        } catch (Exception e) {
            fail();
            throw new RuntimeException(e);
        }

    }

    private List<SubProcessItem> collectAllChildren(Collection<SubProcessItem> subProcessItems) {
        List<SubProcessItem> children = new ArrayList<>();
        for (SubProcessItem subProcessItem : subProcessItems) {
            children.addAll(subProcessItem.getChildren());
        }
        return children;
    }

    private List<SubProcessItem> getAllTreeItems(SubProcessItem rootItem) {
        List<SubProcessItem> subProcessItems = new ArrayList<>();
        subProcessItems.add(rootItem);
        for (SubProcessItem subProcessItem : rootItem.getChildren()) {
            subProcessItems.addAll(getAllTreeItems(subProcessItem));
        }
        return subProcessItems;
    }

}
