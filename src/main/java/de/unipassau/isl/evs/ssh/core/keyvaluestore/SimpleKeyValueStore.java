package de.unipassau.isl.evs.ssh.core.keyvaluestore;

import de.ncoder.typedmap.Key;
import de.ncoder.typedmap.TypedMap;
import de.unipassau.isl.evs.ssh.core.container.AbstractComponent;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by popeye on 6/24/16.
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
