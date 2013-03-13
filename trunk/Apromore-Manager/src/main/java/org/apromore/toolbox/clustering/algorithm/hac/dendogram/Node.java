package org.apromore.toolbox.clustering.algorithm.hac.dendogram;

import java.util.List;

public interface Node {

    Node getFirst();

    Node getSecond();

    int getIndex();

    List<Integer> getChildren();
}
