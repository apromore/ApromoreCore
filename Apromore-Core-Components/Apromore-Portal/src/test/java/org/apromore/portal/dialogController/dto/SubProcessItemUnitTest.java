/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
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
