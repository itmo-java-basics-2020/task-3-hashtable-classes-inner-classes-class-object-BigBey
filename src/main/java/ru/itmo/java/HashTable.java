package ru.itmo.java;

public class HashTable {
    private static final int DEFAULT_CAPACITY = 1000;
    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    private static final Entry DUMMY_ENTRY = new Entry(null, null, true);

    private Entry[] array;
    private int capacity;
    private int size;
    private double loadFactor;
    private int threshold;

    private static class Entry{
        Object key;
        Object value;
        boolean isDeleted;

        Entry(Object key, Object value) {
            this(key, value,false);
        }

        Entry(Object key, Object value, boolean deleted) {
            this.key = key;
            this.value = value;
            this.isDeleted = deleted;
        }
    }

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double loadFactor) {
        this(DEFAULT_CAPACITY, loadFactor);
    }

    public HashTable(int capacity, double loadFactor) {
        this.capacity = capacity;
        this.loadFactor = Math.max(Math.min(loadFactor,1),0);

        size = 0;
        array = new Entry[capacity];

        this.threshold = (int) (this.loadFactor*capacity);
    }

    private int hashCode(Object key){

        return (int)(capacity * (0.618 * Math.abs(key.hashCode()) % 1));
    }

    private int findIndexForKey(Object key){
        int hashIndex = hashCode(key);
        int curIndex = hashIndex;
        int i = 0;
        while (array[curIndex] != null
                && (!(key.equals(array[curIndex].key)) || array[curIndex].isDeleted)) {
            i++;
            curIndex = (hashIndex + i * 23) % capacity;
        }
        if (array[curIndex] == null) {
            return -1;
        }

        return curIndex;
    }

    Object put(Object key, Object value) {
        Entry temp = new Entry(key, value);

        int hashIndex = findIndexForKey(key);

        Object previousValue;

        if (hashIndex != -1) {
            previousValue = array[hashIndex].value;
            array[hashIndex] = temp;

            return previousValue;
        }

        int index = hashCode(key);
        int curIndex = index;
        int i = 0;

        while (!(array[curIndex] == null || array[curIndex].isDeleted)) {
            i++;
            curIndex = (index + i * 23) % capacity;
        }

        array[curIndex] = temp;
        size++;

        if (size >= threshold) {
            resize();
        }

        return null;
    }

    Object get(Object key) {

        int hashIndex = findIndexForKey(key);

        if (hashIndex == -1) {
            return null;
        }
        return array[hashIndex].value;
    }

    Object remove(Object key) {

        int index = findIndexForKey(key);
        if (index == -1) {
            return null;
        }

        Object removedValue = array[index].value;
        array[index] = DUMMY_ENTRY;
        size--;

        return removedValue;
    }

    int size() {
        return size;
    }

    private void resize(){

        Entry[] oldArray = array;

        capacity*=2;
        array = new Entry[capacity];
        threshold = (int) (loadFactor * capacity);
        size = 0;

        for(Entry e : oldArray){
            if(!(e == null || e.isDeleted)){
                this.put(e.key, e.value);
            }
        }
    }

}