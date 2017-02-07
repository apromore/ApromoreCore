package org.apromore.service.logvisualizer.fuzzyminer.transformer;

import org.apromore.service.logvisualizer.fuzzyminer.model.MutableFuzzyGraph;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public abstract class FuzzyGraphTransformer {
    protected String name;

    private FuzzyGraphTransformer() {
    }

    public FuzzyGraphTransformer(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public abstract void transform(MutableFuzzyGraph var1);
}
