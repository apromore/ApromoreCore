package org.apromore.logman.log.activityaware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apromore.logman.log.AttributeMappingNotFoundException;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

public class ActivityTrace extends ArrayList<Activity> {
    public List<String> getAttributeTrace(XEventAttributeClassifier attributeClassifier, boolean useStart) {
        List<String> result = new ArrayList<>();
        for (Activity a : this) {
            result.add(attributeClassifier.getClassIdentity(useStart ? a.getOne() : a.getTwo()).toString());
        }
        return result;
    }
}
