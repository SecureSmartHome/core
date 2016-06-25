/*
 * MIT License
 *
 * Copyright (c) 2016.
 * Bucher Andreas, Fink Simon Dominik, Fraedrich Christoph, Popp Wolfgang,
 * Sell Leon, Werli Philemon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.unipassau.isl.evs.ssh.core.keyvaluestore;

import de.ncoder.typedmap.Key;
import de.ncoder.typedmap.TypedMap;
import de.unipassau.isl.evs.ssh.core.container.AbstractComponent;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The SimpleKeyValueStore provides an abstraction layer of system specific key value stores like Android's
 * SharedPreferences. The {@link SimpleKeyValueStore} writes various actions (to disk) with in a background task using a
 * {@link Writer}.
 */
public class SimpleKeyValueStore extends AbstractComponent {
    public static final Key<SimpleKeyValueStore> KEY = new Key<>(SimpleKeyValueStore.class);

    private TypedMap<Serializable> store = new TypedMap<>();
    private ExecutorService worker = Executors.newSingleThreadExecutor();
    private Writer writer;

    public SimpleKeyValueStore(Writer writer, Loader loader) {
        this.writer = writer;
        for (Entry<Serializable> entry : loader) {
            store.putTyped(new Key<>(entry.getClazz(), entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void destroy() {
        worker.shutdown();
        try {
            worker.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        worker.shutdownNow();
        super.destroy();
    }

    public synchronized int getInt(String key) {
        return store.get(new Key<>(Integer.class, key));
    }

    public synchronized String getString(String key) {
        return store.get(new Key<>(String.class, key));
    }

    public synchronized int putInt(String key, int value) {
        enqueueWriteAction(key, Integer.class, value);
        return store.putTyped(new Key<>(Integer.class, key), value);
    }

    public synchronized String putString(String key, String value) {
        enqueueWriteAction(key, String.class, value);
        return store.putTyped(new Key<>(String.class, key), value);
    }

    public synchronized int removeInt(String key) {
        enqueueRemoveAction(key, Integer.class);
        return store.remove(new Key<>(Integer.class, key));
    }

    public synchronized String removeString(String key) {
        enqueueRemoveAction(key, String.class);
        return store.remove(new Key<>(String.class, key));
    }

    private <T extends Serializable> void enqueueWriteAction(final String key, final Class<T> clazz, final T value) {
        worker.submit(new Runnable() {
            @Override
            public void run() {
                writer.write(key, clazz, value);
            }
        });
    }

    private <T extends Serializable> void enqueueRemoveAction(final String key, final Class<T> clazz) {
        worker.submit(new Runnable() {
            @Override
            public void run() {
                writer.remove(key, clazz);
            }
        });
    }

}
