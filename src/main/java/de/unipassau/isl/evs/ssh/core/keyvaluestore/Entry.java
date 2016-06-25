package de.unipassau.isl.evs.ssh.core.keyvaluestore;

import java.io.Serializable;

/**
 * Created by popeye on 6/25/16.
 */
public class Entry<T extends Serializable> {
    private String key;
    private Serializable value;
    private Class<T> clazz;

    public Entry(String key, Class<T> clazz, T value) {
        this.key = key;
        this.value = value;
        this.clazz = clazz;
    }

    public String getKey() {
        return key;
    }

    public Serializable getValue() {
        return value;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
