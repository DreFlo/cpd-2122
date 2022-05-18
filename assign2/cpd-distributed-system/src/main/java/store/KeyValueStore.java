package store;

import java.io.IOException;

public interface KeyValueStore<K, V> {
    void put(K key, V value) throws IOException;
    V get(K key);
    void delete(K key);
}
