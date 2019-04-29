import org.iota.ict.ixi.model.Attribute;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.ixi.util.TestTemplate;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CalculateWeightsTest extends TestTemplate {

    @Test
    public void calculateWeightsIndependentOfTime() {

        // create vertices
        String firstVertex =  weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() }); // vertex of interest
        String secondVertex = weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() });
        String thirdVertex = weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() });

        // connect second vertex with first vertex
        secondVertex = weighingModule.call("Graph.ixi", "addEdge", new String[] { secondVertex, firstVertex });

        // connect third vertex with first vertex
        thirdVertex = weighingModule.call("Graph.ixi", "addEdge", new String[] { thirdVertex, firstVertex });



        String[] adjacentVertices = weighingModule.call("Graph.ixi", "getReferencingVertices", new String[] { firstVertex }).split(";");
        Assert.assertEquals(2, adjacentVertices.length);
        List<String> list = Arrays.asList(adjacentVertices);
        Assert.assertTrue(list.contains(secondVertex));
        Assert.assertTrue(list.contains(thirdVertex));

        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() });

        Set<String> weights = weighingModule.calculateWeightsIndependentOfTime(identifier);
        Assert.assertEquals(2, weights.size());
        Assert.assertTrue(weights.contains(secondVertex));
        Assert.assertTrue(weights.contains(thirdVertex));

    }

    @Test
    public void calculateWeightsDependingOnTime() throws InterruptedException {

        // create vertices
        String firstVertex =  weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() }); // vertex of interest
        String secondVertex = weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() });
        String thirdVertex = weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() });
        String fourthVertex = weighingModule.call("Graph.ixi", "createVertex", new String[] { Generator.getRandomHash(), Generator.getRandomHash() });

        // connect second vertex with first vertex
        secondVertex = weighingModule.call("Graph.ixi", "addEdge", new String[] { secondVertex, firstVertex });

        // connect third vertex with first vertex
        thirdVertex = weighingModule.call("Graph.ixi", "addEdge", new String[] { thirdVertex, firstVertex });

        // connect fourth vertex with first vertex
        fourthVertex = weighingModule.call("Graph.ixi", "addEdge", new String[] { fourthVertex, firstVertex });

        long lowerbound1 = System.currentTimeMillis();
        String serializedSecondVertex = weighingModule.call("Graph.ixi", "serializeAndSubmit", new String[] { secondVertex }).split(";")[0];
        String serializedThirdVertex = weighingModule.call("Graph.ixi", "serializeAndSubmit", new String[] { thirdVertex }).split(";")[0];
        Thread.sleep(2000);
        long upperbound1 = System.currentTimeMillis();

        Thread.sleep(100);

        long lowerbound2 = System.currentTimeMillis();
        String serializedFourthVertex = weighingModule.call("Graph.ixi", "serializeAndSubmit", new String[] { fourthVertex }).split(";")[0];
        Thread.sleep(2000);
        long upperbound2 = System.currentTimeMillis();

        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(lowerbound1, upperbound1));
        Set<String> weights = weighingModule.calculateWeightsDependingOnTime(identifier);

        Assert.assertEquals(2, weights.size());
        Assert.assertTrue(weights.contains(secondVertex));
        Assert.assertTrue(weights.contains(thirdVertex));


    }

}
