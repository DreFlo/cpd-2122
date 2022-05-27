package store;

import java.io.IOException;

public interface KeyValueStore<K, V> {
    String put(K key, V value) throws IOException;
    V get(K key) throws IOException;
    String delete(K key) throws IOException;
}
