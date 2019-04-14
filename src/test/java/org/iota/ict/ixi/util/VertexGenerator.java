package org.iota.ict.ixi.util;

import org.iota.ict.model.transaction.TransactionBuilder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class VertexGenerator {

    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ9";
    private static SecureRandom r = new SecureRandom();

    public static String random(){
        StringBuilder sb = new StringBuilder(81);
        for( int i = 0; i < 81; i++ )
            sb.append( alphabet.charAt( r.nextInt(alphabet.length()) ) );
        return sb.toString();
    }

    public static List<TransactionBuilder> generateRandomVertex(int edges) {

        List<TransactionBuilder> ret = new ArrayList<>();

        if(edges <= 0)
            return ret;

        String[] e = generateRandomEdges(edges);

        Graph graph = new Graph();
        String tail = graph.createVertex(random(), e);

        return graph.finalizeVertex(tail);

    }

    public static String[] generateRandomEdges(int edges) {

        String[] e = new String[edges];

        for(int i = 0; i < edges; i++)
            e[i] = random();

        return e;

    }

}