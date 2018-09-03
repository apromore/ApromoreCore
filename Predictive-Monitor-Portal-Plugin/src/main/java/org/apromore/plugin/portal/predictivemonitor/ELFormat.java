package org.apromore.plugin.portal.predictivemonitor;

public class ELFormat {
    public static String formatPredictorType(String s) {
        switch (s) {
        case "-1":      return "Case outcome > median";
        case "remtime": return "Remaining time";
        case "next":    return "Next activity";
        default:
            try {
                Double threshold = Double.valueOf(s);
                return "Case outcome > " + s;

            } catch (Throwable e) {
                return "Unknown predictor type: " + s;
            }
        }
    }
}
