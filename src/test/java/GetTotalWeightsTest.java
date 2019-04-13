import org.iota.ict.ixi.util.Attribute;
import org.iota.ict.ixi.util.TestTemplate;
import org.iota.ict.ixi.util.VertexGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class GetTotalWeightsTest extends TestTemplate {

    @Test
    public void getTotalWeightsIndependentOfTimeTest() {

        // create vertices
        String firstVertex = weighingModule.graph.createVertex(VertexGenerator.random(), VertexGenerator.generateRandomEdges(10)); // vertex of interest
        String secondVertex = weighingModule.graph.createVertex(VertexGenerator.random(), VertexGenerator.generateRandomEdges(10));
        String thirdVertex = weighingModule.graph.createVertex(VertexGenerator.random(), VertexGenerator.generateRandomEdges(10));

        // connect second vertex with first vertex
        secondVertex = weighingModule.graph.addEdge(secondVertex, firstVertex);

        // connect third vertex with first vertex
        thirdVertex = weighingModule.graph.addEdge(thirdVertex, firstVertex);

        List<String> adjacentVertices = weighingModule.graph.getReferencingVertices(firstVertex);
        Assert.assertEquals(2, adjacentVertices.size());
        Assert.assertTrue(adjacentVertices.contains(secondVertex));
        Assert.assertTrue(adjacentVertices.contains(thirdVertex));

        Set<String> weights = weighingModule.getTotalWeights(firstVertex, new Attribute[] { new Attribute() });
        Assert.assertEquals(2, adjacentVertices.size());
        Assert.assertTrue(weights.contains(secondVertex));
        Assert.assertTrue(weights.contains(thirdVertex));

    }

}
