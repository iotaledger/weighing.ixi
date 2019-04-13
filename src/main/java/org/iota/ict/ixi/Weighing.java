package org.iota.ict.ixi;

import org.iota.ict.ixi.util.Attribute;
import org.iota.ict.ixi.util.Graph;

import java.util.HashSet;
import java.util.Set;

public class Weighing extends IxiModule {

    public Graph graph = new Graph();

    public Weighing(Ixi ixi) {
        super(ixi);
    }

    @Override
    public void run() { ; }

    // returns the number of referencing vertices to a given vertex that match a given set of attributes regardless of time
    public Set<String> getTotalWeights(String vertex, Attribute[] attributes) {

        Set<String> weights = new HashSet<>();
        for(String v: graph.getReferencingVertices(vertex))

            if(isMatchingAttributes(v, attributes))
                weights.add(v);

        return weights;

    }

    private boolean isMatchingAttributes(String vertex, Attribute[] attributes) {
        // check if available keys match given attributes
        // needs Serialization.ixi to know how data is structured
        return true;
    }

    private boolean isMatchingTimeInterval(long lowerbound, long upperbound) {
        // needs Timestamping.ixi to find the confidence interval
        return true;
    }

}
