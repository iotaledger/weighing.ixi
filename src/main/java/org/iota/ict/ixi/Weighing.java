package org.iota.ict.ixi;

import org.iota.ict.ixi.util.Graph;
import org.iota.ict.model.transaction.Transaction;

import java.util.*;

public class Weighing extends IxiModule {

    private Graph graph = new Graph();
    public Weighing(Ixi ixi) {
        super(ixi);
    }

    @Override
    public void run() { ; }

    // returns total weight of all compound vertices independent of time
    public Map<String, Long> getTotalWeights() {

        Set<Transaction> vertices = new HashSet<>();

        for(Transaction t: graph.getTransactionsByHash().values()) {

            boolean isTip = true;
            for(Transaction x: graph.getTransactionsByHash().values()) {
                if(x==t)
                    continue;
                if(x.trunkHash().equals(t.hash)) {
                    isTip = false;
                    break;
                }
            }

            if(isTip)
                vertices.add(t);

        }

        Map<String, Long> weights = new HashMap<>();

        for(Transaction t: vertices) {

            long weight = 0;

            for(Transaction x: vertices) {

                if(x == t)
                    continue;

                List<String> edges = graph.getEdges(x.hash);

                if(edges.contains(t.branchHash()))
                    weight++;

            }

            weights.put(t.hash, weight);

        }

        return weights;

    }

}
