package org.iota.ict.ixi;

import org.iota.ict.ixi.util.Attribute;
import org.iota.ict.ixi.util.Graph;

import java.util.HashSet;
import java.util.Set;

public class Weighing extends IxiModule {

    private Graph graph = new Graph();

    public Weighing(Ixi ixi) {
        super(ixi);
    }

    @Override
    public void run() { ; }

    // returns total weight of all compound vertices independent of time
    public Set<String> getTotalWeights(String vertex, Attribute[] attributesToMatch) {

        Set<String> vertexTails = graph.getVertexTails();
        Set<String> weights = new HashSet<>();

        for(String x: vertexTails) {

            if(!graph.isReferencing(x,vertex))
                continue;

            if(matchesAttributes(x, attributesToMatch))
                weights.add(x);

        }

        return weights;

    }

    private boolean matchesAttributes(String transactionHash, Attribute[] attributesToMatch) {
        // check if available keys match given attributes
        // needs Serialization.ixi to know how data is structured
        return true;
    }

}
