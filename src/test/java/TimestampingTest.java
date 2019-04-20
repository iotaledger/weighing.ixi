import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.util.TestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.junit.Assert;
import org.junit.Test;

public class TimestampingTest extends TestTemplate {

    @Test
    public void testGetTimestampingInterval() {

        // t1
        TransactionBuilder tb1 = new TransactionBuilder();
        tb1.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb1.attachmentTimestamp = System.currentTimeMillis();
        tb1.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t1 = tb1.build();
        ict2.submit(t1);

        // t2
        TransactionBuilder tb2 = new TransactionBuilder();
        tb2.trunkHash = t1.hash;
        tb2.branchHash = t1.hash;
        tb2.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb2.attachmentTimestamp = System.currentTimeMillis();
        tb2.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t2 = tb2.build();
        ict2.submit(t2);

        // t3
        TransactionBuilder tb3 = new TransactionBuilder();
        tb3.trunkHash = t2.hash;
        tb3.branchHash = t2.hash;
        tb3.attachmentTimestampLowerBound = System.currentTimeMillis();
        tb3.attachmentTimestamp = System.currentTimeMillis();
        tb3.attachmentTimestampUpperBound = System.currentTimeMillis();
        Transaction t3 = tb3.build();
        ict2.submit(t3);

        // t4
        TransactionBuilder tb4 = new TransactionBuilder();
        tb4.trunkHash = t3.hash;
        tb4.branchHash = t3.hash;
        tb4.attachmentTimestampLowerBound = 10;
        tb4.attachmentTimestamp = 11;
        tb4.attachmentTimestampUpperBound = 12;
        Transaction t4 = tb4.build();
        ict2.submit(t4);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String identifier = caller.call(new FunctionEnvironment("Timestamping.ixi", "beginTimestampCalculation"), t3.hash + ";" + t1.hash, 3000);
        String[] interval = caller.call(new FunctionEnvironment("Timestamping.ixi", "getTimestampInterval"), identifier, 3000).split(";");
        Long lowerbound = Long.parseLong(interval[0]);
        Long upperbound = Long.parseLong(interval[1]);

        Assert.assertTrue(lowerbound == t2.attachmentTimestampLowerBound);
        Assert.assertTrue(upperbound == t4.attachmentTimestampUpperBound);

    }

}
