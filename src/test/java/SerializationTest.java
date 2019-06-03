import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.ixi.util.TestTemplate;
import org.junit.Test;

public class SerializationTest extends TestTemplate {

    @Test
    public void testSerialization() {

        // publish a ClassFragment:

        String requestId = "0";
        String referenced_trunk_hash = Generator.getRandomHash();
        String referenced_branch_hash = Generator.getRandomHash();
        String ref0_classHash = "NULL";
        String attribute0_size = "4";
        String attribute1_size = "6";

        String args1 = requestId + ";" + referenced_trunk_hash + ";" + referenced_branch_hash + ";" + ref0_classHash + ";" + attribute0_size + ";" + attribute1_size;
        String classHash = caller.call(new FunctionEnvironment("Serialization.ixi","publishClassFragment"), args1, 3000).split(";")[2];

        // publish a DataFragment:

        String myOtherRequestId = "1";
        String myOtherReferenced_trunk_hash = Generator.getRandomHash();
        String myOtherReferenced_branch_hash = Generator.getRandomHash();
        String data = "A 0 ATTRIB9ZERO9VALUE";

        String args2 = myOtherRequestId + ";" + classHash + ";" + myOtherReferenced_trunk_hash + ";" + myOtherReferenced_branch_hash + ";" + data;

        FunctionEnvironment publishDataFragment = new FunctionEnvironment("Serialization.ixi","publishDataFragment");
        ict1.submitEffect(publishDataFragment,args2);

    }



}
