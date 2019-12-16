package org.apromore.service.csvimporter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.service.csvimporter.impl.Parse;

/**
 * A sample of a CSV log.
 */
public class LogSample implements Constants {

    // Instance variables

    private List<String> header;

    private List<List<String>> lines;

    private Map<String, Integer> heads;
    private String timestampFormat;
    private String startTsFormat;


    // Constructor

    public LogSample(List<String> header, List<List<String>> lines) {
        this.header = header;
        this.lines = lines;
        this.heads = toHeads(header, lines.get(0));
    }


    // Accessors

    public List<String> getHeader() { return header; }

    public List<List<String>> getLines() { return lines; }

    public Map<String, Integer> getHeads() { return heads; }

    public String getTimestampFormat() { return timestampFormat; }

    public void setTimestampFormat(String s) { timestampFormat = s; }

    public String getStartTsFormat() { return startTsFormat; }

    public void setStartTsFormat(String s) { startTsFormat = s; }


    // Internal methods

    private Map<String, Integer> toHeads(List<String> line, List<String> sampleLine) {

        final Parse parse = new Parse();

        // initialize map
        Map<String, Integer> heads = new HashMap<>();
        heads.put(caseid, -1);
        heads.put(activity, -1);
        heads.put(timestamp, -1);
        heads.put(tsStart, -1);
        heads.put(resource, -1);

        for (int i = 0; i <= line.size() - 1; i++) {
            if (sampleLine.get(i) != null) {
                if ((heads.get(caseid) == -1) && getPos(caseIdValues, line.get(i))) {
                    heads.put(caseid, i);
                } else if ((heads.get(activity) == -1) && getPos(activityValues, line.get(i))) {
                    heads.put(activity, i);
                } else if ((heads.get(timestamp) == -1) && getPos(timestampValues, line.get(i).toLowerCase())) {
                    String format = parse.determineDateFormat(sampleLine.get(i));
                    if (format != null) {
                        heads.put(timestamp, i);
                        timestampFormat = format;
                    }
                } else if ((heads.get(tsStart) == -1) && getPos(StartTsValues, line.get(i))) {
                    String format = parse.determineDateFormat(sampleLine.get(i));
                    if (format != null) {
                        heads.put(tsStart, i);
                        startTsFormat = format;
                    }
                } else if ((heads.get(resource) == -1) && getPos(resourceValues, line.get(i))) {
                    heads.put(resource, i);
                }
            }
        }

        return heads;
    }

    /**
     * Gets the pos.
     *
     * @param col  the col: array which has possible names for each of the mandatory fields.
     * @param elem the elem: one item of the CSV line array
     * @return the pos: boolean value confirming if the elem is the required element.
     */
    private static boolean getPos(String[] col, String elem) {
        if (col == timestampValues || col == StartTsValues) {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::equals);
        } else {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::contains);
        }
    }
}
