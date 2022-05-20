package store;

import store.storeRecords.ClusterNodeInformation;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.SortedSet;

public class Utils {
    public static char[] stringToKey(String stringKey) {
        char[] key = new char[256];
        Integer id = Integer.parseInt(stringKey);
        for (int i = 255; i >= 0; i--) {
            char elem = (char) (id % 256);
            id /= 256;
            key[i] = elem;
        }
        return key;
    }

    public static String keyToString(char[] arrayKey) {
        Integer key = 0;
        Integer i = 0;
        for (char chr : arrayKey) {
            key = key * (int) Math.pow(10, i) + (int) chr;
            i++;
        }
        return key.toString();
    }

    public static boolean exclusiveOr(boolean lhs, boolean rhs) {
        return (lhs && !rhs) || (!lhs && rhs);
    }

    public static String hash(byte[] value) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = messageDigest.digest(value);

        HexFormat hexFormat = HexFormat.of().withDelimiter(":");
        String[] hexValues = hexFormat.formatHex(encodedHash).split(":");

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for(int i = hexValues.length - 1; i >= 0; i--){
            hexString.append(hexValues[i]);
        }

        System.out.println(new BigInteger(hexString.toString(), 16));
        return hexString.toString();
    }

    public static float getAngle(String hash){
        BigInteger bigInteger = new BigInteger(hash, 16);
        BigInteger mod = bigInteger.mod(new BigInteger("36500"));
        return mod.floatValue() / 100;
    }

    public static ClusterNodeInformation getClosestNode(List<ClusterNodeInformation> nodeList, float keyAngle){
        if((keyAngle < nodeList.get(0).angle()) || (keyAngle > nodeList.get(nodeList.size() - 1).angle())){
            return nodeList.get(0);
        }

        int start = 0, end = nodeList.size(), mid = 0;
        while(start < end){
            mid = (start + end) / 2;

            if(nodeList.get(mid).angle() == keyAngle){
                return nodeList.get(mid);
            }

            if(keyAngle < nodeList.get(mid).angle()){
                if(mid > 0 && keyAngle > nodeList.get(mid - 1).angle()){
                    return nodeList.get(mid);
                }
                else end = mid;
            }
            else{
                if(mid < nodeList.size() - 1 && keyAngle < nodeList.get(mid + 1).angle()){
                    return nodeList.get(mid + 1);
                }
                else start = mid + 1;
            }
        }

        return nodeList.get(mid);
    }
}
