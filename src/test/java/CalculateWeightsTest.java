import org.iota.ict.ixi.model.Attribute;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.ixi.util.TestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
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
    public void calculateWeightsDependingOnTimeBetweenMaxBounds() throws InterruptedException {

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

        // build chain of transactions
        TransactionBuilder genesisBuilder = new TransactionBuilder();
        genesisBuilder.attachmentTimestampLowerBound = System.currentTimeMillis();
        genesisBuilder.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction genesisTransaction = genesisBuilder.build();
        String genesis = genesisTransaction.hash;
        ict1.submit(genesisTransaction);

        Thread.sleep(100);

        // attach first vertex
        String firstSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { firstVertex, genesis, genesis });
        Thread.sleep(100);

        // attach second vertex
        String secondSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { secondVertex, firstSerializedVertex, firstSerializedVertex });
        Thread.sleep(100);

        // attach third vertex
        String thirdSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { thirdVertex, secondSerializedVertex, secondSerializedVertex });
        Thread.sleep(100);

        // attach fourth vertex
        String fourthSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { fourthVertex, thirdSerializedVertex, thirdSerializedVertex });
        Thread.sleep(100);

        // attach random tip transaction
        TransactionBuilder tip = new TransactionBuilder();
        tip.trunkHash = fourthSerializedVertex;
        tip.branchHash = fourthSerializedVertex;
        tip.attachmentTimestampLowerBound = System.currentTimeMillis();
        tip.attachmentTimestampUpperBound = System.currentTimeMillis();
        ict1.submit(tip.build());
        Thread.sleep(1000);

        // calculate weights between 0 - MAX_LONG_VALUE
        String identifier1 = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(0, Long.MAX_VALUE));
        Set<String> weights1 = weighingModule.calculateWeightsDependingOnTime(identifier1, genesis);

        Assert.assertEquals(3, weights1.size());
        Assert.assertTrue(weights1.contains(secondVertex));
        Assert.assertTrue(weights1.contains(thirdVertex));
        Assert.assertTrue(weights1.contains(fourthVertex));

    }

    public void calculateWeightsDependingOnTimeBetweenSpecificBounds() throws InterruptedException {

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

        // build chain of transactions
        TransactionBuilder genesisBuilder = new TransactionBuilder();
        genesisBuilder.attachmentTimestampLowerBound = System.currentTimeMillis();
        genesisBuilder.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction genesisTransaction = genesisBuilder.build();
        String genesis = genesisTransaction.hash;
        ict1.submit(genesisTransaction);

        Thread.sleep(100);

        // attach first vertex
        String firstSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { firstVertex, genesis, genesis });
        Thread.sleep(100);

        // attach second vertex
        String secondSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { secondVertex, firstSerializedVertex, firstSerializedVertex });
        Thread.sleep(100);

        // attach third vertex
        String thirdSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { thirdVertex, secondSerializedVertex, secondSerializedVertex });
        Thread.sleep(100);

        // attach fourth vertex
        String fourthSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { fourthVertex, thirdSerializedVertex, thirdSerializedVertex });
        Thread.sleep(100);

        // attach random tip transaction
        TransactionBuilder tip = new TransactionBuilder();
        tip.trunkHash = fourthSerializedVertex;
        tip.branchHash = fourthSerializedVertex;
        tip.attachmentTimestampLowerBound = System.currentTimeMillis();
        tip.attachmentTimestampUpperBound = System.currentTimeMillis();
        ict1.submit(tip.build());
        Thread.sleep(1000);

        // calculate weights between 0 - FOURTH VERTEX which should return second and third vertex
        Transaction fourthVertexTransaction = ict1.findTransactionByHash(fourthSerializedVertex);
        String identifier2 = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(0, fourthVertexTransaction.attachmentTimestampUpperBound));
        Set<String> weights2 = weighingModule.calculateWeightsDependingOnTime(identifier2, genesis);

        Assert.assertTrue(weights2.contains(secondVertex));
        Assert.assertTrue(weights2.contains(thirdVertex));

        Assert.assertEquals(2, weights2.size());

    }

    @Test
    public void getLowerVerticesTest() throws InterruptedException {

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

        // build chain of transactions
        TransactionBuilder genesisBuilder = new TransactionBuilder();
        genesisBuilder.attachmentTimestampLowerBound = System.currentTimeMillis();
        genesisBuilder.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction genesisTransaction = genesisBuilder.build();
        String genesis = genesisTransaction.hash;
        ict1.submit(genesisTransaction);

        Thread.sleep(100);

        // attach first vertex
        String firstSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { firstVertex, genesis, genesis });
        Thread.sleep(100);

        // attach second vertex
        String secondSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { secondVertex, firstSerializedVertex, firstSerializedVertex });
        Thread.sleep(100);

        // attach third vertex
        String thirdSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { thirdVertex, secondSerializedVertex, secondSerializedVertex });
        Thread.sleep(100);

        // attach fourth vertex
        String fourthSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { fourthVertex, thirdSerializedVertex, thirdSerializedVertex });
        Thread.sleep(100);

        // attach random tip transaction
        TransactionBuilder tip = new TransactionBuilder();
        tip.trunkHash = fourthSerializedVertex;
        tip.branchHash = fourthSerializedVertex;
        tip.attachmentTimestampLowerBound = System.currentTimeMillis();
        tip.attachmentTimestampUpperBound = System.currentTimeMillis();
        ict1.submit(tip.build());
        Thread.sleep(1000);


        // get all vertices < FOURTH_VERTEX which should return vertex 2 and vertex 3
        Transaction fourthVertexTransaction = ict1.findTransactionByHash(fourthSerializedVertex);

        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(fourthVertexTransaction.attachmentTimestampLowerBound, fourthVertexTransaction.attachmentTimestampUpperBound));
        Set<String> result = weighingModule.getLowerVertices(identifier, genesis);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(secondVertex));
        Assert.assertTrue(result.contains(thirdVertex));

    }

    @Test
    public void getUpperVerticesTest() throws InterruptedException {

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

        // build chain of transactions
        TransactionBuilder genesisBuilder = new TransactionBuilder();
        genesisBuilder.attachmentTimestampLowerBound = System.currentTimeMillis();
        genesisBuilder.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction genesisTransaction = genesisBuilder.build();
        String genesis = genesisTransaction.hash;
        ict1.submit(genesisTransaction);

        Thread.sleep(100);

        // attach first vertex
        String firstSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { firstVertex, genesis, genesis });
        Thread.sleep(100);

        // attach second vertex
        String secondSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { secondVertex, firstSerializedVertex, firstSerializedVertex });
        Thread.sleep(100);

        // attach third vertex
        String thirdSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { thirdVertex, secondSerializedVertex, secondSerializedVertex });
        Thread.sleep(100);

        // attach fourth vertex
        String fourthSerializedVertex = weighingModule.call("Graph.ixi", "serializeAndSubmitToCustomTips", new String[] { fourthVertex, thirdSerializedVertex, thirdSerializedVertex });
        Thread.sleep(100);

        // attach random tip transaction
        TransactionBuilder tip = new TransactionBuilder();
        tip.trunkHash = fourthSerializedVertex;
        tip.branchHash = fourthSerializedVertex;
        tip.attachmentTimestampLowerBound = System.currentTimeMillis();
        tip.attachmentTimestampUpperBound = System.currentTimeMillis();
        ict1.submit(tip.build());
        Thread.sleep(1000);

        // get all vertices > SECOND VERTEX which should return vertex 3 and vertex 4
        Transaction secondVertexTransaction = ict1.findTransactionByHash(secondSerializedVertex);

        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(secondVertexTransaction.attachmentTimestampLowerBound, secondVertexTransaction.attachmentTimestampUpperBound));
        Set<String> result = weighingModule.getUpperVertices(identifier, genesis);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(thirdVertex));
        Assert.assertTrue(result.contains(fourthVertex));

    }

}
