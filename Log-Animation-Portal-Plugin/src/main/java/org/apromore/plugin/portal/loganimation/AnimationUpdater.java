package org.apromore.plugin.portal.loganimation;

import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/11/17.
 */
public class AnimationUpdater {

//    public String updateAnimationData(String animationData, Set<String> removedFlowIDs) {
//        for(String flowID : removedFlowIDs) {
//            while (animationData.contains(flowID)) {
//                String flowData = animationData.substring(animationData.indexOf(flowID) - 23);
//                flowData = flowData.substring(0, flowData.indexOf("}") + 1);
//                animationData = animationData.replace(flowData, "");
//                animationData = animationData.replace(",,", ",");
//            }
//        }
//        return animationData;
//    }

    public String updateAnimationData(String animationData, Set<String> removedFlowIDs) {
        StringTokenizer st = new StringTokenizer(animationData, "}", true);
        StringBuilder sb = new StringBuilder();

        boolean remove = false;
        while(st.hasMoreElements()) {
            String token = st.nextToken();
            if(!token.equals("}")) remove = false;

            for(String flowID : removedFlowIDs) {
                if(token.contains(flowID)) {
                    remove = true;
                    break;
                }
            }
            if(!remove) {
                sb.append(token);
            }
        }
        return animationData;
    }

}
