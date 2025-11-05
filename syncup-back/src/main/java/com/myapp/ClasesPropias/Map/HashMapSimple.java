package com.myapp.ClasesPropias.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementación simple de un mapa hash con encadenamiento (buckets con listas enlazadas).
 * No pretende reemplazar a java.util.HashMap pero provee las operaciones básicas.
 */
public class HashMapSimple<K, V> implements MapSimple<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;
    private final float loadFactor;

    @SuppressWarnings("unchecked")
    public HashMapSimple(int initialCapacity, float loadFactor) {
        this.buckets = (Entry<K, V>[]) new Entry[initialCapacity];
        this.size = 0;
        this.loadFactor = loadFactor;
    }

    public HashMapSimple() {
        this(16, 0.75f);
    }

    private int indexFor(Object key, int length) {
        return (key == null) ? 0 : (Math.abs(Objects.hashCode(key)) % length);
    }

    @Override
    public V put(K key, V value) {
        if (needsResize()) resize();
        int idx = indexFor(key, buckets.length);
        Entry<K, V> head = buckets[idx];
        for (Entry<K, V> e = head; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                V old = e.value;
                e.value = value;
                return old;
            }
        }
        buckets[idx] = new Entry<>(key, value, head);
        size++;
        return null;
    }

    @Override
    public V get(K key) {
        int idx = indexFor(key, buckets.length);
        for (Entry<K, V> e = buckets[idx]; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) return e.value;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int idx = indexFor(key, buckets.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[idx]; e != null; e = e.next) {
            if (Objects.equals(e.key, key)) {
                V old = e.value;
                if (prev == null) buckets[idx] = e.next;
                else prev.next = e.next;
                size--;
                return old;
            }
            prev = e;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) buckets[i] = null;
        size = 0;
    }

    @Override
    public Iterable<K> keys() {
        List<K> list = new ArrayList<>(size);
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; e = e.next) {
                list.add(e.key);
            }
        }
        return list;
    }

    private boolean needsResize() {
        return size + 1 > buckets.length * loadFactor;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCap = buckets.length * 2;
        Entry<K, V>[] newBuckets = (Entry<K, V>[]) new Entry[newCap];
        for (Entry<K, V> bucket : buckets) {
            for (Entry<K, V> e = bucket; e != null; ) {
                Entry<K, V> next = e.next;
                int idx = indexFor(e.key, newCap);
                e.next = newBuckets[idx];
                newBuckets[idx] = e;
                e = next;
            }
        }
        buckets = newBuckets;
    }
}
