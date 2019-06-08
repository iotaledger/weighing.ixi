import org.iota.ict.eee.call.EEEFunctionCallerImplementation;
import org.iota.ict.eee.call.FunctionEnvironment;
import org.iota.ict.ixi.util.Generator;
import org.iota.ict.ixi.util.TestTemplate;
import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.model.transaction.TransactionBuilder;
import org.iota.ict.utils.Trytes;
import org.junit.Assert;
import org.junit.Test;

public class SerializationTest extends TestTemplate {


    @Test
    public void demoEEE_API(){

        //=================================================
        //First, let's publish a random transaction
        //=================================================
        TransactionBuilder transactionBuilder = new TransactionBuilder();
        transactionBuilder.signatureFragments = Trytes.padRight("THE9REFERENCED9TRANSACTION", Transaction.Field.SIGNATURE_FRAGMENTS.tryteLength);
        Transaction theTargetTransaction = transactionBuilder.build();
        ict1.submit(theTargetTransaction);

        //Keep the tx hash
        String theTargetTransactionHash = theTargetTransaction.hash;

        //=================================================
        //Now let's build and publish a few DataFragments
        //=================================================

        EEEFunctionCallerImplementation caller = new EEEFunctionCallerImplementation(ict1);

        //To build a DataFragment, we need a ClassFragment :
        //(in this example, our class have a reference to a random transaction and an attribute name 'ATTRIB' with a length of 33 trytes)
        String response = caller.call(new FunctionEnvironment("Serialization.ixi","publishClassFragment"),
                /*   className;trunk;branch;attribute 0 definition; reference 0 definition   */
                "JUST9ANOTHER9CLASS9NAME;"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";33 ATTRIB;"+Trytes.NULL_HASH,  //using a different classname to avoid collision with the other test
                250);

        String myClassFragmentClassHash = response.split(";")[1];

        safeSleep(300);
        //now that we have our classFragment, let's build and publish a few DataFragments

        //the first dataFragment will reference the target transaction that we published earlier.

        response = caller.call(new FunctionEnvironment("Serialization.ixi","publishDataFragment"),
                /* classhash;trunk;branch;attribute 0;reference 0   */
                myClassFragmentClassHash+";"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";A 0 VALUE9A;R 0 "+theTargetTransactionHash,
                250);
        String firstFragmentHash = response;

        //now we publish a second DataFragment of the same class, referencing our target tx, but with a different value for ATTRIB
        response = caller.call(new FunctionEnvironment("Serialization.ixi","publishDataFragment"),
                myClassFragmentClassHash+";"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";A 0 VALUE9B;R 0 "+theTargetTransactionHash,
                250);
        String secondFragmentHash = response;

        //now we publish a third DataFragment of the same class, but referencing a random tx instead of our target
        response = caller.call(new FunctionEnvironment("Serialization.ixi","publishDataFragment"),
                myClassFragmentClassHash+";"+Trytes.NULL_HASH+";"+Trytes.NULL_HASH+";A 0 VALUE9B;R 0 "+Generator.getRandomHash(),
                250);
        String thirdFragmentHash = response;

        //in a test context: make a small pause to ensure that transactions are propagated.
        safeSleep(1000);

        //=================================================
        //Our data is now published, let's search for it
        //=================================================

        //search for allDataFragments referencing our target.
        // (we expect 2 fragments)
        response = caller.call(new FunctionEnvironment("Serialization.ixi","findReferencing"),
                theTargetTransactionHash,
                250);

        Assert.assertEquals(2, response.split(";").length);
        Assert.assertTrue(response.contains(firstFragmentHash));
        Assert.assertTrue(response.contains(secondFragmentHash));

        //search for allDataFragments referencing our target with ATTRIB value "VALUE9A".
        // (we expect 1 fragment, and it should be firstDataFragment)
        response = caller.call(new FunctionEnvironment("Serialization.ixi","findReferencing"),
                theTargetTransactionHash+";0;VALUE9A",
                250);

        Assert.assertEquals(1, response.split(";").length);
        Assert.assertEquals(firstFragmentHash,response);

    }

    private void safeSleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //ignore
        }
    }

}
