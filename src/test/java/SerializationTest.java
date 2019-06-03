import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.ixi.util.TestTemplate;
import org.iota.ict.utils.Trytes;
import org.junit.Assert;
import org.junit.Test;

public class SerializationTest extends TestTemplate {

    @Test
    public void testSerialization() throws InterruptedException {

        // publish a ClassFragment

        String my_classname = "CLASS9FRAGMENT9TEST";
        String referenced_trunk_hash = Generator.getRandomBundleHead();
        String referenced_branch_hash = Generator.getRandomBundleHead();
        String ref0_classHash = Trytes.NULL_HASH;
        String attribute0_name = "FIRST9ATTRIBUTE";
        String attribute0_size = "17";
        String attribute1_name = "SECOND9ATTRIBUTE";
        String attribute1_size = "17";

        String args1 =  my_classname + ";" +
                        referenced_trunk_hash + ";" +
                        referenced_branch_hash + ";" +
                        ref0_classHash + ";" +
                        attribute0_size + " " +
                        attribute0_name + ";" +
                        attribute1_size + " " +
                        attribute1_name;

        String classHash = caller.call(new FunctionEnvironment("Serialization.ixi", "publishClassFragment"), args1, 3000).split(";")[1];
        Assert.assertNotNull(classHash);

        Thread.sleep(100);

        // publish a DataFragment

        String myOtherReferenced_trunk_hash = Generator.getRandomBundleHead();
        String myOtherReferenced_branch_hash = Generator.getRandomBundleHead();
        String data = "A 0 ATTRIB9ZERO9VALUE;A 1 ANOTHER9ATTRIBUTE";

        String args2 = classHash + ";" + myOtherReferenced_trunk_hash + ";" + myOtherReferenced_branch_hash + ";" + data;

        String result = caller.call(new FunctionEnvironment("Serialization.ixi", "publishDataFragment"), args2, 3000);

        Assert.assertTrue(result.length() > 0);

        System.out.println(result);

    }

}
