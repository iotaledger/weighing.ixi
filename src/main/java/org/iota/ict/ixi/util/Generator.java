package org.iota.ict.ixi.util;

import org.iota.ict.model.transaction.Transaction;
import org.iota.ict.utils.Constants;
import org.iota.ict.utils.Trytes;

import java.security.SecureRandom;

public class Generator {

    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ9";
    private static SecureRandom r = new SecureRandom();

    public static String getRandomHash(){
        StringBuilder sb = new StringBuilder(81);
        for( int i = 0; i < 81; i++ )
            sb.append( alphabet.charAt( r.nextInt(alphabet.length()) ) );
        return sb.toString();
    }

    public static String getRandomBundleHead() {
        String hash = getRandomHash();
        while(!isBundleHead(hash))
            hash = getRandomHash();
        return hash;
    }

    public static boolean isBundleHead(String hash){
        byte[] hashTrits = Trytes.toTrits(hash);
        return isFlagSet(hashTrits, Constants.HashFlags.BUNDLE_HEAD_FLAG);
    }

    private static boolean isFlagSet(byte[] hashTrits, int position) {
        assert hashTrits.length == Transaction.Field.TRUNK_HASH.tritLength;
        return hashTrits[position] == 1;
    }

    public static void main(String[] args) {
        String h = "TKTGCHDTRSC9GDYVXFTAFGGBTRVJHPSEZURRJ99XXJJ9ZGVEHSJABGEVYLWRRPCXAKF99BUORDXHRCZKQ";

        System.out.println(isBundleHead(h));
    }

}