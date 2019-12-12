package org.apromore.service.csvimporter;

import java.util.ArrayList;
import java.util.List;

/**
 * A sample of a CSV log.
 */
public class LogSample {

    private List<String> header = new ArrayList<>();
    private List<List<String>> lines = new ArrayList<>();

    public List<String> getHeader() { return header; }
    public List<List<String>> getLines() { return lines; }
};
