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
    
    public IntList getAttributeTrace(XEventAttributeClassifier attributeClassifier, boolean useStart, 
                                            Map<String,Integer> nameMapping) throws AttributeMappingNotFoundException {
        IntArrayList result = new IntArrayList();
        for (Activity a : this) {
            String attValue = attributeClassifier.getClassIdentity(useStart ? a.getOne() : a.getTwo()).toString();
            if (nameMapping.containsKey(attValue)) {
                result.add(nameMapping.get(attValue));
            }
            else {
                throw new AttributeMappingNotFoundException("Not found mapping for attribute value = " + attValue);
            }
        }
        return result;
    }
}
