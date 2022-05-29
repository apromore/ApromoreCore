package org.apromore.portal.dialogController.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;

@Builder
public class SubProcessItem {
    @Getter
    @Builder.Default
    private String name = "";

    @Getter
    private SubProcess subProcessNode;

    @Getter
    @Builder.Default
    private BPMNDiagram diagram = new BPMNDiagramImpl("");

    @Getter
    @Builder.Default
    private Map<BPMNNode, BPMNNode> subProcessOldToNewNodeMap = new HashMap<>();

    @Builder.Default
    private Collection<SubProcessItem> children = new HashSet<>();

    @Getter @Setter
    private ProcessSummaryType processSummaryType;

    public Collection<SubProcessItem> getChildren() {
        return Collections.unmodifiableCollection(children);
    }

    public void addChild(SubProcessItem child) {
        children.add(child);
    }

    public void addChildAll(Collection<SubProcessItem> items) {
        children.addAll(items);
    }

    public boolean contains(SubProcessItem item) {
        return subProcessNode.getChildren().contains(item.getSubProcessNode());
    }

    public static SubProcessItem buildSubProcessTree(BPMNDiagram diagram, String name) {
        // Create all SubProcessItem from the diagram
        Collection<SubProcessItem> subProcessItems = new HashSet<>();
        int count = 0;
        for (SubProcess subProcess : diagram.getSubProcesses()) {

            BPMNDiagram subProcessDiagram = BPMNDiagramFactory.newBPMNDiagram("");
            Map<BPMNNode, BPMNNode> subProcessOldToNewNodeMap = subProcessDiagram.cloneSubProcessContents(subProcess);

            subProcessItems.add(SubProcessItem.builder()
                .subProcessNode(subProcess)
                .diagram(subProcessDiagram)
                .name(name + "_subprocess" + ++count)
                .subProcessOldToNewNodeMap(subProcessOldToNewNodeMap)
                .build());
        }

        // Create parent-child relationship
        Collection<SubProcessItem> childItems = new HashSet<>();
        for (SubProcessItem item : subProcessItems) {
            for (SubProcessItem other : subProcessItems) {
                if (item.contains(other)) {
                    item.addChild(other);
                    childItems.add(other);
                }
            }
        }

        // Those items which are not updated as child of any are actually child of the top item
        Collection<SubProcessItem> noChildItems = new HashSet<>(subProcessItems);
        noChildItems.removeAll(childItems);
        SubProcessItem topItem = SubProcessItem.builder().diagram(diagram).name(name).build();
        topItem.addChildAll(noChildItems);

        return topItem;
    }
}
