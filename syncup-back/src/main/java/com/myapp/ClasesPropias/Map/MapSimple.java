package com.myapp.ClasesPropias.Map;

/**
 * Interfaz mínima para un Map genérico simple.
 */
public interface MapSimple<K, V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
    boolean containsKey(K key);
    int size();
    boolean isEmpty();
    void clear();
    Iterable<K> keys();
}
