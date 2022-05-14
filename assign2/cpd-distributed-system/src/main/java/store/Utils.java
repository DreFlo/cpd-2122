package store;

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
}
