package org.apromore.service.csvimporter;

import java.util.List;

/**
 * A sample of a CSV log.
 */
public class LogSample {

    private List<String> header;

    private List<List<String>> lines;

    public LogSample(List<String> header, List<List<String>> lines) {
        this.header = header;
        this.lines = lines;
    }

    public List<String> getHeader() { return header; }

    public List<List<String>> getLines() { return lines; }
};
