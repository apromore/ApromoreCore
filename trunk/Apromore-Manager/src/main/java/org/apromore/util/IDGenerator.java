package org.apromore.util;

public class IDGenerator {

    private static int currentId = 1;

    public static int generateID() {
        currentId++;
        return currentId;
    }
}
