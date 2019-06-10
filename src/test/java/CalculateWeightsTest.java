import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.model.Attribute;
import org.iota.ict.ixi.model.Interval;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.ixi.util.TestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.iota.ict.utils.Trytes;
import org.junit.Assert;
import org.junit.Test;

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

        // calculate weight of firstVertex
        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() });
        Set<String> weights = weighingModule.calculateWeightsIndependentOfTime(identifier);
        Assert.assertEquals(2, weights.size());
        Assert.assertTrue(weights.contains(secondVertex));
        Assert.assertTrue(weights.contains(thirdVertex));

    }

    @Test
    public void calculateWeightsDependingOnBoundlessTimeInterval() throws InterruptedException {

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

        // attach vertices to the Tangle (use different timestamps) and get their serialized tails
        // create genesis for random walk start
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

        // calculate weights within time interval [0, MAX_LONG_VALUE], which should return all vertices which point to firstVertex
        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(0, Long.MAX_VALUE));
        Set<String> weights = weighingModule.calculateWeightsDependingOnTime(identifier, genesis);

        Assert.assertEquals(3, weights.size());
        Assert.assertTrue(weights.contains(secondVertex));
        Assert.assertTrue(weights.contains(thirdVertex));
        Assert.assertTrue(weights.contains(fourthVertex));

    }

    @Test
    public void calculateWeightsDependingOnUnusedTimeInterval() throws InterruptedException {

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

        // attach vertices to the Tangle (use different timestamps) and get their serialized tails
        // create genesis for random walk start
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

        // calculate weights within time interval [0, secondVertex], which shouldn't return any vertices, since there aren't any before secondVertex
        Transaction secondVertexTransaction = ict1.findTransactionByHash(secondSerializedVertex);
        String identifier1 = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(0, secondVertexTransaction.attachmentTimestampLowerBound));
        Set<String> weights1 = weighingModule.calculateWeightsDependingOnTime(identifier1, genesis);

        Assert.assertEquals(0, weights1.size());

        // calculate weights within time interval [foruthVertex, MAX_LONG_VALUE], which shouldn't return any vertices, since there aren't any after foruthVertex
        Transaction fourthVertexTransaction = ict1.findTransactionByHash(fourthSerializedVertex);
        String identifier2 = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(fourthVertexTransaction.attachmentTimestampUpperBound, Long.MAX_VALUE));
        Set<String> weights2 = weighingModule.calculateWeightsDependingOnTime(identifier2, genesis);

        Assert.assertEquals(0, weights2.size());

    }

    @Test
    public void calculateWeightsDependingOnSpecificTimeInterval() throws InterruptedException {

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

        // attach vertices to the Tangle (use different timestamps) and get their serialized tails
        // create genesis for random walk start
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

        // calculate weights between [0, fourthVertex], which should return secondVertex and thirdVertex. fourthVertex isn't included, since its lowerbound = previousTransactionUpperbound and its upperbound = nextTransactionLowerbound
        Transaction fourthVertexTransaction = ict1.findTransactionByHash(fourthSerializedVertex);
        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(0, fourthVertexTransaction.attachmentTimestampLowerBound)); // = upperbound of thirdVertex
        Set<String> weights = weighingModule.calculateWeightsDependingOnTime(identifier, genesis);

        Assert.assertTrue(weights.contains(secondVertex));
        Assert.assertTrue(weights.contains(thirdVertex));

        Assert.assertEquals(2, weights.size());

    }

    @Test
    public void calculateWeightsDependingOnSpecificTimeIntervalOfOneVertex() throws InterruptedException {

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

        // attach vertices to the Tangle (use different timestamps) and get their serialized tails
        // create genesis for random walk start
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

        // calculate weights between [secondVertex, fourthVertex], which should return thirdVertex only.
        Transaction secondVertexTransaction = ict1.findTransactionByHash(secondSerializedVertex);
        Transaction fourthVertexTransaction = ict1.findTransactionByHash(fourthSerializedVertex);
        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(secondVertexTransaction.attachmentTimestampUpperBound, fourthVertexTransaction.attachmentTimestampLowerBound));
        Set<String> weights = weighingModule.calculateWeightsDependingOnTime(identifier, genesis);

        Assert.assertTrue(weights.contains(thirdVertex));

        Assert.assertEquals(1, weights.size());

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
        Transaction tipTx = tip.build();
        ict1.submit(tipTx);

        // get all vertices < FOURTH_VERTEX which should return vertex 2 and vertex 3
        Transaction fourthVertexTransaction = ict1.findTransactionByHash(fourthSerializedVertex);

        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(fourthVertexTransaction.attachmentTimestampLowerBound, fourthVertexTransaction.attachmentTimestampUpperBound));
        Set<String> result = weighingModule.getLowerVertices(identifier, genesis);

        Assert.assertTrue(result.contains(secondVertex));
        Assert.assertTrue(result.contains(thirdVertex));
        Assert.assertEquals(2, result.size());

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

        // get all vertices > SECOND VERTEX which should return vertex 3 and vertex 4
        Transaction secondVertexTransaction = ict1.findTransactionByHash(secondSerializedVertex);

        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute() }, new Interval(secondVertexTransaction.attachmentTimestampLowerBound, secondVertexTransaction.attachmentTimestampUpperBound));
        Set<String> result = weighingModule.getUpperVertices(identifier, genesis);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(thirdVertex));
        Assert.assertTrue(result.contains(fourthVertex));

    }

    @Test
    public void calculateWeightsDependingOnAttributesAndBoundlessTimeInterval() throws InterruptedException {

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

        // attach vertices to the Tangle (use different timestamps) and get their serialized tails
        // create genesis for random walk start
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

        // let's build and publish a DataFragment to all adjacent vertices

        // to build a DataFragment, we need a ClassFragment
        // (in this example, our class have a reference to a random transaction and an attribute name 'ATTRIB' with a length of 33 trytes)
        String response = caller.call(new FunctionEnvironment("Serialization.ixi","publishClassFragment"),
                /*   className;trunk;branch;attribute 0 definition; reference 0 definition   */
                "JUST9ANOTHER9CLASS9NAME;"+ Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";33 ATTRIB;"+Trytes.NULL_HASH,  //using a different classname to avoid collision with the other test
                250);

        String myClassFragmentClassHash = response.split(";")[1];

        Thread.sleep(300);

        //now that we have our classFragment, let's build and publish the DataFragments

        //the first dataFragment will reference the secondSerializedVertex

        response = caller.call(new FunctionEnvironment("Serialization.ixi","publishDataFragment"),
                /* classhash;trunk;branch;attribute 0;reference 0   */
                myClassFragmentClassHash+";"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";A 0 VALUE9A;R 0 "+secondSerializedVertex,
                250);
        String firstFragmentHash = response;

        //now we publish a second DataFragment of the same class, referencing our thirdSerializedVertex tx, but with a different value for ATTRIB
        response = caller.call(new FunctionEnvironment("Serialization.ixi","publishDataFragment"),
                myClassFragmentClassHash+";"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";A 0 VALUE9B;R 0 "+thirdSerializedVertex,
                250);
        String secondFragmentHash = response;

        //now we publish a third DataFragment of the same class, referencing our fourthSerializedVertex
        response = caller.call(new FunctionEnvironment("Serialization.ixi","publishDataFragment"),
                myClassFragmentClassHash+";"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";A 0 VALUE9B;R 0 "+fourthSerializedVertex,
                250);
        String thirdFragmentHash = response;

        Thread.sleep(1000);

        // calculate weights within time interval [0, MAX_LONG_VALUE] AND match VALUE9B, which should return the thirdVertex and fourthVertex
        String identifier = weighingModule.beginWeighingCalculation(firstVertex, new Attribute[] { new Attribute(0, "VALUE9B") }, new Interval(0, Long.MAX_VALUE));
        Set<String> weights = weighingModule.calculateWeightsDependingOnTime(identifier, genesis);

        Assert.assertEquals(2, weights.size());
        Assert.assertTrue(weights.contains(thirdVertex));
        Assert.assertTrue(weights.contains(fourthVertex));

    }

}
