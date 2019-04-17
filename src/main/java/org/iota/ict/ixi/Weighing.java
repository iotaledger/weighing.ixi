package org.iota.ict.ixi;

import org.iota.ict.eee.call.EEEFunctionCallerImplementation;
import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.model.Attribute;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.WeighingCalculation;
import org.iota.ict.ixi.util.Generator;

import java.util.*;

public class Weighing extends IxiModule {

    private EEEFunctionCallerImplementation caller;
    private Map<String, WeighingCalculation> calculations = new HashMap<>();

    public Weighing(Ixi ixi) {
        super(ixi);
        caller = new EEEFunctionCallerImplementation(ixi);
    }

    @Override
    public void run() {
        System.out.println("Weighing.ixi loaded!");
    }

    public String call(String service, String function, String... args) {
        String arguments = "";
        for(int i = 0; i < args.length; i++) {
            arguments += args[i];
            if(i < args.length - 1)
                arguments += ";";
        }
        return caller.call(new FunctionEnvironment(service, function), arguments, 3000);
    }

    public String beginWeighingCalculation(String vertex, Attribute[] attributes) {
        String identifier = Generator.getRandomHash();
        calculations.put(identifier, new WeighingCalculation(vertex, attributes));
        return identifier;
    }

    public String beginWeighingCalculation(String vertex, Attribute[] attributes, Interval interval) {
        String identifier = beginWeighingCalculation(vertex, attributes);
        WeighingCalculation calculation = calculations.get(identifier);
        calculation.setInterval(interval);
        return identifier;
    }

    // returns the number of referencing vertices to a given vertex that match a given set of attributes regardless of time
    public Set<String> calculateTotalWeights(String identifier) {

        WeighingCalculation calculation = calculations.get(identifier);
        String vertex = calculation.getVertex();
        Attribute[] attributes = calculation.getAttributes();

        Set<String> weights = new HashSet<>();
        String[] referencingVertices = call("Graph.ixi", "getReferencingVertices", vertex).split(";");

        for(String v: referencingVertices)
            if(isMatchingAttributes(v, attributes))
                weights.add(v);

        calculation.setResult(weights);
        return weights;

    }

    // returns the number of referencing nodes to a given node that correspond to a given set of attributes, depending on time
    public Set<String> calculateTotalWeightsDependingOnTime(String identifier) {

        calculateTotalWeights(identifier);
        WeighingCalculation calculation = calculations.get(identifier);
        Interval interval = calculation.getInterval();
        Set<String> vertices = calculation.getResult();

        for(String v: new HashSet<>(vertices))
            if(!isMatchingTimeInterval(v, interval.getLowerbound(), interval.getUpperbound()))
                vertices.remove(v);

        calculation.setResult(vertices);
        return vertices;

    }

    // returns a list of vertices at the lower bound of the time frame
    //List<String> getLowerVertices(String identifier) {


   // }
    // returns a list of vertices at the upper bound of the time frame
  //  List<Hash> getUpperVertices(Hash identifier)

    private boolean isMatchingAttributes(String vertex, Attribute[] attributes) {
        // check if available keys match given attributes
        // needs Serialization.ixi to know how data is structured
        return true;
    }

    private boolean isMatchingTimeInterval(String transactionHash, long lowerbound, long upperbound) {

        String[] interval = call("Timestamping.ixi", "getTimestampInterval", transactionHash).split(";");
        long l = Long.parseLong(interval[0]);
        long u = Long.parseLong(interval[1]);

        if(l >= lowerbound && u <= upperbound)
            return true;

        return false;

    }

    private boolean isAtTheLowerbound(String identifier) {
        return true;
    }

    private boolean isAtTheUpperbound(String identifier) {
        return true;
    }

}
