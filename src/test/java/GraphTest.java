import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.util.TestTemplate;
import org.junit.Assert;
import org.junit.Test;

public class GraphTest extends TestTemplate {

    @Test
    public void startVertexTest() {

        String data = "MY9DATA99999999999999999999999999999999999999999999999999999999999999999999999999";
        String edge = "MY9EDGE99999999999999999999999999999999999999999999999999999999999999999999999999";

        String vertexHash = caller.call(new FunctionEnvironment("Graph.ixi", "startVertex"), data + ";" + edge, 3000);
        String receivedData = caller.call(new FunctionEnvironment("Graph.ixi", "getData"), vertexHash, 1000);

        Assert.assertEquals(data, receivedData);

    }

}
