package de.unipassau.isl.evs.ssh.core.keyvaluestore;

import java.io.Serializable;

/**
 * Created by popeye on 6/25/16.
 */
public interface Writer {
    <T extends Serializable> void write(String key, Class<T> clazz, T value);

    <T extends Serializable> void remove(String key, Class<T> clazz);
}
