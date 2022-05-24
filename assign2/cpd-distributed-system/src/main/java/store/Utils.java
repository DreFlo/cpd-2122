package store;

import store.messages.SuccessorMessage;
import store.storeRecords.ClusterNodeInformation;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    public static ClusterNodeInformation getSuccessor(List<ClusterNodeInformation> nodeList, String id){
        int index;
        for(index = 0; index < nodeList.size(); index++){
            if (nodeList.get(index).id().equals(id)){
                break;
            }
        }

        if(index == nodeList.size() - 1) return nodeList.get(0);
        else return nodeList.get(index + 1);
    }

    public static void deleteAllKeys(Store store){
        for(String key : store.getKeys()){
            store.delete(key);
        }
    }

    public static ClusterNodeInformation getClusterNodeInformationFromSortedSetById(SortedSet<ClusterNodeInformation> clusterNodes, String nodeId) {
        for (ClusterNodeInformation clusterNodeInformation : clusterNodes.stream().toList()) {
            if (Objects.equals(clusterNodeInformation.id(), nodeId)) {
                return clusterNodeInformation;
            }
        }
        throw new RuntimeException("Node with id: " + nodeId + " not in set");
    }

    public static ClusterNodeInformation sendSuccessorKey(Store store, String id, AbstractMap.SimpleEntry<String, byte[]> keyValue) throws IOException {
        ClusterNodeInformation successor = Utils.getSuccessor(store.getClusterNodes().stream().toList(), id);
        if(store.getId().equals(successor.id())) return null;

        HashMap<String, byte[]> map = new HashMap<>();
        map.put(keyValue.getKey(), keyValue.getValue());
        SuccessorMessage successorMessage = new SuccessorMessage(store.getId(), store.getPort(), map);
        Socket firstSocket = new Socket(successor.ipAddress(), successor.port());
        store.sendTCP(successorMessage, firstSocket);

        return successor;
    }
}
