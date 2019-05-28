package org.iota.ict.ixi;

import org.iota.ict.eee.call.EEEFunctionCallerImplementation;
import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.model.Attribute;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.model.WeighingCalculation;
import org.iota.ict.ixi.util.Generator;

import java.util.*;

public class WeighingModule extends IxiModule {

    private EEEFunctionCallerImplementation caller;
    private Map<String, WeighingCalculation> calculations = new HashMap<>();

    public WeighingModule(Ixi ixi) {
        super(ixi);
        caller = new EEEFunctionCallerImplementation(ixi);
    }

    @Override
    public void run() {
        System.out.println("Weighing.ixi successfully started!");
    }

    /**
     * This method provides an easy interface to invoke functions of depending IXI modules.
     * @param service the module to interact with
     * @param function the function to invoke
     * @param args the arguments of the function
     * @return returns the result of the function
     */
    public String call(String service, String function, String... args) {
        String arguments = "";
        for(int i = 0; i < args.length; i++) {
            arguments += args[i];
            if(i < args.length - 1)
                arguments += ";";
        }
        return caller.call(new FunctionEnvironment(service, function), arguments, 3000);
    }

    /**
     * Initializes a weighing calculation and returns its identifier.
     * @param vertex the vertex for which the weights are to be calculated
     * @param attributes the attributes to match
     * @return returns the identifier of the calculation
     */
    public String beginWeighingCalculation(String vertex, Attribute[] attributes) {
        String identifier = Generator.getRandomHash();
        calculations.put(identifier, new WeighingCalculation(vertex, attributes));
        return identifier;
    }

    /**
     * Initializes a weighing calculation and returns its identifier.
     * @param vertex the vertex for which the weights are to be calculated
     * @param attributes the attributes to match
     * @param interval the time interval to match
     * @return returns the identifier of the calculation
     */
    public String beginWeighingCalculation(String vertex, Attribute[] attributes, Interval interval) {
        String identifier = beginWeighingCalculation(vertex, attributes);
        WeighingCalculation calculation = calculations.get(identifier);
        calculation.setInterval(interval);
        return identifier;
    }

     /**
     * Calculates the number of referencing vertices to a given vertex that match a given set of attributes regardless of time
     * @param identifier the WeighingCalculation identifier, which contains the information of all attributes
     * @return returns the weights of the interested vertex, regardless on time
     */
    public Set<String> calculateWeightsIndependentOfTime(String identifier) {

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

    /**
     * Calculates the number of referencing vertices to a given vertex that correspond to a given set of attributes, depending on time.
     * @param identifier the WeighingCalculation identifier, which contains the information of all attributes and the time window
     * @param randomWalkEntry the entry for the random walk timestamping procedure
     * @return returns the weights of the interested vertex, depending on time
     */
    public Set<String> calculateWeightsDependingOnTime(String identifier, String randomWalkEntry) {

        WeighingCalculation calculation = calculations.get(identifier);
        String vertex = calculation.getVertex();
        Attribute[] attributes = calculation.getAttributes();
        Interval interval = calculation.getInterval();

        Set<String> weights = new HashSet<>();
        String[] referencingVertices = call("Graph.ixi", "getReferencingVertices", vertex).split(";");

        for(String v: referencingVertices)
            if(isMatchingAttributes(v, attributes))
                weights.add(v);

        for(String v: new HashSet<>(weights)) {

            String serializedTail = getSerializedTail(v);
            if(!isMatchingTimeInterval(serializedTail, interval.getLowerbound(), interval.getUpperbound(), randomWalkEntry))
                weights.remove(v);

        }


        calculation.setResult(weights);
        return weights;

    }

    /**
     * Returns all the vertices which match the given set of attributes and are below the lower bound of the time window
     * @param identifier the WeighingCalculation identifier, which contains the information of all attributes and the time window
     * @param randomWalkEntry the entry for the random walk timestamping procedure
     * @return returns the interested set of vertices which are below the lower bound of the time window
     */
    public Set<String> getLowerVertices(String identifier, String randomWalkEntry) {

        WeighingCalculation calculation = calculations.get(identifier);
        String vertex = calculation.getVertex();
        Attribute[] attributes = calculation.getAttributes();
        long lowerbound = calculation.getInterval().getLowerbound();

        String beginWeighingCalculation = beginWeighingCalculation(vertex, attributes, new Interval(0, lowerbound));

        return calculateWeightsDependingOnTime(beginWeighingCalculation, randomWalkEntry);

    }

    /**
     * Returns all the vertices which match the given set of attributes and are above the upper bound of the time window.
     * @param identifier the WeighingCalculation identifier, which contains the information of all attributes and the time window
     * @param randomWalkEntry the entry for the random walk timestamping procedure
     * @return returns the interested set of vertices which are above the upper bound of the time window
     */
    public Set<String> getUpperVertices(String identifier, String randomWalkEntry) {

        WeighingCalculation calculation = calculations.get(identifier);
        String vertex = calculation.getVertex();
        Attribute[] attributes = calculation.getAttributes();
        long upperbound = calculation.getInterval().getUpperbound();

        String beginWeighingCalculation = beginWeighingCalculation(vertex, attributes, new Interval(upperbound, Long.MAX_VALUE));

        return calculateWeightsDependingOnTime(beginWeighingCalculation, randomWalkEntry);

    }

    private boolean isMatchingAttributes(String vertex, Attribute[] attributes) {
        // check if available keys match given attributes
        // needs Serialization.ixi to know how data is structured
        return true;
    }

    /**
     * Checks if a vertex (serializedTail) was issued within a specific time window. This method makes use of Timestamping.ixi to find its confidence interval.
     * @param lowerbound the start of the time window
     * @param upperbound the end of the time window
     * @param randomWalkEntry the entry for the random walk timestamping procedure
     * @return returns true if the transaction was attached in the given time interval, else return false
     */
    private boolean isMatchingTimeInterval(String serializedTail, long lowerbound, long upperbound, String randomWalkEntry) {

        String identifier = caller.call(new FunctionEnvironment("Timestamping.ixi", "beginTimestampCalculation"), serializedTail + ";" + randomWalkEntry, 3000);
        String[] interval = caller.call(new FunctionEnvironment("Timestamping.ixi", "getTimestampInterval"), identifier, 3000).split(";");

        long l = Long.parseLong(interval[0]);
        long u = Long.parseLong(interval[1]);

        if(l >= lowerbound && u <= upperbound)
            return true;

        return false;

    }

    /**
     * Gets the serialized vertex tail for a given virtual vertex tail. This method makes use of Graph.ixi to find it.
     * @param virtualTail the virtual vertex tail to be checked
     * @return returns its serialized tail hash
     */
    public String getSerializedTail(String virtualTail) {
        return call("Graph.ixi", "getSerializedTail", virtualTail);
    }

}